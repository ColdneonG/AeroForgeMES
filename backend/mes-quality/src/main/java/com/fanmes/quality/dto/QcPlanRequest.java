package com.fanmes.quality.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QcPlanRequest(
        @NotBlank @Size(max = 64) String planCode,
        @NotBlank @Size(max = 128) String planName,
        Long productId,
        Long customerId,
        Boolean defaultFlag,
        @Size(max = 32) String status
) {
}
