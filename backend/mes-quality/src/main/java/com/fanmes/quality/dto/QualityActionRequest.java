package com.fanmes.quality.dto;

import jakarta.validation.constraints.Size;

public record QualityActionRequest(
        Long operatorId,
        @Size(max = 500) String remark
) {
}
