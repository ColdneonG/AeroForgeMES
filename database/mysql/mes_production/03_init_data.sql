USE `mes_production`;

-- 电子 SOP 演示数据（可重复执行）。
INSERT IGNORE INTO `mes_production`.`sop_document`
(`id`, `sop_code`, `sop_name`, `category`, `owner_id`, `status`, `current_version_id`, `created_at`, `updated_at`) VALUES
(7900001, 'SOP-FAN-ASSY-001', '风扇总装电子 SOP', 'STANDARD_OPERATION', 1000001, 'ENABLED', 7900101, '2026-07-08 08:00:00', '2026-07-08 08:30:00');

INSERT IGNORE INTO `mes_production`.`sop_version`
(`id`, `sop_id`, `version_no`, `revision_type`, `status`, `effective_from`, `effective_to`, `submit_by`, `submit_at`, `review_by`, `review_at`, `approve_by`, `approve_at`, `publish_by`, `publish_at`, `model_version_id`, `remark`, `created_at`, `updated_at`) VALUES
(7900101, 7900001, 'V1.0', 'NEW', 'EFFECTIVE', '2026-07-08 08:30:00', NULL, 1000001, '2026-07-08 08:10:00', 1000001, '2026-07-08 08:20:00', 1000001, '2026-07-08 08:25:00', 1000001, '2026-07-08 08:30:00', NULL, '演示版：支持任务锁版和步骤确认', '2026-07-08 08:00:00', '2026-07-08 08:30:00');

INSERT IGNORE INTO `mes_production`.`sop_step`
(`id`, `version_id`, `step_no`, `step_name`, `instruction`, `content_type`, `standard_duration`, `key_step`, `min_stay_seconds`, `confirm_required`, `parameter_required`, `photo_required`, `skip_allowed`, `abnormal_handling`, `quality_item_id`, `andon_type_id`, `enabled`, `created_at`, `updated_at`) VALUES
(7900201, 7900101, 10, '工装与物料确认', '确认电机、网罩、扇叶和螺丝齐套；检查工装夹具定位销无松动。', 'TEXT', 60, 1, 0, 1, 0, 0, 0, '缺料或工装异常时发起安灯。', NULL, 7600002, 1, '2026-07-08 08:00:00', '2026-07-08 08:00:00'),
(7900202, 7900101, 20, '电机与支架装配', '按定位孔装入电机支架，先不安装扇叶；使用扭矩工具锁紧固定螺丝，确认线束不被挤压。', 'MODEL_3D', 120, 1, 0, 1, 1, 0, 0, '扭矩不达标时禁止报工。', NULL, 7600001, 1, '2026-07-08 08:00:00', '2026-07-08 08:00:00'),
(7900204, 7900101, 30, '扇叶与螺丝装配', '装入扇叶组件，按对角顺序拧紧固定螺丝；确认扇叶旋转无刮擦、无偏摆。', 'MODEL_3D', 150, 1, 0, 1, 1, 0, 0, '扇叶偏摆或螺丝滑牙时停止装配并发起质量异常。', NULL, 7600001, 1, '2026-07-08 08:00:00', '2026-07-08 08:00:00'),
(7900203, 7900101, 40, '外观与试运行确认', '检查防护网罩卡扣到位，低中高三档运行无异响，填写确认结果。', 'TEXT', 90, 1, 0, 1, 1, 0, 0, '发现异响或外观缺陷时转质量异常。', 3000101, 7600003, 1, '2026-07-08 08:00:00', '2026-07-08 08:00:00');

INSERT IGNORE INTO `mes_production`.`sop_attachment`
(`id`, `version_id`, `step_id`, `attachment_type`, `file_name`, `content_type`, `file_size`, `object_key`, `file_url`, `sha256`, `display_order`, `created_at`) VALUES
(7900501, 7900101, 7900202, 'MODEL_3D', '电机支架装配模型.glb', 'model/gltf-binary', 653300, 'sop/models/20260710/12025-fan.glb', '/api/production/sop/files/7900501', '3ed2dee0ffb7333f0baaa540136f7328a9dc85c3bd9a83da734f443d7f46c153', 20, '2026-07-10 16:20:00'),
(7900502, 7900101, 7900204, 'MODEL_3D', '扇叶螺丝装配模型.glb', 'model/gltf-binary', 653300, 'sop/models/20260710/ad7a03dc-08f9-470d-a119-48690a4ef868.glb', '/api/production/sop/files/7900502', '3ed2dee0ffb7333f0baaa540136f7328a9dc85c3bd9a83da734f443d7f46c153', 30, '2026-07-10 16:20:00');

INSERT IGNORE INTO `mes_production`.`sop_binding`
(`id`, `version_id`, `binding_type`, `product_id`, `route_id`, `route_step_id`, `process_id`, `workstation_id`, `equipment_id`, `task_id`, `priority`, `confirm_mode`, `effective_from`, `effective_to`, `status`, `created_at`, `updated_at`) VALUES
(7900301, 7900101, 'PRODUCT_ROUTE_STATION', 9400001, 9700201, NULL, NULL, 9600001, NULL, NULL, 80, 'PER_TASK', '2026-07-08 08:30:00', NULL, 'ACTIVE', '2026-07-08 08:00:00', '2026-07-08 08:30:00');

-- 条码应用演示数据（可重复执行并修复错误字符集导入的数据）。
-- 请使用 utf8mb4 客户端字符集执行本初始化脚本。
-- Uses upserts so it repairs demo rows that were previously imported with the wrong client charset.

INSERT INTO `mes_production`.`bc_type`
(`id`, `type_code`, `type_name`, `unique_scope`, `status`) VALUES
(7100001, 'PRODUCT_SN', '产品唯一码', '全局唯一', '启用'),
(7100002, 'MATERIAL_BATCH', '材料批次码', '批次唯一', '启用'),
(7100003, 'CARTON_CODE', '外箱码', '全局唯一', '启用')
ON DUPLICATE KEY UPDATE
`type_code` = VALUES(`type_code`), `type_name` = VALUES(`type_name`),
`unique_scope` = VALUES(`unique_scope`), `status` = VALUES(`status`);

INSERT INTO `mes_production`.`bc_rule`
(`id`, `rule_code`, `rule_name`, `type_id`, `rule_expression`, `serial_length`, `status`) VALUES
(7100101, 'BCR-PRODUCT-SN', '产品 SN 规则', 7100001, 'SN-${yyyyMMdd}-${####}', 4, '启用'),
(7100102, 'BCR-MATERIAL-BATCH', '材料批次规则', 7100002, 'MAT-${itemCode}-${yyyyMMdd}', 3, '启用'),
(7100103, 'BCR-CARTON', '外箱码规则', 7100003, 'CTN-${yyyyMMdd}-${####}', 4, '启用')
ON DUPLICATE KEY UPDATE
`rule_code` = VALUES(`rule_code`), `rule_name` = VALUES(`rule_name`),
`type_id` = VALUES(`type_id`), `rule_expression` = VALUES(`rule_expression`),
`serial_length` = VALUES(`serial_length`), `status` = VALUES(`status`);

INSERT INTO `mes_production`.`bc_template`
(`id`, `template_code`, `template_name`, `type_id`, `template_content`, `paper_width`, `paper_height`, `status`) VALUES
(7100201, 'BCT-PRODUCT', '产品标签模板', 7100001, '{"fields":["barcode","item","workOrder"]}', 60.00, 40.00, '启用'),
(7100202, 'BCT-MATERIAL', '物料标签模板', 7100002, '{"fields":["batch","material","supplier"]}', 80.00, 50.00, '启用'),
(7100203, 'BCT-CARTON', '外箱标签模板', 7100003, '{"fields":["carton","qty","product"]}', 100.00, 70.00, '启用')
ON DUPLICATE KEY UPDATE
`template_code` = VALUES(`template_code`), `template_name` = VALUES(`template_name`),
`type_id` = VALUES(`type_id`), `template_content` = VALUES(`template_content`),
`paper_width` = VALUES(`paper_width`), `paper_height` = VALUES(`paper_height`), `status` = VALUES(`status`);

INSERT INTO `mes_production`.`bc_application_rule`
(`id`, `app_rule_code`, `item_id`, `type_id`, `rule_id`, `template_id`, `barcode_mode`, `source_type`, `status`) VALUES
(7100301, 'APP-FAN500-SN', 9400001, 7100001, 7100101, 7100201, '唯一码', '规则生成', '启用'),
(7100302, 'APP-MOTOR-BATCH', 9400003, 7100002, 7100102, 7100202, '批次码', '外部导入', '启用'),
(7100303, 'APP-FAN-CARTON', 9400001, 7100003, 7100103, 7100203, '唯一码', '规则生成', '启用')
ON DUPLICATE KEY UPDATE
`app_rule_code` = VALUES(`app_rule_code`), `item_id` = VALUES(`item_id`),
`type_id` = VALUES(`type_id`), `rule_id` = VALUES(`rule_id`),
`template_id` = VALUES(`template_id`), `barcode_mode` = VALUES(`barcode_mode`),
`source_type` = VALUES(`source_type`), `status` = VALUES(`status`);

INSERT INTO `mes_production`.`bc_barcode`
(`id`, `barcode_value`, `type_id`, `app_rule_id`, `item_id`, `batch_no`, `work_order_id`, `task_id`, `parent_barcode_id`, `print_count`, `source_type`, `status`) VALUES
(7100401, 'SN202607080001', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印'),
(7100402, 'SN202607080002', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已扫码'),
(7100403, 'MAT-MOTOR-20260708-A', 7100002, 7100302, 9400003, 'MOTOR-0708A', 4000001, 4200001, NULL, 0, '外部导入', '已生成'),
(7100404, 'CTN202607080001', 7100003, 7100303, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印')
ON DUPLICATE KEY UPDATE
`type_id` = VALUES(`type_id`), `app_rule_id` = VALUES(`app_rule_id`), `item_id` = VALUES(`item_id`),
`batch_no` = VALUES(`batch_no`), `work_order_id` = VALUES(`work_order_id`), `task_id` = VALUES(`task_id`),
`parent_barcode_id` = VALUES(`parent_barcode_id`), `print_count` = VALUES(`print_count`),
`source_type` = VALUES(`source_type`), `status` = VALUES(`status`);

INSERT INTO `mes_production`.`trace_event`
(`id`, `barcode_id`, `barcode_value`, `event_type`, `biz_type`, `biz_id`, `work_order_id`, `task_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `event_at`) VALUES
(7300001, 7100401, 'SN202607080001', '生成', 'BARCODE', 7100401, 4000001, 4200001, NULL, NULL, NULL, 1000001, '2026-07-08 08:02:00'),
(7300002, 7100401, 'SN202607080001', '扫码', 'SHOP_OPERATION', 4300001, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 08:12:00'),
(7300003, 7100402, 'SN202607080002', '质检', 'QC_INSPECTION', 5000002, 4000001, 4200001, 9700002, 9600002, 2000203, 1000001, '2026-07-08 10:00:00'),
(7300004, 7100403, 'MAT-MOTOR-20260708-A', '关键物料', 'MATERIAL_REPORT', 7200201, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 09:05:00')
ON DUPLICATE KEY UPDATE
`barcode_id` = VALUES(`barcode_id`), `barcode_value` = VALUES(`barcode_value`),
`event_type` = VALUES(`event_type`), `biz_type` = VALUES(`biz_type`), `biz_id` = VALUES(`biz_id`),
`work_order_id` = VALUES(`work_order_id`), `task_id` = VALUES(`task_id`),
`process_id` = VALUES(`process_id`), `station_id` = VALUES(`station_id`),
`device_id` = VALUES(`device_id`), `operator_id` = VALUES(`operator_id`), `event_at` = VALUES(`event_at`);

INSERT INTO `mes_production`.`bc_rule_sequence`
(`rule_id`, `sequence_key`, `current_value`, `updated_at`) VALUES
(7100101, '20260708', 2, '2026-07-08 08:02:00'),
(7100103, '20260708', 1, '2026-07-08 11:30:00')
ON DUPLICATE KEY UPDATE `current_value` = GREATEST(`current_value`, VALUES(`current_value`));

UPDATE `mes_system`.`md_item` SET `item_name` = 'FS-500 工业风扇', `item_type` = '产品', `status` = '启用' WHERE `id` = 9400001;
UPDATE `mes_system`.`md_item` SET `item_name` = 'TF-230 台式风扇', `item_type` = '产品', `status` = '启用' WHERE `id` = 9400002;
UPDATE `mes_system`.`md_item` SET `item_name` = '65W 电机', `item_type` = '物料', `status` = '启用' WHERE `id` = 9400003;
UPDATE `mes_system`.`md_item` SET `item_name` = '500mm 防护网罩', `item_type` = '物料', `status` = '启用' WHERE `id` = 9400004;
UPDATE `mes_auth`.`sys_user` SET `display_name` = '系统管理员', `status` = '启用' WHERE `id` = 1000001;
UPDATE `mes_system`.`md_workstation` SET `station_name` = '装配工位一', `status` = '启用' WHERE `id` = 9600001;
UPDATE `mes_system`.`md_workstation` SET `station_name` = '检测工位一', `status` = '启用' WHERE `id` = 9600002;
UPDATE `mes_equipment`.`eqp_equipment` SET `equipment_name` = '自动锁螺丝机 1#', `status` = '启用' WHERE `id` = 2000201;
UPDATE `mes_equipment`.`eqp_equipment` SET `equipment_name` = '风量检测台 1#', `status` = '启用' WHERE `id` = 2000203;
UPDATE `mes_production`.`route_process` SET `process_name` = '风扇总装', `status` = '启用' WHERE `id` = 9700001;
UPDATE `mes_production`.`route_process` SET `process_name` = '整机检测', `status` = '启用' WHERE `id` = 9700002;
