package com.fanmes.report.domain.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManufacturingDashboardVO {
    private List<ManufacturingLineVO> lines;
    private List<DashboardMetricVO> gauges;
    private List<DashboardStockVO> stock;
}
