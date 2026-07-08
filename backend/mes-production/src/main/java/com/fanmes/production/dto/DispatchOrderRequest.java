package com.fanmes.production.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DispatchOrderRequest(
        @NotBlank @Size(max = 64) String dispatchNo,
        Long workOrderId,
        @NotNull Long lineId,
        Long stationId,
        Long teamId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal planQty,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedEndAt,
        @Size(max = 32) String status
) {
    @AssertTrue(message = "plannedEndAt must be after plannedStartAt")
    public boolean isValidTimeRange() {
        return plannedStartAt == null || plannedEndAt == null || !plannedEndAt.isBefore(plannedStartAt);
    }
}
