package com.fanmes.auth.domain.vo;

import java.util.Set;
import lombok.Data;

@Data
public class LoginVO {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String displayName;
    private Set<String> roles;
    private Set<String> permissions;
}
