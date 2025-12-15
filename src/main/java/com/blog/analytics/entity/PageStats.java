package com.blog.analytics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 页面访问统计实体
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("page_stats")
public class PageStats extends BaseEntity {

    /**
     * 页面URL
     */
    private String pageUrl;

    /**
     * 页面标题
     */
    private String pageTitle;

    /**
     * 日期键(YYYYMMDD)
     */
    private Integer dateKey;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 独立IP数
     */
    private Integer uniqueIpCount;

    /**
     * 平均停留时间(秒)
     */
    private Integer avgDuration;

    /**
     * 跳出次数
     */
    private Integer bounceCount;
}