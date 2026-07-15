package com.fanmes.auth.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserRoleAssignRequest {
    private List<Long> roleIds;
}
