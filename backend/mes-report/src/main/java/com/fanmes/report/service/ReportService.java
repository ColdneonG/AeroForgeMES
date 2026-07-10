package com.fanmes.report.service;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.domain.vo.DailyOutputReportVO;
import com.fanmes.report.domain.vo.ManufacturingDashboardVO;
import com.fanmes.report.domain.vo.ReportDatasetVO;
import java.util.List;
import java.util.Map;

public interface ReportService {
    List<MetricDef> metricDefs(String metricType);
    List<MetricSnapshot> metricSnapshots(MetricQuery query);
    List<BoardConfig> boards(String boardType);
    List<DailyOutputReportVO> dailyOutputReport();
    ReportDatasetVO reportDataset(String metricCode);
    ManufacturingDashboardVO manufacturingDashboard();
    List<Map<String, Object>> lineBoard();
    List<Map<String, Object>> workshopBoard();
    Map<String, Object> controlCenterBoard();
}
