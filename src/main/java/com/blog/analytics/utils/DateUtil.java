package com.blog.analytics.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期工具类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
public class DateUtil {

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_KEY_FORMAT = "yyyyMMdd";
    public static final String HOUR_KEY_FORMAT = "yyyyMMddHH";

    /**
     * 日期格式化器
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    public static final DateTimeFormatter DATE_KEY_FORMATTER = DateTimeFormatter.ofPattern(DATE_KEY_FORMAT);
    public static final DateTimeFormatter HOUR_KEY_FORMATTER = DateTimeFormatter.ofPattern(HOUR_KEY_FORMAT);

    /**
     * 获取当前日期键(YYYYMMDD)
     *
     * @return 日期键
     */
    public static Integer getCurrentDateKey() {
        return Integer.parseInt(LocalDate.now().format(DATE_KEY_FORMATTER));
    }

    /**
     * 获取当前小时键(YYYYMMDDHH)
     *
     * @return 小时键
     */
    public static Integer getCurrentHourKey() {
        return Integer.parseInt(LocalDateTime.now().format(HOUR_KEY_FORMATTER));
    }

    /**
     * 获取指定日期的日期键
     *
     * @param date 日期
     * @return 日期键
     */
    public static Integer getDateKey(LocalDate date) {
        return Integer.parseInt(date.format(DATE_KEY_FORMATTER));
    }

    /**
     * 获取指定日期时间的小时键
     *
     * @param dateTime 日期时间
     * @return 小时键
     */
    public static Integer getHourKey(LocalDateTime dateTime) {
        return Integer.parseInt(dateTime.format(HOUR_KEY_FORMATTER));
    }

    /**
     * 将日期键转换为日期字符串
     *
     * @param dateKey 日期键
     * @return 日期字符串
     */
    public static String dateKeyToString(Integer dateKey) {
        if (dateKey == null) {
            return null;
        }
        String dateStr = dateKey.toString();
        if (dateStr.length() != 8) {
            return dateStr;
        }
        return dateStr.substring(0, 4) + "-" +
               dateStr.substring(4, 6) + "-" +
               dateStr.substring(6, 8);
    }

    /**
     * 将日期键转换为LocalDate
     *
     * @param dateKey 日期键
     * @return LocalDate
     */
    public static LocalDate dateKeyToLocalDate(Integer dateKey) {
        if (dateKey == null) {
            return null;
        }
        String dateStr = dateKeyToString(dateKey);
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 将小时键转换为日期时间字符串
     *
     * @param hourKey 小时键
     * @return 日期时间字符串
     */
    public static String hourKeyToString(Integer hourKey) {
        if (hourKey == null) {
            return null;
        }
        String hourStr = hourKey.toString();
        if (hourStr.length() != 10) {
            return hourStr;
        }
        return hourStr.substring(0, 4) + "-" +
               hourStr.substring(4, 6) + "-" +
               hourStr.substring(6, 8) + " " +
               hourStr.substring(8, 10) + ":00:00";
    }

    /**
     * 获取指定日期范围内的所有日期键
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期键列表
     */
    public static List<Integer> getDateKeyRange(LocalDate startDate, LocalDate endDate) {
        List<Integer> dateKeys = new ArrayList<>();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dateKeys;
        }

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateKeys.add(getDateKey(currentDate));
            currentDate = currentDate.plusDays(1);
        }
        return dateKeys;
    }

    /**
     * 获取最近N天的日期键列表
     *
     * @param days 天数
     * @return 日期键列表
     */
    public static List<Integer> getRecentDateKeys(int days) {
        List<Integer> dateKeys = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            dateKeys.add(getDateKey(currentDate.minusDays(i)));
        }
        return dateKeys;
    }

    /**
     * 获取今天、昨天、明天的日期键
     *
     * @return 包含今天、昨天、明天日期键的数组
     */
    public static Integer[] getAroundDateKeys() {
        LocalDate today = LocalDate.now();
        return new Integer[]{
            getDateKey(today.minusDays(1)), // 昨天
            getDateKey(today),              // 今天
            getDateKey(today.plusDays(1))   // 明天
        };
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 获取指定日期是星期几
     *
     * @param date 日期
     * @return 星期几（1-7，1表示周一，7表示周日）
     */
    public static int getDayOfWeek(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getDayOfWeek().getValue();
    }

    /**
     * 获取指定日期是该年的第几天
     *
     * @param date 日期
     * @return 该年的第几天
     */
    public static int getDayOfYear(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getDayOfYear();
    }

    /**
     * 格式化日期时间
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 获取一天的开始时间（00:00:00）
     *
     * @param date 日期
     * @return 一天的开始时间
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    /**
     * 获取一天的结束时间（23:59:59）
     *
     * @param date 日期
     * @return 一天的结束时间
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59);
    }
}