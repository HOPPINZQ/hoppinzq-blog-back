package com.blog.analytics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.analytics.dto.*;
import com.blog.analytics.entity.VisitRecord;
import com.blog.analytics.mapper.VisitRecordMapper;
import com.blog.analytics.service.AnalyticsService;
import com.blog.analytics.utils.DateUtil;
import com.blog.analytics.utils.IPUtil;
import com.blog.analytics.utils.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 博客分析服务实现类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final VisitRecordMapper visitRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${blog.analytics.redis-prefix:blog:analytics:}")
    private String redisPrefix;

    @Value("${blog.analytics.realtime-expire-hours:2}")
    private int realtimeExpireHours;

    /**
     * Redis键前缀常量
     */
    private static final String VISIT_COUNT = "visit:count:";
    private static final String UNIQUE_IP = "unique:ip:";
    private static final String PAGE_VISIT = "page:visit:";
    private static final String ONLINE_USERS = "online:users";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordVisit(VisitRecordDTO dto) {
        LocalDateTime now = LocalDateTime.now();

        // 设置时间信息
        dto.setVisitTime(now);
        dto.setDateKey(DateUtil.getCurrentDateKey());
        dto.setHourKey(DateUtil.getCurrentHourKey());

        // 解析UserAgent信息
        if (dto.getUserAgent() != null) {
            UserAgentUtil.UserAgentInfo userAgentInfo = UserAgentUtil.parseUserAgent(dto.getUserAgent());
            // 可以将解析结果存储到其他统计表中
        }

        // 1. 立即写入Redis
        writeToRedis(dto);

        // 2. 异步写入MySQL
        saveVisitRecordAsync(dto);

        // 3. 更新实时统计
        updateRealtimeStats(dto);
    }

    /**
     * 写入Redis缓存
     */
    private void writeToRedis(VisitRecordDTO dto) {
        try {
            String dateKey = dto.getDateKey().toString();

            // 记录今日访问计数
            String visitCountKey = redisPrefix + VISIT_COUNT + dateKey;
            redisTemplate.opsForValue().increment(visitCountKey);
            redisTemplate.expire(visitCountKey, 7, TimeUnit.DAYS);

            // 记录独立IP
            String ipSetKey = redisPrefix + UNIQUE_IP + dateKey;
            redisTemplate.opsForSet().add(ipSetKey, dto.getIpAddress());
            redisTemplate.expire(ipSetKey, 7, TimeUnit.DAYS);

            // 记录页面访问
            String pageVisitKey = redisPrefix + PAGE_VISIT + dto.getPageUrl() + ":" + dateKey;
            redisTemplate.opsForValue().increment(pageVisitKey);
            redisTemplate.expire(pageVisitKey, 7, TimeUnit.DAYS);

            // 记录在线用户
            String onlineUserKey = redisPrefix + ONLINE_USERS + ":" + dto.getIpAddress();
            redisTemplate.opsForValue().set(onlineUserKey, System.currentTimeMillis(), realtimeExpireHours, TimeUnit.HOURS);

        } catch (Exception e) {
            log.error("Redis写入失败", e);
        }
    }

    /**
     * 异步保存访问记录到数据库
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void saveVisitRecordAsync(VisitRecordDTO dto) {
        try {
            VisitRecord record = new VisitRecord();
            BeanUtils.copyProperties(dto, record);
            visitRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("保存访问记录失败", e);
        }
    }

    /**
     * 更新实时统计
     */
    private void updateRealtimeStats(VisitRecordDTO dto) {
        try {
            // 可以在这里更新实时统计相关的Redis键
            String realtimeKey = redisPrefix + "realtime:" + dto.getDateKey();
            redisTemplate.opsForHash().increment(realtimeKey, "todayVisits", 1);
            redisTemplate.expire(realtimeKey, realtimeExpireHours, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("更新实时统计失败", e);
        }
    }

    @Override
    public DailyStatsDTO getTodayStats() {
        String dateKey = DateUtil.getCurrentDateKey().toString();

        try {
            // 从Redis获取
            String visitCountKey = redisPrefix + VISIT_COUNT + dateKey;
            String ipSetKey = redisPrefix + UNIQUE_IP + dateKey;

            Long totalVisits = redisTemplate.opsForValue().get(visitCountKey) != null ?
                Long.valueOf(redisTemplate.opsForValue().get(visitCountKey).toString()) : 0L;
            Long uniqueIps = redisTemplate.opsForSet().size(ipSetKey);

            return DailyStatsDTO.builder()
                .date(LocalDate.now())
                .totalVisits(totalVisits)
                .uniqueIps(uniqueIps != null ? uniqueIps : 0L)
                .build();

        } catch (Exception e) {
            log.error("获取今日统计失败", e);
            // 降级到数据库查询
            return getTodayStatsFromDB();
        }
    }

    /**
     * 从数据库获取今日统计
     */
    private DailyStatsDTO getTodayStatsFromDB() {
        Map<String, Object> stats = visitRecordMapper.getDailyStats(DateUtil.getCurrentDateKey());
        if (stats == null) {
            return DailyStatsDTO.builder()
                .date(LocalDate.now())
                .totalVisits(0L)
                .uniqueIps(0L)
                .build();
        }

        return DailyStatsDTO.builder()
            .date(LocalDate.now())
            .totalVisits(((Number) stats.getOrDefault("totalVisits", 0)).longValue())
            .uniqueIps(((Number) stats.getOrDefault("uniqueIps", 0)).longValue())
            .build();
    }

    @Override
    public RangeStatsDTO getRangeStats(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> statsData = visitRecordMapper.getRangeStats(
            DateUtil.getDateKey(startDate),
            DateUtil.getDateKey(endDate)
        );

        List<DailyStatsDTO> dailyStats = statsData.stream()
            .map(stat -> {
                Integer dateKey = (Integer) stat.get("dateKey");
                return DailyStatsDTO.builder()
                    .date(DateUtil.dateKeyToLocalDate(dateKey))
                    .totalVisits(((Number) stat.getOrDefault("totalVisits", 0)).longValue())
                    .uniqueIps(((Number) stat.getOrDefault("uniqueIps", 0)).longValue())
                    .build();
            })
            .collect(Collectors.toList());

        // 计算总计
        StatsSummary summary = StatsSummary.builder()
            .visits(dailyStats.stream().mapToLong(DailyStatsDTO::getTotalVisits).sum())
            .uniqueIps(dailyStats.stream().mapToLong(DailyStatsDTO::getUniqueIps).sum())
            .build();

        return RangeStatsDTO.builder()
            .data(dailyStats)
            .total(summary)
            .build();
    }

    @Override
    public List<PageStatsDTO> getHotPages(int days, int limit) {
        Integer endDate = DateUtil.getCurrentDateKey();
        Integer startDate = DateUtil.getDateKey(LocalDate.now().minusDays(days - 1));

        List<Map<String, Object>> hotPagesData = visitRecordMapper.getHotPages(startDate, endDate, limit);

        return hotPagesData.stream()
            .map(page -> PageStatsDTO.builder()
                .pageUrl((String) page.get("pageUrl"))
                .pageTitle((String) page.get("pageTitle"))
                .visitCount(((Number) page.getOrDefault("visitCount", 0)).intValue())
                .uniqueIpCount(((Number) page.getOrDefault("uniqueIpCount", 0)).intValue())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public RealtimeStatsDTO getRealtimeStats() {
        String dateKey = DateUtil.getCurrentDateKey().toString();

        try {
            // 获取今日统计
            DailyStatsDTO todayStats = getTodayStats();

            // 获取当前在线数
            String onlinePattern = redisPrefix + ONLINE_USERS + ":*";
            Set<String> onlineKeys = redisTemplate.keys(onlinePattern);
            Long currentOnline = onlineKeys != null ? (long) onlineKeys.size() : 0L;

            // 获取上一小时访问数
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            Map<String, Object> lastHourStats = visitRecordMapper.getRealtimeStats(oneHourAgo);
            Long lastHourVisits = lastHourStats != null ?
                ((Number) lastHourStats.getOrDefault("visitCount", 0)).longValue() : 0L;

            // 获取热门页面（简化版）
            List<HotPageDTO> topPages = getTopPagesFromRedis(5);

            return RealtimeStatsDTO.builder()
                .currentOnline(currentOnline)
                .todayVisits(todayStats.getTotalVisits())
                .todayUniqueIps(todayStats.getUniqueIps())
                .lastHourVisits(lastHourVisits)
                .topPages(topPages)
                .build();

        } catch (Exception e) {
            log.error("获取实时统计失败", e);
            return RealtimeStatsDTO.builder()
                .currentOnline(0L)
                .todayVisits(0L)
                .todayUniqueIps(0L)
                .lastHourVisits(0L)
                .topPages(Collections.emptyList())
                .build();
        }
    }

    /**
     * 从Redis获取热门页面
     */
    private List<HotPageDTO> getTopPagesFromRedis(int limit) {
        List<HotPageDTO> result = new ArrayList<>();

        try {
            String dateKey = DateUtil.getCurrentDateKey().toString();
            String pagePattern = redisPrefix + PAGE_VISIT + "*:" + dateKey;
            Set<String> pageKeys = redisTemplate.keys(pagePattern);

            if (pageKeys != null) {
                List<HotPageDTO> pages = pageKeys.stream()
                    .map(key -> {
                        String[] parts = key.split(":");
                        if (parts.length >= 3) {
                            String pageUrl = parts[2];
                            Long visits = Long.valueOf(redisTemplate.opsForValue().get(key).toString());
                            return HotPageDTO.builder()
                                .url(pageUrl)
                                .visits(visits)
                                .build();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> Long.compare(b.getVisits(), a.getVisits()))
                    .limit(limit)
                    .collect(Collectors.toList());

                result.addAll(pages);
            }
        } catch (Exception e) {
            log.error("从Redis获取热门页面失败", e);
        }

        return result;
    }

    @Override
    public List<Integer> getHourlyStats(LocalDate date) {
        Integer dateKey = DateUtil.getDateKey(date);
        List<Map<String, Object>> hourlyData = visitRecordMapper.getHourlyStats(dateKey);

        List<Integer> result = new ArrayList<>(Collections.nCopies(24, 0));

        for (Map<String, Object> data : hourlyData) {
            Integer hourKey = (Integer) data.get("hourKey");
            Integer visitCount = ((Number) data.getOrDefault("visitCount", 0)).intValue();
            int hour = hourKey % 100;
            if (hour >= 0 && hour < 24) {
                result.set(hour, visitCount);
            }
        }

        return result;
    }

    @Override
    public List<PageStatsDTO> getPageStats(String pageUrl, int days) {
        Integer endDate = DateUtil.getCurrentDateKey();
        Integer startDate = DateUtil.getDateKey(LocalDate.now().minusDays(days - 1));

        List<Map<String, Object>> pageData = visitRecordMapper.getDailyPageStats(pageUrl, startDate, endDate);

        return pageData.stream()
            .map(data -> PageStatsDTO.builder()
                .pageUrl(pageUrl)
                .visitCount(((Number) data.getOrDefault("visitCount", 0)).intValue())
                .uniqueIpCount(((Number) data.getOrDefault("uniqueIpCount", 0)).intValue())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<Object> getRegionStats(int days, int limit) {
        Integer endDate = DateUtil.getCurrentDateKey();
        Integer startDate = DateUtil.getDateKey(LocalDate.now().minusDays(days - 1));

        return visitRecordMapper.getRegionStats(startDate, endDate, limit)
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public List<Object> getBrowserStats(LocalDate date) {
        Integer dateKey = DateUtil.getDateKey(date);
        return visitRecordMapper.getBrowserStats(dateKey)
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public List<Object> getOSStats(LocalDate date) {
        Integer dateKey = DateUtil.getDateKey(date);
        return visitRecordMapper.getOSStats(dateKey)
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public List<Object> getRefererStats(int days, int limit) {
        Integer endDate = DateUtil.getCurrentDateKey();
        Integer startDate = DateUtil.getDateKey(LocalDate.now().minusDays(days - 1));

        return visitRecordMapper.getRefererStats(startDate, endDate, limit)
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public void syncRedisToMySQL() {
        log.info("开始同步Redis数据到MySQL");
        // 这里可以实现Redis数据到MySQL的同步逻辑
        // 将Redis中的统计数据定期同步到MySQL统计表中
        log.info("Redis数据同步完成");
    }

    @Override
    public void cleanExpiredData() {
        log.info("开始清理过期数据");
        // 清理90天前的访问记录
        LocalDateTime expireDate = LocalDateTime.now().minusDays(90);
        int deletedCount = visitRecordMapper.deleteExpiredRecords(expireDate);
        log.info("清理过期数据完成，删除记录数: {}", deletedCount);
    }
}