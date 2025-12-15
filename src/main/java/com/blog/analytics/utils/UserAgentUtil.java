package com.blog.analytics.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * UserAgent解析工具类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
public class UserAgentUtil {

    /**
     * 浏览器标识符
     */
    private static final Map<String, String> BROWSER_PATTERNS = new HashMap<>();

    /**
     * 操作系统标识符
     */
    private static final Map<String, String> OS_PATTERNS = new HashMap<>();

    /**
     * 设备类型标识符
     */
    private static final Map<String, String> DEVICE_PATTERNS = new HashMap<>();

    static {
        // 初始化浏览器模式
        BROWSER_PATTERNS.put("Chrome", "Chrome");
        BROWSER_PATTERNS.put("Firefox", "Firefox");
        BROWSER_PATTERNS.put("Safari", "Safari");
        BROWSER_PATTERNS.put("Edge", "Edge");
        BROWSER_PATTERNS.put("Opera", "Opera");
        BROWSER_PATTERNS.put("MSIE", "Internet Explorer");
        BROWSER_PATTERNS.put("Trident", "Internet Explorer");
        BROWSER_PATTERNS.put("MicroMessenger", "WeChat");
        BROWSER_PATTERNS.put("QQBrowser", "QQ Browser");
        BROWSER_PATTERNS.put("UCBrowser", "UC Browser");

        // 初始化操作系统模式
        OS_PATTERNS.put("Windows NT 10", "Windows 10");
        OS_PATTERNS.put("Windows NT 6.3", "Windows 8.1");
        OS_PATTERNS.put("Windows NT 6.2", "Windows 8");
        OS_PATTERNS.put("Windows NT 6.1", "Windows 7");
        OS_PATTERNS.put("Windows NT 6.0", "Windows Vista");
        OS_PATTERNS.put("Windows NT 5.1", "Windows XP");
        OS_PATTERNS.put("Windows NT", "Windows");
        OS_PATTERNS.put("Mac OS X", "macOS");
        OS_PATTERNS.put("iPhone OS", "iOS");
        OS_PATTERNS.put("iPad", "iOS");
        OS_PATTERNS.put("Android", "Android");
        OS_PATTERNS.put("Linux", "Linux");
        OS_PATTERNS.put("Ubuntu", "Ubuntu");

        // 初始化设备类型模式
        DEVICE_PATTERNS.put("Mobile", "Mobile");
        DEVICE_PATTERNS.put("Android", "Mobile");
        DEVICE_PATTERNS.put("iPhone", "Mobile");
        DEVICE_PATTERNS.put("iPad", "Tablet");
        DEVICE_PATTERNS.put("Tablet", "Tablet");
        DEVICE_PATTERNS.put("Windows Phone", "Mobile");
    }

    /**
     * 解析UserAgent信息
     *
     * @param userAgent UserAgent字符串
     * @return 解析结果
     */
    public static UserAgentInfo parseUserAgent(String userAgent) {
        UserAgentInfo info = new UserAgentInfo();

        if (StringUtils.isBlank(userAgent)) {
            return info;
        }

        String ua = userAgent.toLowerCase();

        // 解析浏览器
        info.setBrowser(parseBrowser(ua));

        // 解析操作系统
        info.setOs(parseOS(ua));

        // 解析设备类型
        info.setDevice(parseDevice(ua));

        return info;
    }

    /**
     * 解析浏览器信息
     *
     * @param userAgent UserAgent字符串（小写）
     * @return 浏览器名称
     */
    private static String parseBrowser(String userAgent) {
        // 按优先级检查，避免Chrome被Safari误判
        if (userAgent.contains("opera") || userAgent.contains("opr")) {
            return "Opera";
        }
        if (userAgent.contains("edg/")) {
            return "Edge";
        }
        if (userAgent.contains("chrome") && !userAgent.contains("edg/")) {
            return "Chrome";
        }
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        }
        if (userAgent.contains("firefox")) {
            return "Firefox";
        }
        if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "Internet Explorer";
        }
        if (userAgent.contains("micromessenger")) {
            return "WeChat";
        }
        if (userAgent.contains("qqbrowser")) {
            return "QQ Browser";
        }
        if (userAgent.contains("ucbrowser")) {
            return "UC Browser";
        }

        return "Unknown";
    }

    /**
     * 解析操作系统信息
     *
     * @param userAgent UserAgent字符串（小写）
     * @return 操作系统名称
     */
    private static String parseOS(String userAgent) {
        if (userAgent.contains("windows nt 10")) {
            return "Windows 10";
        }
        if (userAgent.contains("windows nt 6.3")) {
            return "Windows 8.1";
        }
        if (userAgent.contains("windows nt 6.2")) {
            return "Windows 8";
        }
        if (userAgent.contains("windows nt 6.1")) {
            return "Windows 7";
        }
        if (userAgent.contains("windows nt 6.0")) {
            return "Windows Vista";
        }
        if (userAgent.contains("windows nt 5.1")) {
            return "Windows XP";
        }
        if (userAgent.contains("windows nt")) {
            return "Windows";
        }
        if (userAgent.contains("iphone os")) {
            return "iOS";
        }
        if (userAgent.contains("ipad")) {
            return "iOS";
        }
        if (userAgent.contains("mac os x")) {
            return "macOS";
        }
        if (userAgent.contains("android")) {
            return "Android";
        }
        if (userAgent.contains("linux")) {
            return "Linux";
        }
        if (userAgent.contains("ubuntu")) {
            return "Ubuntu";
        }

        return "Unknown";
    }

    /**
     * 解析设备类型
     *
     * @param userAgent UserAgent字符串（小写）
     * @return 设备类型
     */
    private static String parseDevice(String userAgent) {
        if (userAgent.contains("mobile") || userAgent.contains("android") ||
            userAgent.contains("iphone") || userAgent.contains("windows phone")) {
            return "Mobile";
        }
        if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        }
        return "Desktop";
    }

    /**
     * 提取浏览器版本
     *
     * @param userAgent UserAgent字符串
     * @return 浏览器版本
     */
    public static String extractBrowserVersion(String userAgent, String browserName) {
        if (StringUtils.isBlank(userAgent) || StringUtils.isBlank(browserName)) {
            return null;
        }

        String ua = userAgent.toLowerCase();
        String browser = browserName.toLowerCase();

        try {
            switch (browser) {
                case "chrome":
                    return extractVersion(ua, "chrome/");
                case "firefox":
                    return extractVersion(ua, "firefox/");
                case "safari":
                    return extractVersion(ua, "safari/");
                case "edge":
                    return extractVersion(ua, "edg/");
                case "opera":
                    return extractVersion(ua, "opr/") != null ? extractVersion(ua, "opr/") : extractVersion(ua, "opera/");
                case "internet explorer":
                    return extractVersion(ua, "msie ") != null ? extractVersion(ua, "msie ") : extractVersion(ua, "rv:");
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从字符串中提取版本号
     *
     * @param text 文本
     * @param prefix 前缀
     * @return 版本号
     */
    private static String extractVersion(String text, String prefix) {
        int index = text.indexOf(prefix);
        if (index == -1) {
            return null;
        }

        int startIndex = index + prefix.length();
        int endIndex = startIndex;
        while (endIndex < text.length() &&
               (Character.isDigit(text.charAt(endIndex)) || text.charAt(endIndex) == '.')) {
            endIndex++;
        }

        return text.substring(startIndex, endIndex);
    }

    /**
     * UserAgent信息类
     */
    public static class UserAgentInfo {
        private String browser;
        private String os;
        private String device;

        public UserAgentInfo() {}

        public UserAgentInfo(String browser, String os, String device) {
            this.browser = browser;
            this.os = os;
            this.device = device;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        @Override
        public String toString() {
            return "UserAgentInfo{" +
                    "browser='" + browser + '\'' +
                    ", os='" + os + '\'' +
                    ", device='" + device + '\'' +
                    '}';
        }
    }
}