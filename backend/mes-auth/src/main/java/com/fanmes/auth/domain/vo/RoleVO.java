package com.fanmes.auth.domain.vo;

import lombok.Data;

@Data
public class RoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String status;
}
