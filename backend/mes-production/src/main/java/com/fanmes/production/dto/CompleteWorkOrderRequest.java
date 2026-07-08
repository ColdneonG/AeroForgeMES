package com.fanmes.production.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CompleteWorkOrderRequest(
        Long operatorId,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal completedQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal qualifiedQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal defectiveQty,
        @Size(max = 500) String remark
) {
    @AssertTrue(message = "completedQty must be greater than or equal to qualifiedQty + defectiveQty")
    public boolean isValidCompletedQty() {
        if (completedQty == null || qualifiedQty == null || defectiveQty == null) {
            return true;
        }
        return completedQty.compareTo(qualifiedQty.add(defectiveQty)) >= 0;
    }
}
