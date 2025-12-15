package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 统计汇总信息
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class StatsSummary {

    /**
     * 总访问次数
     */
    private Long visits;

    /**
     * 总独立IP数
     */
    private Long uniqueIps;

    /**
     * 总页面浏览量
     */
    private Long pageViews;

    /**
     * 平均跳出率
     */
    private Double avgBounceRate;

    /**
     * 平均会话时长
     */
    private Integer avgSessionDuration;
}