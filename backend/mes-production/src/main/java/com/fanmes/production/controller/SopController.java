package com.fanmes.production.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.common.security.RequirePermission;
import com.fanmes.production.domain.sop.SopAttachment;
import com.fanmes.production.domain.sop.SopBinding;
import com.fanmes.production.domain.sop.SopDocument;
import com.fanmes.production.domain.sop.SopExecutionRecord;
import com.fanmes.production.domain.sop.SopModelVersion;
import com.fanmes.production.domain.sop.SopStep;
import com.fanmes.production.domain.sop.SopStepExecutionRecord;
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
import com.fanmes.production.service.SopService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/production/sop")
public class SopController {
    private final SopService service;

    public SopController(SopService service) {
        this.service = service;
    }

    @GetMapping("/documents")
    @RequirePermission("process:sop:list")
    public ApiResponse<List<SopDocument>> listDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listDocuments(keyword, category, status));
    }

    @GetMapping("/documents/{id}")
    @RequirePermission("process:sop:query")
    public ApiResponse<Map<String, Object>> getDocument(@PathVariable Long id) {
        return ApiResponse.ok(Map.of(
                "document", service.getDocument(id),
                "versions", service.listVersions(id)
        ));
    }

    @PostMapping("/documents")
    @RequirePermission("process:sop:add")
    public ApiResponse<SopDocument> createDocument(@Valid @RequestBody SopDocumentRequest request) {
        return ApiResponse.ok(service.createDocument(request));
    }

    @PutMapping("/documents/{id}")
    @RequirePermission("process:sop:edit")
    public ApiResponse<SopDocument> updateDocument(@PathVariable Long id, @Valid @RequestBody SopDocumentRequest request) {
        return ApiResponse.ok(service.updateDocument(id, request));
    }

    @GetMapping("/documents/{id}/versions")
    @RequirePermission("process:sop:query")
    public ApiResponse<List<SopVersion>> listVersions(@PathVariable Long id) {
        return ApiResponse.ok(service.listVersions(id));
    }

    @PostMapping("/documents/{id}/versions")
    @RequirePermission("process:sop:add")
    public ApiResponse<SopVersion> createVersion(@PathVariable Long id, @Valid @RequestBody SopVersionRequest request) {
        return ApiResponse.ok(service.createVersion(id, request));
    }

    @PostMapping("/versions/{id}/copy")
    @RequirePermission("process:sop:copyVersion")
    public ApiResponse<SopVersion> copyVersion(@PathVariable Long id, @Valid @RequestBody SopVersionRequest request) {
        return ApiResponse.ok(service.copyVersion(id, request));
    }

    @PostMapping("/versions/{id}/submit")
    @RequirePermission("process:sop:submit")
    public ApiResponse<SopVersion> submitVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.submitVersion(id, request));
    }

    @PostMapping("/versions/{id}/reject")
    @RequirePermission("process:sop:review")
    public ApiResponse<SopVersion> rejectVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.rejectVersion(id, request));
    }

    @PostMapping("/versions/{id}/approve")
    @RequirePermission("process:sop:approve")
    public ApiResponse<SopVersion> approveVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.approveVersion(id, request));
    }

    @PostMapping("/versions/{id}/publish")
    @RequirePermission("process:sop:publish")
    public ApiResponse<SopVersion> publishVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.publishVersion(id, request));
    }

    @PostMapping("/versions/{id}/disable")
    @RequirePermission("process:sop:disable")
    public ApiResponse<SopVersion> disableVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.disableVersion(id, request));
    }

    @PostMapping("/versions/{id}/void")
    @RequirePermission("process:sop:void")
    public ApiResponse<SopVersion> voidVersion(@PathVariable Long id, @Valid @RequestBody SopStatusActionRequest request) {
        return ApiResponse.ok(service.voidVersion(id, request));
    }

    @GetMapping("/versions/{id}/steps")
    @RequirePermission("process:sop:query")
    public ApiResponse<List<SopStep>> listSteps(@PathVariable Long id) {
        return ApiResponse.ok(service.listSteps(id));
    }

    @PostMapping("/versions/{id}/steps")
    @RequirePermission("process:sop:edit")
    public ApiResponse<SopStep> createStep(@PathVariable Long id, @Valid @RequestBody SopStepRequest request) {
        return ApiResponse.ok(service.createStep(id, request));
    }

    @PutMapping("/steps/{id}")
    @RequirePermission("process:sop:edit")
    public ApiResponse<SopStep> updateStep(@PathVariable Long id, @Valid @RequestBody SopStepRequest request) {
        return ApiResponse.ok(service.updateStep(id, request));
    }

    @DeleteMapping("/steps/{id}")
    @RequirePermission("process:sop:edit")
    public ApiResponse<Void> deleteStep(@PathVariable Long id) {
        service.deleteStep(id);
        return ApiResponse.ok();
    }

    @GetMapping("/versions/{id}/attachments")
    @RequirePermission("process:sop:query")
    public ApiResponse<List<SopAttachment>> listAttachments(@PathVariable Long id) {
        return ApiResponse.ok(service.listAttachments(id));
    }

    @PostMapping(value = "/versions/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission("process:sop:edit")
    public ApiResponse<SopAttachment> uploadAttachment(
            @PathVariable Long id,
            @RequestParam(required = false) Long stepId,
            @RequestParam(required = false) Integer displayOrder,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.ok(service.uploadAttachment(id, stepId, file, displayOrder));
    }

    @DeleteMapping("/attachments/{id}")
    @RequirePermission("process:sop:edit")
    public ApiResponse<Void> deleteAttachment(@PathVariable Long id) {
        service.deleteAttachment(id);
        return ApiResponse.ok();
    }

    @PostMapping(value = "/versions/{id}/models", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission("process:sopModel:upload")
    public ApiResponse<SopModelVersion> uploadModel(
            @PathVariable Long id,
            @RequestParam(required = false) Long operatorId,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.ok(service.uploadModel(id, file, operatorId));
    }

    @GetMapping("/versions/{id}/bindings")
    @RequirePermission("process:sop:binding")
    public ApiResponse<List<SopBinding>> listBindings(@PathVariable Long id) {
        return ApiResponse.ok(service.listBindings(id));
    }

    @PostMapping("/versions/{id}/bindings")
    @RequirePermission("process:sop:binding")
    public ApiResponse<SopBinding> createBinding(@PathVariable Long id, @Valid @RequestBody SopBindingRequest request) {
        return ApiResponse.ok(service.createBinding(id, request));
    }

    @DeleteMapping("/bindings/{id}")
    @RequirePermission("process:sop:binding")
    public ApiResponse<Void> deleteBinding(@PathVariable Long id) {
        service.deleteBinding(id);
        return ApiResponse.ok();
    }

    @PostMapping("/tasks/{taskId}/lock")
    @RequirePermission("production:sop:execute")
    public ApiResponse<SopTaskSnapshot> lockTaskSop(
            @PathVariable Long taskId,
            @RequestParam(required = false) Long operatorId
    ) {
        return ApiResponse.ok(service.lockTaskSop(taskId, operatorId));
    }

    @GetMapping("/tasks/{taskId}")
    @RequirePermission("production:sop:view")
    public ApiResponse<SopTaskPackage> openTaskSop(
            @PathVariable Long taskId,
            @RequestParam(required = false) Long operatorId
    ) {
        return ApiResponse.ok(service.openTaskSop(taskId, operatorId));
    }

    @PostMapping("/snapshots/{snapshotId}/steps/{stepId}/view")
    @RequirePermission("production:sop:execute")
    public ApiResponse<SopStepExecutionRecord> viewStep(
            @PathVariable Long snapshotId,
            @PathVariable Long stepId,
            @RequestParam(required = false) Long operatorId
    ) {
        return ApiResponse.ok(service.viewStep(snapshotId, stepId, operatorId));
    }

    @PostMapping("/snapshots/{snapshotId}/steps/{stepId}/confirm")
    @RequirePermission("production:sop:confirm")
    public ApiResponse<SopStepExecutionRecord> confirmStep(
            @PathVariable Long snapshotId,
            @PathVariable Long stepId,
            @Valid @RequestBody SopStepExecutionRequest request
    ) {
        return ApiResponse.ok(service.confirmStep(snapshotId, stepId, request));
    }

    @GetMapping("/tasks/{taskId}/report-validation")
    @RequirePermission("production:sop:execute")
    public ApiResponse<SopReportValidationResult> validateReport(@PathVariable Long taskId) {
        return ApiResponse.ok(service.validateReport(taskId));
    }

    @GetMapping("/executions")
    @RequirePermission("production:sop:executionList")
    public ApiResponse<List<SopExecutionRecord>> listExecutions(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long versionId
    ) {
        return ApiResponse.ok(service.listExecutions(taskId, versionId));
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        return resourceResponse(service.loadAttachmentFile(id));
    }

    @GetMapping("/model-files/{id}")
    public ResponseEntity<Resource> getModelFile(@PathVariable Long id) {
        return resourceResponse(service.loadModelFile(id));
    }

    private ResponseEntity<Resource> resourceResponse(SopService.StoredResource stored) {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (stored.contentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(stored.contentType());
            } catch (IllegalArgumentException ignored) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + stored.fileName() + "\"")
                .body(stored.resource());
    }
}
