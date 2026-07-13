package com.fanmes.quality.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.quality.domain.QcDefectRecord;
import com.fanmes.quality.domain.QcInspectionOrder;
import com.fanmes.quality.domain.QcInspectionResult;
import com.fanmes.quality.domain.QcItem;
import com.fanmes.quality.domain.QcItemCategory;
import com.fanmes.quality.domain.QcPlan;
import com.fanmes.quality.domain.QcPlanItem;
import com.fanmes.quality.dto.QcDefectRecordRequest;
import com.fanmes.quality.dto.QcInspectionOrderRequest;
import com.fanmes.quality.dto.QcInspectionResultRequest;
import com.fanmes.quality.dto.QcItemCategoryRequest;
import com.fanmes.quality.dto.QcItemRequest;
import com.fanmes.quality.dto.QcPlanItemRequest;
import com.fanmes.quality.dto.QcPlanRequest;
import com.fanmes.quality.dto.QualityActionRequest;
import com.fanmes.quality.service.QualityService;
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
@RequestMapping("/api/quality")
public class QualityController {
    private final QualityService service;

    public QualityController(QualityService service) {
        this.service = service;
    }

    @GetMapping("/item-categories")
    public ApiResponse<List<QcItemCategory>> listItemCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listItemCategories(keyword, status));
    }

    @GetMapping("/item-categories/{id}")
    public ApiResponse<QcItemCategory> getItemCategory(@PathVariable Long id) {
        return ApiResponse.ok(service.getItemCategory(id));
    }

    @PostMapping("/item-categories")
    public ApiResponse<QcItemCategory> createItemCategory(@Valid @RequestBody QcItemCategoryRequest request) {
        return ApiResponse.ok(service.createItemCategory(request));
    }

    @PutMapping("/item-categories/{id}")
    public ApiResponse<QcItemCategory> updateItemCategory(
            @PathVariable Long id,
            @Valid @RequestBody QcItemCategoryRequest request
    ) {
        return ApiResponse.ok(service.updateItemCategory(id, request));
    }

    @DeleteMapping("/item-categories/{id}")
    public ApiResponse<Void> deleteItemCategory(@PathVariable Long id) {
        service.deleteItemCategory(id);
        return ApiResponse.ok();
    }

    @PostMapping("/item-categories/{id}/enable")
    public ApiResponse<QcItemCategory> enableItemCategory(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.enableItemCategory(id, request));
    }

    @PostMapping("/item-categories/{id}/disable")
    public ApiResponse<QcItemCategory> disableItemCategory(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.disableItemCategory(id, request));
    }

    @PostMapping("/item-categories/{id}/void")
    public ApiResponse<QcItemCategory> voidItemCategory(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.voidItemCategory(id, request));
    }

    @GetMapping("/items")
    public ApiResponse<List<QcItem>> listItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listItems(keyword, categoryId, status));
    }

    @GetMapping("/items/{id}")
    public ApiResponse<QcItem> getItem(@PathVariable Long id) {
        return ApiResponse.ok(service.getItem(id));
    }

    @PostMapping("/items")
    public ApiResponse<QcItem> createItem(@Valid @RequestBody QcItemRequest request) {
        return ApiResponse.ok(service.createItem(request));
    }

    @PutMapping("/items/{id}")
    public ApiResponse<QcItem> updateItem(@PathVariable Long id, @Valid @RequestBody QcItemRequest request) {
        return ApiResponse.ok(service.updateItem(id, request));
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ApiResponse.ok();
    }

    @PostMapping("/items/{id}/enable")
    public ApiResponse<QcItem> enableItem(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.enableItem(id, request));
    }

    @PostMapping("/items/{id}/disable")
    public ApiResponse<QcItem> disableItem(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.disableItem(id, request));
    }

    @PostMapping("/items/{id}/void")
    public ApiResponse<QcItem> voidItem(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.voidItem(id, request));
    }

    @GetMapping("/plans")
    public ApiResponse<List<QcPlan>> listPlans(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listPlans(keyword, productId, customerId, status));
    }

    @GetMapping("/plans/{id}")
    public ApiResponse<QcPlan> getPlan(@PathVariable Long id) {
        return ApiResponse.ok(service.getPlan(id));
    }

    @PostMapping("/plans")
    public ApiResponse<QcPlan> createPlan(@Valid @RequestBody QcPlanRequest request) {
        return ApiResponse.ok(service.createPlan(request));
    }

    @PutMapping("/plans/{id}")
    public ApiResponse<QcPlan> updatePlan(@PathVariable Long id, @Valid @RequestBody QcPlanRequest request) {
        return ApiResponse.ok(service.updatePlan(id, request));
    }

    @DeleteMapping("/plans/{id}")
    public ApiResponse<Void> deletePlan(@PathVariable Long id) {
        service.deletePlan(id);
        return ApiResponse.ok();
    }

    @PostMapping("/plans/{id}/enable")
    public ApiResponse<QcPlan> enablePlan(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.enablePlan(id, request));
    }

    @PostMapping("/plans/{id}/disable")
    public ApiResponse<QcPlan> disablePlan(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.disablePlan(id, request));
    }

    @PostMapping("/plans/{id}/void")
    public ApiResponse<QcPlan> voidPlan(@PathVariable Long id, @Valid @RequestBody QualityActionRequest request) {
        return ApiResponse.ok(service.voidPlan(id, request));
    }

    @GetMapping("/plan-items")
    public ApiResponse<List<QcPlanItem>> listPlanItems(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long qcItemId
    ) {
        return ApiResponse.ok(service.listPlanItems(planId, qcItemId));
    }

    @GetMapping("/plan-items/{id}")
    public ApiResponse<QcPlanItem> getPlanItem(@PathVariable Long id) {
        return ApiResponse.ok(service.getPlanItem(id));
    }

    @PostMapping("/plan-items")
    public ApiResponse<QcPlanItem> createPlanItem(@Valid @RequestBody QcPlanItemRequest request) {
        return ApiResponse.ok(service.createPlanItem(request));
    }

    @PutMapping("/plan-items/{id}")
    public ApiResponse<QcPlanItem> updatePlanItem(@PathVariable Long id, @Valid @RequestBody QcPlanItemRequest request) {
        return ApiResponse.ok(service.updatePlanItem(id, request));
    }

    @DeleteMapping("/plan-items/{id}")
    public ApiResponse<Void> deletePlanItem(@PathVariable Long id) {
        service.deletePlanItem(id);
        return ApiResponse.ok();
    }

    @GetMapping("/inspection-orders")
    public ApiResponse<List<QcInspectionOrder>> listInspectionOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String inspectionType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long operationTaskId,
            @RequestParam(required = false) Long barcodeId
    ) {
        return ApiResponse.ok(service.listInspectionOrders(
                keyword,
                inspectionType,
                status,
                planId,
                workOrderId,
                taskId,
                operationTaskId,
                barcodeId
        ));
    }

    @GetMapping("/inspection-orders/{id}")
    public ApiResponse<QcInspectionOrder> getInspectionOrder(@PathVariable Long id) {
        return ApiResponse.ok(service.getInspectionOrder(id));
    }

    @PostMapping("/inspection-orders")
    public ApiResponse<QcInspectionOrder> createInspectionOrder(@Valid @RequestBody QcInspectionOrderRequest request) {
        return ApiResponse.ok(service.createInspectionOrder(request));
    }

    @PutMapping("/inspection-orders/{id}")
    public ApiResponse<QcInspectionOrder> updateInspectionOrder(
            @PathVariable Long id,
            @Valid @RequestBody QcInspectionOrderRequest request
    ) {
        return ApiResponse.ok(service.updateInspectionOrder(id, request));
    }

    @DeleteMapping("/inspection-orders/{id}")
    public ApiResponse<Void> deleteInspectionOrder(@PathVariable Long id) {
        service.deleteInspectionOrder(id);
        return ApiResponse.ok();
    }

    @PostMapping("/inspection-orders/{id}/start")
    public ApiResponse<QcInspectionOrder> startInspection(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.startInspection(id, request));
    }

    @PostMapping("/inspection-orders/{id}/pass")
    public ApiResponse<QcInspectionOrder> passInspection(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.passInspection(id, request));
    }

    @PostMapping("/inspection-orders/{id}/fail")
    public ApiResponse<QcInspectionOrder> failInspection(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.failInspection(id, request));
    }

    @PostMapping("/inspection-orders/{id}/close")
    public ApiResponse<QcInspectionOrder> closeInspection(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.closeInspection(id, request));
    }

    @PostMapping("/inspection-orders/{id}/void")
    public ApiResponse<QcInspectionOrder> voidInspection(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.voidInspection(id, request));
    }

    @GetMapping("/inspection-results")
    public ApiResponse<List<QcInspectionResult>> listInspectionResults(
            @RequestParam(required = false) Long inspectionId,
            @RequestParam(required = false) Long qcItemId,
            @RequestParam(required = false) String result
    ) {
        return ApiResponse.ok(service.listInspectionResults(inspectionId, qcItemId, result));
    }

    @GetMapping("/inspection-results/{id}")
    public ApiResponse<QcInspectionResult> getInspectionResult(@PathVariable Long id) {
        return ApiResponse.ok(service.getInspectionResult(id));
    }

    @PostMapping("/inspection-results")
    public ApiResponse<QcInspectionResult> createInspectionResult(@Valid @RequestBody QcInspectionResultRequest request) {
        return ApiResponse.ok(service.createInspectionResult(request));
    }

    @PutMapping("/inspection-results/{id}")
    public ApiResponse<QcInspectionResult> updateInspectionResult(
            @PathVariable Long id,
            @Valid @RequestBody QcInspectionResultRequest request
    ) {
        return ApiResponse.ok(service.updateInspectionResult(id, request));
    }

    @DeleteMapping("/inspection-results/{id}")
    public ApiResponse<Void> deleteInspectionResult(@PathVariable Long id) {
        service.deleteInspectionResult(id);
        return ApiResponse.ok();
    }

    @GetMapping("/defect-records")
    public ApiResponse<List<QcDefectRecord>> listDefectRecords(
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long barcodeId,
            @RequestParam(required = false) Long processId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listDefectRecords(sourceType, sourceId, productId, barcodeId, processId, status));
    }

    @GetMapping("/defect-records/{id}")
    public ApiResponse<QcDefectRecord> getDefectRecord(@PathVariable Long id) {
        return ApiResponse.ok(service.getDefectRecord(id));
    }

    @PostMapping("/defect-records")
    public ApiResponse<QcDefectRecord> createDefectRecord(@Valid @RequestBody QcDefectRecordRequest request) {
        return ApiResponse.ok(service.createDefectRecord(request));
    }

    @PutMapping("/defect-records/{id}")
    public ApiResponse<QcDefectRecord> updateDefectRecord(
            @PathVariable Long id,
            @Valid @RequestBody QcDefectRecordRequest request
    ) {
        return ApiResponse.ok(service.updateDefectRecord(id, request));
    }

    @DeleteMapping("/defect-records/{id}")
    public ApiResponse<Void> deleteDefectRecord(@PathVariable Long id) {
        service.deleteDefectRecord(id);
        return ApiResponse.ok();
    }

    @PostMapping("/defect-records/{id}/start")
    public ApiResponse<QcDefectRecord> startDefect(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.startDefect(id, request));
    }

    @PostMapping("/defect-records/{id}/complete")
    public ApiResponse<QcDefectRecord> completeDefect(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.completeDefect(id, request));
    }

    @PostMapping("/defect-records/{id}/close")
    public ApiResponse<QcDefectRecord> closeDefect(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.closeDefect(id, request));
    }

    @PostMapping("/defect-records/{id}/void")
    public ApiResponse<QcDefectRecord> voidDefect(
            @PathVariable Long id,
            @Valid @RequestBody QualityActionRequest request
    ) {
        return ApiResponse.ok(service.voidDefect(id, request));
    }
}
