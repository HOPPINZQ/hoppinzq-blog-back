package com.blog.analytics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 每日统计实体
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("daily_stats")
public class DailyStats extends BaseEntity {

    /**
     * 日期键(YYYYMMDD)
     */
    private Integer dateKey;

    /**
     * 日期字符串(YYYY-MM-DD)
     */
    private String dateStr;

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
     * 跳出率(%)
     */
    private BigDecimal bounceRate;

    /**
     * 平均会话时长(秒)
     */
    private Integer avgSessionDuration;
}