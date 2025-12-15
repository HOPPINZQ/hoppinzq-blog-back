package com.blog.analytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;

/**
 * 每日统计DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class DailyStatsDTO {

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 总访问次数
     */
    private Long totalVisits;

    /**
     * 独立IP数
     */
    private Long uniqueIps;

    /**
     * 页面浏览量
     */
    private Long pageViews;

    /**
     * 跳出率
     */
    private Double bounceRate;

    /**
     * 平均会话时长
     */
    private Integer avgSessionDuration;
}