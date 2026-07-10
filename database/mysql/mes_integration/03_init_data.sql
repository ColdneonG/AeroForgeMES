USE `mes_integration`;

-- 集成模块演示与联调基础数据。
-- 使用 INSERT IGNORE 保证脚本可重复执行，且不会覆盖已有接口日志。
-- 其中 ERP_ROUTE_READ 失败记录用于验证 TC-INT-01、TC-INT-02 和 TC-INT-03。
INSERT IGNORE INTO `int_external_system`
(`id`, `system_code`, `system_name`, `auth_type`, `status`) VALUES
(7000001, 'ERP-DEMO', '联调 ERP 系统', 'TOKEN', 'ENABLED'),
(7000002, 'OPENAPI-DEMO', '联调标准 API 客户端', 'APP_KEY', 'ENABLED'),
(7000003, 'DEVICE-GATEWAY', '设备采集网关', 'SIGN', 'ENABLED');

INSERT IGNORE INTO `int_sync_log`
(`id`, `system_id`, `interface_code`, `direction`, `biz_type`, `biz_id`, `external_no`, `idempotent_key`, `request_payload`, `response_payload`, `sync_status`, `retry_count`, `error_message`, `started_at`, `finished_at`) VALUES
(7000101, 7000001, 'ERP_PRODUCTION_TASK_READ', 'IN', 'PRODUCTION_TASK', 4200001, 'ERP-TASK-20260708-001', 'erp-task-20260708-001', '{"externalNo":"ERP-TASK-20260708-001"}', '{"success":true,"taskId":4200001}', 'SUCCESS', 0, NULL, '2026-07-08 07:30:00', '2026-07-08 07:30:02'),
(7000102, 7000001, 'ERP_ROUTE_READ', 'IN', 'ROUTE', 9700201, 'ERP-ROUTE-FAN-ASSY', 'erp-route-fan-assy-v1', '{"routeCode":"ROUTE-FAN-ASSY"}', '{"success":false}', 'FAILED', 1, '工序编码重复', '2026-07-08 07:35:00', '2026-07-08 07:35:03'),
(7000103, 7000002, 'OPENAPI_WORK_ORDER_WRITE', 'IN', 'WORK_ORDER', 4000003, 'API-WO-20260708-003', 'api-wo-20260708-003', '{"workOrderNo":"WO20260708003"}', '{"success":true,"id":4000003}', 'SUCCESS', 0, NULL, '2026-07-07 08:00:00', '2026-07-07 08:00:01'),
(7000104, 7000003, 'OPENAPI_DEVICE_COUNT_REPORT_WRITE', 'IN', 'DEVICE_COUNT_REPORT', 2000901, 'DEVICE-COUNT-20260708-001', 'device-count-20260708-001', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '{"success":true}', 'SUCCESS', 0, NULL, '2026-07-08 11:00:00', '2026-07-08 11:00:01'),
(7000105, 7000002, 'OPENAPI_COMPLETION_ORDER_READ', 'OUT', 'COMPLETION_ORDER', 7200301, 'COMP20260708001', 'completion-read-20260708-001', '{"completionNo":"COMP20260708001"}', '{"completedQty":60,"qualifiedQty":59}', 'SUCCESS', 0, NULL, '2026-07-08 13:20:00', '2026-07-08 13:20:01');

INSERT IGNORE INTO `int_request_log`
(`id`, `sync_log_id`, `method`, `url`, `headers_json`, `body`, `requested_at`) VALUES
(7000201, 7000101, 'POST', '/api/integration/erp/production-tasks/read', '{"Authorization":"Bearer demo"}', '{"externalNo":"ERP-TASK-20260708-001"}', '2026-07-08 07:30:00'),
(7000202, 7000104, 'POST', '/api/openapi/device-count-reports/write', '{"X-App-Key":"demo"}', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '2026-07-08 11:00:00');

INSERT IGNORE INTO `int_response_log`
(`id`, `sync_log_id`, `http_status`, `body`, `responded_at`, `elapsed_ms`) VALUES
(7000301, 7000101, 200, '{"success":true,"taskId":4200001}', '2026-07-08 07:30:02', 120),
(7000302, 7000102, 400, '{"success":false,"message":"工序编码重复"}', '2026-07-08 07:35:03', 180),
(7000303, 7000104, 200, '{"success":true}', '2026-07-08 11:00:01', 80);
