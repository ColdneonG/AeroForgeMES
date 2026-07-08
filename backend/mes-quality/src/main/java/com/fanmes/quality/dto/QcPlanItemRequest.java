package com.fanmes.quality.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record QcPlanItemRequest(
        Long planId,
        Long qcItemId,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal sampleQty,
        String standardValue,
        BigDecimal upperLimit,
        BigDecimal lowerLimit,
        Boolean requiredFlag
) {
    @AssertTrue(message = "upperLimit must be greater than or equal to lowerLimit")
    public boolean isValidRange() {
        return upperLimit == null || lowerLimit == null || upperLimit.compareTo(lowerLimit) >= 0;
    }
}
