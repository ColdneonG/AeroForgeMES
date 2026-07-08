package com.fanmes.integration.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntegrationSyncService {
    private final JdbcExecutor jdbc;
    private final GenericRecordService records;

    public IntegrationSyncService(JdbcExecutor jdbc, GenericRecordService records) {
        this.jdbc = jdbc;
        this.records = records;
    }

    public Map<String, Object> accept(String interfaceCode, String direction, String bizType, Map<String, Object> body) {
        Long systemId = longValue(body.get("system_id"), body.get("systemId"));
        String externalNo = stringValue(body.get("external_no"), body.get("externalNo"));
        String idempotentKey = stringValue(body.get("idempotent_key"), body.get("idempotentKey"));

        if (StringUtils.hasText(idempotentKey)) {
            List<Map<String, Object>> existing = findByIdempotent(systemId, interfaceCode, idempotentKey);
            if (!existing.isEmpty()) {
                Map<String, Object> duplicate = new LinkedHashMap<>(existing.get(0));
                duplicate.put("duplicate", true);
                return duplicate;
            }
        }

        long id = records.nextId();
        String requestPayload = json(body);
        String responsePayload = json(Map.of("accepted", true, "interfaceCode", interfaceCode, "bizType", bizType));
        jdbc.update("""
                insert into int_sync_log
                (id, system_id, interface_code, direction, biz_type, external_no, idempotent_key,
                 request_payload, response_payload, sync_status, retry_count, started_at, finished_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, Arrays.asList(id, systemId, interfaceCode, direction, bizType, externalNo, idempotentKey,
                requestPayload, responsePayload, "SUCCESS", 0, LocalDateTime.now(), LocalDateTime.now()));
        jdbc.update("""
                insert into int_request_log
                (id, sync_log_id, method, url, headers_json, body, requested_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """, List.of(records.nextId(), id, "POST", interfaceCode, "{}", requestPayload, LocalDateTime.now()));
        jdbc.update("""
                insert into int_response_log
                (id, sync_log_id, http_status, body, responded_at, elapsed_ms)
                values (?, ?, ?, ?, ?, ?)
                """, List.of(records.nextId(), id, 200, responsePayload, LocalDateTime.now(), 0));
        return jdbc.query("select * from int_sync_log where id = ?", List.of(id)).get(0);
    }

    public Map<String, Object> retry(long syncLogId) {
        int affected = jdbc.update("""
                update int_sync_log
                   set sync_status = ?, retry_count = coalesce(retry_count, 0) + 1,
                       error_message = null, started_at = ?, finished_at = ?
                 where id = ?
                """, List.of("SUCCESS", LocalDateTime.now(), LocalDateTime.now(), syncLogId));
        if (affected == 0) {
            throw new IllegalArgumentException("sync log not found: " + syncLogId);
        }
        return jdbc.query("select * from int_sync_log where id = ?", List.of(syncLogId)).get(0);
    }

    private List<Map<String, Object>> findByIdempotent(Long systemId, String interfaceCode, String idempotentKey) {
        if (systemId == null) {
            return jdbc.query("""
                    select * from int_sync_log
                     where system_id is null and interface_code = ? and idempotent_key = ?
                     order by id desc limit 1
                    """, List.of(interfaceCode, idempotentKey));
        }
        return jdbc.query("""
                select * from int_sync_log
                 where system_id = ? and interface_code = ? and idempotent_key = ?
                 order by id desc limit 1
                """, List.of(systemId, interfaceCode, idempotentKey));
    }

    private Long longValue(Object primary, Object fallback) {
        Object value = primary != null ? primary : fallback;
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            return null;
        }
        return Long.parseLong(String.valueOf(value));
    }

    private String stringValue(Object primary, Object fallback) {
        Object value = primary != null ? primary : fallback;
        return value == null ? null : String.valueOf(value);
    }

    private String json(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .map(entry -> json(String.valueOf(entry.getKey())) + ":" + json(entry.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(this::json).collect(Collectors.joining(",", "[", "]"));
        }
        return "\"" + String.valueOf(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n") + "\"";
    }
}
