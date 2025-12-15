package com.blog.analytics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 博客分析应用启动类
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@SpringBootApplication
@MapperScan("com.blog.analytics.mapper")
@EnableAsync
@EnableScheduling
public class BlogAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogAnalyticsApplication.class, args);
    }
}