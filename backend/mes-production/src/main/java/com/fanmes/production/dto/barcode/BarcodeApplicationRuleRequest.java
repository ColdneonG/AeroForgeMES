package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BarcodeApplicationRuleRequest(
        @NotBlank @Size(max = 64) String applicationRuleCode,
        @NotNull Long itemId,
        @NotNull Long typeId,
        @NotNull Long ruleId,
        @NotNull Long templateId,
        @NotBlank @Size(max = 32) String barcodeMode,
        @NotBlank @Size(max = 32) String sourceType,
        @Size(max = 32) String status
) {
}
