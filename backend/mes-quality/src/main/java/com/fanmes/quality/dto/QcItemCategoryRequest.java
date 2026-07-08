package com.fanmes.quality.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QcItemCategoryRequest(
        @NotBlank @Size(max = 64) String categoryCode,
        @NotBlank @Size(max = 128) String categoryName,
        @Size(max = 32) String status
) {
}
