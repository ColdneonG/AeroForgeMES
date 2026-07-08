package com.fanmes.quality.domain;

public record QcItemCategory(
        Long id,
        String categoryCode,
        String categoryName,
        String status
) {
}
