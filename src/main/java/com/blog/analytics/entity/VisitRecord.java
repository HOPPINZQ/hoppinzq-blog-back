package com.blog.analytics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 访问记录实体
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("visit_record")
public class VisitRecord extends BaseEntity {

    /**
     * 访问的页面URL
     */
    private String pageUrl;

    /**
     * 客户端IP地址
     */
    private String ipAddress;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitTime;

    /**
     * 日期键(YYYYMMDD)
     */
    private Integer dateKey;

    /**
     * 小时键(YYYYMMDDHH)
     */
    private Integer hourKey;
}