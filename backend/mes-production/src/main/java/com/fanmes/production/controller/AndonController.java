package com.fanmes.production.controller;

import com.fanmes.common.api.ApiResponse;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/andon")
public class AndonController {
    private final NamedParameterJdbcTemplate jdbc;

    public AndonController(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/exceptions")
    public ApiResponse<List<Map<String, Object>>> listExceptions() {
        String sql = """
                select
                    e.id, e.exception_no as andonNo, e.type_id as typeId, e.reason_id as reasonId,
                    e.work_order_id as workOrderId, e.task_id as taskId, e.operation_task_id as operationTaskId,
                    e.line_id as lineId, e.station_id as stationId, e.equipment_id as equipmentId,
                    e.reported_by as reportedBy, e.reported_at as triggeredAt,
                    e.assigned_to as assignedTo, e.closed_by as closedBy, e.closed_at as closedAt,
                    e.status,
                    t.type_name as exceptionType, r.reason_name as reasonName
                from andon_exception_order e
                left join andon_type t on e.type_id = t.id
                left join andon_reason r on e.reason_id = r.id
                order by e.id desc
                """;
        return ApiResponse.ok(jdbc.queryForList(sql, Map.of()));
    }
}
