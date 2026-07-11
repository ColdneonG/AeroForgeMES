package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopModel(
        Long id,
        String modelCode,
        String modelName,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
