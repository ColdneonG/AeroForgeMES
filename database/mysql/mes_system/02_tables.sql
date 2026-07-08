USE `mes_system`;

-- 文档中“关键字段”未逐项给出类型的表，按同类字段、命名约定和 MySQL 8.x 兼容类型落地。

CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级菜单 ID',
  `menu_code` VARCHAR(64) NOT NULL COMMENT '菜单编码',
  `menu_name` VARCHAR(128) NOT NULL COMMENT '菜单名称',
  `module_code` VARCHAR(64) DEFAULT NULL COMMENT '模块编码',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '路径',
  `sort_no` INT DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_menu_code` (`menu_code`),
  KEY `idx_sys_menu_parent` (`parent_id`),
  KEY `idx_sys_menu_module` (`module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单结构';

CREATE TABLE IF NOT EXISTS `sys_status_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务对象类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务对象 ID',
  `biz_code` VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  `from_status` VARCHAR(32) DEFAULT NULL COMMENT '原状态',
  `to_status` VARCHAR(32) DEFAULT NULL COMMENT '目标状态',
  `action` VARCHAR(64) DEFAULT NULL COMMENT '触发动作',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人',
  `operated_at` DATETIME DEFAULT NULL COMMENT '操作时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`),
  KEY `idx_status_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_status_log_time` (`operated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='状态流转日志';

CREATE TABLE IF NOT EXISTS `sys_operation_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务 ID',
  `action` VARCHAR(64) DEFAULT NULL COMMENT '操作动作',
  `request_body` TEXT COMMENT '请求内容',
  `before_data` TEXT COMMENT '变更前数据',
  `after_data` TEXT COMMENT '变更后数据',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人',
  `operated_at` DATETIME DEFAULT NULL COMMENT '操作时间',
  `result` VARCHAR(32) DEFAULT NULL COMMENT '结果',
  `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_sys_operation_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_sys_operation_log_time` (`operated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志';

CREATE TABLE IF NOT EXISTS `sys_sequence_rule` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `rule_code` VARCHAR(64) NOT NULL COMMENT '规则编码',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `prefix` VARCHAR(32) DEFAULT NULL COMMENT '前缀',
  `date_pattern` VARCHAR(32) DEFAULT NULL COMMENT '日期格式',
  `serial_length` INT DEFAULT NULL COMMENT '流水号长度',
  `reset_cycle` VARCHAR(32) DEFAULT NULL COMMENT '重置周期',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_sequence_rule_code` (`rule_code`),
  KEY `idx_sys_sequence_rule_biz` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='编号规则';

CREATE TABLE IF NOT EXISTS `sys_sequence_value` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `rule_id` BIGINT NOT NULL COMMENT '规则 ID',
  `period_key` VARCHAR(32) NOT NULL COMMENT '周期键',
  `current_value` BIGINT NOT NULL COMMENT '流水号当前值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sequence_value` (`rule_id`, `period_key`),
  CONSTRAINT `fk_sys_sequence_value_rule` FOREIGN KEY (`rule_id`) REFERENCES `sys_sequence_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='流水号当前值';

CREATE TABLE IF NOT EXISTS `md_org` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级组织 ID',
  `org_code` VARCHAR(64) NOT NULL COMMENT '组织编码',
  `org_name` VARCHAR(128) NOT NULL COMMENT '组织名称',
  `org_type` VARCHAR(32) DEFAULT NULL COMMENT '组织类型',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_org_code` (`org_code`),
  KEY `idx_md_org_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织';

CREATE TABLE IF NOT EXISTS `md_workshop` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `workshop_code` VARCHAR(64) NOT NULL COMMENT '车间编码',
  `workshop_name` VARCHAR(128) NOT NULL COMMENT '车间名称',
  `org_id` BIGINT DEFAULT NULL COMMENT '组织 ID',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_workshop_code` (`workshop_code`),
  KEY `idx_md_workshop_org` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车间';

CREATE TABLE IF NOT EXISTS `md_production_line` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `line_code` VARCHAR(64) NOT NULL COMMENT '产线编码',
  `line_name` VARCHAR(128) NOT NULL COMMENT '产线名称',
  `workshop_id` BIGINT DEFAULT NULL COMMENT '车间 ID',
  `capacity_per_hour` DECIMAL(18,6) DEFAULT NULL COMMENT '小时产能',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_production_line_code` (`line_code`),
  KEY `idx_md_production_line_workshop` (`workshop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产线';

CREATE TABLE IF NOT EXISTS `md_workstation` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `station_code` VARCHAR(64) NOT NULL COMMENT '工位编码',
  `station_name` VARCHAR(128) NOT NULL COMMENT '工位名称',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线 ID',
  `process_id` BIGINT DEFAULT NULL COMMENT '工序 ID',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_workstation_code` (`station_code`),
  KEY `idx_md_workstation_line` (`line_id`),
  KEY `idx_md_workstation_process` (`process_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工位';

CREATE TABLE IF NOT EXISTS `md_team` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `team_code` VARCHAR(64) NOT NULL COMMENT '班组编码',
  `team_name` VARCHAR(128) NOT NULL COMMENT '班组名称',
  `workshop_id` BIGINT DEFAULT NULL COMMENT '车间 ID',
  `leader_id` BIGINT DEFAULT NULL COMMENT '班组长 ID',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_team_code` (`team_code`),
  KEY `idx_md_team_workshop` (`workshop_id`),
  KEY `idx_md_team_leader` (`leader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班组';

CREATE TABLE IF NOT EXISTS `md_unit` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `unit_code` VARCHAR(64) NOT NULL COMMENT '计量单位编码',
  `unit_name` VARCHAR(128) NOT NULL COMMENT '计量单位名称',
  `precision_scale` INT DEFAULT NULL COMMENT '精度',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_unit_code` (`unit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='计量单位';

CREATE TABLE IF NOT EXISTS `md_item` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `item_code` VARCHAR(64) NOT NULL COMMENT '物料编码',
  `item_name` VARCHAR(128) NOT NULL COMMENT '物料名称',
  `item_type` VARCHAR(32) DEFAULT NULL COMMENT '产品、物料、半成品、包装物、辅料',
  `spec_model` VARCHAR(128) DEFAULT NULL COMMENT '规格型号',
  `unit_id` BIGINT DEFAULT NULL COMMENT '计量单位 ID',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_item_code` (`item_code`),
  KEY `idx_md_item_type` (`item_type`),
  KEY `idx_md_item_unit` (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品、物料、半成品';

CREATE TABLE IF NOT EXISTS `md_bom` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `bom_code` VARCHAR(64) NOT NULL COMMENT 'BOM 编码',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品 ID',
  `version_no` VARCHAR(32) DEFAULT NULL COMMENT '版本号',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_bom_code` (`bom_code`),
  KEY `idx_md_bom_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='BOM 主表';

CREATE TABLE IF NOT EXISTS `md_bom_item` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `bom_id` BIGINT NOT NULL COMMENT 'BOM ID',
  `material_id` BIGINT NOT NULL COMMENT '物料 ID',
  `qty` DECIMAL(18,6) DEFAULT NULL COMMENT '数量',
  `loss_rate` DECIMAL(18,6) DEFAULT NULL COMMENT '损耗率',
  PRIMARY KEY (`id`),
  KEY `idx_md_bom_item_bom` (`bom_id`),
  KEY `idx_md_bom_item_material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='BOM 明细';

CREATE TABLE IF NOT EXISTS `md_customer` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `customer_code` VARCHAR(64) NOT NULL COMMENT '客户编码',
  `customer_name` VARCHAR(128) NOT NULL COMMENT '客户名称',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_customer_code` (`customer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客户';

CREATE TABLE IF NOT EXISTS `md_supplier` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `supplier_code` VARCHAR(64) NOT NULL COMMENT '供应商编码',
  `supplier_name` VARCHAR(128) NOT NULL COMMENT '供应商名称',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='供应商';
