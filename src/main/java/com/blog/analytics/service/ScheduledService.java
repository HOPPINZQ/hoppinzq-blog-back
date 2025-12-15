package com.blog.analytics.service;

/**
 * 定时任务服务接口
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
public interface ScheduledService {

    /**
     * 每小时同步Redis数据到MySQL
     */
    void syncRedisToMySQLHourly();

    /**
     * 每天凌晨统计数据同步
     */
    void dailyStatsSync();

    /**
     * 清理过期数据（每天凌晨执行）
     */
    void cleanExpiredData();

    /**
     * 生成日报统计（每天凌晨1点执行）
     */
    void generateDailyReport();

    /**
     * 生成周报统计（每周一凌晨2点执行）
     */
    void generateWeeklyReport();

    /**
     * 生成月报统计（每月1号凌晨3点执行）
     */
    void generateMonthlyReport();
}