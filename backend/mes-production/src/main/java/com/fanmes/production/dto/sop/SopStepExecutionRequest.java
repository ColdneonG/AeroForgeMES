package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.Size;

public record SopStepExecutionRequest(
        Long operatorId,
        String parameterJson,
        Long photoAttachmentId,
        @Size(max = 32) String result,
        @Size(max = 128) String idempotencyKey
) {
}
