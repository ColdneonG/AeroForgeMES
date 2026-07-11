package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopModelVersion(
        Long id,
        Long modelId,
        String versionNo,
        String fileName,
        String objectKey,
        String fileUrl,
        String sha256,
        Long fileSize,
        String status,
        LocalDateTime createdAt
) {
}
