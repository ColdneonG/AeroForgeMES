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
                    e.id, e.exception_no, e.type_id, e.reason_id,
                    e.work_order_id, e.task_id, e.operation_task_id,
                    e.line_id, e.station_id, e.equipment_id,
                    e.reported_by, e.reported_at,
                    e.assigned_to, e.closed_by, e.closed_at,
                    e.status,
                    t.type_name, r.reason_name
                from andon_exception_order e
                left join andon_type t on e.type_id = t.id
                left join andon_reason r on e.reason_id = r.id
                order by e.id desc
                """;
        return ApiResponse.ok(jdbc.queryForList(sql, Map.of()));
    }
}
