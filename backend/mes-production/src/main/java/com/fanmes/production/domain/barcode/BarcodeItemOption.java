package com.fanmes.production.domain.barcode;

public record BarcodeItemOption(
        Long id,
        String itemCode,
        String itemName,
        String itemType,
        String status
) {
}
