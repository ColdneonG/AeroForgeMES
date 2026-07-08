package com.fanmes.report.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MetricSnapshot {
    private Long id;
    private Long metricId;
    private String statDimension;
    private Long dimensionId;
    private String statPeriod;
    private LocalDateTime statTime;
    private BigDecimal metricValue;
}
