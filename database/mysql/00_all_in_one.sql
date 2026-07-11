-- ============================================================
-- Fan MES 一体化建库脚本
-- 包含：建库、建表、初始化数据、联调测试数据
-- 兼容：MySQL 8.0.x；请使用 utf8mb4 客户端字符集执行
-- ============================================================

-- ============================================================
-- 数据库：mes_auth
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_auth`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `mes_auth`;

-- 跨逻辑库引用字段保留原数据库设计中的 FK 业务语义。
-- 初始化脚本不创建跨库物理外键，避免微服务逻辑库独立部署时产生强耦合。

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL COMMENT '用户 ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号，唯一',
  `password` VARCHAR(256) DEFAULT NULL,
  `display_name` VARCHAR(64) DEFAULT NULL COMMENT '用户姓名',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `employee_no` VARCHAR(64) DEFAULT NULL COMMENT '员工编号',
  `org_id` BIGINT DEFAULT NULL COMMENT '所属组织',
  `team_id` BIGINT DEFAULT NULL COMMENT '所属班组',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_org` (`org_id`),
  KEY `idx_sys_user_team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(128) NOT NULL COMMENT '角色名称',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role` (`user_id`, `role_id`),
  KEY `idx_sys_user_role_role` (`role_id`),
  CONSTRAINT `fk_sys_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_sys_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关系';

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(128) NOT NULL COMMENT '权限名称',
  `permission_type` VARCHAR(32) DEFAULT NULL COMMENT '权限类型',
  `module_code` VARCHAR(64) DEFAULT NULL COMMENT '模块编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_code` (`permission_code`),
  KEY `idx_sys_permission_module` (`module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限点';

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission` (`role_id`, `permission_id`),
  KEY `idx_sys_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_sys_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `fk_sys_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限';

CREATE TABLE IF NOT EXISTS `sys_data_scope` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `scope_type` VARCHAR(32) DEFAULT NULL COMMENT '数据范围类型',
  `scope_value` VARCHAR(128) DEFAULT NULL COMMENT '数据范围值',
  PRIMARY KEY (`id`),
  KEY `idx_sys_data_scope_role` (`role_id`),
  CONSTRAINT `fk_sys_data_scope_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限';

USE `mes_auth`;

-- 电子 SOP 权限及默认管理员角色授权。
-- Electronic SOP demo seed and permission patch.
-- Run after base schema/data. Safe to run repeatedly because INSERT IGNORE is used.

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

-- 条码应用权限及默认管理员角色授权。
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

-- ============================================================
-- 数据库：mes_system
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_system`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

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

USE `mes_system`;

-- 暂不写入初始化数据。
-- 原数据库设计未给出固定组织、主数据、菜单、编号规则等初始化记录。

-- ============================================================
-- 数据库：mes_equipment
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_equipment`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

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

USE `mes_equipment`;

-- 暂不写入初始化数据。
-- 原数据库设计未给出固定设备类别、设备台账、点位或采集规则初始化数据。

-- ============================================================
-- 数据库：mes_production
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_production`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

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
  `product_name` VARCHAR(128) DEFAULT NULL COMMENT '产品名称(冗余)',
  `route_id` BIGINT DEFAULT NULL COMMENT '工艺路线',
  `line_id` BIGINT DEFAULT NULL COMMENT '产线',
  `line_name` VARCHAR(128) DEFAULT NULL COMMENT '产线名称(冗余)',
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

CREATE TABLE IF NOT EXISTS `bc_rule_sequence` (
  `rule_id` BIGINT NOT NULL COMMENT '条码规则',
  `sequence_key` VARCHAR(32) NOT NULL COMMENT '流水维度，默认日期 yyyyMMdd',
  `current_value` BIGINT NOT NULL DEFAULT 0 COMMENT '当前流水号',
  `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`, `sequence_key`),
  KEY `idx_bc_rule_sequence_updated` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='条码规则并发流水';

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

SET @shop_task_product_name_sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'shop_task' AND COLUMN_NAME = 'product_name') = 0,
  'ALTER TABLE `shop_task` ADD COLUMN `product_name` VARCHAR(128) DEFAULT NULL COMMENT ''产品名称(冗余)'' AFTER `product_id`',
  'SELECT 1'
);
PREPARE shop_task_product_name_stmt FROM @shop_task_product_name_sql;
EXECUTE shop_task_product_name_stmt;
DEALLOCATE PREPARE shop_task_product_name_stmt;

SET @shop_task_line_name_sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'shop_task' AND COLUMN_NAME = 'line_name') = 0,
  'ALTER TABLE `shop_task` ADD COLUMN `line_name` VARCHAR(128) DEFAULT NULL COMMENT ''产线名称(冗余)'' AFTER `line_id`',
  'SELECT 1'
);
PREPARE shop_task_line_name_stmt FROM @shop_task_line_name_sql;
EXECUTE shop_task_line_name_stmt;
DEALLOCATE PREPARE shop_task_line_name_stmt;

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
-- 请使用 utf8mb4 客户端字符集执行本一体化脚本。
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

-- ============================================================
-- 数据库：mes_quality
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_quality`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

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
  `product_name` VARCHAR(128) DEFAULT NULL COMMENT '产品名称(冗余)',
  `work_order_no` VARCHAR(64) DEFAULT NULL COMMENT '工单号(冗余)',
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
  `defect_reason_name` VARCHAR(128) DEFAULT NULL COMMENT '不良原因名称(冗余)',
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

-- 兼容已有库：添加去正则化冗余列（MySQL 8.0 通用写法，兼容 < 8.0.29）
-- 通过 INFORMATION_SCHEMA 检测列是否存在，避免重复添加报错
SET @s = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = 'mes_quality' AND TABLE_NAME = 'qc_inspection_order' AND COLUMN_NAME = 'product_name') = 0,
  'ALTER TABLE `qc_inspection_order` ADD COLUMN `product_name` VARCHAR(128) DEFAULT NULL COMMENT ''产品名称(冗余)'', ADD COLUMN `work_order_no` VARCHAR(64) DEFAULT NULL COMMENT ''工单号(冗余)''',
  'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = 'mes_quality' AND TABLE_NAME = 'qc_defect_record' AND COLUMN_NAME = 'defect_reason_name') = 0,
  'ALTER TABLE `qc_defect_record` ADD COLUMN `defect_reason_name` VARCHAR(128) DEFAULT NULL COMMENT ''不良原因名称(冗余)''',
  'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

USE `mes_quality`;

-- 暂不写入初始化数据。
-- 原数据库设计未给出固定检验项目、检验方案或不良记录初始化数据。

-- ============================================================
-- 数据库：mes_integration
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_integration`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `mes_integration`;

CREATE TABLE IF NOT EXISTS `int_external_system` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `system_code` VARCHAR(64) NOT NULL COMMENT '外部系统编码',
  `system_name` VARCHAR(128) NOT NULL COMMENT '外部系统名称',
  `auth_type` VARCHAR(32) DEFAULT NULL COMMENT '鉴权方式',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_int_external_system_code` (`system_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外部系统';

CREATE TABLE IF NOT EXISTS `int_sync_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `system_id` BIGINT DEFAULT NULL COMMENT '外部系统',
  `interface_code` VARCHAR(64) DEFAULT NULL COMMENT '接口编码',
  `direction` VARCHAR(16) DEFAULT NULL COMMENT '入站、出站',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT 'MES 业务 ID',
  `external_no` VARCHAR(64) DEFAULT NULL COMMENT '外部单号',
  `idempotent_key` VARCHAR(128) DEFAULT NULL COMMENT '幂等键',
  `request_payload` TEXT COMMENT '请求内容',
  `response_payload` TEXT COMMENT '响应内容',
  `sync_status` VARCHAR(32) DEFAULT NULL COMMENT '待同步、同步中、同步成功、同步失败',
  `retry_count` INT DEFAULT NULL COMMENT '重试次数',
  `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '失败原因',
  `started_at` DATETIME DEFAULT NULL COMMENT '开始时间',
  `finished_at` DATETIME DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sync_idempotent` (`system_id`, `interface_code`, `idempotent_key`),
  KEY `idx_int_sync_log_status_retry_time` (`sync_status`, `retry_count`, `started_at`),
  KEY `idx_int_sync_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_int_sync_log_external` (`external_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步日志';

CREATE TABLE IF NOT EXISTS `int_request_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `sync_log_id` BIGINT DEFAULT NULL COMMENT '同步日志 ID',
  `method` VARCHAR(16) DEFAULT NULL COMMENT '请求方法',
  `url` VARCHAR(500) DEFAULT NULL COMMENT '请求 URL',
  `headers_json` TEXT COMMENT '请求头 JSON',
  `body` TEXT COMMENT '请求体',
  `requested_at` DATETIME DEFAULT NULL COMMENT '请求时间',
  PRIMARY KEY (`id`),
  KEY `idx_int_request_log_sync` (`sync_log_id`),
  KEY `idx_int_request_log_time` (`requested_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='接口请求';

CREATE TABLE IF NOT EXISTS `int_response_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `sync_log_id` BIGINT DEFAULT NULL COMMENT '同步日志 ID',
  `http_status` INT DEFAULT NULL COMMENT 'HTTP 状态码',
  `body` TEXT COMMENT '响应体',
  `responded_at` DATETIME DEFAULT NULL COMMENT '响应时间',
  `elapsed_ms` INT DEFAULT NULL COMMENT '耗时毫秒',
  PRIMARY KEY (`id`),
  KEY `idx_int_response_log_sync` (`sync_log_id`),
  KEY `idx_int_response_log_time` (`responded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='接口响应';

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

-- ============================================================
-- 数据库：mes_report
-- ============================================================

CREATE DATABASE IF NOT EXISTS `mes_report`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `mes_report`;

CREATE TABLE IF NOT EXISTS `rpt_metric_def` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `metric_code` VARCHAR(64) NOT NULL COMMENT 'metric code',
  `metric_name` VARCHAR(128) NOT NULL COMMENT 'metric name',
  `metric_type` VARCHAR(32) DEFAULT NULL COMMENT 'metric type',
  `calc_expression` TEXT COMMENT 'calculation expression',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'status',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rpt_metric_def_code` (`metric_code`),
  KEY `idx_rpt_metric_def_type` (`metric_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='metric definition';

CREATE TABLE IF NOT EXISTS `rpt_metric_snapshot` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `metric_id` BIGINT DEFAULT NULL COMMENT 'metric id',
  `stat_dimension` VARCHAR(32) DEFAULT NULL COMMENT 'stat dimension',
  `dimension_id` BIGINT DEFAULT NULL COMMENT 'dimension id',
  `stat_period` VARCHAR(32) DEFAULT NULL COMMENT 'stat period',
  `stat_time` DATETIME DEFAULT NULL COMMENT 'stat time',
  `metric_value` DECIMAL(18,6) DEFAULT NULL COMMENT 'metric value',
  PRIMARY KEY (`id`),
  KEY `idx_metric_snapshot` (`metric_id`, `stat_dimension`, `dimension_id`, `stat_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='metric snapshot';

CREATE TABLE IF NOT EXISTS `rpt_query_condition` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `user_id` BIGINT DEFAULT NULL COMMENT 'user id',
  `report_code` VARCHAR(64) DEFAULT NULL COMMENT 'report code',
  `condition_name` VARCHAR(128) DEFAULT NULL COMMENT 'condition name',
  `condition_json` TEXT COMMENT 'condition json',
  PRIMARY KEY (`id`),
  KEY `idx_rpt_query_condition_user_report` (`user_id`, `report_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='report query condition';

CREATE TABLE IF NOT EXISTS `board_config` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `board_code` VARCHAR(64) NOT NULL COMMENT 'board code',
  `board_name` VARCHAR(128) NOT NULL COMMENT 'board name',
  `board_type` VARCHAR(32) DEFAULT NULL COMMENT 'board type',
  `refresh_seconds` INT DEFAULT NULL COMMENT 'refresh seconds',
  `config_json` TEXT COMMENT 'config json',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'status',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_board_config_code` (`board_code`),
  KEY `idx_board_config_type` (`board_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='board config';

CREATE TABLE IF NOT EXISTS `board_data_cache` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `board_id` BIGINT DEFAULT NULL COMMENT 'board id',
  `dimension_id` BIGINT DEFAULT NULL COMMENT 'dimension id',
  `data_json` TEXT COMMENT 'data json',
  `generated_at` DATETIME DEFAULT NULL COMMENT 'generated time',
  `expire_at` DATETIME DEFAULT NULL COMMENT 'expire time',
  PRIMARY KEY (`id`),
  KEY `idx_board_data_cache_board_dimension` (`board_id`, `dimension_id`),
  KEY `idx_board_data_cache_expire` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='board data cache';

CREATE TABLE IF NOT EXISTS `rpt_report_dataset` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `metric_code` VARCHAR(64) NOT NULL COMMENT 'metric code',
  `row_no` INT NOT NULL COMMENT 'row number',
  `stat_date` DATE DEFAULT NULL COMMENT 'stat date',
  `stat_period` VARCHAR(32) DEFAULT NULL COMMENT 'stat period',
  `dimension_type` VARCHAR(32) DEFAULT NULL COMMENT 'dimension type',
  `dimension_code` VARCHAR(64) DEFAULT NULL COMMENT 'dimension code',
  `dimension_name` VARCHAR(128) DEFAULT NULL COMMENT 'dimension name',
  `line_code` VARCHAR(64) DEFAULT NULL COMMENT 'line code',
  `line_name` VARCHAR(128) DEFAULT NULL COMMENT 'line name',
  `work_order_no` VARCHAR(64) DEFAULT NULL COMMENT 'work order no',
  `product_code` VARCHAR(64) DEFAULT NULL COMMENT 'product code',
  `product_name` VARCHAR(128) DEFAULT NULL COMMENT 'product name',
  `business_no` VARCHAR(64) DEFAULT NULL COMMENT 'business no',
  `business_type` VARCHAR(64) DEFAULT NULL COMMENT 'business type',
  `reason_name` VARCHAR(128) DEFAULT NULL COMMENT 'reason name',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT 'operator name',
  `planned_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'planned quantity',
  `reported_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'reported quantity',
  `qualified_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'qualified quantity',
  `defective_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'defective quantity',
  `report_count` INT DEFAULT NULL COMMENT 'report count',
  `availability` DECIMAL(18,6) DEFAULT NULL COMMENT 'availability',
  `performance` DECIMAL(18,6) DEFAULT NULL COMMENT 'performance',
  `quality_rate` DECIMAL(18,6) DEFAULT NULL COMMENT 'quality rate',
  `oee` DECIMAL(18,6) DEFAULT NULL COMMENT 'oee',
  `total_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'total quantity',
  `total_amount` DECIMAL(18,6) DEFAULT NULL COMMENT 'total amount',
  `started_at` DATETIME DEFAULT NULL COMMENT 'started time',
  `ended_at` DATETIME DEFAULT NULL COMMENT 'ended time',
  `status` VARCHAR(32) DEFAULT NULL COMMENT 'status',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (`id`),
  KEY `idx_rpt_report_dataset_metric` (`metric_code`, `row_no`),
  KEY `idx_rpt_report_dataset_date` (`metric_code`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='report export dataset';

CREATE TABLE IF NOT EXISTS `rpt_dashboard_line` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `line_id` BIGINT DEFAULT NULL COMMENT 'line id',
  `line_code` VARCHAR(64) DEFAULT NULL COMMENT 'line code',
  `line_name` VARCHAR(128) DEFAULT NULL COMMENT 'line name',
  `batch_no` VARCHAR(64) DEFAULT NULL COMMENT 'batch no',
  `work_order_no` VARCHAR(64) DEFAULT NULL COMMENT 'work order no',
  `product_name` VARCHAR(128) DEFAULT NULL COMMENT 'product name',
  `planned_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'planned quantity',
  `completed_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'completed quantity',
  `good_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'good quantity',
  `defect_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'defect quantity',
  `oee` DECIMAL(18,6) DEFAULT NULL COMMENT 'oee',
  `performance` DECIMAL(18,6) DEFAULT NULL COMMENT 'performance',
  `output_trend` VARCHAR(128) DEFAULT NULL COMMENT 'output trend',
  `running_order_count` INT DEFAULT NULL COMMENT 'running order count',
  `active_flag` TINYINT DEFAULT 0 COMMENT 'active flag',
  PRIMARY KEY (`id`),
  KEY `idx_rpt_dashboard_line_code` (`line_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='manufacturing dashboard line snapshot';

CREATE TABLE IF NOT EXISTS `rpt_dashboard_metric` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `metric_key` VARCHAR(32) NOT NULL COMMENT 'metric key',
  `metric_value` DECIMAL(18,6) DEFAULT NULL COMMENT 'metric value',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rpt_dashboard_metric_key` (`metric_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='manufacturing dashboard metric snapshot';

CREATE TABLE IF NOT EXISTS `rpt_dashboard_stock` (
  `id` BIGINT NOT NULL COMMENT 'primary key',
  `material_code` VARCHAR(64) DEFAULT NULL COMMENT 'material code',
  `material_name` VARCHAR(128) DEFAULT NULL COMMENT 'material name',
  `unit_name` VARCHAR(32) DEFAULT NULL COMMENT 'unit name',
  `required_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'required quantity',
  `actual_qty` DECIMAL(18,6) DEFAULT NULL COMMENT 'actual quantity',
  `stock_status` VARCHAR(32) DEFAULT NULL COMMENT 'stock status',
  PRIMARY KEY (`id`),
  KEY `idx_rpt_dashboard_stock_material` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='manufacturing dashboard stock snapshot';

USE `mes_report`;

-- 暂不写入初始化数据。
-- 原数据库设计未给出固定指标、报表条件或看板配置初始化数据。

-- ============================================================
-- 跨模块最小联调数据
-- ============================================================
INSERT IGNORE INTO `mes_system`.`md_org` (`id`, `parent_id`, `org_code`, `org_name`, `org_type`, `status`) VALUES
(9000001, NULL, 'ORG-FAN-MES', '风扇制造事业部', '工厂', '启用');

INSERT IGNORE INTO `mes_system`.`md_workshop` (`id`, `workshop_code`, `workshop_name`, `org_id`, `status`) VALUES
(9100001, 'WS-ASSY', '总装车间', 9000001, '启用'),
(9100002, 'WS-QC', '质量车间', 9000001, '启用');

INSERT IGNORE INTO `mes_system`.`md_team` (`id`, `team_code`, `team_name`, `workshop_id`, `leader_id`, `status`) VALUES
(9200001, 'TEAM-A', '总装一班', 9100001, 1000001, '启用');

INSERT IGNORE INTO `mes_system`.`md_unit` (`id`, `unit_code`, `unit_name`, `precision_scale`, `status`) VALUES
(9300001, 'PCS', '件', 0, '启用'),
(9300002, 'SET', '套', 0, '启用');

INSERT IGNORE INTO `mes_system`.`md_item` (`id`, `item_code`, `item_name`, `item_type`, `spec_model`, `unit_id`, `status`) VALUES
(9400001, 'FG-FAN-500', 'FS-500 落地扇', '产品', '500mm', 9300001, '启用'),
(9400002, 'FG-FAN-230', 'TF-230 台扇', '产品', '230mm', 9300001, '启用'),
(9400003, 'MAT-MOTOR-65', '65W 电机', '物料', '65W', 9300001, '启用'),
(9400004, 'MAT-GUARD-500', '500mm 网罩套件', '物料', '500mm', 9300002, '启用');

INSERT IGNORE INTO `mes_system`.`md_production_line` (`id`, `line_code`, `line_name`, `workshop_id`, `capacity_per_hour`, `status`) VALUES
(9500001, 'LINE-ASSY-01', '总装一线', 9100001, 60.000000, '启用'),
(9500002, 'LINE-ASSY-02', '总装二线', 9100001, 45.000000, '启用');

INSERT IGNORE INTO `mes_system`.`md_workstation` (`id`, `station_code`, `station_name`, `line_id`, `process_id`, `status`) VALUES
(9600001, 'ST-ASSY-01', '装配工位一', 9500001, 9700001, '启用'),
(9600002, 'ST-QC-01', '检验工位一', 9500001, 9700002, '启用');

INSERT IGNORE INTO `mes_system`.`md_customer` (`id`, `customer_code`, `customer_name`, `status`) VALUES
(9800001, 'CUS-DEMO', '联调测试客户', '启用');

INSERT IGNORE INTO `mes_production`.`route_process` (`id`, `process_code`, `process_name`, `process_type`, `standard_time`, `status`) VALUES
(9700001, 'PROC-ASSY', '整机装配', '离散', 12.000000, '启用'),
(9700002, 'PROC-QC', '整机检验', '检验', 6.000000, '启用');

INSERT IGNORE INTO `mes_production`.`route_defect_reason` (`id`, `reason_code`, `reason_name`, `process_id`, `status`) VALUES
(9700101, 'DEF-LOOK', '外观划伤', 9700002, '启用'),
(9700102, 'DEF-NOISE', '运行异响', 9700002, '启用');

INSERT IGNORE INTO `mes_production`.`route_header` (`id`, `route_code`, `route_name`, `version_no`, `is_default`, `status`) VALUES
(9700201, 'ROUTE-FAN-ASSY', '风扇总装检验路线', 'V1.0', 1, '启用');

INSERT IGNORE INTO `mes_production`.`route_step` (`id`, `route_id`, `process_id`, `step_no`, `previous_step_id`, `next_step_id`, `workstation_id`, `sop_id`, `quality_required`) VALUES
(9700301, 9700201, 9700001, 10, NULL, 9700302, 9600001, NULL, 0),
(9700302, 9700201, 9700002, 20, 9700301, NULL, 9600002, NULL, 1);

INSERT IGNORE INTO `mes_production`.`route_product` (`id`, `product_id`, `route_id`, `is_default`, `effective_from`, `effective_to`, `status`) VALUES
(9700401, 9400001, 9700201, 1, '2026-07-01', NULL, '启用'),
(9700402, 9400002, 9700201, 1, '2026-07-01', NULL, '启用');

INSERT IGNORE INTO `mes_auth`.`sys_role` (`id`, `role_code`, `role_name`, `status`) VALUES
(1000001, 'MES_TEST_OPERATOR', '联调测试操作员', '启用');

INSERT IGNORE INTO `mes_auth`.`sys_user` (`id`, `username`, `password`, `display_name`, `mobile`, `employee_no`, `org_id`, `team_id`, `status`) VALUES
(1000001, 'test_operator', '123456', '联调测试员', '13800000001', 'EMP-T001', 9000001, 9200001, '启用');

INSERT IGNORE INTO `mes_auth`.`sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(1000001, 1000001, 1000001);

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000101, 'dashboard:view', '生产驾驶舱查看', 'MENU', 'dashboard'),
(1000102, 'production:work-order:view', '生产工单查看', 'MENU', 'production'),
(1000103, 'production:kitting:view', '齐套欠料查看', 'MENU', 'production'),
(1000104, 'production:dispatch:view', '派工单查看', 'MENU', 'production'),
(1000105, 'shopfloor:task:view', '生产任务单查看', 'MENU', 'shopfloor'),
(1000106, 'equipment:view', '设备监控查看', 'MENU', 'equipment'),
(1000107, 'equipment:ledger:view', '设备台账查看', 'MENU', 'equipment'),
(1000108, 'quality:view', '质量检验查看', 'MENU', 'quality'),
(1000109, 'quality:first-last:view', '首末件检验单查看', 'MENU', 'quality'),
(1000110, 'quality:patrol:view', '巡检单查看', 'MENU', 'quality'),
(1000111, 'quality:inbound:view', '成品入库检验单查看', 'MENU', 'quality'),
(1000112, 'quality:outbound:view', '成品发货检验单查看', 'MENU', 'quality');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000201, 1000001, 1000101),
(1000202, 1000001, 1000102),
(1000203, 1000001, 1000103),
(1000204, 1000001, 1000104),
(1000205, 1000001, 1000105),
(1000206, 1000001, 1000106),
(1000207, 1000001, 1000107),
(1000208, 1000001, 1000108),
(1000209, 1000001, 1000109),
(1000210, 1000001, 1000110),
(1000211, 1000001, 1000111),
(1000212, 1000001, 1000112);

INSERT IGNORE INTO `mes_equipment`.`eqp_category` (`id`, `category_code`, `category_name`, `status`) VALUES
(2000001, 'EQC-ASSEMBLY', '装配设备', 'ENABLED'),
(2000002, 'EQC-TEST', '检测设备', 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_manufacturer` (`id`, `manufacturer_code`, `manufacturer_name`, `contact`, `status`) VALUES
(2000101, 'MFR-DEMO', '联调设备制造商', 'service@example.com', 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_equipment` (`id`, `equipment_code`, `equipment_name`, `category_id`, `manufacturer_id`, `line_id`, `station_id`, `model`, `serial_no`, `purchase_date`, `equipment_status`, `status`) VALUES
(2000201, 'EQ-ASSY-01', '自动锁螺丝机 1#', 2000001, 2000101, 9500001, 9600001, 'AS-500', 'SN-AS-202607-001', '2026-01-10', '正常', 'ENABLED'),
(2000202, 'EQ-ASSY-02', '自动锁螺丝机 2#', 2000001, 2000101, 9500002, 9600001, 'AS-500', 'SN-AS-202607-002', '2026-01-12', '待保养', 'ENABLED'),
(2000203, 'EQ-QC-01', '整机测试台 1#', 2000002, 2000101, 9500001, 9600002, 'QC-900', 'SN-QC-202607-001', '2026-02-08', '正常', 'ENABLED'),
(2000204, 'EQ-QC-02', '老化测试架 1#', 2000002, 2000101, 9500002, 9600002, 'AGING-48', 'SN-AG-202607-001', '2026-02-15', '正常', 'ENABLED');

INSERT IGNORE INTO `mes_quality`.`qc_item_category` (`id`, `category_code`, `category_name`, `status`) VALUES
(3000001, 'QCC-APPEARANCE', '外观检验', '启用'),
(3000002, 'QCC-PERFORMANCE', '性能检验', '启用');

INSERT IGNORE INTO `mes_quality`.`qc_item` (`id`, `category_id`, `item_code`, `item_name`, `value_type`, `standard_value`, `upper_limit`, `lower_limit`, `unit_id`, `status`) VALUES
(3000101, 3000001, 'QCI-SCRATCH', '外观划伤', '文本', '无明显划伤', NULL, NULL, NULL, '启用'),
(3000102, 3000002, 'QCI-NOISE', '运行噪声', '数值', NULL, 55.000000, 0.000000, NULL, '启用'),
(3000103, 3000002, 'QCI-SPEED', '风速档位', '数值', NULL, 3.000000, 1.000000, NULL, '启用');

INSERT IGNORE INTO `mes_quality`.`qc_plan` (`id`, `plan_code`, `plan_name`, `product_id`, `customer_id`, `is_default`, `status`) VALUES
(3000201, 'QCP-FAN-STD', '风扇整机通用检验方案', 9400001, 9800001, 1, '启用');

INSERT IGNORE INTO `mes_quality`.`qc_plan_item` (`id`, `plan_id`, `qc_item_id`, `sample_qty`, `standard_value`, `upper_limit`, `lower_limit`, `required_flag`) VALUES
(3000301, 3000201, 3000101, 1.000000, '无明显划伤', NULL, NULL, 1),
(3000302, 3000201, 3000102, 1.000000, NULL, 55.000000, 0.000000, 1),
(3000303, 3000201, 3000103, 1.000000, NULL, 3.000000, 1.000000, 1);

INSERT IGNORE INTO `mes_production`.`prod_work_order` (`id`, `work_order_no`, `external_no`, `source_type`, `product_id`, `plan_qty`, `completed_qty`, `qualified_qty`, `defective_qty`, `unit_id`, `planned_start_at`, `planned_end_at`, `delivery_date`, `line_id`, `route_id`, `status`) VALUES
(4000001, 'WO20260708001', 'ERP-WO-20260708-001', 'ERP', 9400001, 120.000000, 30.000000, 28.000000, 2.000000, 9300001, '2026-07-08 08:00:00', '2026-07-08 18:00:00', '2026-07-10', 9500001, 9700201, '生产中'),
(4000002, 'WO20260708002', 'ERP-WO-20260708-002', 'ERP', 9400002, 80.000000, 0.000000, 0.000000, 0.000000, 9300001, '2026-07-08 09:00:00', '2026-07-08 17:00:00', '2026-07-11', 9500002, 9700201, '已下发'),
(4000003, 'WO20260708003', 'API-WO-20260708-003', 'API', 9400001, 60.000000, 60.000000, 59.000000, 1.000000, 9300001, '2026-07-07 08:00:00', '2026-07-07 16:00:00', '2026-07-09', 9500001, 9700201, '已完成'),
(4000004, 'WO20260708004', 'MAN-WO-20260708-004', '手工', 9400002, 100.000000, 0.000000, 0.000000, 0.000000, 9300001, '2026-07-09 08:00:00', '2026-07-09 18:00:00', '2026-07-12', 9500002, 9700201, '待下发');

INSERT IGNORE INTO `mes_production`.`prod_dispatch_order` (`id`, `dispatch_no`, `work_order_id`, `line_id`, `station_id`, `team_id`, `plan_qty`, `planned_start_at`, `planned_end_at`, `status`) VALUES
(4100001, 'DISP20260708001', 4000001, 9500001, 9600001, 9200001, 60.000000, '2026-07-08 08:00:00', '2026-07-08 12:00:00', '生产中'),
(4100002, 'DISP20260708002', 4000002, 9500002, 9600001, 9200001, 80.000000, '2026-07-08 13:00:00', '2026-07-08 17:00:00', '已下发');

INSERT IGNORE INTO `mes_production`.`shop_task` (`id`, `task_no`, `work_order_id`, `dispatch_id`, `product_id`, `product_name`, `route_id`, `line_id`, `line_name`, `team_id`, `plan_qty`, `started_at`, `ended_at`, `status`) VALUES
(4200001, 'TASK20260708001', 4000001, 4100001, 9400001, 'FS-500 落地扇', 9700201, 9500001, '总装一线', 9200001, 60.000000, '2026-07-08 08:10:00', NULL, '生产中'),
(4200002, 'TASK20260708002', 4000002, 4100002, 9400002, 'TF-230 台扇', 9700201, 9500002, '总装二线', 9200001, 80.000000, NULL, NULL, '已下发'),
(4200003, 'TASK20260708003', 4000001, 4100001, 9400001, 'FS-500 落地扇', 9700201, 9500001, '总装一线', 9200001, 60.000000, NULL, NULL, '已下发');

UPDATE `mes_production`.`shop_task`
SET `product_name` = 'FS-500 落地扇', `line_name` = '总装一线'
WHERE `id` = 4200001;

UPDATE `mes_production`.`shop_task`
SET `product_name` = 'TF-230 台扇', `line_name` = '总装二线'
WHERE `id` = 4200002;

INSERT IGNORE INTO `mes_production`.`shop_operation_task` (`id`, `operation_task_no`, `task_id`, `route_step_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `plan_qty`, `reported_qty`, `status`) VALUES
(4300001, 'OPT20260708001', 4200001, 9700301, 9700001, 9600001, 2000201, 1000001, 60.000000, 30.000000, '生产中'),
(4300002, 'OPT20260708002', 4200001, 9700302, 9700002, 9600002, 2000203, 1000001, 60.000000, 28.000000, '生产中');

INSERT IGNORE INTO `mes_production`.`prod_kitting_analysis` (`id`, `analysis_no`, `work_order_id`, `task_id`, `analysis_time`, `kitting_status`, `missing_count`, `status`) VALUES
(4400001, 'KIT20260708001', 4000001, 4200001, '2026-07-08 07:40:00', '齐套', 0, '已完成'),
(4400002, 'KIT20260708002', 4000002, 4200002, '2026-07-08 08:30:00', '缺料', 1, '已完成');

INSERT IGNORE INTO `mes_production`.`prod_kitting_missing_item` (`id`, `analysis_id`, `material_id`, `required_qty`, `available_qty`, `missing_qty`, `expected_arrival_at`) VALUES
(4400101, 4400001, 9400003, 60.000000, 80.000000, 0.000000, NULL),
(4400102, 4400002, 9400004, 80.000000, 50.000000, 30.000000, '2026-07-08 14:00:00');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_order` (`id`, `inspection_no`, `inspection_type`, `plan_id`, `work_order_id`, `task_id`, `operation_task_id`, `product_id`, `product_name`, `work_order_no`, `barcode_id`, `inspector_id`, `inspection_at`, `final_result`, `status`) VALUES
(5000001, 'QC20260708001', '首件', 3000201, 4000001, 4200001, 4300001, 9400001, 'FS-500 落地扇', 'WO20260708001', NULL, 1000001, '2026-07-08 08:30:00', '合格', '合格'),
(5000002, 'QC20260708002', '巡检', 3000201, 4000001, 4200001, 4300002, 9400001, 'FS-500 落地扇', 'WO20260708001', NULL, 1000001, '2026-07-08 10:00:00', '不合格', '不合格'),
(5000003, 'QC20260708003', '成品入库', 3000201, 4000003, NULL, NULL, 9400001, 'FS-500 落地扇', 'WO20260708003', NULL, 1000001, '2026-07-07 16:20:00', '合格', '合格'),
(5000004, 'QC20260708004', '末件', 3000201, 4000002, 4200002, NULL, 9400002, 'TF-230 台扇', 'WO20260708002', NULL, 1000001, NULL, NULL, '待检');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_result` (`id`, `inspection_id`, `qc_item_id`, `measured_value`, `result`, `defect_reason_id`, `remark`) VALUES
(5000101, 5000001, 3000101, '无明显划伤', '合格', NULL, '首件外观正常'),
(5000102, 5000001, 3000102, '48', '合格', NULL, '噪声正常'),
(5000103, 5000002, 3000101, '网罩轻微划伤', '不合格', 9700101, '巡检发现外观异常');

INSERT IGNORE INTO `mes_quality`.`qc_defect_record` (`id`, `source_type`, `source_id`, `product_id`, `barcode_id`, `process_id`, `defect_reason_id`, `defect_reason_name`, `defect_qty`, `handle_method`, `rework_order_id`, `status`) VALUES
(5000201, '检验', 5000002, 9400001, NULL, 9700002, 9700101, '外观划伤', 2.000000, '返修', NULL, '待处理');

INSERT IGNORE INTO `mes_auth`.`sys_data_scope` (`id`, `role_id`, `scope_type`, `scope_value`) VALUES
(1000401, 1000001, '全部工厂', 'ALL'),
(1000402, 1000001, '车间', '9100001');

INSERT IGNORE INTO `mes_auth`.`sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `module_code`) VALUES
(1000121, 'production:view', '生产订单模块查看', 'MENU', 'production'),
(1000122, 'barcode:view', '条码应用模块查看', 'MENU', 'barcode'),
(1000123, 'barcode:type:view', '条码类型查看', 'MENU', 'barcode'),
(1000124, 'barcode:rule:view', '条码规则查看', 'MENU', 'barcode'),
(1000125, 'barcode:template:view', '条码模板查看', 'MENU', 'barcode'),
(1000126, 'barcode:application-rule:view', '条码应用规则查看', 'MENU', 'barcode'),
(1000127, 'barcode:generate:view', '条码生成查看', 'MENU', 'barcode'),
(1000128, 'barcode:trace:view', '扫码追溯查看', 'MENU', 'barcode'),
(1000129, 'shopfloor:view', '现场管理模块查看', 'MENU', 'shopfloor'),
(1000130, 'shopfloor:config:view', '现场参数配置查看', 'MENU', 'shopfloor'),
(1000131, 'shopfloor:dispatch:view', '现场派工查看', 'MENU', 'shopfloor'),
(1000132, 'shopfloor:product-status:view', '产品生产状态查看', 'MENU', 'shopfloor'),
(1000133, 'shopfloor:report:view', '生产报工查看', 'MENU', 'shopfloor'),
(1000134, 'shopfloor:completion:view', '生产完工单查看', 'MENU', 'shopfloor'),
(1000135, 'shopfloor:repair-order:view', '返修工单查看', 'MENU', 'shopfloor'),
(1000136, 'shopfloor:tablet-task:view', '平板生产任务单查看', 'MENU', 'shopfloor'),
(1000137, 'shopfloor:operation:view', '工序作业台查看', 'MENU', 'shopfloor'),
(1000138, 'shopfloor:sop:view', '电子 SOP 查看', 'MENU', 'shopfloor'),
(1000139, 'shopfloor:tablet-trace:view', '平板产品追溯查看', 'MENU', 'shopfloor'),
(1000140, 'equipment:category:view', '设备类别查看', 'MENU', 'equipment'),
(1000141, 'equipment:maker:view', '设备制造商查看', 'MENU', 'equipment'),
(1000142, 'equipment:fault-reason:view', '故障原因查看', 'MENU', 'equipment'),
(1000143, 'equipment:inspection:view', '设备点检查看', 'MENU', 'equipment'),
(1000144, 'equipment:maintenance-task:view', '保养任务查看', 'MENU', 'equipment'),
(1000145, 'equipment:maintenance:view', '点检保养维修查看', 'MENU', 'equipment'),
(1000146, 'equipment:repair:view', '报修任务查看', 'MENU', 'equipment'),
(1000147, 'equipment:mobile-status:view', '移动设备状态查看', 'MENU', 'equipment'),
(1000148, 'equipment:oee:view', '设备 OEE 查看', 'MENU', 'equipment'),
(1000149, 'equipment:energy:view', '能源分析查看', 'MENU', 'equipment'),
(1000150, 'equipment-integration:view', '设备对接查看', 'MENU', 'equipment-integration'),
(1000151, 'wage:view', '计件工资查看', 'MENU', 'wage'),
(1000152, 'process:view', '工艺管理查看', 'MENU', 'process'),
(1000153, 'quality:item-category:view', '检验项目分类查看', 'MENU', 'quality'),
(1000154, 'quality:standard-plan:view', '检验标准方案查看', 'MENU', 'quality'),
(1000155, 'andon:view', '安灯管理查看', 'MENU', 'andon'),
(1000156, 'andon:type:view', '安灯类型查看', 'MENU', 'andon'),
(1000157, 'andon:config:view', '异常配置查看', 'MENU', 'andon'),
(1000158, 'andon:reason:view', '异常原因查看', 'MENU', 'andon'),
(1000159, 'andon:exception:view', '现场生产异常查看', 'MENU', 'andon'),
(1000160, 'andon:tablet:view', '平板异常安灯查看', 'MENU', 'andon'),
(1000161, 'andon:mobile-launch:view', '移动异常发起查看', 'MENU', 'andon'),
(1000162, 'andon:mobile-handle:view', '移动异常处理查看', 'MENU', 'andon'),
(1000163, 'miniapp:dashboard:view', '小程序实时看板查看', 'MENU', 'miniapp'),
(1000164, 'miniapp:analysis:view', '小程序生产分析查看', 'MENU', 'miniapp'),
(1000165, 'miniapp:trace:view', '小程序产品追溯查看', 'MENU', 'miniapp'),
(1000166, 'report:view', '报表分析查看', 'MENU', 'report'),
(1000167, 'report:material-trace:view', '关键物料追溯查看', 'MENU', 'report'),
(1000168, 'report:product-trace:view', '产品追溯报表查看', 'MENU', 'report'),
(1000169, 'report:output:view', '产量报表查看', 'MENU', 'report'),
(1000170, 'report:realtime:view', '生产实时信息查看', 'MENU', 'report'),
(1000171, 'report:defects:view', '不良查询查看', 'MENU', 'report'),
(1000172, 'report:workshop-period:view', '车间生产时段报表查看', 'MENU', 'report'),
(1000173, 'board:line:view', '产线看板查看', 'MENU', 'board'),
(1000174, 'board:workshop:view', '车间看板查看', 'MENU', 'board'),
(1000175, 'board:control-center:view', '中控看板查看', 'MENU', 'board'),
(1000176, 'erp:view', 'ERP 接口模块查看', 'MENU', 'erp'),
(1000177, 'erp:tasks-read:view', 'ERP 生产任务读取查看', 'MENU', 'erp'),
(1000178, 'erp:routes-read:view', 'ERP 工艺读取查看', 'MENU', 'erp'),
(1000179, 'standard-api:view', '标准 API 模块查看', 'MENU', 'standard-api'),
(1000180, 'standard-api:units-write:view', '计量单位写入查看', 'MENU', 'standard-api'),
(1000181, 'standard-api:work-orders-write:view', '工单写入查看', 'MENU', 'standard-api'),
(1000182, 'standard-api:tasks-write:view', '任务单写入查看', 'MENU', 'standard-api'),
(1000183, 'standard-api:device-count-write:view', '设备计数报工写入查看', 'MENU', 'standard-api'),
(1000184, 'standard-api:completions-read:view', '完工单读取查看', 'MENU', 'standard-api'),
(1000185, 'system:view', '系统管理查看', 'MENU', 'system'),
(1000186, '/api/auth/**', '认证接口访问', 'API', 'auth'),
(1000187, '/api/system/**', '系统接口访问', 'API', 'system'),
(1000188, '/api/production/**', '生产接口访问', 'API', 'production'),
(1000189, '/api/quality/**', '质量接口访问', 'API', 'quality'),
(1000190, '/api/equipment/**', '设备接口访问', 'API', 'equipment'),
(1000191, '/api/report/**', '报表接口访问', 'API', 'report'),
(1000192, '/api/integration/**', '集成接口访问', 'API', 'integration');

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1000301, 1000001, 1000121),
(1000302, 1000001, 1000122),
(1000303, 1000001, 1000123),
(1000304, 1000001, 1000124),
(1000305, 1000001, 1000125),
(1000306, 1000001, 1000126),
(1000307, 1000001, 1000127),
(1000308, 1000001, 1000128),
(1000309, 1000001, 1000129),
(1000310, 1000001, 1000130),
(1000311, 1000001, 1000131),
(1000312, 1000001, 1000132),
(1000313, 1000001, 1000133),
(1000314, 1000001, 1000134),
(1000315, 1000001, 1000135),
(1000316, 1000001, 1000136),
(1000317, 1000001, 1000137),
(1000318, 1000001, 1000138),
(1000319, 1000001, 1000139),
(1000320, 1000001, 1000140),
(1000321, 1000001, 1000141),
(1000322, 1000001, 1000142),
(1000323, 1000001, 1000143),
(1000324, 1000001, 1000144),
(1000325, 1000001, 1000145),
(1000326, 1000001, 1000146),
(1000327, 1000001, 1000147),
(1000328, 1000001, 1000148),
(1000329, 1000001, 1000149),
(1000330, 1000001, 1000150),
(1000331, 1000001, 1000151),
(1000332, 1000001, 1000152),
(1000333, 1000001, 1000153),
(1000334, 1000001, 1000154),
(1000335, 1000001, 1000155),
(1000336, 1000001, 1000156),
(1000337, 1000001, 1000157),
(1000338, 1000001, 1000158),
(1000339, 1000001, 1000159),
(1000340, 1000001, 1000160),
(1000341, 1000001, 1000161),
(1000342, 1000001, 1000162),
(1000343, 1000001, 1000163),
(1000344, 1000001, 1000164),
(1000345, 1000001, 1000165),
(1000346, 1000001, 1000166),
(1000347, 1000001, 1000167),
(1000348, 1000001, 1000168),
(1000349, 1000001, 1000169),
(1000350, 1000001, 1000170),
(1000351, 1000001, 1000171),
(1000352, 1000001, 1000172),
(1000353, 1000001, 1000173),
(1000354, 1000001, 1000174),
(1000355, 1000001, 1000175),
(1000356, 1000001, 1000176),
(1000357, 1000001, 1000177),
(1000358, 1000001, 1000178),
(1000359, 1000001, 1000179),
(1000360, 1000001, 1000180),
(1000361, 1000001, 1000181),
(1000362, 1000001, 1000182),
(1000363, 1000001, 1000183),
(1000364, 1000001, 1000184),
(1000365, 1000001, 1000185),
(1000366, 1000001, 1000186),
(1000367, 1000001, 1000187),
(1000368, 1000001, 1000188),
(1000369, 1000001, 1000189),
(1000370, 1000001, 1000190),
(1000371, 1000001, 1000191),
(1000372, 1000001, 1000192);

INSERT IGNORE INTO `mes_system`.`sys_menu` (`id`, `parent_id`, `menu_code`, `menu_name`, `module_code`, `path`, `sort_no`) VALUES
(6000001, NULL, 'MENU-PRODUCTION', '生产订单', 'production', '/production/orders', 10),
(6000002, NULL, 'MENU-BARCODE', '条码应用', 'barcode', '/barcode/rules', 20),
(6000003, NULL, 'MENU-SHOPFLOOR', '现场管理', 'shopfloor', '/shopfloor/tasks', 30),
(6000004, NULL, 'MENU-EQUIPMENT', '设备管理', 'equipment', '/equipment/ledger', 40),
(6000005, NULL, 'MENU-QUALITY', '质量管理', 'quality', '/quality/inspection', 50),
(6000006, NULL, 'MENU-ANDON', '安灯管理', 'andon', '/andon', 60),
(6000007, NULL, 'MENU-REPORT', '报表分析', 'report', '/report', 70),
(6000008, NULL, 'MENU-BOARD', '电子看板', 'board', '/boards/control-center', 80),
(6000009, NULL, 'MENU-INTEGRATION', '接口集成', 'integration', '/integration', 90),
(6000010, NULL, 'MENU-SYSTEM', '系统管理', 'system', '/system', 100);

INSERT IGNORE INTO `mes_system`.`sys_sequence_rule` (`id`, `rule_code`, `biz_type`, `prefix`, `date_pattern`, `serial_length`, `reset_cycle`, `status`) VALUES
(6000101, 'SEQ-WO', 'WORK_ORDER', 'WO', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000102, 'SEQ-DISP', 'DISPATCH_ORDER', 'DISP', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000103, 'SEQ-QC', 'INSPECTION_ORDER', 'QC', 'yyyyMMdd', 4, 'DAY', '启用'),
(6000104, 'SEQ-ANDON', 'ANDON_EXCEPTION', 'ANDON', 'yyyyMMdd', 4, 'DAY', '启用');

INSERT IGNORE INTO `mes_system`.`sys_sequence_value` (`id`, `rule_id`, `period_key`, `current_value`) VALUES
(6000201, 6000101, '20260708', 4),
(6000202, 6000102, '20260708', 2),
(6000203, 6000103, '20260708', 4),
(6000204, 6000104, '20260708', 1);

INSERT IGNORE INTO `mes_system`.`md_supplier` (`id`, `supplier_code`, `supplier_name`, `status`) VALUES
(9800101, 'SUP-MOTOR', '联调电机供应商', '启用'),
(9800102, 'SUP-PACK', '联调包装供应商', '启用');

INSERT IGNORE INTO `mes_system`.`md_bom` (`id`, `bom_code`, `product_id`, `version_no`, `status`) VALUES
(9800201, 'BOM-FAN-500-V1', 9400001, 'V1.0', '启用'),
(9800202, 'BOM-FAN-230-V1', 9400002, 'V1.0', '启用');

INSERT IGNORE INTO `mes_system`.`md_bom_item` (`id`, `bom_id`, `material_id`, `qty`, `loss_rate`) VALUES
(9800301, 9800201, 9400003, 1.000000, 0.010000),
(9800302, 9800201, 9400004, 1.000000, 0.020000),
(9800303, 9800202, 9400003, 1.000000, 0.010000),
(9800304, 9800202, 9400004, 1.000000, 0.020000);

INSERT IGNORE INTO `mes_system`.`sys_status_log` (`id`, `biz_type`, `biz_id`, `biz_code`, `from_status`, `to_status`, `action`, `operator_id`, `operated_at`, `remark`) VALUES
(6000401, 'WORK_ORDER', 4000001, 'WO20260708001', '已下发', '生产中', 'start', 1000001, '2026-07-08 08:10:00', '联调开工'),
(6000402, 'QC_INSPECTION', 5000002, 'QC20260708002', '检验中', '不合格', 'confirm', 1000001, '2026-07-08 10:30:00', '联调不良确认'),
(6000403, 'ANDON_EXCEPTION', 7600001, 'ANDON20260708001', '待处理', '处理中', 'handle', 1000001, '2026-07-08 10:45:00', '联调异常处理');

INSERT IGNORE INTO `mes_system`.`sys_operation_log` (`id`, `biz_type`, `biz_id`, `action`, `request_body`, `before_data`, `after_data`, `operator_id`, `operated_at`, `result`, `error_message`) VALUES
(6000501, 'WORK_ORDER', 4000001, 'start', '{"status":"生产中"}', '{"status":"已下发"}', '{"status":"生产中"}', 1000001, '2026-07-08 08:10:00', '成功', NULL),
(6000502, 'INTEGRATION', 7000102, 'retry', '{"syncLogId":7000102}', '{"sync_status":"FAILED"}', '{"sync_status":"PENDING"}', 1000001, '2026-07-08 11:00:00', '成功', NULL);

INSERT IGNORE INTO `mes_production`.`sop_file` (`id`, `sop_code`, `sop_name`, `process_id`, `file_type`, `file_url`, `display_order`, `auto_play_flag`, `status`) VALUES
(9700501, 'SOP-ASSY-001', '风扇整机装配 SOP', 9700001, '电子文件', '/sop/fan-assembly.pdf', 1, 1, '启用'),
(9700502, 'SOP-QC-001', '风扇整机检验 SOP', 9700002, '图片', '/sop/fan-qc.png', 2, 0, '启用');

INSERT IGNORE INTO `mes_production`.`bc_type` (`id`, `type_code`, `type_name`, `unique_scope`, `status`) VALUES
(7100001, 'PRODUCT_SN', '产品唯一码', '全局唯一', '启用'),
(7100002, 'MATERIAL_BATCH', '材料批次码', '批次唯一', '启用'),
(7100003, 'CARTON_CODE', '外箱码', '全局唯一', '启用');

INSERT IGNORE INTO `mes_production`.`bc_rule` (`id`, `rule_code`, `rule_name`, `type_id`, `rule_expression`, `serial_length`, `status`) VALUES
(7100101, 'BCR-PRODUCT-SN', '产品 SN 规则', 7100001, 'SN-${yyyyMMdd}-${####}', 4, '启用'),
(7100102, 'BCR-MATERIAL-BATCH', '材料批次规则', 7100002, 'MAT-${itemCode}-${yyyyMMdd}', 3, '启用'),
(7100103, 'BCR-CARTON', '外箱码规则', 7100003, 'CTN-${yyyyMMdd}-${####}', 4, '启用');

INSERT IGNORE INTO `mes_production`.`bc_rule_sequence` (`rule_id`, `sequence_key`, `current_value`, `updated_at`) VALUES
(7100101, '20260708', 2, '2026-07-08 08:02:00'),
(7100103, '20260708', 1, '2026-07-08 11:30:00');

INSERT IGNORE INTO `mes_production`.`bc_template` (`id`, `template_code`, `template_name`, `type_id`, `template_content`, `paper_width`, `paper_height`, `status`) VALUES
(7100201, 'BCT-PRODUCT', '产品标签模板', 7100001, '{"fields":["barcode","item","workOrder"]}', 60.00, 40.00, '启用'),
(7100202, 'BCT-MATERIAL', '物料标签模板', 7100002, '{"fields":["batch","material","supplier"]}', 80.00, 50.00, '启用'),
(7100203, 'BCT-CARTON', '外箱标签模板', 7100003, '{"fields":["carton","qty","product"]}', 100.00, 70.00, '启用');

INSERT IGNORE INTO `mes_production`.`bc_application_rule` (`id`, `app_rule_code`, `item_id`, `type_id`, `rule_id`, `template_id`, `barcode_mode`, `source_type`, `status`) VALUES
(7100301, 'APP-FAN500-SN', 9400001, 7100001, 7100101, 7100201, '唯一码', '规则生成', '启用'),
(7100302, 'APP-MOTOR-BATCH', 9400003, 7100002, 7100102, 7100202, '批次码', '外部导入', '启用'),
(7100303, 'APP-FAN-CARTON', 9400001, 7100003, 7100103, 7100203, '唯一码', '规则生成', '启用');

INSERT IGNORE INTO `mes_production`.`bc_barcode` (`id`, `barcode_value`, `type_id`, `app_rule_id`, `item_id`, `batch_no`, `work_order_id`, `task_id`, `parent_barcode_id`, `print_count`, `source_type`, `status`) VALUES
(7100401, 'SN202607080001', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印'),
(7100402, 'SN202607080002', 7100001, 7100301, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已扫码'),
(7100403, 'MAT-MOTOR-20260708-A', 7100002, 7100302, 9400003, 'MOTOR-0708A', 4000001, 4200001, NULL, 0, '外部导入', '已生成'),
(7100404, 'CTN202607080001', 7100003, 7100303, 9400001, 'BATCH-FAN-0708A', 4000001, 4200001, NULL, 1, '生成', '已打印');

INSERT IGNORE INTO `mes_production`.`bc_print_record` (`id`, `barcode_id`, `template_id`, `print_by`, `print_at`, `print_count`, `printer_name`) VALUES
(7100501, 7100401, 7100201, 1000001, '2026-07-08 08:05:00', 1, 'LABEL-01'),
(7100502, 7100404, 7100203, 1000001, '2026-07-08 11:30:00', 1, 'LABEL-02');

INSERT IGNORE INTO `mes_production`.`shop_product_status` (`id`, `barcode_id`, `task_id`, `operation_task_id`, `current_process_id`, `current_station_id`, `production_status`, `last_report_id`, `updated_at`) VALUES
(7200001, 7100401, 4200001, 4300001, 9700001, 9600001, '生产中', 7200101, '2026-07-08 09:00:00'),
(7200002, 7100402, 4200001, 4300002, 9700002, 9600002, '已完工', 7200102, '2026-07-08 10:20:00');

INSERT IGNORE INTO `mes_production`.`shop_report` (`id`, `report_no`, `report_type`, `task_id`, `operation_task_id`, `product_id`, `barcode_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `report_qty`, `qualified_qty`, `defective_qty`, `report_at`, `status`) VALUES
(7200101, 'RPT20260708001', '普通', 4200001, 4300001, 9400001, 7100401, 9700001, 9600001, 2000201, 1000001, 30.000000, 30.000000, 0.000000, '2026-07-08 09:00:00', '已完成'),
(7200102, 'RPT20260708002', '检测', 4200001, 4300002, 9400001, 7100402, 9700002, 9600002, 2000203, 1000001, 30.000000, 28.000000, 2.000000, '2026-07-08 10:20:00', '已完成'),
(7200103, 'RPT20260708003', '设备计数', 4200001, 4300001, 9400001, NULL, 9700001, 9600001, 2000201, 1000001, 18.000000, 18.000000, 0.000000, '2026-07-08 11:00:00', '已完成');

INSERT IGNORE INTO `mes_production`.`shop_report_material` (`id`, `report_id`, `material_id`, `material_barcode_id`, `qty`) VALUES
(7200201, 7200101, 9400003, 7100403, 30.000000),
(7200202, 7200101, 9400004, NULL, 30.000000);

INSERT IGNORE INTO `mes_production`.`shop_completion_order` (`id`, `completion_no`, `work_order_id`, `task_id`, `product_id`, `completed_qty`, `qualified_qty`, `defective_qty`, `completed_at`, `confirmed_by`, `status`) VALUES
(7200301, 'COMP20260708001', 4000003, NULL, 9400001, 60.000000, 59.000000, 1.000000, '2026-07-07 16:30:00', 1000001, '已完成'),
(7200302, 'COMP20260708002', 4000001, 4200001, 9400001, 30.000000, 28.000000, 2.000000, '2026-07-08 10:30:00', 1000001, '已完成');

INSERT IGNORE INTO `mes_production`.`shop_rework_order` (`id`, `rework_no`, `source_type`, `source_id`, `barcode_id`, `product_id`, `route_id`, `reason_id`, `status`) VALUES
(7200401, 'RW20260708001', '质量检验', 5000201, 7100402, 9400001, 9700201, 9700101, '待处理');

INSERT IGNORE INTO `mes_production`.`trace_event` (`id`, `barcode_id`, `barcode_value`, `event_type`, `biz_type`, `biz_id`, `work_order_id`, `task_id`, `process_id`, `station_id`, `device_id`, `operator_id`, `event_at`) VALUES
(7300001, 7100401, 'SN202607080001', '生成', 'BARCODE', 7100401, 4000001, 4200001, NULL, NULL, NULL, 1000001, '2026-07-08 08:02:00'),
(7300002, 7100401, 'SN202607080001', '扫码', 'SHOP_OPERATION', 4300001, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 08:12:00'),
(7300003, 7100402, 'SN202607080002', '质检', 'QC_INSPECTION', 5000002, 4000001, 4200001, 9700002, 9600002, 2000203, 1000001, '2026-07-08 10:00:00'),
(7300004, 7100403, 'MAT-MOTOR-20260708-A', '关键物料', 'MATERIAL_REPORT', 7200201, 4000001, 4200001, 9700001, 9600001, 2000201, 1000001, '2026-07-08 09:05:00');

INSERT IGNORE INTO `mes_production`.`andon_type` (`id`, `type_code`, `type_name`, `response_minutes`, `status`) VALUES
(7600001, 'ANDON-EQP', '设备异常', 15, '启用'),
(7600002, 'ANDON-MATERIAL', '物料异常', 20, '启用'),
(7600003, 'ANDON-QUALITY', '质量异常', 10, '启用');

INSERT IGNORE INTO `mes_production`.`andon_config` (`id`, `type_id`, `workshop_id`, `line_id`, `notify_role_id`, `escalation_minutes`, `status`) VALUES
(7600101, 7600001, 9100001, 9500001, 1000001, 30, '启用'),
(7600102, 7600003, 9100002, 9500001, 1000001, 20, '启用');

INSERT IGNORE INTO `mes_production`.`andon_reason` (`id`, `reason_code`, `reason_name`, `type_id`, `status`) VALUES
(7600201, 'ANDON-EQP-STOP', '设备停机', 7600001, '启用'),
(7600202, 'ANDON-MAT-LACK', '关键物料短缺', 7600002, '启用'),
(7600203, 'ANDON-QC-NG', '批量质量异常', 7600003, '启用');

INSERT IGNORE INTO `mes_production`.`andon_exception_order` (`id`, `exception_no`, `type_id`, `reason_id`, `work_order_id`, `task_id`, `operation_task_id`, `line_id`, `station_id`, `equipment_id`, `reported_by`, `reported_at`, `assigned_to`, `closed_by`, `closed_at`, `status`) VALUES
(7600301, 'ANDON20260708001', 7600001, 7600201, 4000001, 4200001, 4300001, 9500001, 9600001, 2000201, 1000001, '2026-07-08 10:35:00', 1000001, NULL, NULL, '处理中'),
(7600302, 'ANDON20260708002', 7600002, 7600202, 4000002, 4200002, NULL, 9500002, 9600001, NULL, 1000001, '2026-07-08 08:45:00', 1000001, 1000001, '2026-07-08 09:30:00', '已关闭');

INSERT IGNORE INTO `mes_production`.`andon_notify_record` (`id`, `exception_id`, `notify_user_id`, `notify_channel`, `notify_at`, `read_at`, `result`) VALUES
(7600401, 7600301, 1000001, 'WEB', '2026-07-08 10:35:10', '2026-07-08 10:36:00', '已读'),
(7600402, 7600302, 1000001, 'MOBILE', '2026-07-08 08:45:10', '2026-07-08 08:46:00', '已读');

INSERT IGNORE INTO `mes_production`.`andon_handle_record` (`id`, `exception_id`, `handler_id`, `handle_action`, `handle_content`, `handled_at`, `result`) VALUES
(7600501, 7600301, 1000001, '接单', '设备维护人员已接单', '2026-07-08 10:45:00', '处理中'),
(7600502, 7600302, 1000001, '关闭', '物料已补齐并关闭', '2026-07-08 09:30:00', '已关闭');

INSERT IGNORE INTO `mes_production`.`wage_piece_rate` (`id`, `product_id`, `process_id`, `rate`, `effective_from`, `effective_to`, `status`) VALUES
(7700001, 9400001, 9700001, 1.200000, '2026-07-01', NULL, '启用'),
(7700002, 9400001, 9700002, 0.800000, '2026-07-01', NULL, '启用');

INSERT IGNORE INTO `mes_production`.`wage_piece_detail` (`id`, `report_id`, `operator_id`, `process_id`, `qualified_qty`, `rate`, `amount`, `calc_at`, `status`) VALUES
(7700101, 7200101, 1000001, 9700001, 30.000000, 1.200000, 36.000000, '2026-07-08 12:00:00', '已确认'),
(7700102, 7200102, 1000001, 9700002, 28.000000, 0.800000, 22.400000, '2026-07-08 12:00:00', '待审核');

INSERT IGNORE INTO `mes_production`.`wage_piece_summary` (`id`, `operator_id`, `period_key`, `total_qty`, `total_amount`, `confirmed_by`, `confirmed_at`, `status`) VALUES
(7700201, 1000001, '2026-07', 58.000000, 58.400000, 1000001, '2026-07-08 12:10:00', '已确认');

INSERT IGNORE INTO `mes_production`.`mes_idempotency_record` (`module_name`, `action_name`, `idempotency_key`, `business_id`, `created_at`) VALUES
('production', 'createWorkOrder', 'idem-wo-20260708001', '4000001', '2026-07-08 08:00:00'),
('integration', 'deviceCountReport', 'idem-device-count-20260708001', '7200103', '2026-07-08 11:00:00');

INSERT IGNORE INTO `mes_production`.`mes_operation_log` (`id`, `module_name`, `target_table`, `target_id`, `action_name`, `old_status`, `new_status`, `operator_id`, `remark`, `operated_at`) VALUES
(7800001, 'production', 'prod_work_order', 4000001, 'start', '已下发', '生产中', 1000001, '联调生产开工', '2026-07-08 08:10:00'),
(7800002, 'andon', 'andon_exception_order', 7600301, 'handle', '待处理', '处理中', 1000001, '联调安灯接单', '2026-07-08 10:45:00');

INSERT IGNORE INTO `mes_equipment`.`eqp_fault_reason` (`id`, `reason_code`, `reason_name`, `category_id`, `status`) VALUES
(2000301, 'EQP-JAM', '机构卡滞', 2000001, 'ENABLED'),
(2000302, 'EQP-SENSOR', '传感器异常', 2000002, 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`eqp_maintenance_task` (`id`, `maintenance_no`, `equipment_id`, `plan_at`, `assigned_to`, `completed_at`, `result`, `status`) VALUES
(2000401, 'MNT20260708001', 2000202, '2026-07-08 14:00:00', 1000001, NULL, NULL, 'PENDING'),
(2000402, 'MNT20260707001', 2000201, '2026-07-07 16:00:00', 1000001, '2026-07-07 16:30:00', '正常', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`eqp_inspection_record` (`id`, `inspection_no`, `equipment_id`, `inspector_id`, `inspection_at`, `result`, `abnormal_desc`, `status`) VALUES
(2000501, 'EINS20260708001', 2000201, 1000001, '2026-07-08 07:50:00', '正常', NULL, 'COMPLETED'),
(2000502, 'EINS20260708002', 2000202, 1000001, '2026-07-08 07:55:00', '异常', '导轨润滑不足', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`eqp_repair_order` (`id`, `repair_no`, `equipment_id`, `fault_reason_id`, `reported_by`, `reported_at`, `assigned_to`, `repair_start_at`, `repair_end_at`, `repair_result`, `status`) VALUES
(2000601, 'RPR20260708001', 2000201, 2000301, 1000001, '2026-07-08 10:35:00', 1000001, '2026-07-08 10:45:00', NULL, NULL, 'PROCESSING'),
(2000602, 'RPR20260707001', 2000203, 2000302, 1000001, '2026-07-07 15:00:00', 1000001, '2026-07-07 15:10:00', '2026-07-07 15:40:00', '已修复', 'COMPLETED');

INSERT IGNORE INTO `mes_equipment`.`iot_device_point` (`id`, `equipment_id`, `point_code`, `point_name`, `data_type`, `unit_id`, `status`) VALUES
(2000701, 2000201, 'DP-EQ-ASSY-COUNT', '装配计数', 'NUMBER', 9300001, 'ENABLED'),
(2000702, 2000203, 'DP-EQ-QC-PASS', '检验合格计数', 'NUMBER', 9300001, 'ENABLED'),
(2000703, 2000204, 'DP-EQ-AGING-TEMP', '老化温度', 'NUMBER', NULL, 'ENABLED');

INSERT IGNORE INTO `mes_equipment`.`iot_collect_record` (`id`, `equipment_id`, `point_id`, `collect_value`, `collect_at`, `quality_flag`) VALUES
(2000801, 2000201, 2000701, '18', '2026-07-08 11:00:00', 'GOOD'),
(2000802, 2000203, 2000702, '28', '2026-07-08 10:20:00', 'GOOD'),
(2000803, 2000204, 2000703, '42.5', '2026-07-08 10:30:00', 'GOOD');

INSERT IGNORE INTO `mes_equipment`.`iot_count_report` (`id`, `equipment_id`, `task_id`, `operation_task_id`, `count_qty`, `report_at`, `sync_log_id`) VALUES
(2000901, 2000201, 4200001, 4300001, 18.000000, '2026-07-08 11:00:00', 7000104),
(2000902, 2000203, 4200001, 4300002, 28.000000, '2026-07-08 10:20:00', NULL);

INSERT IGNORE INTO `mes_equipment`.`iot_debug_log` (`id`, `equipment_id`, `request_payload`, `response_payload`, `result`, `debug_at`) VALUES
(2001001, 2000201, '{"point":"DP-EQ-ASSY-COUNT","value":18}', '{"accepted":true}', 'SUCCESS', '2026-07-08 11:00:01'),
(2001002, 2000204, '{"point":"DP-EQ-AGING-TEMP","value":42.5}', '{"accepted":true}', 'SUCCESS', '2026-07-08 10:30:01');

INSERT IGNORE INTO `mes_equipment`.`eqp_oee_snapshot` (`id`, `equipment_id`, `line_id`, `stat_date`, `availability`, `performance`, `quality_rate`, `oee`) VALUES
(2001101, 2000201, 9500001, '2026-07-08', 0.920000, 0.880000, 0.960000, 0.777216),
(2001102, 2000203, 9500001, '2026-07-08', 0.950000, 0.900000, 0.933333, 0.797999);

INSERT IGNORE INTO `mes_equipment`.`eqp_energy_record` (`id`, `equipment_id`, `energy_type`, `value`, `unit_id`, `record_at`) VALUES
(2001201, 2000201, '电', 38.500000, NULL, '2026-07-08 12:00:00'),
(2001202, 2000204, '电', 52.800000, NULL, '2026-07-08 12:00:00');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_order` (`id`, `inspection_no`, `inspection_type`, `plan_id`, `work_order_id`, `task_id`, `operation_task_id`, `product_id`, `product_name`, `work_order_no`, `barcode_id`, `inspector_id`, `inspection_at`, `final_result`, `status`) VALUES
(5000005, 'QC20260708005', '成品发货', 3000201, 4000003, NULL, NULL, 9400001, 'FS-500 落地扇', 'WO20260708003', 7100404, 1000001, '2026-07-08 13:00:00', '让步接收', '让步接收');

INSERT IGNORE INTO `mes_quality`.`qc_inspection_result` (`id`, `inspection_id`, `qc_item_id`, `measured_value`, `result`, `defect_reason_id`, `remark`) VALUES
(5000104, 5000003, 3000103, '3', '合格', NULL, '成品入库风速档位正常'),
(5000105, 5000005, 3000101, '外箱轻微压痕', '让步接收', 9700101, '客户确认让步接收');

INSERT IGNORE INTO `mes_quality`.`mes_operation_log` (`id`, `module_name`, `target_table`, `target_id`, `action_name`, `old_status`, `new_status`, `operator_id`, `remark`, `operated_at`) VALUES
(5000301, 'quality', 'qc_inspection_order', 5000002, 'confirm', '检验中', '不合格', 1000001, '联调质检确认', '2026-07-08 10:30:00'),
(5000302, 'quality', 'qc_defect_record', 5000201, 'create', NULL, '待处理', 1000001, '联调不良记录', '2026-07-08 10:31:00');

INSERT IGNORE INTO `mes_report`.`rpt_metric_def` (`id`, `metric_code`, `metric_name`, `metric_type`, `calc_expression`, `status`) VALUES
(8000001, 'OUTPUT_QTY_DAY', '日产量', '产量', 'sum(shop_report.qualified_qty)', '启用'),
(8000002, 'DEFECT_QTY_DAY', '日不良数', '质量', 'sum(shop_report.defective_qty)', '启用'),
(8000003, 'OEE_DAY', '日 OEE', '设备', 'avg(eqp_oee_snapshot.oee)', '启用'),
(8000004, 'ANDON_OPEN', '未关闭安灯数', '安灯', 'count(andon_exception_order where status not closed)', '启用'),
(8000005, 'WAGE_AMOUNT_MONTH', '月计件金额', '工资', 'sum(wage_piece_summary.total_amount)', '启用');

INSERT IGNORE INTO `mes_report`.`rpt_metric_snapshot` (`id`, `metric_id`, `stat_dimension`, `dimension_id`, `stat_period`, `stat_time`, `metric_value`) VALUES
(8000101, 8000001, '产线', 9500001, '日', '2026-07-08 12:00:00', 76.000000),
(8000102, 8000002, '产线', 9500001, '日', '2026-07-08 12:00:00', 2.000000),
(8000103, 8000003, '设备', 2000201, '日', '2026-07-08 12:00:00', 0.777216),
(8000104, 8000004, '车间', 9100001, '日', '2026-07-08 12:00:00', 1.000000),
(8000105, 8000005, '人员', 1000001, '月', '2026-07-08 12:00:00', 58.400000);

INSERT IGNORE INTO `mes_report`.`rpt_query_condition` (`id`, `user_id`, `report_code`, `condition_name`, `condition_json`) VALUES
(8000201, 1000001, 'OUTPUT_REPORT', '总装一线今日产量', '{"lineId":9500001,"date":"2026-07-08"}'),
(8000202, 1000001, 'DEFECT_REPORT', '今日不良查询', '{"workshopId":9100002,"date":"2026-07-08"}');

INSERT IGNORE INTO `mes_report`.`board_config` (`id`, `board_code`, `board_name`, `board_type`, `refresh_seconds`, `config_json`, `status`) VALUES
(8000301, 'BOARD-LINE-ASSY-01', '总装一线看板', '产线看板', 30, '{"lineId":9500001,"metrics":["OUTPUT_QTY_DAY","DEFECT_QTY_DAY","OEE_DAY"]}', '启用'),
(8000302, 'BOARD-WORKSHOP-ASSY', '总装车间看板', '车间看板', 60, '{"workshopId":9100001,"metrics":["OUTPUT_QTY_DAY","ANDON_OPEN"]}', '启用'),
(8000303, 'BOARD-CONTROL-CENTER', '中控综合看板', '中控看板', 60, '{"modules":["production","quality","equipment","andon"]}', '启用');

INSERT IGNORE INTO `mes_report`.`board_data_cache` (`id`, `board_id`, `dimension_id`, `data_json`, `generated_at`, `expire_at`) VALUES
(8000401, 8000301, 9500001, '{"output":76,"defects":2,"oee":0.777216,"runningOrders":1}', '2026-07-08 12:00:00', '2026-07-08 12:01:00'),
(8000402, 8000302, 9100001, '{"lines":2,"openAndon":1,"completionRate":0.63}', '2026-07-08 12:00:00', '2026-07-08 12:02:00'),
(8000403, 8000303, 9000001, '{"production":"RUNNING","quality":"WARN","equipment":"WARN","andon":"OPEN"}', '2026-07-08 12:00:00', '2026-07-08 12:02:00');

INSERT IGNORE INTO `mes_integration`.`int_external_system` (`id`, `system_code`, `system_name`, `auth_type`, `status`) VALUES
(7000001, 'ERP-DEMO', '联调 ERP 系统', 'TOKEN', 'ENABLED'),
(7000002, 'OPENAPI-DEMO', '联调标准 API 客户端', 'APP_KEY', 'ENABLED'),
(7000003, 'DEVICE-GATEWAY', '设备采集网关', 'SIGN', 'ENABLED');

INSERT IGNORE INTO `mes_integration`.`int_sync_log` (`id`, `system_id`, `interface_code`, `direction`, `biz_type`, `biz_id`, `external_no`, `idempotent_key`, `request_payload`, `response_payload`, `sync_status`, `retry_count`, `error_message`, `started_at`, `finished_at`) VALUES
(7000101, 7000001, 'ERP_PRODUCTION_TASK_READ', 'IN', 'PRODUCTION_TASK', 4200001, 'ERP-TASK-20260708-001', 'erp-task-20260708-001', '{"externalNo":"ERP-TASK-20260708-001"}', '{"success":true,"taskId":4200001}', 'SUCCESS', 0, NULL, '2026-07-08 07:30:00', '2026-07-08 07:30:02'),
(7000102, 7000001, 'ERP_ROUTE_READ', 'IN', 'ROUTE', 9700201, 'ERP-ROUTE-FAN-ASSY', 'erp-route-fan-assy-v1', '{"routeCode":"ROUTE-FAN-ASSY"}', '{"success":false}', 'FAILED', 1, '工序编码重复', '2026-07-08 07:35:00', '2026-07-08 07:35:03'),
(7000103, 7000002, 'OPENAPI_WORK_ORDER_WRITE', 'IN', 'WORK_ORDER', 4000003, 'API-WO-20260708-003', 'api-wo-20260708-003', '{"workOrderNo":"WO20260708003"}', '{"success":true,"id":4000003}', 'SUCCESS', 0, NULL, '2026-07-07 08:00:00', '2026-07-07 08:00:01'),
(7000104, 7000003, 'OPENAPI_DEVICE_COUNT_REPORT_WRITE', 'IN', 'DEVICE_COUNT_REPORT', 2000901, 'DEVICE-COUNT-20260708-001', 'device-count-20260708-001', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '{"success":true}', 'SUCCESS', 0, NULL, '2026-07-08 11:00:00', '2026-07-08 11:00:01'),
(7000105, 7000002, 'OPENAPI_COMPLETION_ORDER_READ', 'OUT', 'COMPLETION_ORDER', 7200301, 'COMP20260708001', 'completion-read-20260708-001', '{"completionNo":"COMP20260708001"}', '{"completedQty":60,"qualifiedQty":59}', 'SUCCESS', 0, NULL, '2026-07-08 13:20:00', '2026-07-08 13:20:01');

INSERT IGNORE INTO `mes_integration`.`int_request_log` (`id`, `sync_log_id`, `method`, `url`, `headers_json`, `body`, `requested_at`) VALUES
(7000201, 7000101, 'POST', '/api/integration/erp/production-tasks/read', '{"Authorization":"Bearer demo"}', '{"externalNo":"ERP-TASK-20260708-001"}', '2026-07-08 07:30:00'),
(7000202, 7000104, 'POST', '/api/openapi/device-count-reports/write', '{"X-App-Key":"demo"}', '{"equipmentCode":"EQ-ASSY-01","countQty":18}', '2026-07-08 11:00:00');

INSERT IGNORE INTO `mes_integration`.`int_response_log` (`id`, `sync_log_id`, `http_status`, `body`, `responded_at`, `elapsed_ms`) VALUES
(7000301, 7000101, 200, '{"success":true,"taskId":4200001}', '2026-07-08 07:30:02', 120),
(7000302, 7000102, 400, '{"success":false,"message":"工序编码重复"}', '2026-07-08 07:35:03', 180),
(7000303, 7000104, 200, '{"success":true}', '2026-07-08 11:00:01', 80);

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

INSERT IGNORE INTO `mes_auth`.`sys_permission`
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

INSERT IGNORE INTO `mes_auth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
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
