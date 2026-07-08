package com.fanmes.quality.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record QcInspectionOrderRequest(
        @NotBlank @Size(max = 64) String inspectionNo,
        @Size(max = 32) String inspectionType,
        Long planId,
        Long workOrderId,
        Long taskId,
        Long operationTaskId,
        Long productId,
        Long barcodeId,
        Long inspectorId,
        LocalDateTime inspectionAt,
        @Size(max = 32) String finalResult,
        @Size(max = 32) String status
) {
}
