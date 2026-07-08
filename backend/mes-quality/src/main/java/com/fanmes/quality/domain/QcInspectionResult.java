package com.fanmes.quality.domain;

public record QcInspectionResult(
        Long id,
        Long inspectionId,
        Long qcItemId,
        String measuredValue,
        String result,
        Long defectReasonId,
        String remark
) {
}
