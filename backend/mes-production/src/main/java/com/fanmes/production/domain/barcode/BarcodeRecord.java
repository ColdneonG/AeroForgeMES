package com.fanmes.production.domain.barcode;

import java.time.LocalDateTime;

public record BarcodeRecord(
        Long id,
        String barcodeValue,
        Long typeId,
        String typeCode,
        String typeName,
        Long applicationRuleId,
        String applicationRuleCode,
        Long itemId,
        String itemCode,
        String itemName,
        String batchNo,
        Long workOrderId,
        String workOrderNo,
        Long taskId,
        String taskNo,
        Long parentBarcodeId,
        String parentBarcodeValue,
        Integer printCount,
        String sourceType,
        String status,
        LocalDateTime generatedAt
) {
}
