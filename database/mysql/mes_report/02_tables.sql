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
