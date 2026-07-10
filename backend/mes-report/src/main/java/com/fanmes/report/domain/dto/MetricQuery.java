package com.fanmes.report.domain.dto;

import lombok.Data;

@Data
public class MetricQuery {
    private Long metricId;
    private String metricType;
    private String statDimension;
    private Long dimensionId;
}
