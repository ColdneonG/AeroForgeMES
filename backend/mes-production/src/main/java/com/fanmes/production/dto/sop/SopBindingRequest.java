package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SopBindingRequest(
        @NotBlank @Size(max = 32) String bindingType,
        Long productId,
        Long routeId,
        Long routeStepId,
        Long processId,
        Long workstationId,
        Long equipmentId,
        Long taskId,
        Integer priority,
        @Size(max = 32) String confirmMode,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        @Size(max = 32) String status
) {
}
