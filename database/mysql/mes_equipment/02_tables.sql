USE `mes_equipment`;

-- 跨逻辑库引用字段保留原数据库设计中的 FK 业务语义。
-- 初始化脚本不创建跨库物理外键，避免微服务逻辑库独立部署时产生强耦合。

CREATE TABLE IF NOT EXISTS `eqp_category` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `category_code` VARCHAR(64) NOT NULL COMMENT '设备类别编码',
  `category_name` VARCHAR(128) NOT NULL COMMENT '设备类别名称',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_category_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备类别';

CREATE TABLE IF NOT EXISTS `eqp_manufacturer` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `manufacturer_code` VARCHAR(64) NOT NULL COMMENT '设备制造商编码',
  `manufacturer_name` VARCHAR(128) NOT NULL COMMENT '设备制造商名称',
  `contact` VARCHAR(128) DEFAULT NULL COMMENT '联系人',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_manufacturer_code` (`manufacturer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备制造商';

CREATE TABLE IF NOT EXISTS `eqp_fault_reason` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `reason_code` VARCHAR(64) NOT NULL COMMENT '故障原因编码',
  `reason_name` VARCHAR(128) NOT NULL COMMENT '故障原因名称',
  `category_id` BIGINT DEFAULT NULL COMMENT '设备类别',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_fault_reason_code` (`reason_code`),
  KEY `idx_eqp_fault_reason_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='故障原因';

CREATE TABLE IF NOT EXISTS `eqp_equipment` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_code` VARCHAR(64) NOT NULL COMMENT '设备编号',
  `equipment_name` VARCHAR(128) NOT NULL COMMENT '设备名称',
  `category_id` BIGINT DEFAULT NULL COMMENT '设备类别',
  `manufacturer_id` BIGINT DEFAULT NULL COMMENT '制造商',
  `line_id` BIGINT DEFAULT NULL COMMENT '所属产线',
  `station_id` BIGINT DEFAULT NULL COMMENT '所属工位',
  `model` VARCHAR(128) DEFAULT NULL COMMENT '型号',
  `serial_no` VARCHAR(128) DEFAULT NULL COMMENT '出厂编号',
  `purchase_date` DATE DEFAULT NULL COMMENT '采购日期',
  `equipment_status` VARCHAR(32) DEFAULT NULL COMMENT '正常、待保养、待维修、维修中、停用',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_equipment_code` (`equipment_code`),
  KEY `idx_eqp_equipment_category` (`category_id`),
  KEY `idx_eqp_equipment_manufacturer` (`manufacturer_id`),
  KEY `idx_eqp_equipment_line` (`line_id`),
  KEY `idx_eqp_equipment_station` (`station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备台账';

CREATE TABLE IF NOT EXISTS `eqp_maintenance_task` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `maintenance_no` VARCHAR(64) NOT NULL COMMENT '保养任务编号',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `plan_at` DATETIME DEFAULT NULL COMMENT '计划保养时间',
  `assigned_to` BIGINT DEFAULT NULL COMMENT '设备维护人员',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '正常、异常',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待处理、处理中、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_maintenance_task_no` (`maintenance_no`),
  KEY `idx_eqp_maintenance_task_equipment` (`equipment_id`),
  KEY `idx_eqp_maintenance_task_assigned` (`assigned_to`),
  KEY `idx_eqp_maintenance_task_plan` (`plan_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='保养任务';

CREATE TABLE IF NOT EXISTS `eqp_inspection_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `inspection_no` VARCHAR(64) NOT NULL COMMENT '点检单号',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `inspector_id` BIGINT DEFAULT NULL COMMENT '点检人',
  `inspection_at` DATETIME DEFAULT NULL COMMENT '点检时间',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '正常、异常',
  `abnormal_desc` VARCHAR(500) DEFAULT NULL COMMENT '异常说明',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_inspection_record_no` (`inspection_no`),
  KEY `idx_eqp_inspection_record_equipment` (`equipment_id`),
  KEY `idx_eqp_inspection_record_time` (`inspection_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='点检记录';

CREATE TABLE IF NOT EXISTS `eqp_repair_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `repair_no` VARCHAR(64) NOT NULL COMMENT '报修单号',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `fault_reason_id` BIGINT DEFAULT NULL COMMENT '故障原因',
  `reported_by` BIGINT DEFAULT NULL COMMENT '报修人',
  `reported_at` DATETIME DEFAULT NULL COMMENT '报修时间',
  `assigned_to` BIGINT DEFAULT NULL COMMENT '维修人',
  `repair_start_at` DATETIME DEFAULT NULL COMMENT '维修开始',
  `repair_end_at` DATETIME DEFAULT NULL COMMENT '维修结束',
  `repair_result` VARCHAR(32) DEFAULT NULL COMMENT '已修复、未修复、转外修',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待维修、维修中、已完成、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eqp_repair_order_no` (`repair_no`),
  KEY `idx_eqp_repair_order_equipment_status_time` (`equipment_id`, `status`, `reported_at`),
  KEY `idx_eqp_repair_order_fault_reason` (`fault_reason_id`),
  KEY `idx_eqp_repair_order_assigned` (`assigned_to`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报修任务与维修记录';

CREATE TABLE IF NOT EXISTS `iot_device_point` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `point_code` VARCHAR(64) NOT NULL COMMENT '点位编码',
  `point_name` VARCHAR(128) NOT NULL COMMENT '点位名称',
  `data_type` VARCHAR(32) DEFAULT NULL COMMENT '数据类型',
  `unit_id` BIGINT DEFAULT NULL COMMENT '计量单位',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `idx_iot_device_point_equipment` (`equipment_id`),
  UNIQUE KEY `uk_iot_device_point_code` (`point_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备采集点位';

CREATE TABLE IF NOT EXISTS `iot_collect_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `point_id` BIGINT DEFAULT NULL COMMENT '点位',
  `collect_value` VARCHAR(128) DEFAULT NULL COMMENT '采集值',
  `collect_at` DATETIME DEFAULT NULL COMMENT '采集时间',
  `quality_flag` VARCHAR(32) DEFAULT NULL COMMENT '质量标识',
  PRIMARY KEY (`id`),
  KEY `idx_iot_collect_record_equipment` (`equipment_id`),
  KEY `idx_iot_collect_record_point_time` (`point_id`, `collect_at`),
  KEY `idx_iot_collect_record_collect_at` (`collect_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='原始采集记录';

CREATE TABLE IF NOT EXISTS `iot_count_report` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `operation_task_id` BIGINT DEFAULT NULL COMMENT '工序作业',
  `count_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '计数数量',
  `report_at` DATETIME DEFAULT NULL COMMENT '报工时间',
  `sync_log_id` BIGINT DEFAULT NULL COMMENT '同步日志',
  PRIMARY KEY (`id`),
  KEY `idx_iot_count_report_equipment` (`equipment_id`),
  KEY `idx_iot_count_report_task` (`task_id`),
  KEY `idx_iot_count_report_operation` (`operation_task_id`),
  KEY `idx_iot_count_report_sync_log` (`sync_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备计数报工';

CREATE TABLE IF NOT EXISTS `iot_debug_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `request_payload` TEXT COMMENT '请求报文',
  `response_payload` TEXT COMMENT '响应报文',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '结果',
  `debug_at` DATETIME DEFAULT NULL COMMENT '联调时间',
  PRIMARY KEY (`id`),
  KEY `idx_iot_debug_log_equipment` (`equipment_id`),
  KEY `idx_iot_debug_log_time` (`debug_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备联调日志';

CREATE TABLE IF NOT EXISTS `eqp_oee_snapshot` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `stat_date` DATE DEFAULT NULL COMMENT '统计日期',
  `availability` DECIMAL(18,6) DEFAULT NULL COMMENT '稼动率',
  `performance` DECIMAL(18,6) DEFAULT NULL COMMENT '性能',
  `quality_rate` DECIMAL(18,6) DEFAULT NULL COMMENT '质量率',
  `oee` DECIMAL(18,6) DEFAULT NULL COMMENT 'OEE',
  PRIMARY KEY (`id`),
  KEY `idx_eqp_oee_snapshot_equipment_date` (`equipment_id`, `stat_date`),
  KEY `idx_eqp_oee_snapshot_line_date` (`line_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备或产线 OEE 指标';

CREATE TABLE IF NOT EXISTS `eqp_energy_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备',
  `energy_type` VARCHAR(32) DEFAULT NULL COMMENT '能源类型',
  `value` DECIMAL(18,6) DEFAULT NULL COMMENT '值',
  `unit_id` BIGINT DEFAULT NULL COMMENT '计量单位',
  `record_at` DATETIME DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_eqp_energy_record_equipment_time` (`equipment_id`, `record_at`),
  KEY `idx_eqp_energy_record_type` (`energy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='能源分析原始数据';

