package com.fanmes.system.domain.entity;

import lombok.Data;

@Data
public class MasterTeam {
    private Long id;
    private String teamCode;
    private String teamName;
    private Long workshopId;
    private Long leaderId;
    private String status;
}
