package com.fanmes.production.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KittingMissingItemRequest(
        @NotNull Long materialId,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal requiredQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal availableQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal missingQty,
        LocalDateTime expectedArrivalAt
) {
    @AssertTrue(message = "missingQty should equal max(requiredQty - availableQty, 0) when both quantities are present")
    public boolean isValidMissingQty() {
        if (requiredQty == null || availableQty == null || missingQty == null) {
            return true;
        }
        BigDecimal expected = requiredQty.subtract(availableQty).max(BigDecimal.ZERO);
        return missingQty.compareTo(expected) == 0;
    }
}
