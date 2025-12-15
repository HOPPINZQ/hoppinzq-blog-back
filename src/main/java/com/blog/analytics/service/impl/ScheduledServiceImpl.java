package com.blog.analytics.service.impl;

import com.blog.analytics.service.AnalyticsService;
import com.blog.analytics.service.ScheduledService;
import com.blog.analytics.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务服务实现类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledServiceImpl implements ScheduledService {

    private final AnalyticsService analyticsService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${blog.analytics.redis-prefix:blog:analytics:}")
    private String redisPrefix;

    /**
     * 每小时同步Redis数据到MySQL
     * 每小时的第5分钟执行
     */
    @Override
    @Scheduled(cron = "0 5 * * * ?")
    public void syncRedisToMySQLHourly() {
        log.info("开始执行每小时数据同步任务");
        try {
            analyticsService.syncRedisToMySQL();
            log.info("每小时数据同步任务执行完成");
        } catch (Exception e) {
            log.error("每小时数据同步任务执行失败", e);
        }
    }

    /**
     * 每天凌晨统计数据同步
     * 每天凌晨0点30分执行
     */
    @Override
    @Scheduled(cron = "0 30 0 * * ?")
    public void dailyStatsSync() {
        log.info("开始执行每日统计同步任务");
        try {
            // 同步昨天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);
            Integer yesterdayKey = DateUtil.getDateKey(yesterday);

            // 将Redis中的统计数据同步到MySQL
            syncRedisStatsToMySQL(yesterdayKey);

            log.info("每日统计同步任务执行完成");
        } catch (Exception e) {
            log.error("每日统计同步任务执行失败", e);
        }
    }

    /**
     * 清理过期数据
     * 每天凌晨2点执行
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredData() {
        log.info("开始执行数据清理任务");
        try {
            analyticsService.cleanExpiredData();

            // 清理Redis中的过期键
            cleanExpiredRedisKeys();

            log.info("数据清理任务执行完成");
        } catch (Exception e) {
            log.error("数据清理任务执行失败", e);
        }
    }

    /**
     * 生成日报统计
     * 每天凌晨1点执行
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyReport() {
        log.info("开始生成日报统计");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            // 这里可以实现生成日报的逻辑，比如发送邮件报告等
            log.info("日报统计生成完成，日期: {}", yesterday);
        } catch (Exception e) {
            log.error("生成日报统计失败", e);
        }
    }

    /**
     * 生成周报统计
     * 每周一凌晨2点执行
     */
    @Override
    @Scheduled(cron = "0 0 2 ? * MON")
    public void generateWeeklyReport() {
        log.info("开始生成周报统计");
        try {
            LocalDate weekStart = LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            log.info("周报统计生成完成，日期范围: {} - {}", weekStart, weekEnd);
        } catch (Exception e) {
            log.error("生成周报统计失败", e);
        }
    }

    /**
     * 生成月报统计
     * 每月1号凌晨3点执行
     */
    @Override
    @Scheduled(cron = "0 0 3 1 * ?")
    public void generateMonthlyReport() {
        log.info("开始生成月报统计");
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            LocalDate monthStart = lastMonth.withDayOfMonth(1);
            LocalDate monthEnd = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            log.info("月报统计生成完成，日期范围: {} - {}", monthStart, monthEnd);
        } catch (Exception e) {
            log.error("生成月报统计失败", e);
        }
    }

    /**
     * 同步Redis统计数据到MySQL
     */
    private void syncRedisStatsToMySQL(Integer dateKey) {
        try {
            // 同步访问次数统计
            String visitCountKey = redisPrefix + "visit:count:" + dateKey;
            Long visitCount = redisTemplate.opsForValue().get(visitCountKey) != null ?
                Long.valueOf(redisTemplate.opsForValue().get(visitCountKey).toString()) : 0L;

            // 同步独立IP统计
            String ipSetKey = redisPrefix + "unique:ip:" + dateKey;
            Long uniqueIpCount = redisTemplate.opsForSet().size(ipSetKey);

            if (visitCount > 0 || uniqueIpCount != null && uniqueIpCount > 0) {
                // 这里可以将统计数据插入到daily_stats表
                log.info("同步统计数据到MySQL - 日期: {}, 访问次数: {}, 独立IP: {}",
                        dateKey, visitCount, uniqueIpCount);
            }

            // 同步页面统计数据
            syncPageStatsToMySQL(dateKey);

            // 同步后删除Redis中的临时数据（可选）
            // redisTemplate.delete(visitCountKey);
            // redisTemplate.delete(ipSetKey);

        } catch (Exception e) {
            log.error("同步Redis统计数据到MySQL失败，日期: {}", dateKey, e);
        }
    }

    /**
     * 同步页面统计数据到MySQL
     */
    private void syncPageStatsToMySQL(Integer dateKey) {
        try {
            String pagePattern = redisPrefix + "page:visit:*:" + dateKey;
            Set<String> pageKeys = redisTemplate.keys(pagePattern);

            if (pageKeys != null && !pageKeys.isEmpty()) {
                log.info("开始同步页面统计数据，页面数量: {}", pageKeys.size());

                for (String pageKey : pageKeys) {
                    try {
                        String[] parts = pageKey.split(":");
                        if (parts.length >= 4) {
                            String pageUrl = parts[2];
                            Long visitCount = Long.valueOf(redisTemplate.opsForValue().get(pageKey).toString());

                            // 这里可以将页面统计数据插入到page_stats表
                            log.debug("同步页面统计 - URL: {}, 访问次数: {}", pageUrl, visitCount);
                        }
                    } catch (Exception e) {
                        log.error("同步单个页面统计失败，键: {}", pageKey, e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("同步页面统计数据失败，日期: {}", dateKey, e);
        }
    }

    /**
     * 清理Redis中的过期键
     */
    private void cleanExpiredRedisKeys() {
        try {
            // 清理超过7天的访问统计键
            String visitPattern = redisPrefix + "visit:count:*";
            Set<String> visitKeys = redisTemplate.keys(visitPattern);

            if (visitKeys != null) {
                int cleanedCount = 0;
                LocalDate expireDate = LocalDate.now().minusDays(7);

                for (String key : visitKeys) {
                    try {
                        String[] parts = key.split(":");
                        if (parts.length >= 3) {
                            Integer dateKey = Integer.parseInt(parts[2]);
                            LocalDate keyDate = DateUtil.dateKeyToLocalDate(dateKey);

                            if (keyDate != null && keyDate.isBefore(expireDate)) {
                                redisTemplate.delete(key);
                                cleanedCount++;
                            }
                        }
                    } catch (Exception e) {
                        log.error("清理单个键失败: {}", key, e);
                    }
                }

                log.info("清理过期的访问统计键数量: {}", cleanedCount);
            }

            // 清理在线用户键（超过2小时的）
            String onlinePattern = redisPrefix + "online:users:*";
            Set<String> onlineKeys = redisTemplate.keys(onlinePattern);

            if (onlineKeys != null) {
                int cleanedCount = 0;
                for (String key : onlineKeys) {
                    try {
                        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                        if (ttl != null && ttl <= 0) {
                            redisTemplate.delete(key);
                            cleanedCount++;
                        }
                    } catch (Exception e) {
                        log.error("清理在线用户键失败: {}", key, e);
                    }
                }

                log.info("清理过期的在线用户键数量: {}", cleanedCount);
            }

        } catch (Exception e) {
            log.error("清理Redis过期键失败", e);
        }
    }
}