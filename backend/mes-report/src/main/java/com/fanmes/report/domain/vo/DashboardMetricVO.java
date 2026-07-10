package com.fanmes.report.domain.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardMetricVO {
    private String metricKey;
    private BigDecimal value;
}
