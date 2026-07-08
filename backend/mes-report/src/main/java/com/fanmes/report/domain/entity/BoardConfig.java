package com.fanmes.report.domain.entity;

import lombok.Data;

@Data
public class BoardConfig {
    private Long id;
    private String boardCode;
    private String boardName;
    private String boardType;
    private Integer refreshSeconds;
    private String configJson;
    private String status;
}
