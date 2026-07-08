package com.fanmes.production.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkOrderRequest(
        @NotBlank @Size(max = 64) String workOrderNo,
        @Size(max = 64) String externalNo,
        @Size(max = 32) String sourceType,
        @NotNull Long productId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal planQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal completedQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal qualifiedQty,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal defectiveQty,
        Long unitId,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedEndAt,
        LocalDate deliveryDate,
        @NotNull Long lineId,
        Long routeId,
        @Size(max = 32) String status,
        @Size(max = 128) String idempotencyKey
) {
    @AssertTrue(message = "plannedEndAt must be after plannedStartAt")
    public boolean isValidTimeRange() {
        return plannedStartAt == null || plannedEndAt == null || !plannedEndAt.isBefore(plannedStartAt);
    }

    @AssertTrue(message = "completed/qualified/defective quantity must not exceed planQty")
    public boolean isValidQuantityRange() {
        if (planQty == null) {
            return true;
        }
        return notGreaterThanPlan(completedQty) && notGreaterThanPlan(qualifiedQty) && notGreaterThanPlan(defectiveQty);
    }

    private boolean notGreaterThanPlan(BigDecimal value) {
        return value == null || value.compareTo(planQty) <= 0;
    }
}
