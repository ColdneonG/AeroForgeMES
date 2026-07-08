package com.fanmes.system.domain.entity;

import lombok.Data;

@Data
public class SequenceRule {
    private Long id;
    private String ruleCode;
    private String bizType;
    private String prefix;
    private String datePattern;
    private Integer serialLength;
    private String resetCycle;
    private String status;
}
