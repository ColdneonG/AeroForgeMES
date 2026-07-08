package com.fanmes.auth.domain.entity;

import lombok.Data;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String mobile;
    private String employeeNo;
    private Long orgId;
    private Long teamId;
    private String status;
}
