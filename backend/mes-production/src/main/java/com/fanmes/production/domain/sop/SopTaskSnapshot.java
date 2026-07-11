package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopTaskSnapshot(
        Long id,
        Long taskId,
        Long sopId,
        Long versionId,
        String versionNo,
        Long modelId,
        Long modelVersionId,
        String modelSha256,
        String snapshotJson,
        Long lockedBy,
        LocalDateTime lockedAt,
        String matchRule,
        String status
) {
}
