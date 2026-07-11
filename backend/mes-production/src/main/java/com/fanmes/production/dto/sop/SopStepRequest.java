package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SopStepRequest(
        @NotNull Integer stepNo,
        @NotBlank @Size(max = 128) String stepName,
        String instruction,
        @Size(max = 32) String contentType,
        Integer standardDuration,
        Boolean keyStep,
        Integer minStaySeconds,
        Boolean confirmRequired,
        Boolean parameterRequired,
        Boolean photoRequired,
        Boolean skipAllowed,
        @Size(max = 500) String abnormalHandling,
        Long qualityItemId,
        Long andonTypeId,
        Boolean enabled
) {
}
