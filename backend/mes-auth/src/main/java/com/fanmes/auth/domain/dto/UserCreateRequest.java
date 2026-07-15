package com.fanmes.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "账号不能为空")
    @Size(max = 64, message = "账号不能超过 64 个字符")
    private String username;
    @NotBlank(message = "初始密码不能为空")
    @Size(min = 6, max = 128, message = "初始密码长度应为 6 至 128 个字符")
    private String password;
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 64, message = "显示名称不能超过 64 个字符")
    private String displayName;
    @Size(max = 32, message = "手机号不能超过 32 个字符")
    private String mobile;
    @Size(max = 64, message = "工号不能超过 64 个字符")
    private String employeeNo;
    private Long orgId;
    private Long teamId;
    private String status;
    private List<Long> roleIds;
}
