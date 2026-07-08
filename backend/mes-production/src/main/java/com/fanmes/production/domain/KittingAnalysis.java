package com.fanmes.production.domain;

import java.time.LocalDateTime;

public record KittingAnalysis(
        Long id,
        String analysisNo,
        Long workOrderId,
        Long taskId,
        LocalDateTime analysisTime,
        String kittingStatus,
        Integer missingCount,
        String status
) {
}
