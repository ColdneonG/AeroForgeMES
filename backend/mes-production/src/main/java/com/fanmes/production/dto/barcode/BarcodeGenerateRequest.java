package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BarcodeGenerateRequest(
        Long applicationRuleId,
        Long ruleId,
        @Min(1) @Max(500) Integer quantity,
        Long itemId,
        @Size(max = 64) String itemCode,
        @Size(max = 64) String batchNo,
        Long workOrderId,
        Long taskId,
        Long parentBarcodeId,
        List<@Size(max = 128) String> externalValues,
        @Size(max = 32) String sourceType,
        Long operatorId
) {
    @AssertTrue(message = "applicationRuleId or ruleId is required")
    public boolean hasRule() {
        return applicationRuleId != null || ruleId != null;
    }
}
