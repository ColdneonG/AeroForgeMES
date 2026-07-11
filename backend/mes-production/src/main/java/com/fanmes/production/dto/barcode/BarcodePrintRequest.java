package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record BarcodePrintRequest(
        Long templateId,
        @Min(1) @Max(100) Integer copies,
        @Size(max = 128) String printerName,
        Long operatorId,
        @Size(max = 500) String reason
) {
}
