package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 页面统计DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class PageStatsDTO {

    /**
     * 页面URL
     */
    private String pageUrl;

    /**
     * 页面标题
     */
    private String pageTitle;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 独立IP数
     */
    private Integer uniqueIpCount;

    /**
     * 平均停留时间
     */
    private Integer avgDuration;

    /**
     * 跳出率
     */
    private Double bounceRate;
}