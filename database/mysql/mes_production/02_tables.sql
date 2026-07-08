USE `mes_production`;

-- 跨逻辑库引用字段保留原数据库设计中的 FK 业务语义。
-- 初始化脚本不创建跨库物理外键，避免微服务逻辑库独立部署时产生强耦合。

CREATE TABLE IF NOT EXISTS `route_process` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `process_code` VARCHAR(64) NOT NULL COMMENT '工序编码',
  `process_name` VARCHAR(128) NOT NULL COMMENT '工序名称',
  `process_type` VARCHAR(32) DEFAULT NULL COMMENT '离散、流水、检验、包装',
  `standard_time` DECIMAL(18,6) DEFAULT NULL COMMENT '标准工时',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、启用、停用、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_process_code` (`process_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工序';

CREATE TABLE IF NOT EXISTS `route_defect_reason` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `reason_code` VARCHAR(64) NOT NULL COMMENT '原因编码',
  `reason_name` VARCHAR(128) NOT NULL COMMENT '原因名称',
  `process_id` BIGINT DEFAULT NULL COMMENT '适用工序，可为空',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_defect_reason_code` (`reason_code`),
  KEY `idx_route_defect_reason_process` (`process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='不良原因';

CREATE TABLE IF NOT EXISTS `route_header` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `route_code` VARCHAR(64) NOT NULL COMMENT '工艺路线编码',
  `route_name` VARCHAR(128) NOT NULL COMMENT '工艺路线名称',
  `version_no` VARCHAR(32) DEFAULT NULL COMMENT '版本号',
  `is_default` TINYINT DEFAULT NULL COMMENT '是否默认',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、启用、停用、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_header_code` (`route_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工艺路线';

CREATE TABLE IF NOT EXISTS `sop_file` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `sop_code` VARCHAR(64) NOT NULL COMMENT 'SOP 编码',
  `sop_name` VARCHAR(128) NOT NULL COMMENT 'SOP 名称',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '图片、视频、电子文件',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件地址',
  `display_order` INT DEFAULT NULL COMMENT '播放顺序',
  `auto_play_flag` TINYINT DEFAULT NULL COMMENT '是否自动轮播',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sop_file_code` (`sop_code`),
  KEY `idx_sop_file_process` (`process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子 SOP';

CREATE TABLE IF NOT EXISTS `route_step` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `route_id` BIGINT NOT NULL COMMENT '工艺路线',
  `process_id` BIGINT NOT NULL COMMENT '工序',
  `step_no` INT NOT NULL COMMENT '工序顺序',
  `previous_step_id` BIGINT DEFAULT NULL COMMENT '前置工序，可为空',
  `next_step_id` BIGINT DEFAULT NULL COMMENT '后续工序，可为空',
  `workstation_id` BIGINT DEFAULT NULL COMMENT '默认工位',
  `sop_id` BIGINT DEFAULT NULL COMMENT '默认 SOP',
  `quality_required` TINYINT DEFAULT NULL COMMENT '是否需检验',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_step` (`route_id`, `step_no`),
  KEY `idx_route_step_process` (`process_id`),
  KEY `idx_route_step_sop` (`sop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路线工序';

CREATE TABLE IF NOT EXISTS `route_product` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品',
  `route_id` BIGINT NOT NULL COMMENT '工艺路线',
  `is_default` TINYINT DEFAULT NULL COMMENT '是否默认',
  `effective_from` DATE DEFAULT NULL COMMENT '生效日期',
  `effective_to` DATE DEFAULT NULL COMMENT '失效日期',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  KEY `idx_route_product_product` (`product_id`),
  KEY `idx_route_product_route` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品工艺路线绑定';

CREATE TABLE IF NOT EXISTS `prod_work_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `work_order_no` VARCHAR(64) NOT NULL COMMENT '工单编号，唯一',
  `external_no` VARCHAR(64) DEFAULT NULL COMMENT '外部工单号',
  `source_type` VARCHAR(32) DEFAULT NULL COMMENT '手工、导入、ERP、API',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `plan_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '计划数量，大于 0',
  `completed_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '已完工数量',
  `qualified_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '合格数量',
  `defective_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '不良数量',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单位',
  `planned_start_at` DATETIME DEFAULT NULL COMMENT '计划开始时间',
  `planned_end_at` DATETIME DEFAULT NULL COMMENT '计划结束时间',
  `delivery_date` DATE DEFAULT NULL COMMENT '交货日期',
  `line_id` BIGINT DEFAULT NULL COMMENT '建议产线',
  `route_id` BIGINT DEFAULT NULL COMMENT '工艺路线',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、待下发、已下发、生产中、暂停、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prod_work_order_no` (`work_order_no`),
  KEY `idx_prod_work_order_product` (`product_id`),
  KEY `idx_prod_work_order_status` (`status`),
  KEY `idx_prod_work_order_external` (`external_no`),
  KEY `idx_prod_work_order_route` (`route_id`),
  CONSTRAINT `chk_prod_work_order_plan_qty` CHECK (`plan_qty` IS NULL OR `plan_qty` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生产工单';

CREATE TABLE IF NOT EXISTS `prod_kitting_analysis` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `analysis_no` VARCHAR(64) NOT NULL COMMENT '齐套分析编号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '生产工单',
  `task_id` BIGINT DEFAULT NULL COMMENT '生产任务单，可为空',
  `analysis_time` DATETIME DEFAULT NULL COMMENT '分析时间',
  `kitting_status` VARCHAR(32) DEFAULT NULL COMMENT '齐套、欠料、部分齐套',
  `missing_count` INT DEFAULT NULL COMMENT '欠料项数',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prod_kitting_analysis_no` (`analysis_no`),
  KEY `idx_prod_kitting_analysis_work_order` (`work_order_id`),
  KEY `idx_prod_kitting_analysis_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='齐套分析';

CREATE TABLE IF NOT EXISTS `prod_kitting_missing_item` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `analysis_id` BIGINT DEFAULT NULL COMMENT '齐套分析',
  `material_id` BIGINT DEFAULT NULL COMMENT '物料',
  `required_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '需求数量',
  `available_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '可用数量',
  `missing_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '欠料数量',
  `expected_arrival_at` DATETIME DEFAULT NULL COMMENT '预计到料时间',
  PRIMARY KEY (`id`),
  KEY `idx_prod_kitting_missing_analysis` (`analysis_id`),
  KEY `idx_prod_kitting_missing_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='欠料明细';

CREATE TABLE IF NOT EXISTS `prod_dispatch_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `dispatch_no` VARCHAR(64) NOT NULL COMMENT '派工单编号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '生产工单',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `station_id` BIGINT DEFAULT NULL COMMENT '工位，可为空',
  `team_id` BIGINT DEFAULT NULL COMMENT '班组',
  `plan_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '派工数量',
  `planned_start_at` DATETIME DEFAULT NULL COMMENT '计划开工',
  `planned_end_at` DATETIME DEFAULT NULL COMMENT '计划完工',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、待下发、已下发、生产中、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prod_dispatch_order_no` (`dispatch_no`),
  KEY `idx_prod_dispatch_order_work_order` (`work_order_id`),
  KEY `idx_prod_dispatch_order_line` (`line_id`),
  KEY `idx_prod_dispatch_order_team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='派工单';

CREATE TABLE IF NOT EXISTS `shop_task` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `task_no` VARCHAR(64) NOT NULL COMMENT '任务单编号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '生产工单',
  `dispatch_id` BIGINT DEFAULT NULL COMMENT '派工单',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `route_id` BIGINT DEFAULT NULL COMMENT '工艺路线',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `team_id` BIGINT DEFAULT NULL COMMENT '班组',
  `plan_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '计划数量',
  `started_at` DATETIME DEFAULT NULL COMMENT '实际开工',
  `ended_at` DATETIME DEFAULT NULL COMMENT '实际结束',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待下发、已下发、生产中、暂停、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_task_no` (`task_no`),
  KEY `idx_shop_task_work_order` (`work_order_id`),
  KEY `idx_shop_task_line_status` (`line_id`, `status`),
  KEY `idx_shop_task_dispatch` (`dispatch_id`),
  KEY `idx_shop_task_route` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生产任务单';

CREATE TABLE IF NOT EXISTS `shop_operation_task` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `operation_task_no` VARCHAR(64) NOT NULL COMMENT '工序作业编号',
  `task_id` BIGINT DEFAULT NULL COMMENT '生产任务单',
  `route_step_id` BIGINT DEFAULT NULL COMMENT '路线工序',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `station_id` BIGINT DEFAULT NULL COMMENT '工位',
  `device_id` BIGINT DEFAULT NULL COMMENT '设备',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员',
  `plan_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '计划数量',
  `reported_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '已报工数量',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待处理、生产中、暂停、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_operation_task_no` (`operation_task_no`),
  KEY `idx_shop_operation_task_task` (`task_id`),
  KEY `idx_shop_operation_task_route_step` (`route_step_id`),
  KEY `idx_shop_operation_task_process` (`process_id`),
  KEY `idx_shop_operation_task_operator` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工序作业';

CREATE TABLE IF NOT EXISTS `shop_product_status` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '产品码',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `operation_task_id` BIGINT DEFAULT NULL COMMENT '工序作业',
  `current_process_id` BIGINT DEFAULT NULL COMMENT '当前工序',
  `current_station_id` BIGINT DEFAULT NULL COMMENT '当前工位',
  `production_status` VARCHAR(32) DEFAULT NULL COMMENT '待生产、生产中、暂停、返修、已完工、已报废',
  `last_report_id` BIGINT DEFAULT NULL COMMENT '最近报工记录',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_status_barcode` (`barcode_id`),
  KEY `idx_shop_product_status_task` (`task_id`),
  KEY `idx_shop_product_status_operation` (`operation_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品生产状态';

CREATE TABLE IF NOT EXISTS `shop_report` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `report_no` VARCHAR(64) NOT NULL COMMENT '报工单号',
  `report_type` VARCHAR(32) DEFAULT NULL COMMENT '普通、不良、检测、设备计数、关键物料、装箱',
  `task_id` BIGINT DEFAULT NULL COMMENT '生产任务单',
  `operation_task_id` BIGINT DEFAULT NULL COMMENT '工序作业',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '产品码，可为空',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `station_id` BIGINT DEFAULT NULL COMMENT '工位',
  `device_id` BIGINT DEFAULT NULL COMMENT '设备',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员',
  `report_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '报工数量',
  `qualified_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '合格数量',
  `defective_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '不良数量',
  `report_at` DATETIME DEFAULT NULL COMMENT '报工时间',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_report_no` (`report_no`),
  KEY `idx_shop_report_task_time` (`task_id`, `report_at`),
  KEY `idx_shop_report_operator_time` (`operator_id`, `report_at`),
  KEY `idx_shop_report_operation` (`operation_task_id`),
  KEY `idx_shop_report_barcode` (`barcode_id`),
  CONSTRAINT `chk_shop_report_qty` CHECK (
    (`report_qty` IS NULL OR `report_qty` >= 0)
    AND (`qualified_qty` IS NULL OR `qualified_qty` >= 0)
    AND (`defective_qty` IS NULL OR `defective_qty` >= 0)
    AND (`report_qty` IS NULL OR `qualified_qty` IS NULL OR `defective_qty` IS NULL OR `report_qty` >= `qualified_qty` + `defective_qty`)
  )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生产报工';

CREATE TABLE IF NOT EXISTS `shop_report_material` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `report_id` BIGINT DEFAULT NULL COMMENT '报工记录',
  `material_id` BIGINT DEFAULT NULL COMMENT '物料',
  `material_barcode_id` BIGINT DEFAULT NULL COMMENT '材料码',
  `qty` DECIMAL(18,6) DEFAULT NULL COMMENT '用量',
  PRIMARY KEY (`id`),
  KEY `idx_shop_report_material_report` (`report_id`),
  KEY `idx_shop_report_material_material` (`material_id`),
  KEY `idx_shop_report_material_barcode` (`material_barcode_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关键物料报工明细';

CREATE TABLE IF NOT EXISTS `shop_completion_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `completion_no` VARCHAR(64) NOT NULL COMMENT '完工单号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '生产工单',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `completed_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '完工数量',
  `qualified_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '合格数量',
  `defective_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '不良数量',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完工时间',
  `confirmed_by` BIGINT DEFAULT NULL COMMENT '确认人',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_completion_order_no` (`completion_no`),
  KEY `idx_shop_completion_order_work_order` (`work_order_id`),
  KEY `idx_shop_completion_order_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生产完工单';

CREATE TABLE IF NOT EXISTS `shop_rework_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `rework_no` VARCHAR(64) NOT NULL COMMENT '返修工单编号',
  `source_type` VARCHAR(32) DEFAULT NULL COMMENT '质量检验、不良报工、人工创建',
  `source_id` BIGINT DEFAULT NULL COMMENT '来源业务 ID',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '产品码',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `route_id` BIGINT DEFAULT NULL COMMENT '返修工艺路线',
  `reason_id` BIGINT DEFAULT NULL COMMENT '不良原因',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待处理、处理中、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_rework_order_no` (`rework_no`),
  KEY `idx_shop_rework_order_source` (`source_type`, `source_id`),
  KEY `idx_shop_rework_order_barcode` (`barcode_id`),
  KEY `idx_shop_rework_order_reason` (`reason_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='返修工单';

CREATE TABLE IF NOT EXISTS `bc_type` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `type_code` VARCHAR(64) NOT NULL COMMENT '条码类型编码',
  `type_name` VARCHAR(128) NOT NULL COMMENT '条码类型名称',
  `unique_scope` VARCHAR(32) DEFAULT NULL COMMENT '全局唯一、批次唯一',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bc_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码类型';

CREATE TABLE IF NOT EXISTS `bc_rule` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `rule_code` VARCHAR(64) NOT NULL COMMENT '规则编码',
  `rule_name` VARCHAR(128) NOT NULL COMMENT '规则名称',
  `type_id` BIGINT DEFAULT NULL COMMENT '条码类型',
  `rule_expression` VARCHAR(500) DEFAULT NULL COMMENT '规则表达式',
  `serial_length` INT DEFAULT NULL COMMENT '流水号长度',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '草稿、启用、停用、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bc_rule_code` (`rule_code`),
  KEY `idx_bc_rule_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码规则';

CREATE TABLE IF NOT EXISTS `bc_template` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
  `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
  `type_id` BIGINT DEFAULT NULL COMMENT '条码类型',
  `template_content` TEXT COMMENT '标签模板内容',
  `paper_width` DECIMAL(10,2) DEFAULT NULL COMMENT '纸张宽度',
  `paper_height` DECIMAL(10,2) DEFAULT NULL COMMENT '纸张高度',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bc_template_code` (`template_code`),
  KEY `idx_bc_template_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码模板';

CREATE TABLE IF NOT EXISTS `bc_application_rule` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `app_rule_code` VARCHAR(64) NOT NULL COMMENT '应用规则编码',
  `item_id` BIGINT DEFAULT NULL COMMENT '产品或物料',
  `type_id` BIGINT DEFAULT NULL COMMENT '条码类型',
  `rule_id` BIGINT DEFAULT NULL COMMENT '条码规则',
  `template_id` BIGINT DEFAULT NULL COMMENT '标签模板',
  `barcode_mode` VARCHAR(32) DEFAULT NULL COMMENT '唯一码、批次码',
  `source_type` VARCHAR(32) DEFAULT NULL COMMENT '规则生成、传入值生成、外部导入',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bc_application_rule_code` (`app_rule_code`),
  KEY `idx_bc_application_rule_item` (`item_id`),
  KEY `idx_bc_application_rule_type` (`type_id`),
  KEY `idx_bc_application_rule_rule` (`rule_id`),
  KEY `idx_bc_application_rule_template` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码应用规则';

CREATE TABLE IF NOT EXISTS `bc_barcode` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `barcode_value` VARCHAR(128) NOT NULL COMMENT '实际条码内容',
  `type_id` BIGINT DEFAULT NULL COMMENT '条码类型',
  `app_rule_id` BIGINT DEFAULT NULL COMMENT '应用规则',
  `item_id` BIGINT DEFAULT NULL COMMENT '产品或物料',
  `batch_no` VARCHAR(64) DEFAULT NULL COMMENT '批次号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单，可为空',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单，可为空',
  `parent_barcode_id` BIGINT DEFAULT NULL COMMENT '上级条码，箱码或栈板码',
  `print_count` INT DEFAULT NULL COMMENT '打印次数',
  `source_type` VARCHAR(32) DEFAULT NULL COMMENT '生成、导入、外部传入',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '已生成、已打印、已扫码、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_barcode_value` (`barcode_value`),
  UNIQUE KEY `uk_barcode_batch` (`type_id`, `batch_no`, `barcode_value`),
  KEY `idx_bc_barcode_work_order` (`work_order_id`),
  KEY `idx_bc_barcode_task` (`task_id`),
  KEY `idx_bc_barcode_parent` (`parent_barcode_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码实例';

CREATE TABLE IF NOT EXISTS `bc_print_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '条码',
  `template_id` BIGINT DEFAULT NULL COMMENT '标签模板',
  `print_by` BIGINT DEFAULT NULL COMMENT '打印人',
  `print_at` DATETIME DEFAULT NULL COMMENT '打印时间',
  `print_count` INT DEFAULT NULL COMMENT '本次打印份数',
  `printer_name` VARCHAR(128) DEFAULT NULL COMMENT '打印机',
  PRIMARY KEY (`id`),
  KEY `idx_bc_print_record_barcode` (`barcode_id`),
  KEY `idx_bc_print_record_template` (`template_id`),
  KEY `idx_bc_print_record_time` (`print_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码打印记录';

CREATE TABLE IF NOT EXISTS `trace_event` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '条码',
  `barcode_value` VARCHAR(128) DEFAULT NULL COMMENT '冗余条码值',
  `event_type` VARCHAR(64) DEFAULT NULL COMMENT '生成、打印、扫码、报工、质检、装箱、维修、安灯',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '关联业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '关联业务 ID',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `station_id` BIGINT DEFAULT NULL COMMENT '工位',
  `device_id` BIGINT DEFAULT NULL COMMENT '设备',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人',
  `event_at` DATETIME DEFAULT NULL COMMENT '事件时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_barcode` (`barcode_id`, `event_at`),
  KEY `idx_trace_biz` (`biz_type`, `biz_id`),
  KEY `idx_trace_task` (`task_id`, `event_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='追溯事件';

CREATE TABLE IF NOT EXISTS `andon_type` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `type_code` VARCHAR(64) NOT NULL COMMENT '安灯类型编码',
  `type_name` VARCHAR(128) NOT NULL COMMENT '安灯类型名称',
  `response_minutes` INT DEFAULT NULL COMMENT '响应分钟数',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_andon_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='安灯类型';

CREATE TABLE IF NOT EXISTS `andon_config` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `type_id` BIGINT DEFAULT NULL COMMENT '安灯类型',
  `workshop_id` BIGINT DEFAULT NULL COMMENT '车间',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `notify_role_id` BIGINT DEFAULT NULL COMMENT '通知角色',
  `escalation_minutes` INT DEFAULT NULL COMMENT '升级分钟数',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `idx_andon_config_type` (`type_id`),
  KEY `idx_andon_config_line` (`line_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异常配置';

CREATE TABLE IF NOT EXISTS `andon_reason` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `reason_code` VARCHAR(64) NOT NULL COMMENT '异常原因编码',
  `reason_name` VARCHAR(128) NOT NULL COMMENT '异常原因名称',
  `type_id` BIGINT DEFAULT NULL COMMENT '安灯类型',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_andon_reason_code` (`reason_code`),
  KEY `idx_andon_reason_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异常原因';

CREATE TABLE IF NOT EXISTS `andon_exception_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `exception_no` VARCHAR(64) NOT NULL COMMENT '异常单号',
  `type_id` BIGINT DEFAULT NULL COMMENT '安灯类型',
  `reason_id` BIGINT DEFAULT NULL COMMENT '异常原因',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `operation_task_id` BIGINT DEFAULT NULL COMMENT '工序作业',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `station_id` BIGINT DEFAULT NULL COMMENT '工位',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `reported_by` BIGINT DEFAULT NULL COMMENT '发起人',
  `reported_at` DATETIME DEFAULT NULL COMMENT '发起时间',
  `assigned_to` BIGINT DEFAULT NULL COMMENT '处理人',
  `closed_by` BIGINT DEFAULT NULL COMMENT '关闭人',
  `closed_at` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待处理、处理中、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_andon_exception_order_no` (`exception_no`),
  KEY `idx_andon_exception_order_task` (`task_id`),
  KEY `idx_andon_exception_order_equipment` (`equipment_id`),
  KEY `idx_andon_exception_order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异常单';

CREATE TABLE IF NOT EXISTS `andon_notify_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `exception_id` BIGINT DEFAULT NULL COMMENT '异常单 ID',
  `notify_user_id` BIGINT DEFAULT NULL COMMENT '通知用户 ID',
  `notify_channel` VARCHAR(32) DEFAULT NULL COMMENT '通知渠道',
  `notify_at` DATETIME DEFAULT NULL COMMENT '通知时间',
  `read_at` DATETIME DEFAULT NULL COMMENT '读取时间',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '结果',
  PRIMARY KEY (`id`),
  KEY `idx_andon_notify_record_exception` (`exception_id`),
  KEY `idx_andon_notify_record_user` (`notify_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知记录';

CREATE TABLE IF NOT EXISTS `andon_handle_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `exception_id` BIGINT DEFAULT NULL COMMENT '异常单 ID',
  `handler_id` BIGINT DEFAULT NULL COMMENT '处理人',
  `handle_action` VARCHAR(64) DEFAULT NULL COMMENT '处理动作',
  `handle_content` VARCHAR(500) DEFAULT NULL COMMENT '处理内容',
  `handled_at` DATETIME DEFAULT NULL COMMENT '处理时间',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '结果',
  PRIMARY KEY (`id`),
  KEY `idx_andon_handle_record_exception` (`exception_id`),
  KEY `idx_andon_handle_record_handler` (`handler_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处理记录';

CREATE TABLE IF NOT EXISTS `wage_piece_rate` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品，可为空',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `rate` DECIMAL(18,6) DEFAULT NULL COMMENT '单件工资',
  `effective_from` DATE DEFAULT NULL COMMENT '生效日期',
  `effective_to` DATE DEFAULT NULL COMMENT '失效日期',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  KEY `idx_wage_piece_rate_product_process` (`product_id`, `process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工序计件单价';

CREATE TABLE IF NOT EXISTS `wage_piece_detail` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `report_id` BIGINT DEFAULT NULL COMMENT '报工记录',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `qualified_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '合格数量',
  `rate` DECIMAL(18,6) DEFAULT NULL COMMENT '单价',
  `amount` DECIMAL(18,6) DEFAULT NULL COMMENT '金额',
  `calc_at` DATETIME DEFAULT NULL COMMENT '计算时间',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待审核、已确认、已关闭、作废',
  PRIMARY KEY (`id`),
  KEY `idx_wage_piece_detail_report` (`report_id`),
  KEY `idx_wage_piece_detail_operator` (`operator_id`),
  KEY `idx_wage_piece_detail_process` (`process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='计件工资明细';

CREATE TABLE IF NOT EXISTS `wage_piece_summary` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员',
  `period_key` VARCHAR(32) DEFAULT NULL COMMENT '期间键',
  `total_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '总数量',
  `total_amount` DECIMAL(18,6) DEFAULT NULL COMMENT '总金额',
  `confirmed_by` BIGINT DEFAULT NULL COMMENT '确认人',
  `confirmed_at` DATETIME DEFAULT NULL COMMENT '确认时间',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `idx_wage_piece_summary_operator_period` (`operator_id`, `period_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='计件工资汇总';


CREATE TABLE IF NOT EXISTS `mes_idempotency_record` (
  `module_name` VARCHAR(64) NOT NULL COMMENT 'module name',
  `action_name` VARCHAR(64) NOT NULL COMMENT 'action name',
  `idempotency_key` VARCHAR(128) NOT NULL COMMENT 'idempotency key',
  `business_id` VARCHAR(64) DEFAULT NULL COMMENT 'business id',
  `created_at` DATETIME DEFAULT NULL COMMENT 'created time',
  PRIMARY KEY (`module_name`, `action_name`, `idempotency_key`),
  KEY `idx_mes_idempotency_record_business` (`business_id`),
  KEY `idx_mes_idempotency_record_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='idempotency record';

CREATE TABLE IF NOT EXISTS `mes_operation_log` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `module_name` VARCHAR(64) DEFAULT NULL COMMENT 'module name',
  `target_table` VARCHAR(64) DEFAULT NULL COMMENT 'target table',
  `target_id` BIGINT DEFAULT NULL COMMENT 'target id',
  `action_name` VARCHAR(64) DEFAULT NULL COMMENT 'action name',
  `old_status` VARCHAR(32) DEFAULT NULL COMMENT 'old status',
  `new_status` VARCHAR(32) DEFAULT NULL COMMENT 'new status',
  `operator_id` BIGINT DEFAULT NULL COMMENT 'operator id',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT 'remark',
  `operated_at` DATETIME DEFAULT NULL COMMENT 'operated time',
  PRIMARY KEY (`id`),
  KEY `idx_mes_operation_log_target` (`target_table`, `target_id`),
  KEY `idx_mes_operation_log_operator` (`operator_id`),
  KEY `idx_mes_operation_log_time` (`operated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='operation log';
