package com.fanmes.quality.domain;

import java.math.BigDecimal;

public record QcItem(
        Long id,
        Long categoryId,
        String itemCode,
        String itemName,
        String valueType,
        String standardValue,
        BigDecimal upperLimit,
        BigDecimal lowerLimit,
        Long unitId,
        String status
) {
}
