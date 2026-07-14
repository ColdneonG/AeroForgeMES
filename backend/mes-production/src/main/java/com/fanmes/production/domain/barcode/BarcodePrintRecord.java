package com.fanmes.production.domain.barcode;

import java.time.LocalDateTime;

public record BarcodePrintRecord(
        Long id,
        Long barcodeId,
        String barcodeValue,
        Long templateId,
        String templateName,
        Long printBy,
        String printByName,
        LocalDateTime printAt,
        Integer printCount,
        String printerName
) {
}
