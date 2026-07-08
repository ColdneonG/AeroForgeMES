package com.fanmes.report.domain.entity;

import lombok.Data;

@Data
public class MetricDef {
    private Long id;
    private String metricCode;
    private String metricName;
    private String metricType;
    private String calcExpression;
    private String status;
}
