package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 热门页面DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class HotPageDTO {

    /**
     * 页面URL
     */
    private String url;

    /**
     * 页面标题
     */
    private String title;

    /**
     * 访问次数
     */
    private Long visits;

    /**
     * 独立IP数
     */
    private Long uniqueIps;
}