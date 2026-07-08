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

