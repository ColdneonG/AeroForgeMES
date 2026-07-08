package com.fanmes.production.dto;

import jakarta.validation.constraints.Size;

public record ProductionActionRequest(
        Long operatorId,
        @Size(max = 500) String remark
) {
}
