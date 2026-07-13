package com.fanmes.production.domain;

import java.math.BigDecimal;

public record OperationProgress(
        Long operationTaskId,
        String operationTaskNo,
        Long taskId,
        String taskNo,
        Integer stepNo,
        Long processId,
        String processCode,
        String processName,
        Long stationId,
        BigDecimal planQty,
        BigDecimal reportedQty,
        BigDecimal qualifiedQty,
        BigDecimal defectiveQty,
        String status
) {
}
