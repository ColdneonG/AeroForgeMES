package com.fanmes.production.domain.barcode;

public record BarcodeApplicationRule(
        Long id,
        String applicationRuleCode,
        Long itemId,
        String itemCode,
        String itemName,
        Long typeId,
        String typeCode,
        String typeName,
        Long ruleId,
        String ruleCode,
        String ruleName,
        String ruleExpression,
        Integer serialLength,
        Long templateId,
        String templateCode,
        String templateName,
        String barcodeMode,
        String sourceType,
        String status
) {
}
