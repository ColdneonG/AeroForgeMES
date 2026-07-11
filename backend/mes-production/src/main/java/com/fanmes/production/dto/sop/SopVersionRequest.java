package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SopVersionRequest(
        @Size(max = 32) String versionNo,
        @Size(max = 32) String revisionType,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        Long modelVersionId,
        Long operatorId,
        @Size(max = 500) String remark
) {
}
