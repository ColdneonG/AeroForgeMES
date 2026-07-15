package com.fanmes.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 64, message = "显示名称不能超过 64 个字符")
    private String displayName;

    @Size(max = 32, message = "手机号不能超过 32 个字符")
    private String mobile;
}
