package com.fanmes.quality.domain;

import java.math.BigDecimal;

public record QcPlanItem(
        Long id,
        Long planId,
        Long qcItemId,
        BigDecimal sampleQty,
        String standardValue,
        BigDecimal upperLimit,
        BigDecimal lowerLimit,
        Boolean requiredFlag
) {
}
