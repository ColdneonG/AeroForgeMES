USE `mes_report`;

CREATE TABLE IF NOT EXISTS `rpt_metric_def` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `metric_code` VARCHAR(64) NOT NULL COMMENT '指标编码',
  `metric_name` VARCHAR(128) NOT NULL COMMENT '指标名称',
  `metric_type` VARCHAR(32) DEFAULT NULL COMMENT '产量、质量、设备、安灯、工资',
  `calc_expression` TEXT COMMENT '计算口径',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '启用、停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rpt_metric_def_code` (`metric_code`),
  KEY `idx_rpt_metric_def_type` (`metric_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='指标定义';

CREATE TABLE IF NOT EXISTS `rpt_metric_snapshot` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `metric_id` BIGINT DEFAULT NULL COMMENT '指标',
  `stat_dimension` VARCHAR(32) DEFAULT NULL COMMENT '工厂、车间、产线、设备、人员、产品',
  `dimension_id` BIGINT DEFAULT NULL COMMENT '维度 ID',
  `stat_period` VARCHAR(32) DEFAULT NULL COMMENT '小时、日、月',
  `stat_time` DATETIME DEFAULT NULL COMMENT '统计时间',
  `metric_value` DECIMAL(18,6) DEFAULT NULL COMMENT '指标值',
  PRIMARY KEY (`id`),
  KEY `idx_metric_snapshot` (`metric_id`, `stat_dimension`, `dimension_id`, `stat_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='指标快照';

CREATE TABLE IF NOT EXISTS `rpt_query_condition` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `report_code` VARCHAR(64) DEFAULT NULL COMMENT '报表编码',
  `condition_name` VARCHAR(128) DEFAULT NULL COMMENT '条件名称',
  `condition_json` TEXT COMMENT '条件 JSON',
  PRIMARY KEY (`id`),
  KEY `idx_rpt_query_condition_user_report` (`user_id`, `report_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报表查询条件';

CREATE TABLE IF NOT EXISTS `board_config` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `board_code` VARCHAR(64) NOT NULL COMMENT '看板编码',
  `board_name` VARCHAR(128) NOT NULL COMMENT '看板名称',
  `board_type` VARCHAR(32) DEFAULT NULL COMMENT '看板类型',
  `refresh_seconds` INT DEFAULT NULL COMMENT '刷新秒数',
  `config_json` TEXT COMMENT '配置 JSON',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_board_config_code` (`board_code`),
  KEY `idx_board_config_type` (`board_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='看板配置';

CREATE TABLE IF NOT EXISTS `board_data_cache` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `board_id` BIGINT DEFAULT NULL COMMENT '看板 ID',
  `dimension_id` BIGINT DEFAULT NULL COMMENT '维度 ID',
  `data_json` TEXT COMMENT '看板数据 JSON',
  `generated_at` DATETIME DEFAULT NULL COMMENT '生成时间',
  `expire_at` DATETIME DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  KEY `idx_board_data_cache_board_dimension` (`board_id`, `dimension_id`),
  KEY `idx_board_data_cache_expire` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='看板数据缓存';

