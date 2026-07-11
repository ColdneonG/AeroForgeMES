package com.fanmes.production.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fanmes.common.audit.OperationLogService;
import com.fanmes.common.exception.BadRequestException;
import com.fanmes.common.exception.NotFoundException;
import com.fanmes.common.id.IdGenerator;
import com.fanmes.common.idempotency.IdempotencyService;
import com.fanmes.production.domain.sop.SopAttachment;
import com.fanmes.production.domain.sop.SopBinding;
import com.fanmes.production.domain.sop.SopDocument;
import com.fanmes.production.domain.sop.SopExecutionRecord;
import com.fanmes.production.domain.sop.SopMatchCandidate;
import com.fanmes.production.domain.sop.SopModel;
import com.fanmes.production.domain.sop.SopModelVersion;
import com.fanmes.production.domain.sop.SopStatuses;
import com.fanmes.production.domain.sop.SopStep;
import com.fanmes.production.domain.sop.SopStepExecutionRecord;
import com.fanmes.production.domain.sop.SopTaskContext;
import com.fanmes.production.domain.sop.SopTaskPackage;
import com.fanmes.production.domain.sop.SopTaskSnapshot;
import com.fanmes.production.domain.sop.SopVersion;
import com.fanmes.production.dto.sop.SopBindingRequest;
import com.fanmes.production.dto.sop.SopDocumentRequest;
import com.fanmes.production.dto.sop.SopReportValidationResult;
import com.fanmes.production.dto.sop.SopStatusActionRequest;
import com.fanmes.production.dto.sop.SopStepExecutionRequest;
import com.fanmes.production.dto.sop.SopStepRequest;
import com.fanmes.production.dto.sop.SopVersionRequest;
import com.fanmes.production.infrastructure.SopRepository;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SopService {
    private static final Set<String> GLB_ONLY = Set.of("glb");

    private final SopRepository repository;
    private final SopFileStorageService storageService;
    private final OperationLogService operationLogService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public SopService(
            SopRepository repository,
            SopFileStorageService storageService,
            OperationLogService operationLogService,
            IdempotencyService idempotencyService,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.storageService = storageService;
        this.operationLogService = operationLogService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    public List<SopDocument> listDocuments(String keyword, String category, String status) {
        return repository.listDocuments(keyword, category, status);
    }

    public SopDocument getDocument(Long id) {
        return repository.findDocumentById(id)
                .orElseThrow(() -> notFound("sop_document", id));
    }

    @Transactional
    public SopDocument createDocument(SopDocumentRequest request) {
        repository.findDocumentByCode(request.sopCode())
                .ifPresent(existing -> {
                    throw new BadRequestException("SOP code already exists: " + existing.sopCode());
                });
        LocalDateTime now = LocalDateTime.now();
        SopDocument document = new SopDocument(
                IdGenerator.nextId(),
                request.sopCode().trim(),
                request.sopName().trim(),
                trimToDefault(request.category(), "STANDARD_OPERATION"),
                request.ownerId(),
                trimToDefault(request.status(), SopStatuses.ENABLED),
                null,
                now,
                now
        );
        repository.insertDocument(document);
        record("create", "sop_document", document.id(), null, document.status(), request.ownerId(), "create SOP document");
        return document;
    }

    @Transactional
    public SopDocument updateDocument(Long id, SopDocumentRequest request) {
        SopDocument existing = getDocument(id);
        repository.findDocumentByCode(request.sopCode())
                .filter(other -> !other.id().equals(id))
                .ifPresent(other -> {
                    throw new BadRequestException("SOP code already exists: " + other.sopCode());
                });
        SopDocument document = new SopDocument(
                id,
                request.sopCode().trim(),
                request.sopName().trim(),
                trimToDefault(request.category(), existing.category()),
                request.ownerId(),
                trimToDefault(request.status(), existing.status()),
                existing.currentVersionId(),
                existing.createdAt(),
                LocalDateTime.now()
        );
        repository.updateDocument(document);
        record("update", "sop_document", id, existing.status(), document.status(), request.ownerId(), "update SOP document");
        return document;
    }

    public List<SopVersion> listVersions(Long sopId) {
        getDocument(sopId);
        return repository.listVersions(sopId);
    }

    public SopVersion getVersion(Long id) {
        return repository.findVersionById(id)
                .orElseThrow(() -> notFound("sop_version", id));
    }

    @Transactional
    public SopVersion createVersion(Long sopId, SopVersionRequest request) {
        getDocument(sopId);
        String versionNo = trimToDefault(request.versionNo(), nextVersionNo(sopId));
        ensureVersionNoAvailable(sopId, versionNo);
        LocalDateTime now = LocalDateTime.now();
        SopVersion version = new SopVersion(
                IdGenerator.nextId(),
                sopId,
                versionNo,
                trimToDefault(request.revisionType(), "NEW"),
                SopStatuses.DRAFT,
                request.effectiveFrom(),
                request.effectiveTo(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                request.modelVersionId(),
                trim(request.remark()),
                now,
                now
        );
        repository.insertVersion(version);
        record("create-version", "sop_version", version.id(), null, version.status(), request.operatorId(), "create SOP version");
        return version;
    }

    @Transactional
    public SopVersion copyVersion(Long versionId, SopVersionRequest request) {
        SopVersion source = getVersion(versionId);
        String versionNo = trimToDefault(request.versionNo(), nextVersionNo(source.sopId()));
        ensureVersionNoAvailable(source.sopId(), versionNo);
        SopVersion copied = createVersion(source.sopId(), new SopVersionRequest(
                versionNo,
                trimToDefault(request.revisionType(), "MINOR"),
                request.effectiveFrom(),
                request.effectiveTo(),
                source.modelVersionId(),
                request.operatorId(),
                trimToDefault(request.remark(), "copy from " + source.versionNo())
        ));
        for (SopStep step : repository.listSteps(source.id())) {
            SopStep copiedStep = new SopStep(
                    IdGenerator.nextId(),
                    copied.id(),
                    step.stepNo(),
                    step.stepName(),
                    step.instruction(),
                    step.contentType(),
                    step.standardDuration(),
                    step.keyStep(),
                    step.minStaySeconds(),
                    step.confirmRequired(),
                    step.parameterRequired(),
                    step.photoRequired(),
                    step.skipAllowed(),
                    step.abnormalHandling(),
                    step.qualityItemId(),
                    step.andonTypeId(),
                    step.enabled(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            repository.insertStep(copiedStep);
        }
        for (SopAttachment attachment : repository.listAttachments(source.id())) {
            repository.insertAttachment(new SopAttachment(
                    IdGenerator.nextId(),
                    copied.id(),
                    null,
                    attachment.attachmentType(),
                    attachment.fileName(),
                    attachment.contentType(),
                    attachment.fileSize(),
                    attachment.objectKey(),
                    attachment.fileUrl(),
                    attachment.sha256(),
                    attachment.displayOrder(),
                    LocalDateTime.now()
            ));
        }
        record("copy-version", "sop_version", copied.id(), source.versionNo(), copied.versionNo(), request.operatorId(), "copy SOP version");
        return copied;
    }

    @Transactional
    public SopVersion submitVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = requireEditableVersion(versionId);
        validateVersionContent(version.id());
        SopVersion next = withStatus(version, SopStatuses.PENDING_REVIEW, request.operatorId(), null, null, null, trim(request.remark()));
        repository.updateVersion(next);
        record("submit", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    @Transactional
    public SopVersion rejectVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = getVersion(versionId);
        if (!SopStatuses.PENDING_REVIEW.equals(version.status())) {
            throw new BadRequestException("only pending review SOP version can be rejected");
        }
        SopVersion next = new SopVersion(
                version.id(), version.sopId(), version.versionNo(), version.revisionType(), SopStatuses.REJECTED,
                version.effectiveFrom(), version.effectiveTo(), version.submitBy(), version.submitAt(),
                request.operatorId(), LocalDateTime.now(), version.approveBy(), version.approveAt(),
                version.publishBy(), version.publishAt(), version.modelVersionId(), trim(request.remark()),
                version.createdAt(), LocalDateTime.now()
        );
        repository.updateVersion(next);
        record("reject", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    @Transactional
    public SopVersion approveVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = getVersion(versionId);
        if (!SopStatuses.PENDING_REVIEW.equals(version.status())) {
            throw new BadRequestException("only pending review SOP version can be approved");
        }
        SopVersion next = new SopVersion(
                version.id(), version.sopId(), version.versionNo(), version.revisionType(), SopStatuses.APPROVED,
                version.effectiveFrom(), version.effectiveTo(), version.submitBy(), version.submitAt(),
                request.operatorId(), LocalDateTime.now(), request.operatorId(), LocalDateTime.now(),
                version.publishBy(), version.publishAt(), version.modelVersionId(), trim(request.remark()),
                version.createdAt(), LocalDateTime.now()
        );
        repository.updateVersion(next);
        record("approve", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    @Transactional
    public SopVersion publishVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = getVersion(versionId);
        if (!SopStatuses.APPROVED.equals(version.status())) {
            throw new BadRequestException("only approved SOP version can be published");
        }
        validateVersionContent(version.id());
        List<Long> conflictIds = repository.listBindingConflictIds(version.id());
        if (!conflictIds.isEmpty()) {
            throw new BadRequestException("SOP binding conflicts with published versions: " + conflictIds);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime effectiveFrom = request.effectiveFrom() == null ? now : request.effectiveFrom();
        String status = effectiveFrom.isAfter(now) ? SopStatuses.PENDING_EFFECTIVE : SopStatuses.EFFECTIVE;
        SopVersion next = new SopVersion(
                version.id(), version.sopId(), version.versionNo(), version.revisionType(), status,
                effectiveFrom, version.effectiveTo(), version.submitBy(), version.submitAt(),
                version.reviewBy(), version.reviewAt(), version.approveBy(), version.approveAt(),
                request.operatorId(), now, version.modelVersionId(), trim(request.remark()),
                version.createdAt(), now
        );
        repository.updateVersion(next);
        if (SopStatuses.EFFECTIVE.equals(status)) {
            repository.updateDocumentCurrentVersion(version.sopId(), version.id());
        }
        record("publish", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    @Transactional
    public SopVersion disableVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = getVersion(versionId);
        if (!Set.of(SopStatuses.EFFECTIVE, SopStatuses.PENDING_EFFECTIVE).contains(version.status())) {
            throw new BadRequestException("only effective SOP version can be disabled");
        }
        SopVersion next = setSimpleStatus(version, SopStatuses.DISABLED, request.remark());
        repository.updateVersion(next);
        record("disable", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    @Transactional
    public SopVersion voidVersion(Long versionId, SopStatusActionRequest request) {
        SopVersion version = getVersion(versionId);
        if (!SopStatuses.isReleasedVersion(version.status()) && !SopStatuses.REJECTED.equals(version.status())) {
            throw new BadRequestException("SOP version cannot be voided from status: " + version.status());
        }
        SopVersion next = setSimpleStatus(version, SopStatuses.VOID, request.remark());
        repository.updateVersion(next);
        record("void", "sop_version", versionId, version.status(), next.status(), request.operatorId(), request.remark());
        return next;
    }

    public List<SopStep> listSteps(Long versionId) {
        getVersion(versionId);
        return repository.listSteps(versionId);
    }

    @Transactional
    public SopStep createStep(Long versionId, SopStepRequest request) {
        requireEditableVersion(versionId);
        LocalDateTime now = LocalDateTime.now();
        SopStep step = toStep(IdGenerator.nextId(), versionId, request, now, now);
        repository.insertStep(step);
        return step;
    }

    @Transactional
    public SopStep updateStep(Long id, SopStepRequest request) {
        SopStep existing = getStep(id);
        requireEditableVersion(existing.versionId());
        SopStep step = toStep(id, existing.versionId(), request, existing.createdAt(), LocalDateTime.now());
        repository.updateStep(step);
        return step;
    }

    @Transactional
    public void deleteStep(Long id) {
        SopStep existing = getStep(id);
        requireEditableVersion(existing.versionId());
        repository.deleteStep(id);
    }

    public SopStep getStep(Long id) {
        return repository.findStepById(id)
                .orElseThrow(() -> notFound("sop_step", id));
    }

    public List<SopAttachment> listAttachments(Long versionId) {
        getVersion(versionId);
        return repository.listAttachments(versionId);
    }

    @Transactional
    public SopAttachment uploadAttachment(Long versionId, Long stepId, MultipartFile file, Integer displayOrder) {
        requireEditableVersion(versionId);
        if (stepId != null) {
            SopStep step = getStep(stepId);
            if (!versionId.equals(step.versionId())) {
                throw new BadRequestException("attachment step does not belong to SOP version");
            }
        }
        SopFileStorageService.StoredFile stored = storageService.store(file, "sop/attachments", null);
        Long id = IdGenerator.nextId();
        SopAttachment attachment = new SopAttachment(
                id,
                versionId,
                stepId,
                attachmentType(stored.fileName(), stored.contentType()),
                stored.fileName(),
                stored.contentType(),
                stored.fileSize(),
                stored.objectKey(),
                "/api/production/sop/files/" + id,
                stored.sha256(),
                displayOrder == null ? 0 : displayOrder,
                LocalDateTime.now()
        );
        repository.insertAttachment(attachment);
        return attachment;
    }

    @Transactional
    public void deleteAttachment(Long id) {
        SopAttachment attachment = repository.findAttachmentById(id)
                .orElseThrow(() -> notFound("sop_attachment", id));
        requireEditableVersion(attachment.versionId());
        repository.deleteAttachment(id);
    }

    @Transactional
    public SopModelVersion uploadModel(Long versionId, MultipartFile file, Long operatorId) {
        SopVersion version = requireEditableVersion(versionId);
        SopFileStorageService.StoredFile stored = storageService.store(file, "sop/models", GLB_ONLY);
        LocalDateTime now = LocalDateTime.now();
        Long modelId = IdGenerator.nextId();
        SopModel model = new SopModel(
                modelId,
                "MODEL-" + version.sopId() + "-" + modelId,
                stored.fileName(),
                SopStatuses.ENABLED,
                now,
                now
        );
        repository.insertModel(model);
        Long modelVersionId = IdGenerator.nextId();
        SopModelVersion modelVersion = new SopModelVersion(
                modelVersionId,
                modelId,
                trimToDefault(version.versionNo(), "V1.0"),
                stored.fileName(),
                stored.objectKey(),
                "/api/production/sop/model-files/" + modelVersionId,
                stored.sha256(),
                stored.fileSize(),
                SopStatuses.EFFECTIVE,
                now
        );
        repository.insertModelVersion(modelVersion);
        repository.updateVersionModelVersion(versionId, modelVersionId);
        record("upload-model", "sop_model_version", modelVersionId, null, modelVersion.status(), operatorId, stored.fileName());
        return modelVersion;
    }

    public List<SopBinding> listBindings(Long versionId) {
        getVersion(versionId);
        return repository.listBindings(versionId);
    }

    @Transactional
    public SopBinding createBinding(Long versionId, SopBindingRequest request) {
        requireEditableVersion(versionId);
        validateBindingTarget(request);
        LocalDateTime now = LocalDateTime.now();
        SopBinding binding = new SopBinding(
                IdGenerator.nextId(),
                versionId,
                request.bindingType().trim(),
                request.productId(),
                request.routeId(),
                request.routeStepId(),
                request.processId(),
                request.workstationId(),
                request.equipmentId(),
                request.taskId(),
                request.priority() == null ? 10 : request.priority(),
                trimToDefault(request.confirmMode(), "PER_TASK"),
                request.effectiveFrom(),
                request.effectiveTo(),
                trimToDefault(request.status(), SopStatuses.ACTIVE),
                now,
                now
        );
        repository.insertBinding(binding);
        return binding;
    }

    @Transactional
    public void deleteBinding(Long id) {
        SopBinding binding = repository.findBindingById(id)
                .orElseThrow(() -> notFound("sop_binding", id));
        requireEditableVersion(binding.versionId());
        repository.deleteBinding(id);
    }

    @Transactional
    public SopTaskSnapshot lockTaskSop(Long taskId, Long operatorId) {
        return repository.findSnapshotByTaskId(taskId)
                .orElseGet(() -> {
                    try {
                        return createTaskSnapshot(taskId, operatorId);
                    } catch (DuplicateKeyException ex) {
                        return repository.findSnapshotByTaskId(taskId)
                                .orElseThrow(() -> ex);
                    }
                });
    }

    @Transactional
    public SopTaskPackage openTaskSop(Long taskId, Long operatorId) {
        SopTaskSnapshot snapshot = lockTaskSop(taskId, operatorId);
        SopExecutionRecord execution = repository.findExecutionBySnapshotId(snapshot.id())
                .orElseGet(() -> {
                    SopExecutionRecord newExecution = new SopExecutionRecord(
                            IdGenerator.nextId(),
                            snapshot.id(),
                            snapshot.taskId(),
                            snapshot.sopId(),
                            snapshot.versionId(),
                            operatorId,
                            LocalDateTime.now(),
                            null,
                            SopStatuses.ACTIVE
                    );
                    repository.insertExecution(newExecution);
                    record("open", "sop_execution_record", newExecution.id(), null, newExecution.status(), operatorId, "open SOP task");
                    return newExecution;
                });
        return packageOf(snapshot, execution);
    }

    @Transactional
    public SopStepExecutionRecord viewStep(Long snapshotId, Long stepId, Long operatorId) {
        SopTaskSnapshot snapshot = getSnapshot(snapshotId);
        SopExecutionRecord execution = ensureExecution(snapshot, operatorId);
        SopStep step = getStep(stepId);
        ensureStepBelongsToSnapshot(snapshot, step);
        SopStepExecutionRecord existing = repository.findStepRecord(snapshot.id(), step.id()).orElse(null);
        SopStepExecutionRecord record = new SopStepExecutionRecord(
                existing == null ? IdGenerator.nextId() : existing.id(),
                execution.id(),
                snapshot.id(),
                snapshot.taskId(),
                step.id(),
                step.stepNo(),
                existing == null ? LocalDateTime.now() : existing.viewStartedAt(),
                existing == null ? null : existing.confirmedAt(),
                existing == null ? null : existing.staySeconds(),
                existing == null ? null : existing.parameterJson(),
                existing == null ? null : existing.photoAttachmentId(),
                existing == null ? "VIEWED" : existing.result(),
                operatorId,
                existing == null ? null : existing.idempotencyKey()
        );
        repository.upsertStepView(record);
        record("view-step", "sop_step_execution_record", record.id(), null, record.result(), operatorId, "view step " + step.stepNo());
        return record;
    }

    @Transactional
    public SopStepExecutionRecord confirmStep(Long snapshotId, Long stepId, SopStepExecutionRequest request) {
        if (StringUtils.hasText(request.idempotencyKey())) {
            String businessId = idempotencyService.findBusinessId("production-sop", "confirm-step", request.idempotencyKey())
                    .orElse(null);
            if (businessId != null) {
                return repository.findStepRecord(snapshotId, stepId)
                        .orElseThrow(() -> new BadRequestException("idempotent SOP step record is missing: " + businessId));
            }
        }

        SopTaskSnapshot snapshot = getSnapshot(snapshotId);
        SopExecutionRecord execution = ensureExecution(snapshot, request.operatorId());
        SopStep step = getStep(stepId);
        ensureStepBelongsToSnapshot(snapshot, step);
        SopStepExecutionRecord viewed = repository.findStepRecord(snapshot.id(), step.id())
                .orElseGet(() -> viewStep(snapshot.id(), step.id(), request.operatorId()));
        if (viewed.confirmedAt() != null) {
            return viewed;
        }
        LocalDateTime now = LocalDateTime.now();
        int staySeconds = viewed.viewStartedAt() == null ? 0 : (int) Duration.between(viewed.viewStartedAt(), now).getSeconds();
        int requiredStay = step.minStaySeconds() == null ? 0 : step.minStaySeconds();
        if (requiredStay > 0 && staySeconds < requiredStay) {
            throw new BadRequestException("step requires at least " + requiredStay + " seconds viewing before confirmation");
        }
        if (Boolean.TRUE.equals(step.parameterRequired()) && !StringUtils.hasText(request.parameterJson())) {
            throw new BadRequestException("step requires parameter input");
        }
        if (Boolean.TRUE.equals(step.photoRequired()) && request.photoAttachmentId() == null) {
            throw new BadRequestException("step requires photo attachment");
        }
        SopStepExecutionRecord confirmed = new SopStepExecutionRecord(
                viewed.id(),
                execution.id(),
                snapshot.id(),
                snapshot.taskId(),
                step.id(),
                step.stepNo(),
                viewed.viewStartedAt(),
                now,
                staySeconds,
                trim(request.parameterJson()),
                request.photoAttachmentId(),
                trimToDefault(request.result(), "CONFIRMED"),
                request.operatorId(),
                trim(request.idempotencyKey())
        );
        repository.confirmStep(confirmed);
        if (StringUtils.hasText(request.idempotencyKey())) {
            idempotencyService.record("production-sop", "confirm-step", request.idempotencyKey(), confirmed.id().toString());
        }
        int pending = repository.countPendingRequiredSteps(snapshot.id(), snapshot.versionId());
        if (pending == 0) {
            repository.completeExecution(execution.id());
            repository.updateSnapshotStatus(snapshot.id(), SopStatuses.COMPLETED);
        }
        record("confirm-step", "sop_step_execution_record", confirmed.id(), viewed.result(), confirmed.result(), request.operatorId(), "confirm step " + step.stepNo());
        return confirmed;
    }

    public SopReportValidationResult validateReport(Long taskId) {
        SopTaskSnapshot snapshot = repository.findSnapshotByTaskId(taskId).orElse(null);
        if (snapshot == null) {
            return new SopReportValidationResult(false, taskId, null, 0, List.of("SOP has not been locked for this task"));
        }
        int pending = repository.countPendingRequiredSteps(snapshot.id(), snapshot.versionId());
        List<String> messages = new ArrayList<>();
        if (pending > 0) {
            messages.add("There are " + pending + " required SOP steps pending confirmation");
        }
        return new SopReportValidationResult(pending == 0, taskId, snapshot.id(), pending, messages);
    }

    public List<SopExecutionRecord> listExecutions(Long taskId, Long versionId) {
        return repository.listExecutions(taskId, versionId);
    }

    public StoredResource loadAttachmentFile(Long attachmentId) {
        SopAttachment attachment = repository.findAttachmentById(attachmentId)
                .orElseThrow(() -> notFound("sop_attachment", attachmentId));
        return new StoredResource(storageService.load(attachment.objectKey()), attachment.contentType(), attachment.fileName());
    }

    public StoredResource loadModelFile(Long modelVersionId) {
        SopModelVersion modelVersion = repository.findModelVersionById(modelVersionId)
                .orElseThrow(() -> notFound("sop_model_version", modelVersionId));
        return new StoredResource(storageService.load(modelVersion.objectKey()), "model/gltf-binary", modelVersion.fileName());
    }

    private SopTaskSnapshot createTaskSnapshot(Long taskId, Long operatorId) {
        SopTaskContext task = repository.findTaskContext(taskId)
                .orElseThrow(() -> notFound("shop_task", taskId));
        List<SopMatchCandidate> candidates = repository.listMatchCandidates(task);
        if (candidates.isEmpty()) {
            throw new BadRequestException("no effective SOP matched for task: " + taskId);
        }
        SopMatchCandidate selected = candidates.get(0);
        if (candidates.size() > 1 && samePriority(selected, candidates.get(1))) {
            throw new BadRequestException("multiple SOP bindings matched with same priority for task: " + taskId);
        }
        SopModelVersion modelVersion = selected.version().modelVersionId() == null
                ? null
                : repository.findModelVersionById(selected.version().modelVersionId()).orElse(null);
        SopTaskSnapshot snapshot = new SopTaskSnapshot(
                IdGenerator.nextId(),
                taskId,
                selected.document().id(),
                selected.version().id(),
                selected.version().versionNo(),
                modelVersion == null ? null : modelVersion.modelId(),
                modelVersion == null ? null : modelVersion.id(),
                modelVersion == null ? null : modelVersion.sha256(),
                snapshotJson(selected, task, modelVersion),
                operatorId,
                LocalDateTime.now(),
                selected.binding().bindingType() + ":" + selected.binding().priority(),
                SopStatuses.ACTIVE
        );
        repository.insertSnapshot(snapshot);
        record("lock", "sop_task_snapshot", snapshot.id(), null, snapshot.status(), operatorId, "lock SOP version " + snapshot.versionNo());
        return snapshot;
    }

    private SopTaskPackage packageOf(SopTaskSnapshot snapshot, SopExecutionRecord execution) {
        SopDocument document = getDocument(snapshot.sopId());
        SopVersion version = getVersion(snapshot.versionId());
        return new SopTaskPackage(
                document,
                version,
                snapshot,
                execution,
                repository.listSteps(snapshot.versionId()),
                repository.listAttachments(snapshot.versionId()),
                repository.listStepRecordsBySnapshot(snapshot.id())
        );
    }

    private SopExecutionRecord ensureExecution(SopTaskSnapshot snapshot, Long operatorId) {
        return repository.findExecutionBySnapshotId(snapshot.id())
                .orElseGet(() -> {
                    SopExecutionRecord execution = new SopExecutionRecord(
                            IdGenerator.nextId(),
                            snapshot.id(),
                            snapshot.taskId(),
                            snapshot.sopId(),
                            snapshot.versionId(),
                            operatorId,
                            LocalDateTime.now(),
                            null,
                            SopStatuses.ACTIVE
                    );
                    repository.insertExecution(execution);
                    return execution;
                });
    }

    private SopTaskSnapshot getSnapshot(Long snapshotId) {
        return repository.findSnapshotById(snapshotId)
                .orElseThrow(() -> notFound("sop_task_snapshot", snapshotId));
    }

    private SopVersion requireEditableVersion(Long versionId) {
        SopVersion version = getVersion(versionId);
        if (!SopStatuses.isEditableVersion(version.status())) {
            throw new BadRequestException("SOP version is not editable: " + version.status());
        }
        return version;
    }

    private void validateVersionContent(Long versionId) {
        if (repository.countSteps(versionId) == 0) {
            throw new BadRequestException("SOP version must contain at least one enabled step");
        }
    }

    private void validateBindingTarget(SopBindingRequest request) {
        if (request.taskId() == null
                && request.productId() == null
                && request.routeId() == null
                && request.routeStepId() == null
                && request.processId() == null
                && request.workstationId() == null
                && request.equipmentId() == null) {
            throw new BadRequestException("SOP binding must target at least one business object");
        }
    }

    private void ensureStepBelongsToSnapshot(SopTaskSnapshot snapshot, SopStep step) {
        if (!snapshot.versionId().equals(step.versionId())) {
            throw new BadRequestException("SOP step does not belong to locked snapshot version");
        }
    }

    private SopStep toStep(Long id, Long versionId, SopStepRequest request, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new SopStep(
                id,
                versionId,
                request.stepNo(),
                request.stepName().trim(),
                trim(request.instruction()),
                trimToDefault(request.contentType(), "TEXT"),
                request.standardDuration(),
                defaultBoolean(request.keyStep(), false),
                request.minStaySeconds() == null ? 0 : request.minStaySeconds(),
                defaultBoolean(request.confirmRequired(), true),
                defaultBoolean(request.parameterRequired(), false),
                defaultBoolean(request.photoRequired(), false),
                defaultBoolean(request.skipAllowed(), false),
                trim(request.abnormalHandling()),
                request.qualityItemId(),
                request.andonTypeId(),
                defaultBoolean(request.enabled(), true),
                createdAt,
                updatedAt
        );
    }

    private SopVersion withStatus(
            SopVersion version,
            String status,
            Long submitBy,
            Long reviewBy,
            Long approveBy,
            Long publishBy,
            String remark
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new SopVersion(
                version.id(), version.sopId(), version.versionNo(), version.revisionType(), status,
                version.effectiveFrom(), version.effectiveTo(),
                submitBy == null ? version.submitBy() : submitBy,
                submitBy == null ? version.submitAt() : now,
                reviewBy == null ? version.reviewBy() : reviewBy,
                reviewBy == null ? version.reviewAt() : now,
                approveBy == null ? version.approveBy() : approveBy,
                approveBy == null ? version.approveAt() : now,
                publishBy == null ? version.publishBy() : publishBy,
                publishBy == null ? version.publishAt() : now,
                version.modelVersionId(), remark,
                version.createdAt(), now
        );
    }

    private SopVersion setSimpleStatus(SopVersion version, String status, String remark) {
        return new SopVersion(
                version.id(), version.sopId(), version.versionNo(), version.revisionType(), status,
                version.effectiveFrom(), version.effectiveTo(), version.submitBy(), version.submitAt(),
                version.reviewBy(), version.reviewAt(), version.approveBy(), version.approveAt(),
                version.publishBy(), version.publishAt(), version.modelVersionId(), trim(remark),
                version.createdAt(), LocalDateTime.now()
        );
    }

    private boolean samePriority(SopMatchCandidate left, SopMatchCandidate right) {
        Integer leftPriority = left.binding().priority() == null ? 0 : left.binding().priority();
        Integer rightPriority = right.binding().priority() == null ? 0 : right.binding().priority();
        return leftPriority.equals(rightPriority);
    }

    private String snapshotJson(SopMatchCandidate selected, SopTaskContext task, SopModelVersion modelVersion) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("task", task);
        snapshot.put("document", selected.document());
        snapshot.put("version", selected.version());
        snapshot.put("binding", selected.binding());
        snapshot.put("steps", repository.listSteps(selected.version().id()));
        snapshot.put("attachments", repository.listAttachments(selected.version().id()));
        snapshot.put("modelVersion", modelVersion);
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("failed to create SOP task snapshot");
        }
    }

    private String nextVersionNo(Long sopId) {
        int next = repository.listVersions(sopId).size() + 1;
        return "V" + next + ".0";
    }

    private void ensureVersionNoAvailable(Long sopId, String versionNo) {
        repository.findVersionByNo(sopId, versionNo)
                .ifPresent(existing -> {
                    throw new BadRequestException("SOP version already exists: " + versionNo);
                });
    }

    private String attachmentType(String fileName, String contentType) {
        String lower = fileName == null ? "" : fileName.toLowerCase();
        if (lower.endsWith(".glb")) return "MODEL_3D";
        if (lower.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(contentType)) return "PDF";
        if (lower.endsWith(".mp4") || lower.endsWith(".webm") || startsWith(contentType, "video/")) return "VIDEO";
        if (startsWith(contentType, "image/")) return "IMAGE";
        return "FILE";
    }

    private boolean startsWith(String value, String prefix) {
        return value != null && value.toLowerCase().startsWith(prefix);
    }

    private Boolean defaultBoolean(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String trimToDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value.trim() : defaultValue;
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void record(
            String actionName,
            String tableName,
            Long targetId,
            String oldStatus,
            String newStatus,
            Long operatorId,
            String remark
    ) {
        operationLogService.recordStatusChange("production-sop", tableName, targetId, actionName, oldStatus, newStatus, operatorId, remark);
    }

    private NotFoundException notFound(String tableName, Long id) {
        return new NotFoundException(tableName + " not found: " + id);
    }

    public record StoredResource(Resource resource, String contentType, String fileName) {
    }
}
