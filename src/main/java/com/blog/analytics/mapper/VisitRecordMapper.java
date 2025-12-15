package com.blog.analytics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.analytics.entity.VisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问记录Mapper接口
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecord> {

    /**
     * 获取指定日期的访问统计
     *
     * @param dateKey 日期键
     * @return 统计结果
     */
    Map<String, Object> getDailyStats(@Param("dateKey") Integer dateKey);

    /**
     * 获取指定日期范围的访问统计
     *
     * @param startDate 开始日期键
     * @param endDate 结束日期键
     * @return 统计结果列表
     */
    List<Map<String, Object>> getRangeStats(@Param("startDate") Integer startDate,
                                           @Param("endDate") Integer endDate);

    /**
     * 获取热门页面统计
     *
     * @param startDate 开始日期键
     * @param endDate 结束日期键
     * @param limit 限制数量
     * @return 热门页面列表
     */
    List<Map<String, Object>> getHotPages(@Param("startDate") Integer startDate,
                                         @Param("endDate") Integer endDate,
                                         @Param("limit") Integer limit);

    /**
     * 获取实时统计（最近一小时）
     *
     * @param startTime 开始时间
     * @return 统计结果
     */
    Map<String, Object> getRealtimeStats(@Param("startTime") LocalDateTime startTime);

    /**
     * 获取小时统计
     *
     * @param dateKey 日期键
     * @return 小时统计列表
     */
    List<Map<String, Object>> getHourlyStats(@Param("dateKey") Integer dateKey);

    /**
     * 删除过期记录
     *
     * @param expireTime 过期时间
     * @return 删除数量
     */
    int deleteExpiredRecords(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 获取独立IP数
     *
     * @param dateKey 日期键
     * @return 独立IP数
     */
    Long getUniqueIpCount(@Param("dateKey") Integer dateKey);

    /**
     * 获取指定日期的页面统计
     *
     * @param dateKey 日期键
     * @return 页面统计列表
     */
    List<Map<String, Object>> getPageStats(@Param("dateKey") Integer dateKey);

    /**
     * 获取指定URL和日期范围的页面统计
     *
     * @param pageUrl 页面URL
     * @param startDate 开始日期键
     * @param endDate 结束日期键
     * @return 页面统计列表
     */
    List<Map<String, Object>> getDailyPageStats(@Param("pageUrl") String pageUrl,
                                                @Param("startDate") Integer startDate,
                                                @Param("endDate") Integer endDate);

    /**
     * 获取地域统计
     *
     * @param startDate 开始日期键
     * @param endDate 结束日期键
     * @param limit 限制数量
     * @return 地域统计列表
     */
    List<Map<String, Object>> getRegionStats(@Param("startDate") Integer startDate,
                                            @Param("endDate") Integer endDate,
                                            @Param("limit") Integer limit);

    /**
     * 获取浏览器统计
     *
     * @param dateKey 日期键
     * @return 浏览器统计列表
     */
    List<Map<String, Object>> getBrowserStats(@Param("dateKey") Integer dateKey);

    /**
     * 获取操作系统统计
     *
     * @param dateKey 日期键
     * @return 操作系统统计列表
     */
    List<Map<String, Object>> getOSStats(@Param("dateKey") Integer dateKey);

    /**
     * 获取访问来源统计
     *
     * @param startDate 开始日期键
     * @param endDate 结束日期键
     * @param limit 限制数量
     * @return 来源统计列表
     */
    List<Map<String, Object>> getRefererStats(@Param("startDate") Integer startDate,
                                             @Param("endDate") Integer endDate,
                                             @Param("limit") Integer limit);
}