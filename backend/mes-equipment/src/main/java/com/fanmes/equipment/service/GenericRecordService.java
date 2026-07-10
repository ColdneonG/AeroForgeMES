package com.fanmes.equipment.service;

import com.fanmes.equipment.domain.TableSpec;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GenericRecordService {
    private final JdbcExecutor jdbc;
    public GenericRecordService(JdbcExecutor jdbc) {
        this.jdbc = jdbc;
    }
    private String selectExpr(TableSpec spec) {
        return spec.selectExpression() != null ? spec.selectExpression() : "*";
    }

    private String from(TableSpec spec) {
        return spec.fromClause() != null ? spec.fromClause() : "from " + quote(spec.table());
    }

    public Map<String, Object> list(TableSpec spec, Map<String, String> params) {
        int page = parseInt(params.get("page"), 1);
        int size = Math.min(parseInt(params.get("size"), 20), 200);
        List<Object> args = new ArrayList<>();
        String where = whereClause(spec, params, args);
        String prefix = spec.columnPrefix() != null ? spec.columnPrefix() : "";
        String sql = "select " + selectExpr(spec) + " " + from(spec) + where + " order by " + prefix + "id desc limit ? offset ?";
        args.add(size);
        args.add((page - 1) * size);

        List<Map<String, Object>> rows = jdbc.query(sql, args);
        List<Object> countArgs = new ArrayList<>();
        String countWhere = whereClause(spec, params, countArgs);
        Number total = (Number) jdbc.query("select count(1) total " + from(spec) + countWhere, countArgs)
                .get(0)
                .get("total");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", rows);
        result.put("page", page);
        result.put("size", size);
        result.put("total", total.longValue());
        return result;
    }

    public Map<String, Object> get(TableSpec spec, long id) {
        String prefix = spec.columnPrefix() != null ? spec.columnPrefix() : "";
        List<Map<String, Object>> rows = jdbc.query(
                "select " + selectExpr(spec) + " " + from(spec) + " where " + prefix + "id = ?",
                List.of(id)
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("record not found: " + id);
        }
        return rows.get(0);
    }

    public Map<String, Object> create(TableSpec spec, Map<String, Object> body) {
        validateRequired(spec, body);
        Map<String, Object> values = writableValues(spec, body);
        values.putIfAbsent("id", nextId());
        if (spec.hasStatus() && StringUtils.hasText(spec.defaultStatus())) {
            values.putIfAbsent(spec.statusColumn(), spec.defaultStatus());
        }

        String columns = String.join(", ", values.keySet().stream().map(this::quote).toList());
        String placeholders = String.join(", ", values.keySet().stream().map(k -> "?").toList());
        jdbc.update("insert into " + quote(spec.table()) + " (" + columns + ") values (" + placeholders + ")",
                new ArrayList<>(values.values()));
        return get(spec, ((Number) values.get("id")).longValue());
    }

    public Map<String, Object> update(TableSpec spec, long id, Map<String, Object> body) {
        Map<String, Object> values = writableValues(spec, body);
        values.remove("id");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("no writable field supplied");
        }
        String setters = String.join(", ", values.keySet().stream().map(column -> quote(column) + " = ?").toList());
        List<Object> args = new ArrayList<>(values.values());
        args.add(id);
        int affected = jdbc.update("update " + quote(spec.table()) + " set " + setters + " where id = ?", args);
        if (affected == 0) {
            throw new IllegalArgumentException("record not found: " + id);
        }
        return get(spec, id);
    }

    public Map<String, Object> changeStatus(TableSpec spec, long id, String status) {
        if (!spec.hasStatus()) {
            throw new IllegalArgumentException("resource has no status column");
        }
        int affected = jdbc.update(
                "update " + quote(spec.table()) + " set " + quote(spec.statusColumn()) + " = ? where id = ?",
                List.of(status, id)
        );
        if (affected == 0) {
            throw new IllegalArgumentException("record not found: " + id);
        }
        return get(spec, id);
    }

    public Map<String, Object> changeEquipmentRunningStatus(long id, String equipmentStatus) {
        int affected = jdbc.update(
                "update eqp_equipment set equipment_status = ? where id = ?",
                List.of(equipmentStatus, id)
        );
        if (affected == 0) {
            throw new IllegalArgumentException("equipment not found: " + id);
        }
        return jdbc.query("select * from eqp_equipment where id = ?", List.of(id)).get(0);
    }

    private String whereClause(TableSpec spec, Map<String, String> params, List<Object> args) {
        Set<String> allowed = spec.filterSet();
        List<String> parts = new ArrayList<>();
        String prefix = spec.columnPrefix() != null ? spec.columnPrefix() : "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (allowed.contains(entry.getKey()) && StringUtils.hasText(entry.getValue())) {
                parts.add(prefix + quote(entry.getKey()) + " = ?");
                args.add(entry.getValue());
            }
        }
        return parts.isEmpty() ? "" : " where " + String.join(" and ", parts);
    }

    private Map<String, Object> writableValues(TableSpec spec, Map<String, Object> body) {
        Set<String> allowed = spec.writableSet();
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            if (allowed.contains(entry.getKey())) {
                values.put(entry.getKey(), entry.getValue());
            }
        }
        return values;
    }

    private void validateRequired(TableSpec spec, Map<String, Object> body) {
        for (String column : spec.requiredColumns()) {
            Object value = body.get(column);
            if (value == null || !StringUtils.hasText(String.valueOf(value))) {
                throw new IllegalArgumentException("missing required field: " + column);
            }
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private long nextId() {
        return Instant.now().toEpochMilli() * 1000 + (long) (Math.random() * 1000);
    }

    private String quote(String value) {
        return "`" + UriComponentsBuilder.fromPath(value).build().getPath().replace("`", "") + "`";
    }
}
