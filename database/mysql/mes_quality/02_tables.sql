USE `mes_quality`;

-- 跨逻辑库引用字段保留原数据库设计中的 FK 业务语义。
-- 初始化脚本不创建跨库物理外键，避免微服务逻辑库独立部署时产生强耦合。

CREATE TABLE IF NOT EXISTS `qc_item_category` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `category_code` VARCHAR(64) NOT NULL COMMENT '检验项目分类编码',
  `category_name` VARCHAR(128) NOT NULL COMMENT '检验项目分类名称',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qc_item_category_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检验项目分类';

CREATE TABLE IF NOT EXISTS `qc_item` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `category_id` BIGINT DEFAULT NULL COMMENT '检验项目分类',
  `item_code` VARCHAR(64) NOT NULL COMMENT '检验项目编码',
  `item_name` VARCHAR(128) NOT NULL COMMENT '检验项目名称',
  `value_type` VARCHAR(32) DEFAULT NULL COMMENT '值类型',
  `standard_value` VARCHAR(128) DEFAULT NULL COMMENT '标准值',
  `upper_limit` DECIMAL(18,6) DEFAULT NULL COMMENT '上限',
  `lower_limit` DECIMAL(18,6) DEFAULT NULL COMMENT '下限',
  `unit_id` BIGINT DEFAULT NULL COMMENT '计量单位',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qc_item_code` (`item_code`),
  KEY `idx_qc_item_category` (`category_id`),
  KEY `idx_qc_item_unit` (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检验项目';

CREATE TABLE IF NOT EXISTS `qc_plan` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `plan_code` VARCHAR(64) NOT NULL COMMENT '检验标准方案编码',
  `plan_name` VARCHAR(128) NOT NULL COMMENT '检验标准方案名称',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户',
  `is_default` TINYINT DEFAULT NULL COMMENT '是否默认',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qc_plan_code` (`plan_code`),
  KEY `idx_qc_plan_product` (`product_id`),
  KEY `idx_qc_plan_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检验标准方案';

CREATE TABLE IF NOT EXISTS `qc_plan_item` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `plan_id` BIGINT DEFAULT NULL COMMENT '检验方案',
  `qc_item_id` BIGINT DEFAULT NULL COMMENT '检验项目',
  `sample_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '抽样数量',
  `standard_value` VARCHAR(128) DEFAULT NULL COMMENT '标准值',
  `upper_limit` DECIMAL(18,6) DEFAULT NULL COMMENT '上限',
  `lower_limit` DECIMAL(18,6) DEFAULT NULL COMMENT '下限',
  `required_flag` TINYINT DEFAULT NULL COMMENT '是否必检',
  PRIMARY KEY (`id`),
  KEY `idx_qc_plan_item_plan` (`plan_id`),
  KEY `idx_qc_plan_item_item` (`qc_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='方案项目';

CREATE TABLE IF NOT EXISTS `qc_inspection_order` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `inspection_no` VARCHAR(64) NOT NULL COMMENT '检验单号',
  `inspection_type` VARCHAR(32) DEFAULT NULL COMMENT '首件、末件、巡检、成品入库、成品发货',
  `plan_id` BIGINT DEFAULT NULL COMMENT '检验方案',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单',
  `task_id` BIGINT DEFAULT NULL COMMENT '任务单',
  `operation_task_id` BIGINT DEFAULT NULL COMMENT '工序作业',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '产品码或批次码',
  `inspector_id` BIGINT DEFAULT NULL COMMENT '质检员',
  `inspection_at` DATETIME DEFAULT NULL COMMENT '检验时间',
  `final_result` VARCHAR(32) DEFAULT NULL COMMENT '合格、不合格、让步接收',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待检、检验中、合格、不合格、已关闭、作废',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qc_inspection_order_no` (`inspection_no`),
  KEY `idx_qc_inspection_order_type_status_time` (`inspection_type`, `status`, `inspection_at`),
  KEY `idx_qc_inspection_order_plan` (`plan_id`),
  KEY `idx_qc_inspection_order_task` (`task_id`),
  KEY `idx_qc_inspection_order_operation` (`operation_task_id`),
  KEY `idx_qc_inspection_order_barcode` (`barcode_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检验单';

CREATE TABLE IF NOT EXISTS `qc_inspection_result` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `inspection_id` BIGINT DEFAULT NULL COMMENT '检验单',
  `qc_item_id` BIGINT DEFAULT NULL COMMENT '检验项目',
  `measured_value` VARCHAR(128) DEFAULT NULL COMMENT '实测值',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '合格、不合格、让步接收',
  `defect_reason_id` BIGINT DEFAULT NULL COMMENT '不良原因，不合格时必填',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`),
  KEY `idx_qc_inspection_result_inspection` (`inspection_id`),
  KEY `idx_qc_inspection_result_item` (`qc_item_id`),
  KEY `idx_qc_inspection_result_defect_reason` (`defect_reason_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检验结果';

CREATE TABLE IF NOT EXISTS `qc_defect_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `source_type` VARCHAR(32) DEFAULT NULL COMMENT '检验、报工',
  `source_id` BIGINT DEFAULT NULL COMMENT '来源 ID',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品',
  `barcode_id` BIGINT DEFAULT NULL COMMENT '产品码',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序',
  `defect_reason_id` BIGINT DEFAULT NULL COMMENT '不良原因',
  `defect_qty` DECIMAL(18,6) DEFAULT NULL COMMENT '不良数量',
  `handle_method` VARCHAR(32) DEFAULT NULL COMMENT '返修、报废、让步接收',
  `rework_order_id` BIGINT DEFAULT NULL COMMENT '返修工单',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '待处理、处理中、已完成、已关闭',
  PRIMARY KEY (`id`),
  KEY `idx_qc_defect_record_product_process` (`product_id`, `process_id`),
  KEY `idx_qc_defect_record_source` (`source_type`, `source_id`),
  KEY `idx_qc_defect_record_barcode` (`barcode_id`),
  KEY `idx_qc_defect_record_rework` (`rework_order_id`),
  CONSTRAINT `chk_qc_defect_record_qty` CHECK (`defect_qty` IS NULL OR `defect_qty` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='不良记录';


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