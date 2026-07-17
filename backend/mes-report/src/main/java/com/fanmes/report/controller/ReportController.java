package com.fanmes.report.controller;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.domain.vo.DailyOutputReportVO;
import com.fanmes.report.domain.vo.ManufacturingDashboardVO;
import com.fanmes.report.domain.vo.ReportDatasetVO;
import com.fanmes.report.service.ReportService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/metrics")
    public ApiResponse<List<MetricDef>> metricDefs(@RequestParam(required = false) String metricType) {
        return ApiResponse.ok(reportService.metricDefs(metricType));
    }

    @GetMapping("/metric-snapshots")
    public ApiResponse<List<MetricSnapshot>> metricSnapshots(MetricQuery query) {
        return ApiResponse.ok(reportService.metricSnapshots(query));
    }

    @GetMapping("/boards")
    public ApiResponse<List<BoardConfig>> boards(@RequestParam(required = false) String boardType) {
        return ApiResponse.ok(reportService.boards(boardType));
    }

    @GetMapping("/metric-data/{metricCode}")
    public ApiResponse<ReportDatasetVO> metricData(@PathVariable String metricCode) {
        return ApiResponse.ok(reportService.reportDataset(metricCode));
    }

    @GetMapping("/dashboard/manufacturing")
    public ApiResponse<ManufacturingDashboardVO> manufacturingDashboard() {
        return ApiResponse.ok(reportService.manufacturingDashboard());
    }

    @GetMapping("/production-output")
    public ApiResponse<List<MetricSnapshot>> productionOutput(MetricQuery query) {
        query.setMetricType("产量");
        return ApiResponse.ok(reportService.metricSnapshots(query));
    }

    @GetMapping("/production-output/daily")
    public ApiResponse<List<DailyOutputReportVO>> dailyProductionOutput() {
        return ApiResponse.ok(reportService.dailyOutputReport());
    }

    @GetMapping("/boards/line")
    public ApiResponse<List<Map<String, Object>>> lineBoardData() {
        return ApiResponse.ok(reportService.lineBoard());
    }

    @GetMapping("/boards/workshop")
    public ApiResponse<List<Map<String, Object>>> workshopBoardData() {
        return ApiResponse.ok(reportService.workshopBoard());
    }

    @GetMapping("/boards/control-center")
    public ApiResponse<Map<String, Object>> controlCenterBoardData() {
        return ApiResponse.ok(reportService.controlCenterBoard());
    }

    @GetMapping("/boards/line-config")
    public ApiResponse<List<BoardConfig>> lineBoards() {
        return ApiResponse.ok(reportService.boards("产线"));
    }

    @GetMapping("/boards/workshop-config")
    public ApiResponse<List<BoardConfig>> workshopBoards() {
        return ApiResponse.ok(reportService.boards("车间"));
    }
}
