package com.fanmes.quality.service;

import com.fanmes.common.audit.OperationLogService;
import com.fanmes.common.core.StatusTransitions;
import com.fanmes.common.exception.BadRequestException;
import com.fanmes.common.exception.NotFoundException;
import com.fanmes.common.id.IdGenerator;
import com.fanmes.quality.domain.QcDefectRecord;
import com.fanmes.quality.domain.QcInspectionOrder;
import com.fanmes.quality.domain.QcInspectionResult;
import com.fanmes.quality.domain.QcItem;
import com.fanmes.quality.domain.QcItemCategory;
import com.fanmes.quality.domain.QcPlan;
import com.fanmes.quality.domain.QcPlanItem;
import com.fanmes.quality.domain.QualityStatuses;
import com.fanmes.quality.dto.QcDefectRecordRequest;
import com.fanmes.quality.dto.QcInspectionOrderRequest;
import com.fanmes.quality.dto.QcInspectionResultRequest;
import com.fanmes.quality.dto.QcItemCategoryRequest;
import com.fanmes.quality.dto.QcItemRequest;
import com.fanmes.quality.dto.QcPlanItemRequest;
import com.fanmes.quality.dto.QcPlanRequest;
import com.fanmes.quality.dto.QualityActionRequest;
import com.fanmes.quality.infrastructure.QualityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class QualityService {
    private static final Map<String, Set<String>> CONFIG_TRANSITIONS = Map.of(
            QualityStatuses.ENABLED, Set.of(QualityStatuses.DRAFT, QualityStatuses.DISABLED),
            QualityStatuses.DISABLED, Set.of(QualityStatuses.ENABLED),
            QualityStatuses.VOIDED, Set.of(QualityStatuses.DRAFT, QualityStatuses.ENABLED, QualityStatuses.DISABLED)
    );
    private static final Map<String, Set<String>> INSPECTION_TRANSITIONS = Map.of(
            QualityStatuses.INSPECTING, Set.of(QualityStatuses.WAIT_INSPECTION),
            QualityStatuses.QUALIFIED, Set.of(QualityStatuses.WAIT_INSPECTION, QualityStatuses.INSPECTING),
            QualityStatuses.UNQUALIFIED, Set.of(QualityStatuses.WAIT_INSPECTION, QualityStatuses.INSPECTING),
            QualityStatuses.CLOSED, Set.of(QualityStatuses.QUALIFIED, QualityStatuses.UNQUALIFIED),
            QualityStatuses.VOIDED, Set.of(QualityStatuses.WAIT_INSPECTION, QualityStatuses.INSPECTING)
    );
    private static final Map<String, Set<String>> DEFECT_TRANSITIONS = Map.of(
            QualityStatuses.PROCESSING, Set.of(QualityStatuses.PENDING),
            QualityStatuses.COMPLETED, Set.of(QualityStatuses.PENDING, QualityStatuses.PROCESSING),
            QualityStatuses.CLOSED, Set.of(QualityStatuses.COMPLETED),
            QualityStatuses.VOIDED, Set.of(QualityStatuses.PENDING, QualityStatuses.PROCESSING)
    );
    private static final Set<String> RESULT_VALUES = Set.of(
            QualityStatuses.QUALIFIED,
            QualityStatuses.UNQUALIFIED,
            QualityStatuses.CONCESSION
    );

    private final QualityRepository repository;
    private final OperationLogService operationLogService;

    public QualityService(QualityRepository repository, OperationLogService operationLogService) {
        this.repository = repository;
        this.operationLogService = operationLogService;
    }

    public List<QcItemCategory> listItemCategories(String keyword, String status) {
        return repository.listItemCategories(keyword, status);
    }

    public QcItemCategory getItemCategory(Long id) {
        return repository.findItemCategoryById(id)
                .orElseThrow(() -> notFound("qc_item_category", id));
    }

    @Transactional
    public QcItemCategory createItemCategory(QcItemCategoryRequest request) {
        QcItemCategory item = new QcItemCategory(
                IdGenerator.nextId(),
                request.categoryCode().trim(),
                request.categoryName().trim(),
                defaultStatus(request.status(), QualityStatuses.DRAFT)
        );
        repository.insertItemCategory(item);
        return item;
    }

    @Transactional
    public QcItemCategory updateItemCategory(Long id, QcItemCategoryRequest request) {
        QcItemCategory existing = getItemCategory(id);
        ensureEditable(existing.status(), "qc_item_category");
        QcItemCategory item = new QcItemCategory(
                id,
                request.categoryCode().trim(),
                request.categoryName().trim(),
                defaultStatus(request.status(), existing.status())
        );
        repository.updateItemCategory(item);
        return item;
    }

    @Transactional
    public void deleteItemCategory(Long id) {
        QcItemCategory existing = getItemCategory(id);
        ensureEditable(existing.status(), "qc_item_category");
        if (repository.countItemsByCategory(id) > 0) {
            throw new BadRequestException("qc_item_category is referenced by qc_item: " + id);
        }
        repository.deleteById("qc_item_category", id);
    }

    @Transactional
    public QcItemCategory enableItemCategory(Long id, QualityActionRequest request) {
        return changeItemCategoryStatus(id, QualityStatuses.ENABLED, "enable", request);
    }

    @Transactional
    public QcItemCategory disableItemCategory(Long id, QualityActionRequest request) {
        return changeItemCategoryStatus(id, QualityStatuses.DISABLED, "disable", request);
    }

    @Transactional
    public QcItemCategory voidItemCategory(Long id, QualityActionRequest request) {
        return changeItemCategoryStatus(id, QualityStatuses.VOIDED, "void", request);
    }

    public List<QcItem> listItems(String keyword, Long categoryId, String status) {
        return repository.listItems(keyword, categoryId, status);
    }

    public QcItem getItem(Long id) {
        return repository.findItemById(id)
                .orElseThrow(() -> notFound("qc_item", id));
    }

    @Transactional
    public QcItem createItem(QcItemRequest request) {
        ensureCategoryExists(request.categoryId());
        QcItem item = toItem(IdGenerator.nextId(), request, null);
        repository.insertItem(item);
        return item;
    }

    @Transactional
    public QcItem updateItem(Long id, QcItemRequest request) {
        QcItem existing = getItem(id);
        ensureEditable(existing.status(), "qc_item");
        ensureCategoryExists(request.categoryId());
        QcItem item = toItem(id, request, existing.status());
        repository.updateItem(item);
        return item;
    }

    @Transactional
    public void deleteItem(Long id) {
        QcItem existing = getItem(id);
        ensureEditable(existing.status(), "qc_item");
        if (repository.countPlanItemsByItem(id) > 0 || repository.countResultsByItem(id) > 0) {
            throw new BadRequestException("qc_item is referenced by plan items or inspection results: " + id);
        }
        repository.deleteById("qc_item", id);
    }

    @Transactional
    public QcItem enableItem(Long id, QualityActionRequest request) {
        return changeItemStatus(id, QualityStatuses.ENABLED, "enable", request);
    }

    @Transactional
    public QcItem disableItem(Long id, QualityActionRequest request) {
        return changeItemStatus(id, QualityStatuses.DISABLED, "disable", request);
    }

    @Transactional
    public QcItem voidItem(Long id, QualityActionRequest request) {
        return changeItemStatus(id, QualityStatuses.VOIDED, "void", request);
    }

    public List<QcPlan> listPlans(String keyword, Long productId, Long customerId, String status) {
        return repository.listPlans(keyword, productId, customerId, status);
    }

    public QcPlan getPlan(Long id) {
        return repository.findPlanById(id)
                .orElseThrow(() -> notFound("qc_plan", id));
    }

    @Transactional
    public QcPlan createPlan(QcPlanRequest request) {
        QcPlan plan = toPlan(IdGenerator.nextId(), request, null);
        repository.insertPlan(plan);
        return plan;
    }

    @Transactional
    public QcPlan updatePlan(Long id, QcPlanRequest request) {
        QcPlan existing = getPlan(id);
        ensureEditable(existing.status(), "qc_plan");
        QcPlan plan = toPlan(id, request, existing.status());
        repository.updatePlan(plan);
        return plan;
    }

    @Transactional
    public void deletePlan(Long id) {
        QcPlan existing = getPlan(id);
        ensureEditable(existing.status(), "qc_plan");
        if (repository.countPlanItemsByPlan(id) > 0 || repository.countInspectionOrdersByPlan(id) > 0) {
            throw new BadRequestException("qc_plan is referenced by plan items or inspection orders: " + id);
        }
        repository.deleteById("qc_plan", id);
    }

    @Transactional
    public QcPlan enablePlan(Long id, QualityActionRequest request) {
        return changePlanStatus(id, QualityStatuses.ENABLED, "enable", request);
    }

    @Transactional
    public QcPlan disablePlan(Long id, QualityActionRequest request) {
        return changePlanStatus(id, QualityStatuses.DISABLED, "disable", request);
    }

    @Transactional
    public QcPlan voidPlan(Long id, QualityActionRequest request) {
        return changePlanStatus(id, QualityStatuses.VOIDED, "void", request);
    }

    public List<QcPlanItem> listPlanItems(Long planId, Long qcItemId) {
        return repository.listPlanItems(planId, qcItemId);
    }

    public QcPlanItem getPlanItem(Long id) {
        return repository.findPlanItemById(id)
                .orElseThrow(() -> notFound("qc_plan_item", id));
    }

    @Transactional
    public QcPlanItem createPlanItem(QcPlanItemRequest request) {
        ensurePlanUsable(request.planId());
        ensureItemUsable(request.qcItemId());
        QcPlanItem item = toPlanItem(IdGenerator.nextId(), request);
        repository.insertPlanItem(item);
        return item;
    }

    @Transactional
    public QcPlanItem updatePlanItem(Long id, QcPlanItemRequest request) {
        getPlanItem(id);
        ensurePlanUsable(request.planId());
        ensureItemUsable(request.qcItemId());
        QcPlanItem item = toPlanItem(id, request);
        repository.updatePlanItem(item);
        return item;
    }

    @Transactional
    public void deletePlanItem(Long id) {
        getPlanItem(id);
        repository.deleteById("qc_plan_item", id);
    }

    public List<QcInspectionOrder> listInspectionOrders(
            String keyword,
            String inspectionType,
            String status,
            Long planId,
            Long taskId,
            Long operationTaskId,
            Long barcodeId
    ) {
        return repository.listInspectionOrders(keyword, inspectionType, status, planId, taskId, operationTaskId, barcodeId);
    }

    public QcInspectionOrder getInspectionOrder(Long id) {
        return repository.findInspectionOrderById(id)
                .orElseThrow(() -> notFound("qc_inspection_order", id));
    }

    @Transactional
    public QcInspectionOrder createInspectionOrder(QcInspectionOrderRequest request) {
        ensurePlanUsable(request.planId());
        QcInspectionOrder order = toInspectionOrder(IdGenerator.nextId(), request, null);
        repository.insertInspectionOrder(order);
        operationLogService.recordStatusChange(
                "quality",
                "qc_inspection_order",
                order.id(),
                "create",
                null,
                order.status(),
                request.inspectorId(),
                "create inspection order"
        );
        return order;
    }

    @Transactional
    public QcInspectionOrder updateInspectionOrder(Long id, QcInspectionOrderRequest request) {
        QcInspectionOrder existing = getInspectionOrder(id);
        ensureEditable(existing.status(), "qc_inspection_order");
        ensurePlanUsable(request.planId());
        QcInspectionOrder order = toInspectionOrder(id, request, existing.status());
        repository.updateInspectionOrder(order);
        return order;
    }

    @Transactional
    public void deleteInspectionOrder(Long id) {
        QcInspectionOrder existing = getInspectionOrder(id);
        ensureEditable(existing.status(), "qc_inspection_order");
        if (repository.countResultsByInspection(id) > 0 || repository.countDefectsByInspection(id) > 0) {
            throw new BadRequestException("qc_inspection_order is referenced by results or defect records: " + id);
        }
        repository.deleteById("qc_inspection_order", id);
    }

    @Transactional
    public QcInspectionOrder startInspection(Long id, QualityActionRequest request) {
        return changeInspectionStatus(id, QualityStatuses.INSPECTING, null, "start", request);
    }

    @Transactional
    public QcInspectionOrder passInspection(Long id, QualityActionRequest request) {
        return changeInspectionStatus(id, QualityStatuses.QUALIFIED, QualityStatuses.QUALIFIED, "pass", request);
    }

    @Transactional
    public QcInspectionOrder failInspection(Long id, QualityActionRequest request) {
        return changeInspectionStatus(id, QualityStatuses.UNQUALIFIED, QualityStatuses.UNQUALIFIED, "fail", request);
    }

    @Transactional
    public QcInspectionOrder closeInspection(Long id, QualityActionRequest request) {
        QcInspectionOrder existing = getInspectionOrder(id);
        if (!Set.of(QualityStatuses.QUALIFIED, QualityStatuses.UNQUALIFIED).contains(existing.status())) {
            throw new BadRequestException("qc_inspection_order cannot close from " + existing.status());
        }
        return changeInspectionStatus(id, QualityStatuses.CLOSED, existing.finalResult(), "close", request);
    }

    @Transactional
    public QcInspectionOrder voidInspection(Long id, QualityActionRequest request) {
        return changeInspectionStatus(id, QualityStatuses.VOIDED, null, "void", request);
    }

    public List<QcInspectionResult> listInspectionResults(Long inspectionId, Long qcItemId, String result) {
        return repository.listInspectionResults(inspectionId, qcItemId, result);
    }

    public QcInspectionResult getInspectionResult(Long id) {
        return repository.findInspectionResultById(id)
                .orElseThrow(() -> notFound("qc_inspection_result", id));
    }

    @Transactional
    public QcInspectionResult createInspectionResult(QcInspectionResultRequest request) {
        QcInspectionOrder order = ensureInspectionOrderOpen(request.inspectionId());
        ensureItemUsable(request.qcItemId());
        validateResult(request.result(), request.defectReasonId());
        QcInspectionResult result = toInspectionResult(IdGenerator.nextId(), request);
        repository.insertInspectionResult(result);
        refreshInspectionOutcome(order.id());
        return result;
    }

    @Transactional
    public QcInspectionResult updateInspectionResult(Long id, QcInspectionResultRequest request) {
        getInspectionResult(id);
        QcInspectionOrder order = ensureInspectionOrderOpen(request.inspectionId());
        ensureItemUsable(request.qcItemId());
        validateResult(request.result(), request.defectReasonId());
        QcInspectionResult result = toInspectionResult(id, request);
        repository.updateInspectionResult(result);
        refreshInspectionOutcome(order.id());
        return result;
    }

    @Transactional
    public void deleteInspectionResult(Long id) {
        QcInspectionResult existing = getInspectionResult(id);
        QcInspectionOrder order = ensureInspectionOrderOpen(existing.inspectionId());
        repository.deleteById("qc_inspection_result", id);
        refreshInspectionOutcome(order.id());
    }

    public List<QcDefectRecord> listDefectRecords(
            String sourceType,
            Long sourceId,
            Long productId,
            Long barcodeId,
            Long processId,
            String status
    ) {
        return repository.listDefectRecords(sourceType, sourceId, productId, barcodeId, processId, status);
    }

    public QcDefectRecord getDefectRecord(Long id) {
        return repository.findDefectRecordById(id)
                .orElseThrow(() -> notFound("qc_defect_record", id));
    }

    @Transactional
    public QcDefectRecord createDefectRecord(QcDefectRecordRequest request) {
        validateDefect(request.defectQty());
        QcDefectRecord record = toDefectRecord(IdGenerator.nextId(), request, null);
        repository.insertDefectRecord(record);
        operationLogService.recordStatusChange(
                "quality",
                "qc_defect_record",
                record.id(),
                "create",
                null,
                record.status(),
                null,
                "create defect record"
        );
        return record;
    }

    @Transactional
    public QcDefectRecord updateDefectRecord(Long id, QcDefectRecordRequest request) {
        QcDefectRecord existing = getDefectRecord(id);
        ensureEditable(existing.status(), "qc_defect_record");
        validateDefect(request.defectQty());
        QcDefectRecord record = toDefectRecord(id, request, existing.status());
        repository.updateDefectRecord(record);
        return record;
    }

    @Transactional
    public void deleteDefectRecord(Long id) {
        QcDefectRecord existing = getDefectRecord(id);
        ensureEditable(existing.status(), "qc_defect_record");
        repository.deleteById("qc_defect_record", id);
    }

    @Transactional
    public QcDefectRecord startDefect(Long id, QualityActionRequest request) {
        return changeDefectStatus(id, QualityStatuses.PROCESSING, "start", request);
    }

    @Transactional
    public QcDefectRecord completeDefect(Long id, QualityActionRequest request) {
        return changeDefectStatus(id, QualityStatuses.COMPLETED, "complete", request);
    }

    @Transactional
    public QcDefectRecord closeDefect(Long id, QualityActionRequest request) {
        return changeDefectStatus(id, QualityStatuses.CLOSED, "close", request);
    }

    @Transactional
    public QcDefectRecord voidDefect(Long id, QualityActionRequest request) {
        return changeDefectStatus(id, QualityStatuses.VOIDED, "void", request);
    }

    private QcItem toItem(Long id, QcItemRequest request, String existingStatus) {
        return new QcItem(
                id,
                request.categoryId(),
                request.itemCode().trim(),
                request.itemName().trim(),
                trimToNull(request.valueType()),
                trimToNull(request.standardValue()),
                request.upperLimit(),
                request.lowerLimit(),
                request.unitId(),
                defaultStatus(request.status(), existingStatus == null ? QualityStatuses.DRAFT : existingStatus)
        );
    }

    private QcPlan toPlan(Long id, QcPlanRequest request, String existingStatus) {
        return new QcPlan(
                id,
                request.planCode().trim(),
                request.planName().trim(),
                request.productId(),
                request.customerId(),
                request.defaultFlag(),
                defaultStatus(request.status(), existingStatus == null ? QualityStatuses.DRAFT : existingStatus)
        );
    }

    private QcPlanItem toPlanItem(Long id, QcPlanItemRequest request) {
        return new QcPlanItem(
                id,
                request.planId(),
                request.qcItemId(),
                defaultQty(request.sampleQty()),
                trimToNull(request.standardValue()),
                request.upperLimit(),
                request.lowerLimit(),
                request.requiredFlag()
        );
    }

    private QcInspectionOrder toInspectionOrder(Long id, QcInspectionOrderRequest request, String existingStatus) {
        return new QcInspectionOrder(
                id,
                request.inspectionNo().trim(),
                trimToNull(request.inspectionType()),
                request.planId(),
                request.workOrderId(),
                null,
                request.taskId(),
                request.operationTaskId(),
                request.productId(),
                null,
                request.barcodeId(),
                request.inspectorId(),
                request.inspectionAt() == null ? LocalDateTime.now() : request.inspectionAt(),
                trimToNull(request.finalResult()),
                defaultStatus(request.status(), existingStatus == null ? QualityStatuses.WAIT_INSPECTION : existingStatus)
        );
    }

    private QcInspectionResult toInspectionResult(Long id, QcInspectionResultRequest request) {
        return new QcInspectionResult(
                id,
                request.inspectionId(),
                request.qcItemId(),
                trimToNull(request.measuredValue()),
                request.result().trim(),
                request.defectReasonId(),
                trimToNull(request.remark())
        );
    }

    private QcDefectRecord toDefectRecord(Long id, QcDefectRecordRequest request, String existingStatus) {
        return new QcDefectRecord(
                id,
                trimToNull(request.sourceType()),
                request.sourceId(),
                request.productId(),
                request.barcodeId(),
                request.processId(),
                request.defectReasonId(),
                null,
                defaultQty(request.defectQty()),
                trimToNull(request.handleMethod()),
                request.reworkOrderId(),
                defaultStatus(request.status(), existingStatus == null ? QualityStatuses.PENDING : existingStatus)
        );
    }

    private QcItemCategory changeItemCategoryStatus(
            Long id,
            String targetStatus,
            String actionName,
            QualityActionRequest request
    ) {
        QcItemCategory existing = getItemCategory(id);
        ensureTransition(existing.status(), targetStatus, CONFIG_TRANSITIONS, "qc_item_category");
        ensureUpdated(repository.updateItemCategoryStatus(id, existing.status(), targetStatus), "qc_item_category", id);
        recordStatus("qc_item_category", id, actionName, existing.status(), targetStatus, request);
        return new QcItemCategory(existing.id(), existing.categoryCode(), existing.categoryName(), targetStatus);
    }

    private QcItem changeItemStatus(Long id, String targetStatus, String actionName, QualityActionRequest request) {
        QcItem existing = getItem(id);
        ensureTransition(existing.status(), targetStatus, CONFIG_TRANSITIONS, "qc_item");
        ensureUpdated(repository.updateItemStatus(id, existing.status(), targetStatus), "qc_item", id);
        recordStatus("qc_item", id, actionName, existing.status(), targetStatus, request);
        return new QcItem(
                existing.id(),
                existing.categoryId(),
                existing.itemCode(),
                existing.itemName(),
                existing.valueType(),
                existing.standardValue(),
                existing.upperLimit(),
                existing.lowerLimit(),
                existing.unitId(),
                targetStatus
        );
    }

    private QcPlan changePlanStatus(Long id, String targetStatus, String actionName, QualityActionRequest request) {
        QcPlan existing = getPlan(id);
        ensureTransition(existing.status(), targetStatus, CONFIG_TRANSITIONS, "qc_plan");
        ensureUpdated(repository.updatePlanStatus(id, existing.status(), targetStatus), "qc_plan", id);
        recordStatus("qc_plan", id, actionName, existing.status(), targetStatus, request);
        return new QcPlan(
                existing.id(),
                existing.planCode(),
                existing.planName(),
                existing.productId(),
                existing.customerId(),
                existing.defaultFlag(),
                targetStatus
        );
    }

    private QcInspectionOrder changeInspectionStatus(
            Long id,
            String targetStatus,
            String finalResult,
            String actionName,
            QualityActionRequest request
    ) {
        QcInspectionOrder existing = getInspectionOrder(id);
        ensureTransition(existing.status(), targetStatus, INSPECTION_TRANSITIONS, "qc_inspection_order");
        String targetResult = finalResult == null ? existing.finalResult() : finalResult;
        ensureUpdated(repository.updateInspectionOrderStatus(id, existing.status(), targetStatus, targetResult), "qc_inspection_order", id);
        recordStatus("qc_inspection_order", id, actionName, existing.status(), targetStatus, request);
        return new QcInspectionOrder(
                existing.id(),
                existing.inspectionNo(),
                existing.inspectionType(),
                existing.planId(),
                existing.workOrderId(),
                existing.workOrderNo(),
                existing.taskId(),
                existing.operationTaskId(),
                existing.productId(),
                existing.productName(),
                existing.barcodeId(),
                existing.inspectorId(),
                existing.inspectionAt(),
                targetResult,
                targetStatus
        );
    }

    private QcDefectRecord changeDefectStatus(Long id, String targetStatus, String actionName, QualityActionRequest request) {
        QcDefectRecord existing = getDefectRecord(id);
        ensureTransition(existing.status(), targetStatus, DEFECT_TRANSITIONS, "qc_defect_record");
        ensureUpdated(repository.updateDefectStatus(id, existing.status(), targetStatus), "qc_defect_record", id);
        recordStatus("qc_defect_record", id, actionName, existing.status(), targetStatus, request);
        return new QcDefectRecord(
                existing.id(),
                existing.sourceType(),
                existing.sourceId(),
                existing.productId(),
                existing.barcodeId(),
                existing.processId(),
                existing.defectReasonId(),
                existing.defectReasonName(),
                existing.defectQty(),
                existing.handleMethod(),
                existing.reworkOrderId(),
                targetStatus
        );
    }

    private void refreshInspectionOutcome(Long inspectionId) {
        QcInspectionOrder existing = getInspectionOrder(inspectionId);
        if (QualityStatuses.isTerminal(existing.status())) {
            return;
        }
        List<QcInspectionResult> results = repository.listInspectionResults(inspectionId, null, null);
        if (results.isEmpty()) {
            return;
        }
        String finalResult = QualityStatuses.QUALIFIED;
        for (QcInspectionResult result : results) {
            if (QualityStatuses.UNQUALIFIED.equals(result.result())) {
                finalResult = QualityStatuses.UNQUALIFIED;
                break;
            }
            if (QualityStatuses.CONCESSION.equals(result.result())) {
                finalResult = QualityStatuses.CONCESSION;
            }
        }
        String targetStatus = QualityStatuses.UNQUALIFIED.equals(finalResult)
                ? QualityStatuses.UNQUALIFIED
                : QualityStatuses.QUALIFIED;
        if (existing.status().equals(targetStatus) && finalResult.equals(existing.finalResult())) {
            return;
        }
        if (!Set.of(QualityStatuses.WAIT_INSPECTION, QualityStatuses.INSPECTING, QualityStatuses.QUALIFIED, QualityStatuses.UNQUALIFIED)
                .contains(existing.status())) {
            throw new BadRequestException("qc_inspection_order cannot refresh outcome from " + existing.status());
        }
        repository.updateInspectionOrderStatus(inspectionId, existing.status(), targetStatus, finalResult);
        operationLogService.recordStatusChange(
                "quality",
                "qc_inspection_order",
                inspectionId,
                "refresh-outcome",
                existing.status(),
                targetStatus,
                null,
                "inspection result changed"
        );
    }

    private QcInspectionOrder ensureInspectionOrderOpen(Long inspectionId) {
        if (inspectionId == null) {
            throw new BadRequestException("inspectionId is required");
        }
        QcInspectionOrder order = getInspectionOrder(inspectionId);
        ensureEditable(order.status(), "qc_inspection_order");
        return order;
    }

    private void ensureCategoryExists(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        repository.findItemCategoryById(categoryId)
                .orElseThrow(() -> new BadRequestException("qc_item_category not found: " + categoryId));
    }

    private void ensureItemUsable(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("qcItemId is required");
        }
        QcItem item = repository.findItemById(itemId)
                .orElseThrow(() -> new BadRequestException("qc_item not found: " + itemId));
        if (QualityStatuses.isTerminal(item.status()) || QualityStatuses.DISABLED.equals(item.status())) {
            throw new BadRequestException("qc_item is disabled, closed, or voided: " + itemId);
        }
    }

    private void ensurePlanUsable(Long planId) {
        if (planId == null) {
            return;
        }
        QcPlan plan = repository.findPlanById(planId)
                .orElseThrow(() -> new BadRequestException("qc_plan not found: " + planId));
        if (QualityStatuses.isTerminal(plan.status()) || QualityStatuses.DISABLED.equals(plan.status())) {
            throw new BadRequestException("qc_plan is disabled, closed, or voided: " + planId);
        }
    }

    private void validateResult(String result, Long defectReasonId) {
        if (!StringUtils.hasText(result)) {
            throw new BadRequestException("inspection result is required");
        }
        String normalized = result.trim();
        if (!RESULT_VALUES.contains(normalized)) {
            throw new BadRequestException("inspection result must be 合格/不合格/让步接收");
        }
        if (QualityStatuses.UNQUALIFIED.equals(normalized) && defectReasonId == null) {
            throw new BadRequestException("defectReasonId is required when result is 不合格");
        }
    }

    private void validateDefect(BigDecimal defectQty) {
        if (defectQty != null && defectQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("defectQty must be greater than or equal to 0");
        }
    }

    private void ensureEditable(String status, String tableName) {
        StatusTransitions.ensureEditable(status, tableName);
    }

    private void ensureTransition(
            String currentStatus,
            String targetStatus,
            Map<String, Set<String>> transitions,
            String tableName
    ) {
        StatusTransitions.ensureTransition(currentStatus, targetStatus, transitions, tableName);
    }

    private void recordStatus(
            String tableName,
            Long id,
            String actionName,
            String oldStatus,
            String newStatus,
            QualityActionRequest request
    ) {
        operationLogService.recordStatusChange(
                "quality",
                tableName,
                id,
                actionName,
                oldStatus,
                newStatus,
                request == null ? null : request.operatorId(),
                request == null ? null : request.remark()
        );
    }

    private void ensureUpdated(int updated, String tableName, Long id) {
        if (updated == 0) {
            throw new BadRequestException(tableName + " status changed, refresh and retry: " + id);
        }
    }

    private BigDecimal defaultQty(BigDecimal qty) {
        return qty == null ? BigDecimal.ZERO : qty;
    }

    private String defaultStatus(String status, String defaultStatus) {
        return StringUtils.hasText(status) ? status.trim() : defaultStatus;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private NotFoundException notFound(String tableName, Long id) {
        return new NotFoundException(tableName + " not found: " + id);
    }
}
