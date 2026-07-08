package com.fanmes.quality.domain;

public record QcPlan(
        Long id,
        String planCode,
        String planName,
        Long productId,
        Long customerId,
        Boolean defaultFlag,
        String status
) {
}
