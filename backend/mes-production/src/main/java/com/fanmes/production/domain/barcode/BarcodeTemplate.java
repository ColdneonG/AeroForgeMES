package com.fanmes.production.domain.barcode;

import java.math.BigDecimal;

public record BarcodeTemplate(
        Long id,
        String templateCode,
        String templateName,
        Long typeId,
        String typeCode,
        String typeName,
        String templateContent,
        BigDecimal paperWidth,
        BigDecimal paperHeight,
        String status
) {
}
