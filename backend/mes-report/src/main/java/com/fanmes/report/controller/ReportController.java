package com.fanmes.report.controller;

import com.fanmes.common.api.Result;
import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/metrics")
    public Result<List<MetricDef>> metricDefs(@RequestParam(required = false) String metricType) {
        return Result.success(reportService.metricDefs(metricType));
    }

    @GetMapping("/metric-snapshots")
    public Result<List<MetricSnapshot>> metricSnapshots(MetricQuery query) {
        return Result.success(reportService.metricSnapshots(query));
    }

    @GetMapping("/boards")
    public Result<List<BoardConfig>> boards(@RequestParam(required = false) String boardType) {
        return Result.success(reportService.boards(boardType));
    }

    @GetMapping("/production-output")
    public Result<List<MetricSnapshot>> productionOutput(MetricQuery query) {
        query.setMetricType("产量");
        return Result.success(reportService.metricSnapshots(query));
    }

    @GetMapping("/boards/line")
    public Result<List<BoardConfig>> lineBoards() {
        return Result.success(reportService.boards("产线"));
    }

    @GetMapping("/boards/workshop")
    public Result<List<BoardConfig>> workshopBoards() {
        return Result.success(reportService.boards("车间"));
    }
}
