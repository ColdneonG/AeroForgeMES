package com.fanmes.auth.domain.vo;

import java.util.List;
import lombok.Data;

@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String displayName;
    private String mobile;
    private String employeeNo;
    private Long orgId;
    private String orgName;
    private Long teamId;
    private String teamName;
    private String status;
    private List<RoleVO> roles;
}
