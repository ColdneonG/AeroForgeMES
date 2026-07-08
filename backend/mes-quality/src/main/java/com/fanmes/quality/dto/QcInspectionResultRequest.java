package com.fanmes.quality.dto;

import jakarta.validation.constraints.Size;

public record QcInspectionResultRequest(
        Long inspectionId,
        Long qcItemId,
        @Size(max = 128) String measuredValue,
        @Size(max = 32) String result,
        Long defectReasonId,
        @Size(max = 500) String remark
) {
}
