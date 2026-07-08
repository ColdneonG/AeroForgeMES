package com.fanmes.production.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShopTask(
        Long id,
        String taskNo,
        Long workOrderId,
        Long dispatchId,
        Long productId,
        Long routeId,
        Long lineId,
        Long teamId,
        BigDecimal planQty,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String status
) {
}
