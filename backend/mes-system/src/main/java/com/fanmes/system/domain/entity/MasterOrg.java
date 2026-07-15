package com.fanmes.system.domain.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MasterOrg {
    private Long id;
    private Long parentId;
    private String orgCode;
    private String orgName;
    private String orgType;
    private String status;
    private List<MasterOrg> children = new ArrayList<>();
}
