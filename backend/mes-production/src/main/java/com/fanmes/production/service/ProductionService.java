package com.fanmes.production.service;

import com.fanmes.common.audit.OperationLogService;
import com.fanmes.common.core.StatusTransitions;
import com.fanmes.common.exception.BadRequestException;
import com.fanmes.common.exception.NotFoundException;
import com.fanmes.common.id.IdGenerator;
import com.fanmes.common.idempotency.IdempotencyService;
import com.fanmes.production.domain.DispatchOrder;
import com.fanmes.production.domain.KittingAnalysis;
import com.fanmes.production.domain.KittingMissingItem;
import com.fanmes.production.domain.ProductionStatuses;
import com.fanmes.production.domain.ShopTask;
import com.fanmes.production.domain.WorkOrder;
import com.fanmes.production.dto.CompleteWorkOrderRequest;
import com.fanmes.production.dto.CreateTaskRequest;
import com.fanmes.production.dto.DispatchOrderRequest;
import com.fanmes.production.dto.KittingAnalysisRequest;
import com.fanmes.production.dto.KittingMissingItemRequest;
import com.fanmes.production.dto.ProductionActionRequest;
import com.fanmes.production.dto.WorkOrderRequest;
import com.fanmes.production.infrastructure.ProductionRepository;
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
public class ProductionService {
    private static final Map<String, Set<String>> WORK_ORDER_TRANSITIONS = Map.of(
            ProductionStatuses.WAIT_ISSUE, Set.of(ProductionStatuses.DRAFT),
            ProductionStatuses.ISSUED, Set.of(ProductionStatuses.WAIT_ISSUE),
            ProductionStatuses.RUNNING, Set.of(ProductionStatuses.ISSUED, ProductionStatuses.PAUSED),
            ProductionStatuses.PAUSED, Set.of(ProductionStatuses.RUNNING),
            ProductionStatuses.COMPLETED, Set.of(ProductionStatuses.RUNNING, ProductionStatuses.ISSUED, ProductionStatuses.PAUSED),
            ProductionStatuses.CLOSED, Set.of(ProductionStatuses.COMPLETED),
            ProductionStatuses.VOIDED, Set.of(ProductionStatuses.DRAFT, ProductionStatuses.WAIT_ISSUE)
    );
    private static final Map<String, Set<String>> KITTING_TRANSITIONS = Map.of(
            ProductionStatuses.COMPLETED, Set.of(ProductionStatuses.DRAFT),
            ProductionStatuses.CLOSED, Set.of(ProductionStatuses.COMPLETED),
            ProductionStatuses.VOIDED, Set.of(ProductionStatuses.DRAFT)
    );
    private static final Map<String, Set<String>> DISPATCH_TRANSITIONS = Map.of(
            ProductionStatuses.WAIT_ISSUE, Set.of(ProductionStatuses.DRAFT),
            ProductionStatuses.ISSUED, Set.of(ProductionStatuses.WAIT_ISSUE),
            ProductionStatuses.RUNNING, Set.of(ProductionStatuses.ISSUED),
            ProductionStatuses.COMPLETED, Set.of(ProductionStatuses.RUNNING, ProductionStatuses.ISSUED),
            ProductionStatuses.CLOSED, Set.of(ProductionStatuses.COMPLETED),
            ProductionStatuses.VOIDED, Set.of(ProductionStatuses.DRAFT, ProductionStatuses.WAIT_ISSUE)
    );
    private static final Map<String, Set<String>> SHOP_TASK_TRANSITIONS = Map.of(
            ProductionStatuses.RUNNING, Set.of(ProductionStatuses.ISSUED, ProductionStatuses.PAUSED),
            ProductionStatuses.PAUSED, Set.of(ProductionStatuses.RUNNING),
            ProductionStatuses.COMPLETED, Set.of(ProductionStatuses.RUNNING, ProductionStatuses.ISSUED, ProductionStatuses.PAUSED)
    );

    private final ProductionRepository repository;
    private final OperationLogService operationLogService;
    private final IdempotencyService idempotencyService;

    public ProductionService(
            ProductionRepository repository,
            OperationLogService operationLogService,
            IdempotencyService idempotencyService
    ) {
        this.repository = repository;
        this.operationLogService = operationLogService;
        this.idempotencyService = idempotencyService;
    }

    public List<WorkOrder> listWorkOrders(
            String keyword,
            String status,
            Long productId,
            Long lineId,
            String sourceType,
            String externalNo
    ) {
        return repository.listWorkOrders(keyword, status, productId, lineId, sourceType, externalNo);
    }

    public WorkOrder getWorkOrder(Long id) {
        return repository.findWorkOrderById(id)
                .orElseThrow(() -> notFound("prod_work_order", id));
    }

    @Transactional
    public WorkOrder createWorkOrder(WorkOrderRequest request) {
        if (StringUtils.hasText(request.idempotencyKey())) {
            return idempotentCreateWorkOrder(request);
        }
        if (StringUtils.hasText(request.externalNo())) {
            return repository.findWorkOrderByExternalNo(request.externalNo())
                    .orElseGet(() -> insertWorkOrder(request));
        }
        return insertWorkOrder(request);
    }

    @Transactional
    public WorkOrder updateWorkOrder(Long id, WorkOrderRequest request) {
        WorkOrder existing = getWorkOrder(id);
        ensureEditable(existing.status(), "prod_work_order");
        WorkOrder order = toWorkOrder(id, request);
        repository.updateWorkOrder(order);
        return order;
    }

    @Transactional
    public void deleteWorkOrder(Long id) {
        WorkOrder existing = getWorkOrder(id);
        ensureEditable(existing.status(), "prod_work_order");
        if (repository.countKittingByWorkOrder(id) > 0 || repository.countDispatchByWorkOrder(id) > 0) {
            throw new BadRequestException("prod_work_order is referenced by kitting analysis or dispatch order: " + id);
        }
        repository.deleteById("prod_work_order", id);
    }

    @Transactional
    public WorkOrder issueWorkOrder(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.WAIT_ISSUE, "issue", request);
    }

    @Transactional
    public WorkOrder confirmWorkOrderIssue(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.ISSUED, "confirm-issue", request);
    }

    @Transactional
    public WorkOrder startWorkOrder(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.RUNNING, "start", request);
    }

    @Transactional
    public WorkOrder pauseWorkOrder(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.PAUSED, "pause", request);
    }

    @Transactional
    public WorkOrder closeWorkOrder(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.CLOSED, "close", request);
    }

    @Transactional
    public WorkOrder voidWorkOrder(Long id, ProductionActionRequest request) {
        return changeWorkOrderStatus(id, ProductionStatuses.VOIDED, "void", request);
    }

    @Transactional
    public WorkOrder completeWorkOrder(Long id, CompleteWorkOrderRequest request) {
        WorkOrder existing = getWorkOrder(id);
        ensureTransition(existing.status(), ProductionStatuses.COMPLETED, WORK_ORDER_TRANSITIONS, "prod_work_order");
        if (request.completedQty().compareTo(existing.planQty()) > 0) {
            throw new BadRequestException("completedQty must not exceed planQty");
        }
        WorkOrder completed = new WorkOrder(
                existing.id(),
                existing.workOrderNo(),
                existing.externalNo(),
                existing.sourceType(),
                existing.productId(),
                existing.planQty(),
                request.completedQty(),
                defaultQty(request.qualifiedQty()),
                defaultQty(request.defectiveQty()),
                existing.unitId(),
                existing.plannedStartAt(),
                existing.plannedEndAt(),
                existing.deliveryDate(),
                existing.lineId(),
                existing.routeId(),
                ProductionStatuses.COMPLETED
        );
        ensureUpdated(repository.completeWorkOrder(id, existing.status(), completed), "prod_work_order", id);
        operationLogService.recordStatusChange(
                "production",
                "prod_work_order",
                id,
                "complete",
                existing.status(),
                ProductionStatuses.COMPLETED,
                request.operatorId(),
                request.remark()
        );
        return completed;
    }

    public List<KittingAnalysis> listKittingAnalyses(String keyword, String status, Long workOrderId, Long taskId) {
        return repository.listKittingAnalyses(keyword, status, workOrderId, taskId);
    }

    public KittingAnalysis getKittingAnalysis(Long id) {
        return repository.findKittingAnalysisById(id)
                .orElseThrow(() -> notFound("prod_kitting_analysis", id));
    }

    @Transactional
    public KittingAnalysis createKittingAnalysis(KittingAnalysisRequest request) {
        ensureWorkOrderExists(request.workOrderId());
        KittingAnalysis analysis = toKittingAnalysis(IdGenerator.nextId(), request);
        repository.insertKittingAnalysis(analysis);
        return analysis;
    }

    @Transactional
    public KittingAnalysis updateKittingAnalysis(Long id, KittingAnalysisRequest request) {
        KittingAnalysis existing = getKittingAnalysis(id);
        ensureEditable(existing.status(), "prod_kitting_analysis");
        ensureWorkOrderExists(request.workOrderId());
        KittingAnalysis analysis = toKittingAnalysis(id, request);
        repository.updateKittingAnalysis(analysis);
        return analysis;
    }

    @Transactional
    public void deleteKittingAnalysis(Long id) {
        KittingAnalysis existing = getKittingAnalysis(id);
        ensureEditable(existing.status(), "prod_kitting_analysis");
        if (repository.countMissingByAnalysis(id) > 0) {
            throw new BadRequestException("prod_kitting_analysis is referenced by missing items: " + id);
        }
        repository.deleteById("prod_kitting_analysis", id);
    }

    @Transactional
    public KittingAnalysis completeKittingAnalysis(Long id, ProductionActionRequest request) {
        refreshKittingSummary(id);
        return changeKittingStatus(id, ProductionStatuses.COMPLETED, "complete", request);
    }

    @Transactional
    public KittingAnalysis closeKittingAnalysis(Long id, ProductionActionRequest request) {
        return changeKittingStatus(id, ProductionStatuses.CLOSED, "close", request);
    }

    @Transactional
    public KittingAnalysis voidKittingAnalysis(Long id, ProductionActionRequest request) {
        return changeKittingStatus(id, ProductionStatuses.VOIDED, "void", request);
    }

    public List<KittingMissingItem> listMissingItems(Long analysisId, Long materialId) {
        return repository.listMissingItems(analysisId, materialId);
    }

    public List<KittingMissingItem> listMissingBoard() {
        return repository.listMissingBoard();
    }

    public KittingMissingItem getMissingItem(Long id) {
        return repository.findMissingItemById(id)
                .orElseThrow(() -> notFound("prod_kitting_missing_item", id));
    }

    @Transactional
    public KittingMissingItem createMissingItem(Long analysisId, KittingMissingItemRequest request) {
        KittingAnalysis analysis = getKittingAnalysis(analysisId);
        ensureEditable(analysis.status(), "prod_kitting_analysis");
        KittingMissingItem item = toMissingItem(IdGenerator.nextId(), analysisId, request);
        repository.insertMissingItem(item);
        refreshKittingSummary(analysisId);
        return item;
    }

    @Transactional
    public KittingMissingItem updateMissingItem(Long id, KittingMissingItemRequest request) {
        KittingMissingItem existing = getMissingItem(id);
        KittingAnalysis analysis = getKittingAnalysis(existing.analysisId());
        ensureEditable(analysis.status(), "prod_kitting_analysis");
        KittingMissingItem item = toMissingItem(id, existing.analysisId(), request);
        repository.updateMissingItem(item);
        refreshKittingSummary(existing.analysisId());
        return item;
    }

    @Transactional
    public void deleteMissingItem(Long id) {
        KittingMissingItem existing = getMissingItem(id);
        KittingAnalysis analysis = getKittingAnalysis(existing.analysisId());
        ensureEditable(analysis.status(), "prod_kitting_analysis");
        repository.deleteById("prod_kitting_missing_item", id);
        refreshKittingSummary(existing.analysisId());
    }

    public List<DispatchOrder> listDispatchOrders(
            String keyword,
            String status,
            Long workOrderId,
            Long lineId,
            Long teamId
    ) {
        return repository.listDispatchOrders(keyword, status, workOrderId, lineId, teamId);
    }

    public List<ShopTask> listShopTasks(
            String keyword,
            String status,
            Long workOrderId,
            Long dispatchId,
            Long lineId,
            Long teamId
    ) {
        return repository.listShopTasks(keyword, status, workOrderId, dispatchId, lineId, teamId);
    }

    public DispatchOrder getDispatchOrder(Long id) {
        return repository.findDispatchOrderById(id)
                .orElseThrow(() -> notFound("prod_dispatch_order", id));
    }

    @Transactional
    public DispatchOrder createDispatchOrder(DispatchOrderRequest request) {
        ensureWorkOrderExists(request.workOrderId());
        DispatchOrder dispatch = toDispatchOrder(IdGenerator.nextId(), request);
        repository.insertDispatchOrder(dispatch);
        return dispatch;
    }

    @Transactional
    public DispatchOrder updateDispatchOrder(Long id, DispatchOrderRequest request) {
        DispatchOrder existing = getDispatchOrder(id);
        ensureEditable(existing.status(), "prod_dispatch_order");
        ensureWorkOrderExists(request.workOrderId());
        DispatchOrder dispatch = toDispatchOrder(id, request);
        repository.updateDispatchOrder(dispatch);
        return dispatch;
    }

    @Transactional
    public void deleteDispatchOrder(Long id) {
        DispatchOrder existing = getDispatchOrder(id);
        ensureEditable(existing.status(), "prod_dispatch_order");
        if (repository.countTaskByDispatch(id) > 0) {
            throw new BadRequestException("prod_dispatch_order is referenced by shop_task: " + id);
        }
        repository.deleteById("prod_dispatch_order", id);
    }

    @Transactional
    public DispatchOrder issueDispatchOrder(Long id, ProductionActionRequest request) {
        return changeDispatchStatus(id, ProductionStatuses.WAIT_ISSUE, "issue", request);
    }

    @Transactional
    public DispatchOrder confirmDispatchIssue(Long id, ProductionActionRequest request) {
        DispatchOrder dispatch = changeDispatchStatus(id, ProductionStatuses.ISSUED, "confirm-issue", request);
        repository.updateTaskStatusByDispatch(id, ProductionStatuses.ISSUED);
        return dispatch;
    }

    @Transactional
    public DispatchOrder startDispatchOrder(Long id, ProductionActionRequest request) {
        DispatchOrder dispatch = changeDispatchStatus(id, ProductionStatuses.RUNNING, "start", request);
        repository.updateTaskStatusByDispatch(id, ProductionStatuses.RUNNING);
        return dispatch;
    }

    @Transactional
    public DispatchOrder completeDispatchOrder(Long id, ProductionActionRequest request) {
        DispatchOrder dispatch = changeDispatchStatus(id, ProductionStatuses.COMPLETED, "complete", request);
        repository.updateTaskStatusByDispatch(id, ProductionStatuses.COMPLETED);
        return dispatch;
    }

    @Transactional
    public DispatchOrder closeDispatchOrder(Long id, ProductionActionRequest request) {
        DispatchOrder dispatch = changeDispatchStatus(id, ProductionStatuses.CLOSED, "close", request);
        repository.updateTaskStatusByDispatch(id, ProductionStatuses.CLOSED);
        return dispatch;
    }

    @Transactional
    public DispatchOrder voidDispatchOrder(Long id, ProductionActionRequest request) {
        return changeDispatchStatus(id, ProductionStatuses.VOIDED, "void", request);
    }

    @Transactional
    public ShopTask createTaskFromDispatch(Long dispatchId, CreateTaskRequest request) {
        DispatchOrder dispatch = getDispatchOrder(dispatchId);
        if (!Set.of(ProductionStatuses.WAIT_ISSUE, ProductionStatuses.ISSUED).contains(dispatch.status())) {
            throw new BadRequestException("dispatch order must be wait-issued or issued before creating task");
        }
        return repository.findTaskByDispatchId(dispatchId)
                .orElseGet(() -> insertTaskFromDispatch(dispatch, request));
    }

    @Transactional
    public ShopTask startShopTask(Long id, ProductionActionRequest request) {
        return changeShopTaskStatus(id, ProductionStatuses.RUNNING, "start", request);
    }

    @Transactional
    public ShopTask pauseShopTask(Long id, ProductionActionRequest request) {
        return changeShopTaskStatus(id, ProductionStatuses.PAUSED, "pause", request);
    }

    @Transactional
    public ShopTask resumeShopTask(Long id, ProductionActionRequest request) {
        return changeShopTaskStatus(id, ProductionStatuses.RUNNING, "resume", request);
    }

    @Transactional
    public ShopTask completeShopTask(Long id, ProductionActionRequest request) {
        return changeShopTaskStatus(id, ProductionStatuses.COMPLETED, "complete", request);
    }

    private WorkOrder insertWorkOrder(WorkOrderRequest request) {
        return insertWorkOrder(IdGenerator.nextId(), request);
    }

    private WorkOrder insertWorkOrder(Long id, WorkOrderRequest request) {
        WorkOrder order = toWorkOrder(id, request);
        repository.insertWorkOrder(order);
        operationLogService.recordStatusChange(
                "production",
                "prod_work_order",
                order.id(),
                "create",
                null,
                order.status(),
                null,
                "create work order"
        );
        return order;
    }

    private WorkOrder idempotentCreateWorkOrder(WorkOrderRequest request) {
        return idempotencyService.findBusinessId("production", "create-work-order", request.idempotencyKey())
                .map(Long::valueOf)
                .map(this::getWorkOrder)
                .orElseGet(() -> {
                    Long id = IdGenerator.nextId();
                    if (!idempotencyService.record("production", "create-work-order", request.idempotencyKey(), id.toString())) {
                        return idempotencyService.findBusinessId("production", "create-work-order", request.idempotencyKey())
                                .map(Long::valueOf)
                                .map(this::getWorkOrder)
                                .orElseThrow(() -> new BadRequestException("idempotency record is missing business id"));
                    }
                    return insertWorkOrder(id, request);
                });
    }

    private WorkOrder changeWorkOrderStatus(
            Long id,
            String targetStatus,
            String actionName,
            ProductionActionRequest request
    ) {
        WorkOrder existing = getWorkOrder(id);
        ensureTransition(existing.status(), targetStatus, WORK_ORDER_TRANSITIONS, "prod_work_order");
        ensureUpdated(repository.updateWorkOrderStatus(id, existing.status(), targetStatus), "prod_work_order", id);
        recordStatus("prod_work_order", id, actionName, existing.status(), targetStatus, request);
        return new WorkOrder(
                existing.id(),
                existing.workOrderNo(),
                existing.externalNo(),
                existing.sourceType(),
                existing.productId(),
                existing.planQty(),
                existing.completedQty(),
                existing.qualifiedQty(),
                existing.defectiveQty(),
                existing.unitId(),
                existing.plannedStartAt(),
                existing.plannedEndAt(),
                existing.deliveryDate(),
                existing.lineId(),
                existing.routeId(),
                targetStatus
        );
    }

    private KittingAnalysis changeKittingStatus(
            Long id,
            String targetStatus,
            String actionName,
            ProductionActionRequest request
    ) {
        KittingAnalysis existing = getKittingAnalysis(id);
        ensureTransition(existing.status(), targetStatus, KITTING_TRANSITIONS, "prod_kitting_analysis");
        ensureUpdated(repository.updateKittingAnalysisStatus(id, existing.status(), targetStatus), "prod_kitting_analysis", id);
        recordStatus("prod_kitting_analysis", id, actionName, existing.status(), targetStatus, request);
        return new KittingAnalysis(
                existing.id(),
                existing.analysisNo(),
                existing.workOrderId(),
                existing.taskId(),
                existing.analysisTime(),
                existing.kittingStatus(),
                existing.missingCount(),
                targetStatus
        );
    }

    private DispatchOrder changeDispatchStatus(
            Long id,
            String targetStatus,
            String actionName,
            ProductionActionRequest request
    ) {
        DispatchOrder existing = getDispatchOrder(id);
        ensureTransition(existing.status(), targetStatus, DISPATCH_TRANSITIONS, "prod_dispatch_order");
        ensureUpdated(repository.updateDispatchStatus(id, existing.status(), targetStatus), "prod_dispatch_order", id);
        recordStatus("prod_dispatch_order", id, actionName, existing.status(), targetStatus, request);
        return new DispatchOrder(
                existing.id(),
                existing.dispatchNo(),
                existing.workOrderId(),
                existing.lineId(),
                existing.stationId(),
                existing.teamId(),
                existing.planQty(),
                existing.plannedStartAt(),
                existing.plannedEndAt(),
                targetStatus
        );
    }

    private ShopTask changeShopTaskStatus(
            Long id,
            String targetStatus,
            String actionName,
            ProductionActionRequest request
    ) {
        ShopTask existing = repository.findShopTaskById(id)
                .orElseThrow(() -> notFound("shop_task", id));
        ensureTransition(existing.status(), targetStatus, SHOP_TASK_TRANSITIONS, "shop_task");
        ensureUpdated(repository.updateShopTaskStatus(id, existing.status(), targetStatus), "shop_task", id);
        recordStatus("shop_task", id, actionName, existing.status(), targetStatus, request);
        return repository.findShopTaskById(id)
                .orElseThrow(() -> notFound("shop_task", id));
    }

    private void refreshKittingSummary(Long analysisId) {
        KittingAnalysis analysis = getKittingAnalysis(analysisId);
        int missingCount = repository.countPositiveMissingByAnalysis(analysisId);
        String kittingStatus = missingCount > 0 ? "欠料" : "齐套";
        KittingAnalysis refreshed = new KittingAnalysis(
                analysis.id(),
                analysis.analysisNo(),
                analysis.workOrderId(),
                analysis.taskId(),
                analysis.analysisTime(),
                kittingStatus,
                missingCount,
                analysis.status()
        );
        repository.updateKittingAnalysis(refreshed);
    }

    private ShopTask insertTaskFromDispatch(DispatchOrder dispatch, CreateTaskRequest request) {
        WorkOrder workOrder = dispatch.workOrderId() == null ? null : getWorkOrder(dispatch.workOrderId());
        ShopTask task = new ShopTask(
                IdGenerator.nextId(),
                StringUtils.hasText(request.taskNo()) ? request.taskNo().trim() : "TASK-" + dispatch.dispatchNo(),
                dispatch.workOrderId(),
                dispatch.id(),
                workOrder == null ? null : workOrder.productId(),
                workOrder == null ? null : workOrder.routeId(),
                dispatch.lineId(),
                dispatch.teamId(),
                dispatch.planQty(),
                null,
                null,
                dispatch.status(),
                null, null, null, null
        );
        repository.insertShopTask(task);
        operationLogService.recordStatusChange(
                "production",
                "shop_task",
                task.id(),
                "create-from-dispatch",
                null,
                task.status(),
                null,
                "created from dispatch " + dispatch.id()
        );
        return task;
    }

    private WorkOrder toWorkOrder(Long id, WorkOrderRequest request) {
        return new WorkOrder(
                id,
                request.workOrderNo().trim(),
                trimToNull(request.externalNo()),
                trimToNull(request.sourceType()),
                request.productId(),
                request.planQty(),
                defaultQty(request.completedQty()),
                defaultQty(request.qualifiedQty()),
                defaultQty(request.defectiveQty()),
                request.unitId(),
                request.plannedStartAt(),
                request.plannedEndAt(),
                request.deliveryDate(),
                request.lineId(),
                request.routeId(),
                defaultStatus(request.status(), ProductionStatuses.DRAFT)
        );
    }

    private KittingAnalysis toKittingAnalysis(Long id, KittingAnalysisRequest request) {
        int missingCount = request.missingCount() == null ? 0 : request.missingCount();
        return new KittingAnalysis(
                id,
                request.analysisNo().trim(),
                request.workOrderId(),
                request.taskId(),
                request.analysisTime() == null ? LocalDateTime.now() : request.analysisTime(),
                StringUtils.hasText(request.kittingStatus()) ? request.kittingStatus().trim() : (missingCount > 0 ? "欠料" : "齐套"),
                missingCount,
                defaultStatus(request.status(), ProductionStatuses.DRAFT)
        );
    }

    private KittingMissingItem toMissingItem(Long id, Long analysisId, KittingMissingItemRequest request) {
        return new KittingMissingItem(
                id,
                analysisId,
                request.materialId(),
                defaultQty(request.requiredQty()),
                defaultQty(request.availableQty()),
                defaultQty(request.missingQty()),
                request.expectedArrivalAt()
        );
    }

    private DispatchOrder toDispatchOrder(Long id, DispatchOrderRequest request) {
        return new DispatchOrder(
                id,
                request.dispatchNo().trim(),
                request.workOrderId(),
                request.lineId(),
                request.stationId(),
                request.teamId(),
                request.planQty(),
                request.plannedStartAt(),
                request.plannedEndAt(),
                defaultStatus(request.status(), ProductionStatuses.DRAFT)
        );
    }

    private void ensureWorkOrderExists(Long workOrderId) {
        if (workOrderId == null) {
            return;
        }
        repository.findWorkOrderById(workOrderId)
                .orElseThrow(() -> new BadRequestException("prod_work_order not found: " + workOrderId));
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
            ProductionActionRequest request
    ) {
        operationLogService.recordStatusChange(
                "production",
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
