package com.fanmes.report.repository;

import com.fanmes.report.domain.dto.MetricQuery;
import com.fanmes.report.domain.entity.BoardConfig;
import com.fanmes.report.domain.entity.MetricDef;
import com.fanmes.report.domain.entity.MetricSnapshot;
import java.util.List;
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
}
