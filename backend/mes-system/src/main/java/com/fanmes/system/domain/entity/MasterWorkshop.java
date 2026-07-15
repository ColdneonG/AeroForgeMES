package com.fanmes.system.domain.entity;

import lombok.Data;

@Data
public class MasterWorkshop {
    private Long id;
    private String workshopCode;
    private String workshopName;
    private Long orgId;
    private String status;
}
