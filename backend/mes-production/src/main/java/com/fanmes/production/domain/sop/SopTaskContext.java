package com.fanmes.production.domain.sop;

public record SopTaskContext(
        Long taskId,
        String taskNo,
        Long workOrderId,
        Long productId,
        Long routeId,
        Long lineId,
        Long workstationId,
        String status
) {
}
