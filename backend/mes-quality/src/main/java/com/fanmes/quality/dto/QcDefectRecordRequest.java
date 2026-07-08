package com.fanmes.quality.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record QcDefectRecordRequest(
        @Size(max = 32) String sourceType,
        Long sourceId,
        Long productId,
        Long barcodeId,
        Long processId,
        Long defectReasonId,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal defectQty,
        @Size(max = 32) String handleMethod,
        Long reworkOrderId,
        @Size(max = 32) String status
) {
}
