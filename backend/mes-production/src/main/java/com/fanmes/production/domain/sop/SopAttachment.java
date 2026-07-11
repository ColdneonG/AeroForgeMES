package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopAttachment(
        Long id,
        Long versionId,
        Long stepId,
        String attachmentType,
        String fileName,
        String contentType,
        Long fileSize,
        String objectKey,
        String fileUrl,
        String sha256,
        Integer displayOrder,
        LocalDateTime createdAt
) {
}
