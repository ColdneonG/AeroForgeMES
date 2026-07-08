package com.fanmes.quality.domain;

import java.math.BigDecimal;

public record QcDefectRecord(
        Long id,
        String sourceType,
        Long sourceId,
        Long productId,
        Long barcodeId,
        Long processId,
        Long defectReasonId,
        BigDecimal defectQty,
        String handleMethod,
        Long reworkOrderId,
        String status
) {
}
