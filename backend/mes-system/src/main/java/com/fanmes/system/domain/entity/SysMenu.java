package com.fanmes.system.domain.entity;

import lombok.Data;

@Data
public class SysMenu {
    private Long id;
    private Long parentId;
    private String menuCode;
    private String menuName;
    private String moduleCode;
    private String path;
    private Integer sortNo;
}
