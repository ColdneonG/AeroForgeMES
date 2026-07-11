package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BarcodeBatchPrintRequest(
        @NotEmpty @Size(max = 200) List<Long> barcodeIds,
        Long templateId,
        @Min(1) @Max(100) Integer copies,
        @Size(max = 128) String printerName,
        Long operatorId,
        @Size(max = 500) String reason
) {
    public BarcodePrintRequest toPrintRequest() {
        return new BarcodePrintRequest(templateId, copies, printerName, operatorId, reason);
    }
}
