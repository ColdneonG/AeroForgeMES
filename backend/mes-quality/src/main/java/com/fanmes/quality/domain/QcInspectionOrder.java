package com.fanmes.quality.domain;

import java.time.LocalDateTime;

public record QcInspectionOrder(
        Long id,
        String inspectionNo,
        String inspectionType,
        Long planId,
        Long workOrderId,
        Long taskId,
        Long operationTaskId,
        Long productId,
        Long barcodeId,
        Long inspectorId,
        LocalDateTime inspectionAt,
        String finalResult,
        String status
) {
}
