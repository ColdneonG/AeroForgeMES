package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopStep(
        Long id,
        Long versionId,
        Integer stepNo,
        String stepName,
        String instruction,
        String contentType,
        Integer standardDuration,
        Boolean keyStep,
        Integer minStaySeconds,
        Boolean confirmRequired,
        Boolean parameterRequired,
        Boolean photoRequired,
        Boolean skipAllowed,
        String abnormalHandling,
        Long qualityItemId,
        Long andonTypeId,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
