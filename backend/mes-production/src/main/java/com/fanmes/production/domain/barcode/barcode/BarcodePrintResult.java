package com.fanmes.production.domain.barcode;

public record BarcodePrintResult(
        BarcodeRecord barcode,
        BarcodePrintRecord printRecord,
        BarcodeTemplate template
) {
}
