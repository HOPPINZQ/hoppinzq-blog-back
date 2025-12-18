package com.blog.analytics.controller;

import com.blog.analytics.dto.*;
import com.blog.analytics.service.AnalyticsService;
import com.blog.analytics.utils.IPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 博客分析控制器
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final OkHttpClient okHttpClient;

    /**
     * 记录页面访问
     */
    @PostMapping("/visit")
    public HoppinResponse<Void> recordVisit(
            @Valid @RequestBody VisitRecordDTO dto,
            HttpServletRequest request) {

        try {
            // 设置IP地址
            dto.setIpAddress(IPUtil.getClientIp(request));

            // 记录访问
            analyticsService.recordVisit(dto);

            log.debug("记录访问成功: pageUrl={}, ip={}", dto.getPageUrl(), dto.getIpAddress());
            return HoppinResponse.success("访问记录成功");

        } catch (Exception e) {
            log.error("记录访问失败", e);
            return HoppinResponse.fail("记录访问失败");
        }
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/stats/today")
        public HoppinResponse<DailyStatsDTO> getTodayStats() {
        try {
            DailyStatsDTO stats = analyticsService.getTodayStats();
            return HoppinResponse.success(stats);
        } catch (Exception e) {
            log.error("获取今日统计失败", e);
            return HoppinResponse.fail("获取今日统计失败");
        }
    }

    /**
     * 获取日期范围统计
     */
    @GetMapping("/stats/range")
    public HoppinResponse<RangeStatsDTO> getRangeStats(
            @RequestParam @NotNull(message = "开始日期不能为空")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @NotNull(message = "结束日期不能为空")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            if (startDate.isAfter(endDate)) {
                return HoppinResponse.fail(400, "开始日期不能晚于结束日期");
            }

            if (startDate.isBefore(endDate.minusYears(1))) {
                return HoppinResponse.fail(400, "查询范围不能超过一年");
            }

            RangeStatsDTO stats = analyticsService.getRangeStats(startDate, endDate);
            return HoppinResponse.success(stats);

        } catch (Exception e) {
            log.error("获取日期范围统计失败", e);
            return HoppinResponse.fail("获取日期范围统计失败");
        }
    }

    /**
     * 获取热门页面排行
     */
    @GetMapping("/stats/hot-pages")
    public HoppinResponse<List<PageStatsDTO>> getHotPages(
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        try {
            List<PageStatsDTO> pages = analyticsService.getHotPages(days, limit);
            return HoppinResponse.success(pages);

        } catch (Exception e) {
            log.error("获取热门页面失败", e);
            return HoppinResponse.fail("获取热门页面失败");
        }
    }

    /**
     * 获取实时访问数据
     */
    @GetMapping("/stats/realtime")
        public HoppinResponse<RealtimeStatsDTO> getRealtimeStats() {
        try {
            RealtimeStatsDTO stats = analyticsService.getRealtimeStats();
            return HoppinResponse.success(stats);

        } catch (Exception e) {
            log.error("获取实时统计失败", e);
            return HoppinResponse.fail("获取实时统计失败");
        }
    }

    /**
     * 获取小时访问统计
     */
    @GetMapping("/stats/hourly")
    public HoppinResponse<List<Integer>> getHourlyStats(
            @RequestParam @NotNull(message = "日期不能为空")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            if (date.isAfter(LocalDate.now())) {
                return HoppinResponse.fail(400, "查询日期不能晚于今天");
            }

            List<Integer> hourlyStats = analyticsService.getHourlyStats(date);
            return HoppinResponse.success(hourlyStats);

        } catch (Exception e) {
            log.error("获取小时统计失败", e);
            return HoppinResponse.fail("获取小时统计失败");
        }
    }

    /**
     * 获取页面访问统计
     */
    @GetMapping("/stats/page")
    public HoppinResponse<List<PageStatsDTO>> getPageStats(
            @RequestParam @NotNull(message = "页面URL不能为空") String pageUrl,
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days) {

        try {
            List<PageStatsDTO> pageStats = analyticsService.getPageStats(pageUrl, days);
            return HoppinResponse.success(pageStats);

        } catch (Exception e) {
            log.error("获取页面统计失败", e);
            return HoppinResponse.fail("获取页面统计失败");
        }
    }

    /**
     * 获取地域访问统计
     */
    @GetMapping("/stats/region")
    public HoppinResponse<List<Object>> getRegionStats(
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        try {
            List<Object> regionStats = analyticsService.getRegionStats(days, limit);
            return HoppinResponse.success(regionStats);

        } catch (Exception e) {
            log.error("获取地域统计失败", e);
            return HoppinResponse.fail("获取地域统计失败");
        }
    }

    /**
     * 获取浏览器访问统计
     */
    @GetMapping("/stats/browser")
    public HoppinResponse<List<Object>> getBrowserStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            LocalDate queryDate = date != null ? date : LocalDate.now();
            if (queryDate.isAfter(LocalDate.now())) {
                return HoppinResponse.fail(400, "查询日期不能晚于今天");
            }

            List<Object> browserStats = analyticsService.getBrowserStats(queryDate);
            return HoppinResponse.success(browserStats);

        } catch (Exception e) {
            log.error("获取浏览器统计失败", e);
            return HoppinResponse.fail("获取浏览器统计失败");
        }
    }

    /**
     * 获取操作系统访问统计
     */
    @GetMapping("/stats/os")
    public HoppinResponse<List<Object>> getOSStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            LocalDate queryDate = date != null ? date : LocalDate.now();
            if (queryDate.isAfter(LocalDate.now())) {
                return HoppinResponse.fail(400, "查询日期不能晚于今天");
            }

            List<Object> osStats = analyticsService.getOSStats(queryDate);
            return HoppinResponse.success(osStats);

        } catch (Exception e) {
            log.error("获取操作系统统计失败", e);
            return HoppinResponse.fail("获取操作系统统计失败");
        }
    }

    /**
     * 获取访问来源统计
     */
    @GetMapping("/stats/referer")
    public HoppinResponse<List<Object>> getRefererStats(
            @RequestParam(defaultValue = "7") @Min(1) @Max(365) int days,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        try {
            List<Object> refererStats = analyticsService.getRefererStats(days, limit);
            return HoppinResponse.success(refererStats);

        } catch (Exception e) {
            log.error("获取来源统计失败", e);
            return HoppinResponse.fail("获取来源统计失败");
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public HoppinResponse<String> health() {
        return HoppinResponse.success("服务运行正常");
    }

    /**
     * 获取API概览信息
     */
    @GetMapping("/info")
    public HoppinResponse<Object> info() {
        try {
            Map<String, Object> info = new HashMap<>();
            info.put("service", "博客分析服务");
            info.put("version", "1.0.0");
            info.put("description", "提供博客访问统计和分析功能");

            List<String> endpoints = new ArrayList<>();
            endpoints.add("/api/analytics/visit - 记录页面访问");
            endpoints.add("/api/analytics/stats/today - 获取今日统计");
            endpoints.add("/api/analytics/stats/range - 获取日期范围统计");
            endpoints.add("/api/analytics/stats/hot-pages - 获取热门页面");
            endpoints.add("/api/analytics/stats/realtime - 获取实时统计");
            endpoints.add("/api/analytics/stats/hourly - 获取小时统计");
            endpoints.add("/api/analytics/stats/page - 获取页面统计");
            endpoints.add("/api/analytics/stats/region - 获取地域统计");
            endpoints.add("/api/analytics/stats/browser - 获取浏览器统计");
            endpoints.add("/api/analytics/stats/os - 获取操作系统统计");
            endpoints.add("/api/analytics/stats/referer - 获取来源统计");
            endpoints.add("/api/analytics/proxy/news/hot-list - 获取新闻热搜");
            endpoints.add("/api/analytics/proxy/weather - 获取天气");
            endpoints.add("/api/analytics/proxy/horoscope - 获取星座运势");

            info.put("endpoints", endpoints);

            return HoppinResponse.success(info);

        } catch (Exception e) {
            log.error("获取API信息失败", e);
            return HoppinResponse.fail("获取API信息失败");
        }
    }
}