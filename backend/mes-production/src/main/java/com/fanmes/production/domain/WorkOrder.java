package com.fanmes.production.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkOrder(
        Long id,
        String workOrderNo,
        String externalNo,
        String sourceType,
        Long productId,
        BigDecimal planQty,
        BigDecimal completedQty,
        BigDecimal qualifiedQty,
        BigDecimal defectiveQty,
        Long unitId,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedEndAt,
        LocalDate deliveryDate,
        Long lineId,
        Long routeId,
        String status
) {
}
