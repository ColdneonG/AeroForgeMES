package com.fanmes.production.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KittingMissingItem(
        Long id,
        Long analysisId,
        Long materialId,
        BigDecimal requiredQty,
        BigDecimal availableQty,
        BigDecimal missingQty,
        LocalDateTime expectedArrivalAt
) {
}
