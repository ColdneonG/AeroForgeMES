package com.fanmes.production.domain.barcode;

import java.util.List;

public record BarcodeGenerationResult(
        int quantity,
        List<BarcodeRecord> barcodes
) {
}
