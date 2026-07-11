package com.fanmes.production.domain.barcode;

public record BarcodeType(
        Long id,
        String typeCode,
        String typeName,
        String uniqueScope,
        String status
) {
}
