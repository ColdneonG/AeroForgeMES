package com.fanmes.production.domain.barcode;

public record BarcodeTraceNode(
        String id,
        String parentId,
        String barcode,
        String title,
        String relation,
        String type,
        String status
) {
}
