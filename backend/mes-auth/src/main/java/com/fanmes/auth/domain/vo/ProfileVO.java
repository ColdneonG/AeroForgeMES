package com.fanmes.auth.domain.vo;

import java.util.Set;
import lombok.Data;

@Data
public class ProfileVO {
    private Long userId;
    private String username;
    private String displayName;
    private String mobile;
    private String employeeNo;
    private Long orgId;
    private String orgName;
    private Long teamId;
    private String teamName;
    private String status;
    private Set<String> roles;
    private Set<String> permissions;
}
