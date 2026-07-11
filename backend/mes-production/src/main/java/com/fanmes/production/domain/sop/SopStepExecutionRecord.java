package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopStepExecutionRecord(
        Long id,
        Long executionId,
        Long snapshotId,
        Long taskId,
        Long stepId,
        Integer stepNo,
        LocalDateTime viewStartedAt,
        LocalDateTime confirmedAt,
        Integer staySeconds,
        String parameterJson,
        Long photoAttachmentId,
        String result,
        Long operatorId,
        String idempotencyKey
) {
}
