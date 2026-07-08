package com.fanmes.report.service.impl;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.repository.ReportRepository;
import com.fanmes.report.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Override
    public List<MetricDef> metricDefs(String metricType) {
        return reportRepository.findMetricDefs(metricType);
    }

    @Override
    public List<MetricSnapshot> metricSnapshots(MetricQuery query) {
        return reportRepository.findMetricSnapshots(query);
    }

    @Override
    public List<BoardConfig> boards(String boardType) {
        return reportRepository.findBoards(boardType);
    }
}
