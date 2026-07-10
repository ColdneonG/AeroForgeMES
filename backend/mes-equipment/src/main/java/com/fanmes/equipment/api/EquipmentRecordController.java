package com.fanmes.equipment.api;

import com.fanmes.common.api.ApiResponse;
import com.fanmes.equipment.domain.TableSpec;
import com.fanmes.equipment.service.GenericRecordService;
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
public class EquipmentRecordController {
    private final GenericRecordService service;
    private final Map<String, TableSpec> specs = specs();
    private final Map<String, String> legacyResources = Map.ofEntries(
            Map.entry("func-20", "categories"),
            Map.entry("func-21", "manufacturers"),
            Map.entry("func-22", "fault-reasons"),
            Map.entry("func-23", "equipments"),
            Map.entry("func-24", "maintenance-tasks"),
            Map.entry("func-25", "inspections"),
            Map.entry("func-26", "repairs"),
            Map.entry("func-27", "equipments"),
            Map.entry("oee", "oee-snapshots"),
            Map.entry("func-29", "energy-records"),
            Map.entry("func-30", "count-reports")
    );

    public EquipmentRecordController(GenericRecordService service) {
        this.service = service;
    }

    @GetMapping("/api/equipment/{resource}")
    public ApiResponse<Map<String, Object>> list(@PathVariable String resource, @RequestParam Map<String, String> params) {
        return ApiResponse.ok(service.list(spec(resource), params));
    }

    @GetMapping("/api/equipment/{resource}/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String resource, @PathVariable long id) {
        return ApiResponse.ok(service.get(spec(resource), id));
    }

    @PostMapping("/api/equipment/{resource}")
    public ApiResponse<Map<String, Object>> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(service.create(spec(resource), body));
    }

    @PutMapping("/api/equipment/{resource}/{id}")
    public ApiResponse<Map<String, Object>> update(
            @PathVariable String resource,
            @PathVariable long id,
            @RequestBody Map<String, Object> body
    ) {
        return ApiResponse.ok(service.update(spec(resource), id, body));
    }

    @PostMapping("/api/equipment/{resource}/{id}/{action}")
    public ApiResponse<Map<String, Object>> action(
            @PathVariable String resource,
            @PathVariable long id,
            @PathVariable String action
    ) {
        String status = switch (action) {
            case "submit" -> "PROCESSING";
            case "confirm", "complete" -> "COMPLETED";
            case "close" -> "CLOSED";
            case "cancel" -> "CANCELLED";
            case "enable" -> "ENABLED";
            case "disable" -> "DISABLED";
            default -> throw new IllegalArgumentException("unsupported action: " + action);
        };
        return ApiResponse.ok(service.changeStatus(spec(resource), id, status));
    }

    @PostMapping("/api/equipment/equipments/{id}/running-status")
    public ApiResponse<Map<String, Object>> runningStatus(@PathVariable long id, @RequestBody Map<String, Object> body) {
        Object status = body.get("equipment_status");
        if (status == null) {
            status = body.get("equipmentStatus");
        }
        if (status == null) {
            throw new IllegalArgumentException("missing required field: equipment_status");
        }
        return ApiResponse.ok(service.changeEquipmentRunningStatus(id, String.valueOf(status)));
    }

    @GetMapping("/api/mes/{func}")
    public ApiResponse<Map<String, Object>> legacyList(@PathVariable String func, @RequestParam Map<String, String> params) {
        return list(legacyResource(func), params);
    }

    @PostMapping("/api/mes/{func}")
    public ApiResponse<Map<String, Object>> legacyCreate(@PathVariable String func, @RequestBody Map<String, Object> body) {
        return create(legacyResource(func), body);
    }

    @PutMapping("/api/mes/{func}/{id}")
    public ApiResponse<Map<String, Object>> legacyUpdate(
            @PathVariable String func,
            @PathVariable long id,
            @RequestBody Map<String, Object> body
    ) {
        return update(legacyResource(func), id, body);
    }

    @GetMapping("/api/equipment/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of("service", "mes-equipment", "status", "UP"));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Object>> handle(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    private TableSpec spec(String resource) {
        TableSpec spec = specs.get(resource);
        if (spec == null) {
            throw new IllegalArgumentException("unknown equipment resource: " + resource);
        }
        return spec;
    }

    private String legacyResource(String func) {
        String resource = legacyResources.get(func);
        if (resource == null) {
            throw new IllegalArgumentException("unknown legacy function: " + func);
        }
        return resource;
    }

    private Map<String, TableSpec> specs() {
        Map<String, TableSpec> map = new LinkedHashMap<>();
        map.put("categories", new TableSpec("categories", "eqp_category",
                List.of("id", "category_code", "category_name", "status"),
                List.of("category_code", "category_name"),
                List.of("category_code", "category_name", "status"),
                "status", "ENABLED"));
        map.put("manufacturers", new TableSpec("manufacturers", "eqp_manufacturer",
                List.of("id", "manufacturer_code", "manufacturer_name", "contact", "status"),
                List.of("manufacturer_code", "manufacturer_name"),
                List.of("manufacturer_code", "manufacturer_name", "status"),
                "status", "ENABLED"));
        map.put("fault-reasons", new TableSpec("fault-reasons", "eqp_fault_reason",
                List.of("id", "reason_code", "reason_name", "category_id", "status"),
                List.of("reason_code", "reason_name"),
                List.of("reason_code", "reason_name", "category_id", "status"),
                "status", "ENABLED"));
        map.put("equipments", new TableSpec("equipments", "eqp_equipment",
                List.of("id", "equipment_code", "equipment_name", "category_id", "manufacturer_id", "line_id", "station_id", "model", "serial_no", "purchase_date", "equipment_status", "status"),
                List.of("equipment_code", "equipment_name"),
                List.of("equipment_code", "equipment_name", "category_id", "manufacturer_id", "line_id", "station_id", "equipment_status", "status"),
                "status", "ENABLED",
                "from eqp_equipment e" +
                " left join eqp_category c on e.category_id = c.id" +
                " left join eqp_manufacturer m on e.manufacturer_id = m.id" +
                " left join mes_system.md_production_line l on e.line_id = l.id" +
                " left join mes_system.md_workstation ws on e.station_id = ws.id",
                "e.",
                "e.*, c.category_name, m.manufacturer_name, l.line_name, ws.station_name"));
        map.put("maintenance-tasks", new TableSpec("maintenance-tasks", "eqp_maintenance_task",
                List.of("id", "maintenance_no", "equipment_id", "plan_at", "assigned_to", "completed_at", "result", "status"),
                List.of("maintenance_no"),
                List.of("maintenance_no", "equipment_id", "assigned_to", "result", "status"),
                "status", "PENDING"));
        map.put("inspections", new TableSpec("inspections", "eqp_inspection_record",
                List.of("id", "inspection_no", "equipment_id", "inspector_id", "inspection_at", "result", "abnormal_desc", "status"),
                List.of("inspection_no"),
                List.of("inspection_no", "equipment_id", "inspector_id", "result", "status"),
                "status", "COMPLETED"));
        map.put("repairs", new TableSpec("repairs", "eqp_repair_order",
                List.of("id", "repair_no", "equipment_id", "fault_reason_id", "reported_by", "reported_at", "assigned_to", "repair_start_at", "repair_end_at", "repair_result", "status"),
                List.of("repair_no"),
                List.of("repair_no", "equipment_id", "fault_reason_id", "reported_by", "assigned_to", "repair_result", "status"),
                "status", "PENDING"));
        map.put("device-points", new TableSpec("device-points", "iot_device_point",
                List.of("id", "equipment_id", "point_code", "point_name", "data_type", "unit_id", "status"),
                List.of("point_code", "point_name"),
                List.of("equipment_id", "point_code", "point_name", "data_type", "status"),
                "status", "ENABLED"));
        map.put("collect-records", new TableSpec("collect-records", "iot_collect_record",
                List.of("id", "equipment_id", "point_id", "collect_value", "collect_at", "quality_flag"),
                List.of(),
                List.of("equipment_id", "point_id", "quality_flag"),
                null, null));
        map.put("count-reports", new TableSpec("count-reports", "iot_count_report",
                List.of("id", "equipment_id", "task_id", "operation_task_id", "count_qty", "report_at", "sync_log_id"),
                List.of(),
                List.of("equipment_id", "task_id", "operation_task_id", "sync_log_id"),
                null, null));
        map.put("debug-logs", new TableSpec("debug-logs", "iot_debug_log",
                List.of("id", "equipment_id", "request_payload", "response_payload", "result", "debug_at"),
                List.of(),
                List.of("equipment_id", "result"),
                null, null));
        map.put("oee-snapshots", new TableSpec("oee-snapshots", "eqp_oee_snapshot",
                List.of("id", "equipment_id", "line_id", "stat_date", "availability", "performance", "quality_rate", "oee"),
                List.of(),
                List.of("equipment_id", "line_id", "stat_date"),
                null, null));
        map.put("energy-records", new TableSpec("energy-records", "eqp_energy_record",
                List.of("id", "equipment_id", "energy_type", "value", "unit_id", "record_at"),
                List.of(),
                List.of("equipment_id", "energy_type", "unit_id"),
                null, null));
        return map;
    }
}
