package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopDocument(
        Long id,
        String sopCode,
        String sopName,
        String category,
        Long ownerId,
        String status,
        Long currentVersionId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
