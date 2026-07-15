package com.fanmes.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {
    @NotBlank(message = "账号状态不能为空")
    private String status;
}
