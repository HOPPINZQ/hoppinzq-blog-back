package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * 日期范围统计DTO
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
public class RangeStatsDTO {

    /**
     * 统计数据列表
     */
    private List<DailyStatsDTO> data;

    /**
     * 总计统计
     */
    private StatsSummary total;
}