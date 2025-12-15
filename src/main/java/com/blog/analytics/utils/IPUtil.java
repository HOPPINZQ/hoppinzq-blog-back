package com.blog.analytics.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
public class IPUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String IP_SEPARATOR = ",";

    /**
     * 获取客户端真实IP地址
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，通常X-Forwarded-For会包含多个IP，第一个为真实IP
        if (StringUtils.isNotBlank(ip) && ip.contains(IP_SEPARATOR)) {
            ip = ip.split(IP_SEPARATOR)[0].trim();
        }

        // IPv6 localhost转IPv4
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }

        return StringUtils.isBlank(ip) ? LOCALHOST_IPV4 : ip;
    }

    /**
     * 检查是否为内网IP
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        // IPv4内网地址范围
        return ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.") ||
               LOCALHOST_IPV4.equals(ip) ||
               isLocalhost(ip);
    }

    /**
     * 检查是否为本地IP
     *
     * @param ip IP地址
     * @return 是否为本地IP
     */
    public static boolean isLocalhost(String ip) {
        return LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip) || "localhost".equalsIgnoreCase(ip);
    }

    /**
     * 验证IP地址格式是否正确
     *
     * @param ip IP地址
     * @return 是否为有效IP
     */
    public static boolean isValidIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        // 简单的IP地址格式验证
        return ip.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$") ||
               ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    }

    /**
     * 获取IP归属地（简单实现，实际项目中可以集成第三方IP库）
     *
     * @param ip IP地址
     * @return IP归属地信息
     */
    public static String getIpLocation(String ip) {
        if (StringUtils.isBlank(ip)) {
            return "未知";
        }

        if (isInternalIp(ip)) {
            return "内网IP";
        }

        // 这里可以集成第三方IP地址库，如：
        // - GeoIP2
        // - 纯真IP库
        // - 百度IP定位API等
        // 目前返回默认值
        return "未知地区";
    }
}