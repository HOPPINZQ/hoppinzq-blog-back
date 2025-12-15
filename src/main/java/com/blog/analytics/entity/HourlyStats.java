package com.blog.analytics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小时统计实体
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hourly_stats")
public class HourlyStats extends BaseEntity {

    /**
     * 小时键(YYYYMMDDHH)
     */
    private Integer hourKey;

    /**
     * 小时字符串(YYYY-MM-DD HH)
     */
    private String hourStr;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 独立IP数
     */
    private Integer uniqueIpCount;
}