package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SopStatusActionRequest(
        Long operatorId,
        LocalDateTime effectiveFrom,
        @Size(max = 500) String remark
) {
}
