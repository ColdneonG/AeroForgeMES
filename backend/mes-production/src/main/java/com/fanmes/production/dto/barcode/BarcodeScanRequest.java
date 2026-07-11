package com.fanmes.production.dto.barcode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BarcodeScanRequest(
        @NotBlank @Size(max = 128) String barcodeValue,
        @Size(max = 64) String eventType,
        @Size(max = 64) String bizType,
        Long bizId,
        Long workOrderId,
        Long taskId,
        Long processId,
        Long stationId,
        Long deviceId,
        Long operatorId,
        @Size(max = 128) String parentBarcodeValue
) {
}
