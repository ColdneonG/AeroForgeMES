package com.fanmes.quality.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record QcItemRequest(
        Long categoryId,
        @NotBlank @Size(max = 64) String itemCode,
        @NotBlank @Size(max = 128) String itemName,
        @Size(max = 32) String valueType,
        @Size(max = 128) String standardValue,
        BigDecimal upperLimit,
        BigDecimal lowerLimit,
        Long unitId,
        @Size(max = 32) String status
) {
    @AssertTrue(message = "upperLimit must be greater than or equal to lowerLimit")
    public boolean isValidRange() {
        return upperLimit == null || lowerLimit == null || upperLimit.compareTo(lowerLimit) >= 0;
    }
}
