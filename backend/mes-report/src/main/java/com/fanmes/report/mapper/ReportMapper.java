package com.fanmes.report.mapper;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.domain.vo.DailyOutputReportVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {
    List<MetricDef> findMetricDefs(@Param("metricType") String metricType);
    List<MetricSnapshot> findMetricSnapshots(MetricQuery query);
    List<DailyOutputReportVO> findDailyOutputReport();
    List<Map<String, Object>> findReportDataset(@Param("metricCode") String metricCode);
    List<BoardConfig> findBoards(@Param("boardType") String boardType);
    List<Map<String, Object>> findManufacturingLines();
    List<Map<String, Object>> findLineOutputTrend();
    Map<String, Object> findDashboardOeeMetrics();
    List<Map<String, Object>> findDashboardStock();
}
