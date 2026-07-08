package com.fanmes.production.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DispatchOrder(
        Long id,
        String dispatchNo,
        Long workOrderId,
        Long lineId,
        Long stationId,
        Long teamId,
        BigDecimal planQty,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedEndAt,
        String status
) {
}
