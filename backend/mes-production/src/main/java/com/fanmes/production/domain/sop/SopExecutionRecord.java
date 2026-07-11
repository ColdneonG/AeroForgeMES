package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopExecutionRecord(
        Long id,
        Long snapshotId,
        Long taskId,
        Long sopId,
        Long versionId,
        Long operatorId,
        LocalDateTime openedAt,
        LocalDateTime completedAt,
        String status
) {
}
