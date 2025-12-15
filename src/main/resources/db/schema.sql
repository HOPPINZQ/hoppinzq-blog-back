-- 博客访问统计数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog_analytics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_analytics;

-- 访问记录表
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_url` varchar(500) NOT NULL COMMENT '访问的页面URL',
  `ip_address` varchar(45) NOT NULL COMMENT '客户端IP地址',
  `user_agent` text COMMENT '用户代理信息',
  `referer` varchar(500) COMMENT '来源页面',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `hour_key` int NOT NULL COMMENT '小时键(YYYYMMDDHH)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_ip_address` (`ip_address`),
  INDEX `idx_date_key` (`date_key`),
  INDEX `idx_hour_key` (`hour_key`),
  INDEX `idx_visit_time` (`visit_time`),
  INDEX `idx_page_url` (`page_url`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问记录表';

-- 每日统计表
DROP TABLE IF EXISTS `daily_stats`;
CREATE TABLE `daily_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `date_key` int NOT NULL UNIQUE COMMENT '日期键(YYYYMMDD)',
  `date_str` varchar(10) NOT NULL COMMENT '日期字符串(YYYY-MM-DD)',
  `total_visits` bigint NOT NULL DEFAULT 0 COMMENT '总访问次数',
  `unique_ips` bigint NOT NULL DEFAULT 0 COMMENT '独立IP数',
  `page_views` bigint NOT NULL DEFAULT 0 COMMENT '页面浏览量',
  `bounce_rate` decimal(5,2) DEFAULT 0.00 COMMENT '跳出率(%)',
  `avg_session_duration` int DEFAULT 0 COMMENT '平均会话时长(秒)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_key` (`date_key`),
  INDEX `idx_date_str` (`date_str`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_total_visits` (`total_visits`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日统计表';

-- 页面访问统计表
DROP TABLE IF EXISTS `page_stats`;
CREATE TABLE `page_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_url` varchar(500) NOT NULL COMMENT '页面URL',
  `page_title` varchar(200) COMMENT '页面标题',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `visit_count` int NOT NULL DEFAULT 1 COMMENT '访问次数',
  `unique_ip_count` int NOT NULL DEFAULT 1 COMMENT '独立IP数',
  `avg_duration` int DEFAULT 0 COMMENT '平均停留时间(秒)',
  `bounce_count` int DEFAULT 0 COMMENT '跳出次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_date` (`page_url`, `date_key`),
  INDEX `idx_page_url` (`page_url`),
  INDEX `idx_date_key` (`date_key`),
  INDEX `idx_visit_count` (`visit_count`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面访问统计表';

-- 小时统计表
DROP TABLE IF EXISTS `hourly_stats`;
CREATE TABLE `hourly_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hour_key` int NOT NULL UNIQUE COMMENT '小时键(YYYYMMDDHH)',
  `hour_str` varchar(13) NOT NULL COMMENT '小时字符串(YYYY-MM-DD HH)',
  `visit_count` int NOT NULL DEFAULT 0 COMMENT '访问次数',
  `unique_ip_count` int NOT NULL DEFAULT 0 COMMENT '独立IP数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hour_key` (`hour_key`),
  INDEX `idx_hour_str` (`hour_str`),
  INDEX `idx_visit_count` (`visit_count`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小时统计表';

-- IP地域统计表
DROP TABLE IF EXISTS `ip_region_stats`;
CREATE TABLE `ip_region_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ip_address` varchar(45) NOT NULL COMMENT 'IP地址',
  `country` varchar(50) COMMENT '国家',
  `province` varchar(50) COMMENT '省份',
  `city` varchar(50) COMMENT '城市',
  `isp` varchar(50) COMMENT '运营商',
  `visit_count` int NOT NULL DEFAULT 1 COMMENT '访问次数',
  `first_visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次访问时间',
  `last_visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ip_address` (`ip_address`),
  INDEX `idx_country` (`country`),
  INDEX `idx_province` (`province`),
  INDEX `idx_city` (`city`),
  INDEX `idx_visit_count` (`visit_count`),
  INDEX `idx_last_visit_time` (`last_visit_time`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IP地域统计表';

-- 访问来源统计表
DROP TABLE IF EXISTS `referer_stats`;
CREATE TABLE `referer_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `referer_domain` varchar(200) NOT NULL COMMENT '来源域名',
  `referer_url` varchar(500) COMMENT '来源URL',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `visit_count` int NOT NULL DEFAULT 1 COMMENT '访问次数',
  `unique_ip_count` int NOT NULL DEFAULT 1 COMMENT '独立IP数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_referer_date` (`referer_domain`, `date_key`),
  INDEX `idx_referer_domain` (`referer_domain`),
  INDEX `idx_date_key` (`date_key`),
  INDEX `idx_visit_count` (`visit_count`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问来源统计表';

-- 用户代理统计表
DROP TABLE IF EXISTS `user_agent_stats`;
CREATE TABLE `user_agent_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `browser` varchar(50) COMMENT '浏览器',
  `browser_version` varchar(20) COMMENT '浏览器版本',
  `os` varchar(50) COMMENT '操作系统',
  `os_version` varchar(20) COMMENT '操作系统版本',
  `device` varchar(20) COMMENT '设备类型',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `visit_count` int NOT NULL DEFAULT 1 COMMENT '访问次数',
  `unique_ip_count` int NOT NULL DEFAULT 1 COMMENT '独立IP数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ua_date` (`browser`, `os`, `device`, `date_key`),
  INDEX `idx_browser` (`browser`),
  INDEX `idx_os` (`os`),
  INDEX `idx_device` (`device`),
  INDEX `idx_date_key` (`date_key`),
  INDEX `idx_visit_count` (`visit_count`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户代理统计表';

-- 添加外键约束（可选，根据性能需求决定是否启用）
-- ALTER TABLE `page_stats` ADD CONSTRAINT `fk_page_stats_date` FOREIGN KEY (`date_key`) REFERENCES `daily_stats` (`date_key`);
-- ALTER TABLE `hourly_stats` ADD CONSTRAINT `fk_hourly_stats_date` FOREIGN KEY (`hour_key`) REFERENCES `daily_stats` (`date_key`);

-- 插入示例数据（可选）
-- INSERT INTO `daily_stats` (`date_key`, `date_str`, `total_visits`, `unique_ips`)
-- VALUES (20251212, '2025-12-12', 0, 0);