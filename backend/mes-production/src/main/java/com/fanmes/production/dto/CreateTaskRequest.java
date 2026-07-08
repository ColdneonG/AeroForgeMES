package com.fanmes.production.dto;

import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @Size(max = 64) String taskNo,
        Long operatorId,
        @Size(max = 500) String remark
) {
}
