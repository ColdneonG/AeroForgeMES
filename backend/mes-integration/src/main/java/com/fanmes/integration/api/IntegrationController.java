package com.fanmes.integration.api;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.integration.domain.TableSpec;
import com.fanmes.integration.service.GenericRecordService;
import com.fanmes.integration.service.IntegrationSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class IntegrationController {
    private final GenericRecordService records;
    private final IntegrationSyncService syncService;
    private final Map<String, TableSpec> specs = specs();
    private final Map<String, String> legacyInterface = Map.of(
            "func-59", "ERP_PRODUCTION_TASK_READ",
            "func-60", "ERP_ROUTE_READ",
            "func-61", "OPENAPI_UNIT_WRITE",
            "func-62", "OPENAPI_WORK_ORDER_WRITE",
            "func-63", "OPENAPI_PRODUCTION_TASK_WRITE",
            "func-64", "OPENAPI_DEVICE_COUNT_REPORT_WRITE",
            "func-65", "OPENAPI_COMPLETION_ORDER_READ"
    );

    public IntegrationController(GenericRecordService records, IntegrationSyncService syncService) {
        this.records = records;
        this.syncService = syncService;
    }

    @GetMapping("/api/integration/{resource}")
    public ApiResponse<Map<String, Object>> list(@PathVariable String resource, @RequestParam Map<String, String> params) {
        return ApiResponse.ok(records.list(spec(resource), params));
    }

    @GetMapping("/api/integration/{resource}/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String resource, @PathVariable long id) {
        return ApiResponse.ok(records.get(spec(resource), id));
    }

    @PostMapping("/api/integration/{resource}")
    public ApiResponse<Map<String, Object>> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(records.create(spec(resource), body));
    }

    @PutMapping("/api/integration/{resource}/{id}")
    public ApiResponse<Map<String, Object>> update(
            @PathVariable String resource,
            @PathVariable long id,
            @RequestBody Map<String, Object> body
    ) {
        return ApiResponse.ok(records.update(spec(resource), id, body));
    }

    @PostMapping("/api/integration/{resource}/{id}/{action}")
    public ApiResponse<Map<String, Object>> action(
            @PathVariable String resource,
            @PathVariable long id,
            @PathVariable String action
    ) {
        if ("retry".equals(action) && "sync-logs".equals(resource)) {
            return ApiResponse.ok(syncService.retry(id));
        }
        String status = switch (action) {
            case "enable" -> "ENABLED";
            case "disable" -> "DISABLED";
            case "success", "confirm", "complete" -> "SUCCESS";
            case "fail" -> "FAILED";
            case "close" -> "CLOSED";
            case "cancel" -> "CANCELLED";
            default -> throw new IllegalArgumentException("unsupported action: " + action);
        };
        return ApiResponse.ok(records.changeStatus(spec(resource), id, status));
    }

    @PostMapping("/api/integration/erp/production-tasks/read")
    public ApiResponse<Map<String, Object>> readProductionTasks(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("ERP_PRODUCTION_TASK_READ", "IN", "PRODUCTION_TASK", body));
    }

    @PostMapping("/api/integration/erp/routes/read")
    public ApiResponse<Map<String, Object>> readRoutes(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("ERP_ROUTE_READ", "IN", "ROUTE", body));
    }

    @PostMapping("/api/openapi/units/write")
    public ApiResponse<Map<String, Object>> writeUnit(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("OPENAPI_UNIT_WRITE", "IN", "UNIT", body));
    }

    @PostMapping("/api/openapi/work-orders/write")
    public ApiResponse<Map<String, Object>> writeWorkOrder(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("OPENAPI_WORK_ORDER_WRITE", "IN", "WORK_ORDER", body));
    }

    @PostMapping("/api/openapi/production-tasks/write")
    public ApiResponse<Map<String, Object>> writeProductionTask(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("OPENAPI_PRODUCTION_TASK_WRITE", "IN", "PRODUCTION_TASK", body));
    }

    @PostMapping("/api/openapi/device-count-reports/write")
    public ApiResponse<Map<String, Object>> writeDeviceCountReport(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("OPENAPI_DEVICE_COUNT_REPORT_WRITE", "IN", "DEVICE_COUNT_REPORT", body));
    }

    @PostMapping("/api/openapi/completion-orders/read")
    public ApiResponse<Map<String, Object>> readCompletionOrder(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(syncService.accept("OPENAPI_COMPLETION_ORDER_READ", "OUT", "COMPLETION_ORDER", body));
    }

    @GetMapping("/api/mes/{func}")
    public ApiResponse<Map<String, Object>> legacyList(@PathVariable String func, @RequestParam Map<String, String> params) {
        Map<String, String> enriched = new LinkedHashMap<>(params);
        enriched.put("interface_code", legacyCode(func));
        return list("sync-logs", enriched);
    }

    @PostMapping("/api/mes/{func}")
    public ApiResponse<Map<String, Object>> legacyWrite(@PathVariable String func, @RequestBody Map<String, Object> body) {
        String code = legacyCode(func);
        String bizType = switch (func) {
            case "func-59", "func-63" -> "PRODUCTION_TASK";
            case "func-60" -> "ROUTE";
            case "func-61" -> "UNIT";
            case "func-62" -> "WORK_ORDER";
            case "func-64" -> "DEVICE_COUNT_REPORT";
            case "func-65" -> "COMPLETION_ORDER";
            default -> "STANDARD_API";
        };
        String direction = "func-65".equals(func) ? "OUT" : "IN";
        return ApiResponse.ok(syncService.accept(code, direction, bizType, body));
    }

    @GetMapping("/api/integration/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of("service", "mes-integration", "status", "UP"));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Object>> handle(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    private String legacyCode(String func) {
        String code = legacyInterface.get(func);
        if (code == null) {
            throw new IllegalArgumentException("unknown legacy function: " + func);
        }
        return code;
    }

    private TableSpec spec(String resource) {
        TableSpec spec = specs.get(resource);
        if (spec == null) {
            throw new IllegalArgumentException("unknown integration resource: " + resource);
        }
        return spec;
    }

    private Map<String, TableSpec> specs() {
        Map<String, TableSpec> map = new LinkedHashMap<>();
        map.put("external-systems", new TableSpec("external-systems", "int_external_system",
                List.of("id", "system_code", "system_name", "auth_type", "status"),
                List.of("system_code", "system_name"),
                List.of("system_code", "system_name", "auth_type", "status"),
                "status", "ENABLED"));
        map.put("sync-logs", new TableSpec("sync-logs", "int_sync_log",
                List.of("id", "system_id", "interface_code", "direction", "biz_type", "biz_id", "external_no", "idempotent_key", "request_payload", "response_payload", "sync_status", "retry_count", "error_message", "started_at", "finished_at"),
                List.of(),
                List.of("system_id", "interface_code", "direction", "biz_type", "biz_id", "external_no", "idempotent_key", "sync_status"),
                "sync_status", "PENDING"));
        map.put("request-logs", new TableSpec("request-logs", "int_request_log",
                List.of("id", "sync_log_id", "method", "url", "headers_json", "body", "requested_at"),
                List.of(),
                List.of("sync_log_id", "method", "url"),
                null, null));
        map.put("response-logs", new TableSpec("response-logs", "int_response_log",
                List.of("id", "sync_log_id", "http_status", "body", "responded_at", "elapsed_ms"),
                List.of(),
                List.of("sync_log_id", "http_status"),
                null, null));
        return map;
    }
}
