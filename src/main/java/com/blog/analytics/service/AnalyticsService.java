package com.blog.analytics.service;

import com.blog.analytics.dto.*;
import com.blog.analytics.dto.VisitRecordDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 博客分析服务接口
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
public interface AnalyticsService {

    /**
     * 记录页面访问
     *
     * @param dto 访问记录DTO
     */
    void recordVisit(VisitRecordDTO dto);

    /**
     * 获取今日统计
     *
     * @return 今日统计
     */
    DailyStatsDTO getTodayStats();

    /**
     * 获取日期范围统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围统计
     */
    RangeStatsDTO getRangeStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取热门页面
     *
     * @param days 统计天数
     * @param limit 返回数量
     * @return 热门页面列表
     */
    List<PageStatsDTO> getHotPages(int days, int limit);

    /**
     * 获取实时统计
     *
     * @return 实时统计
     */
    RealtimeStatsDTO getRealtimeStats();

    /**
     * 获取小时统计
     *
     * @param date 日期
     * @return 小时统计列表
     */
    List<Integer> getHourlyStats(LocalDate date);

    /**
     * 获取页面访问统计
     *
     * @param pageUrl 页面URL
     * @param days 统计天数
     * @return 页面统计列表
     */
    List<PageStatsDTO> getPageStats(String pageUrl, int days);

    /**
     * 获取地域统计
     *
     * @param days 统计天数
     * @param limit 返回数量
     * @return 地域统计列表
     */
    List<Object> getRegionStats(int days, int limit);

    /**
     * 获取浏览器统计
     *
     * @param date 日期
     * @return 浏览器统计列表
     */
    List<Object> getBrowserStats(LocalDate date);

    /**
     * 获取操作系统统计
     *
     * @param date 日期
     * @return 操作系统统计列表
     */
    List<Object> getOSStats(LocalDate date);

    /**
     * 获取访问来源统计
     *
     * @param days 统计天数
     * @param limit 返回数量
     * @return 来源统计列表
     */
    List<Object> getRefererStats(int days, int limit);

    /**
     * 同步Redis数据到MySQL
     */
    void syncRedisToMySQL();

    /**
     * 清理过期数据
     */
    void cleanExpiredData();
}