package com.fanmes.production.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record KittingAnalysisRequest(
        @NotBlank @Size(max = 64) String analysisNo,
        Long workOrderId,
        Long taskId,
        LocalDateTime analysisTime,
        @Size(max = 32) String kittingStatus,
        @Min(0) Integer missingCount,
        @Size(max = 32) String status
) {
}
