package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopVersion(
        Long id,
        Long sopId,
        String versionNo,
        String revisionType,
        String status,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        Long submitBy,
        LocalDateTime submitAt,
        Long reviewBy,
        LocalDateTime reviewAt,
        Long approveBy,
        LocalDateTime approveAt,
        Long publishBy,
        LocalDateTime publishAt,
        Long modelVersionId,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
