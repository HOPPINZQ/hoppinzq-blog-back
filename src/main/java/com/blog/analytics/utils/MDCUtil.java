package com.blog.analytics.utils;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * MDC工具类，用于管理logId
 */
public class MDCUtil {
    public static final String LOG_ID_KEY = "logId";

    /**
     * 生成logId并设置到MDC中
     * @return 生成的logId
     */
    public static String generateAndSetLogId() {
        String logId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(LOG_ID_KEY, logId);
        return logId;
    }

    /**
     * 设置logId到MDC中
     * @param logId logId
     */
    public static void setLogId(String logId) {
        if (logId != null && !logId.isEmpty()) {
            MDC.put(LOG_ID_KEY, logId);
        }
    }

    /**
     * 从MDC中获取logId
     * @return logId
     */
    public static String getLogId() {
        return MDC.get(LOG_ID_KEY);
    }

    /**
     * 从MDC中移除logId
     */
    public static void removeLogId() {
        MDC.remove(LOG_ID_KEY);
    }

    /**
     * 清除所有MDC内容
     */
    public static void clear() {
        MDC.clear();
    }
}