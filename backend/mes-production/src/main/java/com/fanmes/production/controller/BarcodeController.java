package com.fanmes.production.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.common.security.RequirePermission;
import com.fanmes.production.domain.barcode.BarcodeApplicationRule;
import com.fanmes.production.domain.barcode.BarcodeGenerationResult;
import com.fanmes.production.domain.barcode.BarcodeItemOption;
import com.fanmes.production.domain.barcode.BarcodePrintRecord;
import com.fanmes.production.domain.barcode.BarcodePrintResult;
import com.fanmes.production.domain.barcode.BarcodeRecord;
import com.fanmes.production.domain.barcode.BarcodeRule;
import com.fanmes.production.domain.barcode.BarcodeTemplate;
import com.fanmes.production.domain.barcode.BarcodeTraceEvent;
import com.fanmes.production.domain.barcode.BarcodeTraceResult;
import com.fanmes.production.domain.barcode.BarcodeType;
import com.fanmes.production.dto.ProductionActionRequest;
import com.fanmes.production.dto.barcode.BarcodeApplicationRuleRequest;
import com.fanmes.production.dto.barcode.BarcodeBatchPrintRequest;
import com.fanmes.production.dto.barcode.BarcodeGenerateRequest;
import com.fanmes.production.dto.barcode.BarcodeParentBindRequest;
import com.fanmes.production.dto.barcode.BarcodePrintRequest;
import com.fanmes.production.dto.barcode.BarcodeRuleRequest;
import com.fanmes.production.dto.barcode.BarcodeScanRequest;
import com.fanmes.production.service.BarcodeService;
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
public class BarcodeController {
    private final BarcodeService service;

    public BarcodeController(BarcodeService service) {
        this.service = service;
    }

    @GetMapping("/barcodes/types")
    @RequirePermission("barcode:type:view")
    public ApiResponse<List<BarcodeType>> listTypes(@RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listTypes(status));
    }

    @GetMapping("/barcodes/items")
    @RequirePermission("barcode:application-rule:view")
    public ApiResponse<List<BarcodeItemOption>> listItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listItems(keyword, status));
    }

    @GetMapping("/barcodes/templates")
    @RequirePermission("barcode:template:view")
    public ApiResponse<List<BarcodeTemplate>> listTemplates(
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listTemplates(typeId, status));
    }

    @GetMapping("/barcodes/rules")
    @RequirePermission("barcode:rule:view")
    public ApiResponse<List<BarcodeRule>> listRules(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listRules(keyword, typeId, status));
    }

    @GetMapping("/barcodes/rules/{id}")
    @RequirePermission("barcode:rule:view")
    public ApiResponse<BarcodeRule> getRule(@PathVariable Long id) {
        return ApiResponse.ok(service.getRule(id));
    }

    @PostMapping("/barcodes/rules")
    @RequirePermission("barcode:rule:add")
    public ApiResponse<BarcodeRule> createRule(@Valid @RequestBody BarcodeRuleRequest request) {
        return ApiResponse.ok(service.createRule(request));
    }

    @PutMapping("/barcodes/rules/{id}")
    @RequirePermission("barcode:rule:edit")
    public ApiResponse<BarcodeRule> updateRule(@PathVariable Long id, @Valid @RequestBody BarcodeRuleRequest request) {
        return ApiResponse.ok(service.updateRule(id, request));
    }

    @PostMapping("/barcodes/rules/{id}/enable")
    @RequirePermission("barcode:rule:edit")
    public ApiResponse<BarcodeRule> enableRule(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.enableRule(id, request));
    }

    @PostMapping("/barcodes/rules/{id}/disable")
    @RequirePermission("barcode:rule:edit")
    public ApiResponse<BarcodeRule> disableRule(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.disableRule(id, request));
    }

    @DeleteMapping("/barcodes/rules/{id}")
    @RequirePermission("barcode:rule:delete")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        service.deleteRule(id);
        return ApiResponse.ok();
    }

    @GetMapping("/barcodes/application-rules")
    @RequirePermission("barcode:application-rule:view")
    public ApiResponse<List<BarcodeApplicationRule>> listApplicationRules(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.ok(service.listApplicationRules(keyword, itemId, typeId, status));
    }

    @PostMapping("/barcodes/application-rules")
    @RequirePermission("barcode:application-rule:add")
    public ApiResponse<BarcodeApplicationRule> createApplicationRule(
            @Valid @RequestBody BarcodeApplicationRuleRequest request
    ) {
        return ApiResponse.ok(service.createApplicationRule(request));
    }

    @PutMapping("/barcodes/application-rules/{id}")
    @RequirePermission("barcode:application-rule:edit")
    public ApiResponse<BarcodeApplicationRule> updateApplicationRule(
            @PathVariable Long id,
            @Valid @RequestBody BarcodeApplicationRuleRequest request
    ) {
        return ApiResponse.ok(service.updateApplicationRule(id, request));
    }

    @DeleteMapping("/barcodes/application-rules/{id}")
    @RequirePermission("barcode:application-rule:delete")
    public ApiResponse<Void> deleteApplicationRule(@PathVariable Long id) {
        service.deleteApplicationRule(id);
        return ApiResponse.ok();
    }

    @GetMapping("/barcodes")
    @RequirePermission("barcode:generate:view")
    public ApiResponse<List<BarcodeRecord>> listBarcodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String batchNo
    ) {
        return ApiResponse.ok(service.listBarcodes(keyword, status, typeId, workOrderId, taskId, batchNo));
    }

    @GetMapping("/barcodes/{id}")
    @RequirePermission("barcode:generate:view")
    public ApiResponse<BarcodeRecord> getBarcode(@PathVariable Long id) {
        return ApiResponse.ok(service.getBarcode(id));
    }

    @PostMapping("/barcodes/generate")
    @RequirePermission("barcode:generate")
    public ApiResponse<BarcodeGenerationResult> generate(@Valid @RequestBody BarcodeGenerateRequest request) {
        return ApiResponse.ok(service.generate(request));
    }

    @PostMapping("/barcodes/{id}/print")
    @RequirePermission("barcode:print")
    public ApiResponse<BarcodePrintResult> print(
            @PathVariable Long id,
            @Valid @RequestBody BarcodePrintRequest request
    ) {
        return ApiResponse.ok(service.printBarcode(id, request));
    }

    @PostMapping("/barcodes/print")
    @RequirePermission("barcode:print")
    public ApiResponse<List<BarcodePrintResult>> batchPrint(@Valid @RequestBody BarcodeBatchPrintRequest request) {
        return ApiResponse.ok(service.batchPrint(request));
    }

    @GetMapping("/barcodes/{id}/print-records")
    @RequirePermission("barcode:generate:view")
    public ApiResponse<List<BarcodePrintRecord>> listPrintRecords(@PathVariable Long id) {
        return ApiResponse.ok(service.listPrintRecords(id));
    }

    @GetMapping("/barcodes/{id}/events")
    @RequirePermission("barcode:trace:view")
    public ApiResponse<List<BarcodeTraceEvent>> listEvents(@PathVariable Long id) {
        return ApiResponse.ok(service.listEvents(id));
    }

    @PostMapping("/barcodes/scan")
    @RequirePermission("barcode:scan")
    public ApiResponse<BarcodeRecord> scan(@Valid @RequestBody BarcodeScanRequest request) {
        return ApiResponse.ok(service.scan(request));
    }

    @PostMapping("/barcodes/{id}/parent")
    @RequirePermission("barcode:bind")
    public ApiResponse<BarcodeRecord> bindParent(
            @PathVariable Long id,
            @Valid @RequestBody BarcodeParentBindRequest request
    ) {
        return ApiResponse.ok(service.bindParent(id, request));
    }

    @PostMapping("/barcodes/{id}/close")
    @RequirePermission("barcode:close")
    public ApiResponse<BarcodeRecord> close(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.close(id, request));
    }

    @PostMapping("/barcodes/{id}/void")
    @RequirePermission("barcode:void")
    public ApiResponse<BarcodeRecord> voidBarcode(
            @PathVariable Long id,
            @Valid @RequestBody ProductionActionRequest request
    ) {
        return ApiResponse.ok(service.voidBarcode(id, request));
    }

    @GetMapping("/trace/{keyword}")
    @RequirePermission("barcode:trace:view")
    public ApiResponse<BarcodeTraceResult> trace(
            @PathVariable String keyword,
            @RequestParam(required = false, defaultValue = "SN") String mode
    ) {
        return ApiResponse.ok(service.trace(keyword, mode));
    }
}
