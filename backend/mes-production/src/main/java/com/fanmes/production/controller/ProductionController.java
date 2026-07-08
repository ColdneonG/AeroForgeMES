package com.fanmes.production.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.production.domain.DispatchOrder;
import com.fanmes.production.domain.KittingAnalysis;
import com.fanmes.production.domain.KittingMissingItem;
import com.fanmes.production.domain.ShopTask;
import com.fanmes.production.domain.WorkOrder;
import com.fanmes.production.dto.CompleteWorkOrderRequest;
import com.fanmes.production.dto.CreateTaskRequest;
import com.fanmes.production.dto.DispatchOrderRequest;
import com.fanmes.production.dto.KittingAnalysisRequest;
import com.fanmes.production.dto.KittingMissingItemRequest;
import com.fanmes.production.dto.ProductionActionRequest;
import com.fanmes.production.dto.WorkOrderRequest;
import com.fanmes.production.service.ProductionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/production")
public class ProductionController {
    private final ProductionService service;

    public ProductionController(ProductionService service) {
        this.service = service;
    }

    @GetMapping("/work-orders")
    public ApiResponse<List<WorkOrder>> listWorkOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) String externalNo
    ) {
        return ApiResponse.ok(service.listWorkOrders(keyword, status, productId, lineId, sourceType, externalNo));
    }

    @GetMapping("/work-orders/{id}")
    public ApiResponse<WorkOrder> getWorkOrder(@PathVariable Long id) {
        return ApiResponse.ok(service.getWorkOrder(id));
    }

    @PostMapping("/work-orders")
    public ApiResponse<WorkOrder> createWorkOrder(@Valid @RequestBody WorkOrderRequest request) {
        return ApiResponse.ok(service.createWorkOrder(request));
    }

    @PutMapping("/work-orders/{id}")
    public ApiResponse<WorkOrder> updateWorkOrder(@PathVariable Long id, @Valid @RequestBody WorkOrderRequest request) {
        return ApiResponse.ok(service.updateWorkOrder(id, request));
    }

    @DeleteMapping("/work-orders/{id}")
    public ApiResponse<Void> deleteWorkOrder(@PathVariable Long id) {
        service.deleteWorkOrder(id);
        return ApiResponse.ok();
    }

    @PostMapping("/work-orders/{id}/issue")
    public ApiResponse<WorkOrder> issueWorkOrder(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.issueWorkOrder(id, request));
    }

    @PostMapping("/work-orders/{id}/confirm-issue")
    public ApiResponse<WorkOrder> confirmWorkOrderIssue(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.confirmWorkOrderIssue(id, request));
    }

    @PostMapping("/work-orders/{id}/start")
    public ApiResponse<WorkOrder> startWorkOrder(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.startWorkOrder(id, request));
    }

    @PostMapping("/work-orders/{id}/pause")
    public ApiResponse<WorkOrder> pauseWorkOrder(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.pauseWorkOrder(id, request));
    }

    @PostMapping("/work-orders/{id}/complete")
    public ApiResponse<WorkOrder> completeWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody CompleteWorkOrderRequest request
    ) {
        return ApiResponse.ok(service.completeWorkOrder(id, request));
    }

    @PostMapping("/work-orders/{id}/close")
    public ApiResponse<WorkOrder> closeWorkOrder(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.closeWorkOrder(id, request));
    }

    @PostMapping("/work-orders/{id}/void")
    public ApiResponse<WorkOrder> voidWorkOrder(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.voidWorkOrder(id, request));
    }

    @GetMapping("/kitting-analyses")
    public ApiResponse<List<KittingAnalysis>> listKittingAnalyses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) Long taskId
    ) {
        return ApiResponse.ok(service.listKittingAnalyses(keyword, status, workOrderId, taskId));
    }

    @GetMapping("/kitting-analyses/{id}")
    public ApiResponse<KittingAnalysis> getKittingAnalysis(@PathVariable Long id) {
        return ApiResponse.ok(service.getKittingAnalysis(id));
    }

    @PostMapping("/kitting-analyses")
    public ApiResponse<KittingAnalysis> createKittingAnalysis(@Valid @RequestBody KittingAnalysisRequest request) {
        return ApiResponse.ok(service.createKittingAnalysis(request));
    }

    @PutMapping("/kitting-analyses/{id}")
    public ApiResponse<KittingAnalysis> updateKittingAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody KittingAnalysisRequest request
    ) {
        return ApiResponse.ok(service.updateKittingAnalysis(id, request));
    }

    @DeleteMapping("/kitting-analyses/{id}")
    public ApiResponse<Void> deleteKittingAnalysis(@PathVariable Long id) {
        service.deleteKittingAnalysis(id);
        return ApiResponse.ok();
    }

    @PostMapping("/kitting-analyses/{id}/complete")
    public ApiResponse<KittingAnalysis> completeKittingAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.completeKittingAnalysis(id, request));
    }

    @PostMapping("/kitting-analyses/{id}/close")
    public ApiResponse<KittingAnalysis> closeKittingAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.closeKittingAnalysis(id, request));
    }

    @PostMapping("/kitting-analyses/{id}/void")
    public ApiResponse<KittingAnalysis> voidKittingAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.voidKittingAnalysis(id, request));
    }

    @GetMapping("/kitting-missing-items")
    public ApiResponse<List<KittingMissingItem>> listMissingItems(
            @RequestParam(required = false) Long analysisId,
            @RequestParam(required = false) Long materialId
    ) {
        return ApiResponse.ok(service.listMissingItems(analysisId, materialId));
    }

    @GetMapping("/kitting-missing-board")
    public ApiResponse<List<KittingMissingItem>> listMissingBoard() {
        return ApiResponse.ok(service.listMissingBoard());
    }

    @GetMapping("/kitting-missing-items/{id}")
    public ApiResponse<KittingMissingItem> getMissingItem(@PathVariable Long id) {
        return ApiResponse.ok(service.getMissingItem(id));
    }

    @PostMapping("/kitting-analyses/{analysisId}/missing-items")
    public ApiResponse<KittingMissingItem> createMissingItem(
            @PathVariable Long analysisId,
            @Valid @RequestBody KittingMissingItemRequest request
    ) {
        return ApiResponse.ok(service.createMissingItem(analysisId, request));
    }

    @PutMapping("/kitting-missing-items/{id}")
    public ApiResponse<KittingMissingItem> updateMissingItem(
            @PathVariable Long id,
            @Valid @RequestBody KittingMissingItemRequest request
    ) {
        return ApiResponse.ok(service.updateMissingItem(id, request));
    }

    @DeleteMapping("/kitting-missing-items/{id}")
    public ApiResponse<Void> deleteMissingItem(@PathVariable Long id) {
        service.deleteMissingItem(id);
        return ApiResponse.ok();
    }

    @GetMapping("/dispatch-orders")
    public ApiResponse<List<DispatchOrder>> listDispatchOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long teamId
    ) {
        return ApiResponse.ok(service.listDispatchOrders(keyword, status, workOrderId, lineId, teamId));
    }

    @GetMapping("/dispatch-orders/{id}")
    public ApiResponse<DispatchOrder> getDispatchOrder(@PathVariable Long id) {
        return ApiResponse.ok(service.getDispatchOrder(id));
    }

    @PostMapping("/dispatch-orders")
    public ApiResponse<DispatchOrder> createDispatchOrder(@Valid @RequestBody DispatchOrderRequest request) {
        return ApiResponse.ok(service.createDispatchOrder(request));
    }

    @PutMapping("/dispatch-orders/{id}")
    public ApiResponse<DispatchOrder> updateDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody DispatchOrderRequest request
    ) {
        return ApiResponse.ok(service.updateDispatchOrder(id, request));
    }

    @DeleteMapping("/dispatch-orders/{id}")
    public ApiResponse<Void> deleteDispatchOrder(@PathVariable Long id) {
        service.deleteDispatchOrder(id);
        return ApiResponse.ok();
    }

    @PostMapping("/dispatch-orders/{id}/issue")
    public ApiResponse<DispatchOrder> issueDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.issueDispatchOrder(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/confirm-issue")
    public ApiResponse<DispatchOrder> confirmDispatchIssue(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.confirmDispatchIssue(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/start")
    public ApiResponse<DispatchOrder> startDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.startDispatchOrder(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/complete")
    public ApiResponse<DispatchOrder> completeDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.completeDispatchOrder(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/close")
    public ApiResponse<DispatchOrder> closeDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.closeDispatchOrder(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/void")
    public ApiResponse<DispatchOrder> voidDispatchOrder(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.voidDispatchOrder(id, request));
    }

    @PostMapping("/dispatch-orders/{id}/tasks")
    public ApiResponse<ShopTask> createTaskFromDispatch(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return ApiResponse.ok(service.createTaskFromDispatch(id, request));
    }

    @GetMapping("/tasks")
    public ApiResponse<List<ShopTask>> listShopTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) Long dispatchId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long teamId
    ) {
        return ApiResponse.ok(service.listShopTasks(keyword, status, workOrderId, dispatchId, lineId, teamId));
    }

    @PostMapping("/tasks/{id}/start")
    public ApiResponse<ShopTask> startShopTask(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.startShopTask(id, request));
    }

    @PostMapping("/tasks/{id}/pause")
    public ApiResponse<ShopTask> pauseShopTask(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.pauseShopTask(id, request));
    }

    @PostMapping("/tasks/{id}/resume")
    public ApiResponse<ShopTask> resumeShopTask(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.resumeShopTask(id, request));
    }

    @PostMapping("/tasks/{id}/complete")
    public ApiResponse<ShopTask> completeShopTask(@PathVariable Long id, @Valid @RequestBody ProductionActionRequest request) {
        return ApiResponse.ok(service.completeShopTask(id, request));
    }
}
