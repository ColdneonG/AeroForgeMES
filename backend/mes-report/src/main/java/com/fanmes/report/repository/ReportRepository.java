package com.fanmes.report.repository;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import com.fanmes.report.domain.vo.DailyOutputReportVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportRepository {

    @Select("""
            <script>
            select id, metric_code, metric_name, metric_type, calc_expression, status
            from rpt_metric_def
            <where>
                <if test="metricType != null and metricType != ''">
                    metric_type = #{metricType}
                </if>
            </where>
            order by id
            </script>
            """)
    List<MetricDef> findMetricDefs(@Param("metricType") String metricType);

    @Select("""
            <script>
            select s.id, s.metric_id, s.stat_dimension, s.dimension_id,
                   s.stat_period, s.stat_time, s.metric_value
            from rpt_metric_snapshot s
            left join rpt_metric_def d on d.id = s.metric_id
            <where>
                <if test="metricType != null and metricType != ''">
                    and d.metric_type = #{metricType}
                </if>
                <if test="metricId != null">
                    and s.metric_id = #{metricId}
                </if>
                <if test="statDimension != null and statDimension != ''">
                    and s.stat_dimension = #{statDimension}
                </if>
                <if test="dimensionId != null">
                    and s.dimension_id = #{dimensionId}
                </if>
            </where>
            order by s.stat_time desc
            limit 100
            </script>
            """)
    List<MetricSnapshot> findMetricSnapshots(MetricQuery query);

    @Select("""
            select stat_date as statDate, line_code as lineCode, line_name as lineName,
                   work_order_no as workOrderNo, product_code as productCode,
                   product_name as productName, planned_qty as plannedQty,
                   reported_qty as reportedQty, qualified_qty as qualifiedQty,
                   defective_qty as defectiveQty, report_count as reportCount,
                   status
            from rpt_report_dataset
            where metric_code = 'OUTPUT_QTY_DAY'
            order by row_no
            limit 500
            """)
    List<DailyOutputReportVO> findDailyOutputReport();

    @Select("""
            select metric_code as metricCode, row_no as rowNo,
                   stat_date as statDate, stat_period as statPeriod,
                   dimension_type as dimensionType, dimension_code as dimensionCode,
                   dimension_name as dimensionName, line_code as lineCode, line_name as lineName,
                   work_order_no as workOrderNo, product_code as productCode, product_name as productName,
                   business_no as businessNo, business_type as businessType, reason_name as reasonName,
                   operator_name as operatorName, planned_qty as plannedQty, reported_qty as reportedQty,
                   qualified_qty as qualifiedQty, defective_qty as defectiveQty, report_count as reportCount,
                   availability, performance, quality_rate as qualityRate, oee,
                   total_qty as totalQty, total_amount as totalAmount,
                   started_at as startedAt, ended_at as endedAt, status, remark
            from rpt_report_dataset
            where metric_code = #{metricCode}
            order by row_no
            limit 500
            """)
    List<Map<String, Object>> findReportDataset(@Param("metricCode") String metricCode);

    @Select("""
            <script>
            select id, board_code, board_name, board_type, refresh_seconds, config_json, status
            from board_config
            <where>
                <if test="boardType != null and boardType != ''">
                    board_type = #{boardType}
                </if>
            </where>
            order by id
            </script>
            """)
    List<BoardConfig> findBoards(@Param("boardType") String boardType);

    @Select("""
            select line_id as lineId, line_code as lineCode, line_name as lineName,
                   batch_no as batchNo, work_order_no as workOrderNo, product_name as productName,
                   planned_qty as plannedQty, completed_qty as completedQty, good_qty as goodQty,
                   defect_qty as defectQty, oee, performance, output_trend as outputTrend,
                   running_order_count as runningOrderCount, active_flag as activeFlag
            from rpt_dashboard_line
            order by id
            """)
    List<Map<String, Object>> findManufacturingLines();

    @Select("""
            select line_id as lineId, output_trend as outputTrend
            from rpt_dashboard_line
            order by id
            """)
    List<Map<String, Object>> findLineOutputTrend();

    @Select("""
            select max(case when metric_key = 'availability' then metric_value end) as availability,
                   max(case when metric_key = 'performance' then metric_value end) as performance,
                   max(case when metric_key = 'quality' then metric_value end) as quality,
                   max(case when metric_key = 'oee' then metric_value end) as oee
            from rpt_dashboard_metric
            """)
    Map<String, Object> findDashboardOeeMetrics();

    @Select("""
            select material_code as materialCode, material_name as materialName, unit_name as unitName,
                   required_qty as requiredQty, actual_qty as actualQty, stock_status as stockStatus
            from rpt_dashboard_stock
            order by id
            """)
    List<Map<String, Object>> findDashboardStock();
}
