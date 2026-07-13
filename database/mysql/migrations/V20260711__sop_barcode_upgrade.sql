-- ============================================================
-- Fan MES 电子 SOP 与条码应用增量升级脚本
-- 适用：已有数据库；可重复执行
-- 要求：MySQL 8.0，客户端字符集 utf8mb4
-- ============================================================

USE `mes_production`;

-- Electronic SOP standalone migration.
-- Safe to run repeatedly: tables use IF NOT EXISTS.

CREATE TABLE IF NOT EXISTS `sop_document` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `sop_code` VARCHAR(64) NOT NULL COMMENT 'SOP code',
  `sop_name` VARCHAR(128) NOT NULL COMMENT 'SOP name',
  `category` VARCHAR(64) DEFAULT NULL COMMENT 'SOP category',
  `owner_id` BIGINT DEFAULT NULL COMMENT 'owner user',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'ENABLED/DISABLED/VOID',
  `current_version_id` BIGINT DEFAULT NULL COMMENT 'current effective version',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_document_code` (`sop_code`),
  KEY `idx_sop_document_status` (`status`),
  KEY `idx_sop_document_current_version` (`current_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='versioned SOP document';

CREATE TABLE IF NOT EXISTS `sop_version` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `sop_id` BIGINT NOT NULL COMMENT 'SOP document',
  `version_no` VARCHAR(32) NOT NULL COMMENT 'version number',
  `revision_type` VARCHAR(32) DEFAULT NULL COMMENT 'NEW/MINOR/MAJOR/EMERGENCY',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'DRAFT/PENDING_REVIEW/APPROVED/EFFECTIVE',
  `effective_from` DATETIME DEFAULT NULL COMMENT 'effective from',
  `effective_to` DATETIME DEFAULT NULL COMMENT 'effective to',
  `submit_by` BIGINT DEFAULT NULL COMMENT 'submit user',
  `submit_at` DATETIME DEFAULT NULL COMMENT 'submit time',
  `review_by` BIGINT DEFAULT NULL COMMENT 'review user',
  `review_at` DATETIME DEFAULT NULL COMMENT 'review time',
  `approve_by` BIGINT DEFAULT NULL COMMENT 'approve user',
  `approve_at` DATETIME DEFAULT NULL COMMENT 'approve time',
  `publish_by` BIGINT DEFAULT NULL COMMENT 'publish user',
  `publish_at` DATETIME DEFAULT NULL COMMENT 'publish time',
  `model_version_id` BIGINT DEFAULT NULL COMMENT 'bound GLB model version',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT 'remark',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_version_no` (`sop_id`, `version_no`),
  KEY `idx_sop_version_status` (`status`),
  KEY `idx_sop_version_model` (`model_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP version';

CREATE TABLE IF NOT EXISTS `sop_step` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `version_id` BIGINT NOT NULL COMMENT 'SOP version',
  `step_no` INT NOT NULL COMMENT 'step number',
  `step_name` VARCHAR(128) NOT NULL COMMENT 'step name',
  `instruction` TEXT COMMENT 'operation instruction',
  `content_type` VARCHAR(32) DEFAULT NULL COMMENT 'TEXT/IMAGE/VIDEO/PDF/MODEL_3D',
  `standard_duration` INT DEFAULT NULL COMMENT 'standard duration seconds',
  `key_step` TINYINT DEFAULT NULL COMMENT 'key step flag',
  `min_stay_seconds` INT DEFAULT NULL COMMENT 'minimum viewing seconds',
  `confirm_required` TINYINT DEFAULT NULL COMMENT 'confirmation required',
  `parameter_required` TINYINT DEFAULT NULL COMMENT 'parameter required',
  `photo_required` TINYINT DEFAULT NULL COMMENT 'photo required',
  `skip_allowed` TINYINT DEFAULT NULL COMMENT 'skip allowed',
  `abnormal_handling` VARCHAR(500) DEFAULT NULL COMMENT 'abnormal handling',
  `quality_item_id` BIGINT DEFAULT NULL COMMENT 'quality item',
  `andon_type_id` BIGINT DEFAULT NULL COMMENT 'andon type',
  `enabled` TINYINT DEFAULT NULL COMMENT 'enabled flag',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_step_no` (`version_id`, `step_no`),
  KEY `idx_sop_step_version` (`version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP step';

CREATE TABLE IF NOT EXISTS `sop_attachment` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `version_id` BIGINT NOT NULL COMMENT 'SOP version',
  `step_id` BIGINT DEFAULT NULL COMMENT 'SOP step',
  `attachment_type` VARCHAR(32) DEFAULT NULL COMMENT 'IMAGE/VIDEO/PDF/FILE/MODEL_3D',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT 'original file name',
  `content_type` VARCHAR(128) DEFAULT NULL COMMENT 'MIME type',
  `file_size` BIGINT DEFAULT NULL COMMENT 'file size',
  `object_key` VARCHAR(500) DEFAULT NULL COMMENT 'storage object key',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT 'download URL',
  `sha256` VARCHAR(64) DEFAULT NULL COMMENT 'SHA-256',
  `display_order` INT DEFAULT NULL COMMENT 'display order',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  PRIMARY KEY (`id`),
  KEY `idx_sop_attachment_version` (`version_id`),
  KEY `idx_sop_attachment_step` (`step_id`),
  KEY `idx_sop_attachment_hash` (`sha256`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP attachment metadata';

CREATE TABLE IF NOT EXISTS `sop_binding` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `version_id` BIGINT NOT NULL COMMENT 'SOP version',
  `binding_type` VARCHAR(32) NOT NULL COMMENT 'TASK/PRODUCT_ROUTE_STATION/PRODUCT_ROUTE/PRODUCT_PROCESS',
  `product_id` BIGINT DEFAULT NULL COMMENT 'product',
  `route_id` BIGINT DEFAULT NULL COMMENT 'route',
  `route_step_id` BIGINT DEFAULT NULL COMMENT 'route step',
  `process_id` BIGINT DEFAULT NULL COMMENT 'process',
  `workstation_id` BIGINT DEFAULT NULL COMMENT 'workstation',
  `equipment_id` BIGINT DEFAULT NULL COMMENT 'equipment',
  `task_id` BIGINT DEFAULT NULL COMMENT 'task',
  `priority` INT DEFAULT NULL COMMENT 'match priority',
  `confirm_mode` VARCHAR(32) DEFAULT NULL COMMENT 'confirmation mode',
  `effective_from` DATETIME DEFAULT NULL COMMENT 'effective from',
  `effective_to` DATETIME DEFAULT NULL COMMENT 'effective to',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'ACTIVE/DISABLED',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`id`),
  KEY `idx_sop_binding_version` (`version_id`),
  KEY `idx_sop_binding_task` (`task_id`),
  KEY `idx_sop_binding_product_route` (`product_id`, `route_id`, `workstation_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP binding';

CREATE TABLE IF NOT EXISTS `sop_model` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `model_code` VARCHAR(64) NOT NULL COMMENT 'model code',
  `model_name` VARCHAR(128) NOT NULL COMMENT 'model name',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'ENABLED/DISABLED/VOID',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_model_code` (`model_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP 3D model';

CREATE TABLE IF NOT EXISTS `sop_model_version` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `model_id` BIGINT NOT NULL COMMENT 'model',
  `version_no` VARCHAR(32) NOT NULL COMMENT 'version number',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT 'file name',
  `object_key` VARCHAR(500) DEFAULT NULL COMMENT 'storage object key',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT 'download URL',
  `sha256` VARCHAR(64) DEFAULT NULL COMMENT 'SHA-256',
  `file_size` BIGINT DEFAULT NULL COMMENT 'file size',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'DRAFT/EFFECTIVE/DISABLED',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_model_version_no` (`model_id`, `version_no`),
  KEY `idx_sop_model_version_hash` (`sha256`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP GLB model version';

CREATE TABLE IF NOT EXISTS `sop_task_snapshot` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `task_id` BIGINT NOT NULL COMMENT 'shop task',
  `sop_id` BIGINT NOT NULL COMMENT 'SOP document',
  `version_id` BIGINT NOT NULL COMMENT 'locked SOP version',
  `version_no` VARCHAR(32) DEFAULT NULL COMMENT 'locked version number',
  `model_id` BIGINT DEFAULT NULL COMMENT 'locked model',
  `model_version_id` BIGINT DEFAULT NULL COMMENT 'locked model version',
  `model_sha256` VARCHAR(64) DEFAULT NULL COMMENT 'model SHA-256',
  `snapshot_json` LONGTEXT COMMENT 'immutable snapshot JSON',
  `locked_by` BIGINT DEFAULT NULL COMMENT 'lock user',
  `locked_at` DATETIME DEFAULT NULL COMMENT 'lock time',
  `match_rule` VARCHAR(128) DEFAULT NULL COMMENT 'match rule',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'ACTIVE/COMPLETED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_task_snapshot_task` (`task_id`),
  KEY `idx_sop_task_snapshot_version` (`version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='locked SOP task snapshot';

CREATE TABLE IF NOT EXISTS `sop_execution_record` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `snapshot_id` BIGINT NOT NULL COMMENT 'task snapshot',
  `task_id` BIGINT NOT NULL COMMENT 'shop task',
  `sop_id` BIGINT NOT NULL COMMENT 'SOP document',
  `version_id` BIGINT NOT NULL COMMENT 'SOP version',
  `operator_id` BIGINT DEFAULT NULL COMMENT 'operator',
  `opened_at` DATETIME DEFAULT NULL COMMENT 'opened time',
  `completed_at` DATETIME DEFAULT NULL COMMENT 'completed time',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'ACTIVE/COMPLETED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_execution_snapshot` (`snapshot_id`),
  KEY `idx_sop_execution_task` (`task_id`, `opened_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP execution record';

CREATE TABLE IF NOT EXISTS `sop_step_execution_record` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `execution_id` BIGINT NOT NULL COMMENT 'execution record',
  `snapshot_id` BIGINT NOT NULL COMMENT 'task snapshot',
  `task_id` BIGINT NOT NULL COMMENT 'shop task',
  `step_id` BIGINT NOT NULL COMMENT 'SOP step',
  `step_no` INT DEFAULT NULL COMMENT 'step number',
  `view_started_at` DATETIME DEFAULT NULL COMMENT 'view start time',
  `confirmed_at` DATETIME DEFAULT NULL COMMENT 'confirmation time',
  `stay_seconds` INT DEFAULT NULL COMMENT 'actual stay seconds',
  `parameter_json` TEXT COMMENT 'parameter values',
  `photo_attachment_id` BIGINT DEFAULT NULL COMMENT 'photo attachment',
  `result` VARCHAR(32) DEFAULT NULL COMMENT 'VIEWED/CONFIRMED/SKIPPED',
  `operator_id` BIGINT DEFAULT NULL COMMENT 'operator',
  `idempotency_key` VARCHAR(128) DEFAULT NULL COMMENT 'idempotency key',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_step_execution_snapshot_step` (`snapshot_id`, `step_id`),
  KEY `idx_sop_step_execution_task` (`task_id`, `step_no`),
  KEY `idx_sop_step_execution_idem` (`idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOP step execution record';

USE `mes_production`;

-- Electronic barcode module incremental migration.
-- The original bc_* and trace_event tables remain the source of truth.
CREATE TABLE IF NOT EXISTS `bc_rule_sequence` (
  `rule_id` BIGINT NOT NULL COMMENT 'barcode rule',
  `sequence_key` VARCHAR(32) NOT NULL COMMENT 'sequence partition, yyyyMMdd by default',
  `current_value` BIGINT NOT NULL DEFAULT 0 COMMENT 'last allocated sequence',
  `updated_at` DATETIME DEFAULT NULL COMMENT 'updated time',
  PRIMARY KEY (`rule_id`, `sequence_key`),
  KEY `idx_bc_rule_sequence_updated` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='concurrent barcode sequence';

-- Electronic SOP demo seed and permission patch.
-- Run after base schema/data. Safe to run repeatedly because INSERT IGNORE is used.

USE `mes_auth`;

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000901, 'process:sop:list', '电子 SOP 列表', 'BUTTON', 'process'),
(1000902, 'process:sop:query', '电子 SOP 查询', 'BUTTON', 'process'),
(1000903, 'process:sop:add', '电子 SOP 新增', 'BUTTON', 'process'),
(1000904, 'process:sop:edit', '电子 SOP 编辑', 'BUTTON', 'process'),
(1000905, 'process:sop:copyVersion', '电子 SOP 复制版本', 'BUTTON', 'process'),
(1000906, 'process:sop:submit', '电子 SOP 提交审核', 'BUTTON', 'process'),
(1000907, 'process:sop:review', '电子 SOP 审核', 'BUTTON', 'process'),
(1000908, 'process:sop:approve', '电子 SOP 批准', 'BUTTON', 'process'),
(1000909, 'process:sop:publish', '电子 SOP 发布', 'BUTTON', 'process'),
(1000910, 'process:sop:disable', '电子 SOP 停用', 'BUTTON', 'process'),
(1000911, 'process:sop:void', '电子 SOP 作废', 'BUTTON', 'process'),
(1000912, 'process:sop:binding', '电子 SOP 绑定', 'BUTTON', 'process'),
(1000913, 'process:sopModel:upload', '电子 SOP 三维模型上传', 'BUTTON', 'process'),
(1000914, 'production:sop:view', '生产 SOP 查看', 'BUTTON', 'shopfloor'),
(1000915, 'production:sop:execute', '生产 SOP 执行', 'BUTTON', 'shopfloor'),
(1000916, 'production:sop:confirm', '生产 SOP 步骤确认', 'BUTTON', 'shopfloor'),
(1000917, 'production:sop:executionList', '生产 SOP 执行记录', 'BUTTON', 'shopfloor');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000901, 1000001, 1000901),
(1000902, 1000001, 1000902),
(1000903, 1000001, 1000903),
(1000904, 1000001, 1000904),
(1000905, 1000001, 1000905),
(1000906, 1000001, 1000906),
(1000907, 1000001, 1000907),
(1000908, 1000001, 1000908),
(1000909, 1000001, 1000909),
(1000910, 1000001, 1000910),
(1000911, 1000001, 1000911),
(1000912, 1000001, 1000912),
(1000913, 1000001, 1000913),
(1000914, 1000001, 1000914),
(1000915, 1000001, 1000915),
(1000916, 1000001, 1000916),
(1000917, 1000001, 1000917);

INSERT IGNORE INTO `sys_permission`
(`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000951, 'barcode:rule:add', '条码规则新增', 'BUTTON', 'barcode'),
(1000952, 'barcode:rule:edit', '条码规则编辑和启停', 'BUTTON', 'barcode'),
(1000953, 'barcode:rule:delete', '条码规则删除', 'BUTTON', 'barcode'),
(1000954, 'barcode:application-rule:add', '条码应用规则新增', 'BUTTON', 'barcode'),
(1000955, 'barcode:application-rule:edit', '条码应用规则编辑', 'BUTTON', 'barcode'),
(1000956, 'barcode:application-rule:delete', '条码应用规则删除', 'BUTTON', 'barcode'),
(1000957, 'barcode:generate', '条码批量生成', 'BUTTON', 'barcode'),
(1000958, 'barcode:print', '条码打印与补打', 'BUTTON', 'barcode'),
(1000959, 'barcode:scan', '条码扫码登记', 'BUTTON', 'barcode'),
(1000960, 'barcode:bind', '条码包装关系绑定', 'BUTTON', 'barcode'),
(1000961, 'barcode:close', '条码关闭', 'BUTTON', 'barcode'),
(1000962, 'barcode:void', '条码作废', 'BUTTON', 'barcode');

INSERT IGNORE INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000951, 1000001, 1000951),
(1000952, 1000001, 1000952),
(1000953, 1000001, 1000953),
(1000954, 1000001, 1000954),
(1000955, 1000001, 1000955),
(1000956, 1000001, 1000956),
(1000957, 1000001, 1000957),
(1000958, 1000001, 1000958),
(1000959, 1000001, 1000959),
(1000960, 1000001, 1000960),
(1000961, 1000001, 1000961),
(1000962, 1000001, 1000962);

-- 以下为演示数据；如生产环境不需要演示数据，可删除本段后执行。
USE `mes_production`;
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

-- 请使用 utf8mb4 客户端字符集执行本升级脚本。
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
