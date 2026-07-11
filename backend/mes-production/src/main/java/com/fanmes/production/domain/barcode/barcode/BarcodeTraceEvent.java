package com.fanmes.production.domain.barcode;

import java.time.LocalDateTime;

public record BarcodeTraceEvent(
        Long id,
        Long barcodeId,
        String barcode,
        String eventType,
        String bizType,
        Long bizId,
        Long workOrderId,
        String workOrderNo,
        Long taskId,
        String taskNo,
        Long processId,
        String processName,
        Long stationId,
        String stationName,
        Long deviceId,
        String equipmentName,
        Long operatorId,
        String operatorName,
        LocalDateTime eventAt,
        String result
) {
}
