package com.fanmes.production.dto.sop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SopDocumentRequest(
        @NotBlank @Size(max = 64) String sopCode,
        @NotBlank @Size(max = 128) String sopName,
        @Size(max = 64) String category,
        Long ownerId,
        @Size(max = 32) String status
) {
}
