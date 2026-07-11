package com.fanmes.production.domain.sop;

import java.time.LocalDateTime;

public record SopBinding(
        Long id,
        Long versionId,
        String bindingType,
        Long productId,
        Long routeId,
        Long routeStepId,
        Long processId,
        Long workstationId,
        Long equipmentId,
        Long taskId,
        Integer priority,
        String confirmMode,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
