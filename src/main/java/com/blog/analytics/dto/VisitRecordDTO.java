package com.blog.analytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 访问记录DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
public class VisitRecordDTO {

    /**
     * 访问的页面URL
     */
    @NotBlank(message = "页面URL不能为空")
    @Size(max = 500, message = "页面URL长度不能超过500个字符")
    private String pageUrl;

    /**
     * 客户端IP地址
     */
    private String ipAddress;

    /**
     * 用户代理信息
     */
    @Size(max = 1000, message = "用户代理信息长度不能超过1000个字符")
    private String userAgent;

    /**
     * 来源页面
     */
    @Size(max = 500, message = "来源页面长度不能超过500个字符")
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