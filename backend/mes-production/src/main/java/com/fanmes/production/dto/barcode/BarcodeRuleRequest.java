package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BarcodeRuleRequest(
        @NotBlank @Size(max = 64) String ruleCode,
        @NotBlank @Size(max = 128) String ruleName,
        @NotNull Long typeId,
        @NotBlank @Size(max = 500) String ruleExpression,
        @NotNull @Min(1) @Max(32) Integer serialLength,
        @Size(max = 32) String status
) {
}
