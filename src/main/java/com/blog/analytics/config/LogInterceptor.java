package com.blog.analytics.config;

import com.blog.analytics.utils.MDCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器，用于为每个请求生成logId并添加到MDC
 */
public class LogInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成并设置logId
        String logId = MDCUtil.generateAndSetLogId();
        logger.debug("Request received: {} {}", request.getMethod(), request.getRequestURI());
        
        // 将logId添加到响应头
        response.setHeader("X-Log-Id", logId);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 处理请求完成后的操作（可选）
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除MDC中的logId，避免内存泄漏
        logger.debug("Request completed: {} {}", request.getMethod(), request.getRequestURI());
        MDCUtil.clear();
    }
}