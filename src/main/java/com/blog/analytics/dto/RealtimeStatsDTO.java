package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

/**
 * 实时统计DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class RealtimeStatsDTO {

    /**
     * 当前在线人数
     */
    private Long currentOnline;

    /**
     * 今日访问次数
     */
    private Long todayVisits;

    /**
     * 今日独立IP数
     */
    private Long todayUniqueIps;

    /**
     * 上一小时访问次数
     */
    private Long lastHourVisits;

    /**
     * 热门页面列表
     */
    private List<HotPageDTO> topPages;
}