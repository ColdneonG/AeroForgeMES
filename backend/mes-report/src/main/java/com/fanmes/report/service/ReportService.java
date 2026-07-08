package com.fanmes.report.service;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import java.util.List;

public interface ReportService {
    List<MetricDef> metricDefs(String metricType);
    List<MetricSnapshot> metricSnapshots(MetricQuery query);
    List<BoardConfig> boards(String boardType);
}
