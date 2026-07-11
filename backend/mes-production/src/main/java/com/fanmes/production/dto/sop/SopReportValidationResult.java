package com.fanmes.production.dto.sop;

import java.util.List;

public record SopReportValidationResult(
        boolean allowed,
        Long taskId,
        Long snapshotId,
        int pendingRequiredSteps,
        List<String> messages
) {
}
