package com.fanmes.production.domain.barcode;

public record BarcodeRule(
        Long id,
        String ruleCode,
        String ruleName,
        Long typeId,
        String typeCode,
        String typeName,
        String ruleExpression,
        Integer serialLength,
        String status,
        Long applicationRuleId,
        String applicationRuleCode,
        Long itemId,
        String itemCode,
        String itemName,
        Long templateId,
        String templateCode,
        String templateName,
        String barcodeMode,
        String sourceType
) {
}
