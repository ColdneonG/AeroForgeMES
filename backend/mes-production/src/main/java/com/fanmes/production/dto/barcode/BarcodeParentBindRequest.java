package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BarcodeParentBindRequest(
        @NotBlank @Size(max = 128) String parentBarcodeValue,
        Long operatorId,
        @Size(max = 500) String remark
) {
}
