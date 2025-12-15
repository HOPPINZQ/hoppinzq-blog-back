package com.blog.analytics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据库连接测试
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Configuration
@Slf4j
public class DatabaseTest {

    @Bean
    public CommandLineRunner testDatabaseConnection(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // 测试数据源
                log.info("数据源类型: {}", dataSource.getClass().getName());
                log.info("测试数据库连接...");

                // 执行简单查询测试连接
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.info("数据库连接测试成功，返回值: {}", result);

                // 测试数据库版本
                String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
                log.info("MySQL版本: {}", version);

                // 测试数据库访问
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY)");
                log.info("数据库表创建测试成功");

                // 清理测试表
                jdbcTemplate.execute("DROP TABLE IF EXISTS test_table");
                log.info("测试表清理完成");

                log.info("数据库连接配置验证通过！");

            } catch (Exception e) {
                log.error("数据库连接测试失败！", e);
                throw e;
            }
        };
    }
}