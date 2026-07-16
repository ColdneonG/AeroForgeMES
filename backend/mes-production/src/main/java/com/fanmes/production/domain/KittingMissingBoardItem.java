package com.fanmes.production.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Display model for the material-shortage board. */
public record KittingMissingBoardItem(
        Long id,
        Long analysisId,
        String workOrderNo,
        Long materialId,
        BigDecimal requiredQty,
        BigDecimal availableQty,
        BigDecimal missingQty,
        String status,
        LocalDateTime expectedArrivalAt
) {
}
