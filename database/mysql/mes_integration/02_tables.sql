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

