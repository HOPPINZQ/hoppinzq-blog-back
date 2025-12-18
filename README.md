# 📊 博客访问统计后端功能开发大纲

## 1. 项目现状分析

### 后端技术栈（选定）
- **后端框架**: Java 8 + Spring Boot 2.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis 7+
- **ORM**: MyBatis-Plus 3.5
- **构建工具**: Maven

### 需求分析
- 添加后端访问统计功能
- 记录网站总访问次数
- 记录单篇文章访问次数
- 记录客户端IP地址
- 提供统计数据查询接口
- 简化架构，无需登录认证

## 2. 访问统计数据结构设计

### 2.1 数据库表结构

#### visit_record 表 (访问记录)
```sql
CREATE TABLE `visit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_url` varchar(500) NOT NULL COMMENT '访问的页面URL',
  `ip_address` varchar(45) NOT NULL COMMENT '客户端IP地址',
  `user_agent` text COMMENT '用户代理信息',
  `referer` varchar(500) COMMENT '来源页面',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `hour_key` int NOT NULL COMMENT '小时键(YYYYMMDDHH)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_ip_address` (`ip_address`),
  INDEX `idx_date_key` (`date_key`),
  INDEX `idx_hour_key` (`hour_key`),
  INDEX `idx_visit_time` (`visit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问记录表';
```

#### daily_stats 表 (每日统计)
```sql
CREATE TABLE `daily_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_key` int NOT NULL UNIQUE COMMENT '日期键(YYYYMMDD)',
  `total_visits` bigint NOT NULL DEFAULT 0 COMMENT '总访问次数',
  `unique_ips` bigint NOT NULL DEFAULT 0 COMMENT '独立IP数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`date_key`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计表';
```

#### page_stats 表 (页面访问统计)
```sql
CREATE TABLE `page_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `page_url` varchar(500) NOT NULL COMMENT '页面URL',
  `date_key` int NOT NULL COMMENT '日期键(YYYYMMDD)',
  `visit_count` int NOT NULL DEFAULT 1 COMMENT '访问次数',
  `unique_ip_count` int NOT NULL DEFAULT 1 COMMENT '独立IP数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_date` (`page_url`, `date_key`),
  INDEX `idx_date_key` (`date_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面访问统计表';
```

### 2.2 Java 实体类定义

```java
// VisitRecord.java
@Data
@TableName("visit_record")
public class VisitRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String pageUrl;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private LocalDateTime visitTime;
    private Integer dateKey;
    private Integer hourKey;
    private LocalDateTime createdAt;
}

// DailyStats.java
@Data
@TableName("daily_stats")
public class DailyStats {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer dateKey;
    private Long totalVisits;
    private Long uniqueIps;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// PageStats.java
@Data
@TableName("page_stats")
public class PageStats {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String pageUrl;
    private Integer dateKey;
    private Integer visitCount;
    private Integer uniqueIpCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 3. RESTful API接口设计

### 3.1 访问统计相关接口

#### POST /api/analytics/visit
**功能**: 记录页面访问
```java
// 请求体 (VisitRecordDTO)
{
  "pageUrl": "/posts/article-slug",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "referer": "https://google.com"
}

// 响应
{
  "success": true,
  "message": "访问记录成功"
}
```

#### GET /api/analytics/stats/today
**功能**: 获取今日访问统计
```java
// 响应
{
  "totalVisits": 245,
  "uniqueIps": 189,
  "date": "2025-12-08"
}
```

#### GET /api/analytics/stats/range
**功能**: 获取指定日期范围统计
**查询参数**:
- `startDate`: 开始日期 (yyyy-MM-dd)
- `endDate`: 结束日期 (yyyy-MM-dd)

```java
// 响应
{
  "data": [
    {
      "date": "2025-12-01",
      "totalVisits": 320,
      "uniqueIps": 256
    },
    {
      "date": "2025-12-02",
      "totalVisits": 298,
      "uniqueIps": 241
    }
  ],
  "total": {
    "visits": 618,
    "uniqueIps": 497
  }
}
```

#### GET /api/analytics/stats/hot-pages
**功能**: 获取热门页面排行
**查询参数**:
- `days`: 统计天数 (默认: 7)
- `limit`: 返回数量 (默认: 10)

```java
// 响应
{
  "data": [
    {
      "pageUrl": "/posts/java-tutorial",
      "title": "Java教程",
      "visitCount": 1250,
      "uniqueIpCount": 980
    },
    {
      "pageUrl": "/posts/spring-boot-guide",
      "title": "Spring Boot指南",
      "visitCount": 986,
      "uniqueIpCount": 765
    }
  ]
}
```

#### GET /api/analytics/stats/realtime
**功能**: 获取实时访问数据
```java
// 响应 (从Redis获取)
{
  "currentOnline": 12,
  "todayVisits": 245,
  "todayUniqueIps": 189,
  "lastHourVisits": 38,
  "topPages": [
    {
      "url": "/",
      "visits": 89
    }
  ]
}
```

## 4. 技术选型建议

### 4.1 后端框架选择

#### 推荐方案: Java 8 + Spring Boot 2.x
**优势**:
- 成熟稳定的企业级框架，生态完善
- 强大的依赖注入和AOP特性
- 丰富的监控和管理功能
- 优秀的性能表现
- 与现有Java生态系统无缝集成

**核心依赖**:
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.5</version>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 4.2 数据库选择

#### 推荐方案: MySQL 8.0+
**优势**:
- 成熟稳定的开源数据库
- 优秀的读写性能
- 丰富的索引类型和优化特性
- 完善的主从复制和集群方案
- 与Spring Boot集成良好

**优化配置**:
- 使用 InnoDB 存储引擎
- 配置适当的缓冲池大小
- 启用查询缓存
- 定期优化表结构

### 4.3 缓存选择: Redis 7+

**优势**:
- 高性能的内存数据库
- 支持多种数据结构
- 持久化支持
- 集群和哨兵模式
- 丰富的Java客户端

**使用场景**:
- 实时访问计数
- 热点页面缓存
- IP去重统计
- 限流控制

### 4.4 ORM选择: MyBatis-Plus

**优势**:
- 简化CRUD操作
- 强大的条件构造器
- 内置分页插件
- 代码生成器
- 兼容MyBatis所有功能

## 5. Java 代码示例

### 5.1 项目结构

```
blog-analytics/
├── src/main/java/com/blog/analytics/
│   ├── AnalyticsApplication.java
│   ├── config/
│   │   ├── RedisConfig.java
│   │   └── MybatisPlusConfig.java
│   ├── controller/
│   │   └── AnalyticsController.java
│   ├── service/
│   │   ├── AnalyticsService.java
│   │   └── impl/
│   │       └── AnalyticsServiceImpl.java
│   ├── mapper/
│   │   └── AnalyticsMapper.java
│   ├── entity/
│   │   ├── VisitRecord.java
│   │   ├── DailyStats.java
│   │   └── PageStats.java
│   ├── dto/
│   │   ├── VisitRecordDTO.java
│   │   └── StatsResponseDTO.java
│   └── utils/
│       ├── IPUtil.java
│       └── DateUtil.java
└── src/main/resources/
    ├── application.yml
    └── mapper/
        └── AnalyticsMapper.xml
```

### 5.2 Controller 层

```java
// AnalyticsController.java
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * 记录页面访问
     */
    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<Void>> recordVisit(
            @RequestBody VisitRecordDTO dto,
            HttpServletRequest request) {
        // 设置IP地址
        dto.setIpAddress(IPUtil.getClientIp(request));

        // 记录访问
        analyticsService.recordVisit(dto);

        return ResponseEntity.ok(ApiResponse.success("访问记录成功"));
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/stats/today")
    public ResponseEntity<ApiResponse<DailyStatsDTO>> getTodayStats() {
        DailyStatsDTO stats = analyticsService.getTodayStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 获取日期范围统计
     */
    @GetMapping("/stats/range")
    public ResponseEntity<ApiResponse<RangeStatsDTO>> getRangeStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        RangeStatsDTO stats = analyticsService.getRangeStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 获取热门页面
     */
    @GetMapping("/stats/hot-pages")
    public ResponseEntity<ApiResponse<List<PageStatsDTO>>> getHotPages(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int limit) {
        List<PageStatsDTO> pages = analyticsService.getHotPages(days, limit);
        return ResponseEntity.ok(ApiResponse.success(pages));
    }

    /**
     * 获取实时统计
     */
    @GetMapping("/stats/realtime")
    public ResponseEntity<ApiResponse<RealtimeStatsDTO>> getRealtimeStats() {
        RealtimeStatsDTO stats = analyticsService.getRealtimeStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
```

### 5.3 Service 层核心实现

```java
// AnalyticsServiceImpl.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsMapper analyticsMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AsyncService asyncService;

    @Override
    public void recordVisit(VisitRecordDTO dto) {
        LocalDateTime now = LocalDateTime.now();

        // 设置时间信息
        dto.setVisitTime(now);
        dto.setDateKey(Integer.parseInt(now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        dto.setHourKey(Integer.parseInt(now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"))));

        // 1. 立即写入Redis
        writeToRedis(dto);

        // 2. 异步写入MySQL
        asyncService.saveVisitRecord(dto);

        // 3. 更新实时统计
        updateRealtimeStats(dto);
    }

    private void writeToRedis(VisitRecordDTO dto) {
        try {
            String dateKey = dto.getDateKey().toString();

            // 记录今日访问计数
            String visitCountKey = String.format("analytics:visit:%s", dateKey);
            redisTemplate.opsForValue().increment(visitCountKey);

            // 记录独立IP
            String ipSetKey = String.format("analytics:ip:%s", dateKey);
            redisTemplate.opsForSet().add(ipSetKey, dto.getIpAddress());

            // 记录页面访问
            String pageVisitKey = String.format("analytics:page:%s:%s",
                dto.getPageUrl(), dateKey);
            redisTemplate.opsForValue().increment(pageVisitKey);

            // 设置过期时间（7天）
            redisTemplate.expire(visitCountKey, 7, TimeUnit.DAYS);
            redisTemplate.expire(ipSetKey, 7, TimeUnit.DAYS);
            redisTemplate.expire(pageVisitKey, 7, TimeUnit.DAYS);

        } catch (Exception e) {
            log.error("Redis写入失败", e);
        }
    }

    @Override
    public DailyStatsDTO getTodayStats() {
        String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 从Redis获取
        String visitCountKey = String.format("analytics:visit:%s", dateKey);
        String ipSetKey = String.format("analytics:ip:%s", dateKey);

        Long totalVisits = redisTemplate.opsForValue().get(visitCountKey) != null ?
            Long.valueOf(redisTemplate.opsForValue().get(visitCountKey).toString()) : 0L;
        Long uniqueIps = redisTemplate.opsForSet().size(ipSetKey);

        return DailyStatsDTO.builder()
            .date(LocalDate.now())
            .totalVisits(totalVisits)
            .uniqueIps(uniqueIps != null ? uniqueIps : 0L)
            .build();
    }

    @Override
    public RealtimeStatsDTO getRealtimeStats() {
        String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 获取实时数据
        Long todayVisits = getVisitCount(dateKey);
        Long todayUniqueIps = getUniqueIpCount(dateKey);

        // 获取当前在线数（通过访问时间判断）
        String onlineKey = "analytics:online";
        Long currentOnline = redisTemplate.opsForSet().size(onlineKey);

        return RealtimeStatsDTO.builder()
            .todayVisits(todayVisits)
            .todayUniqueIps(todayUniqueIps)
            .currentOnline(currentOnline != null ? currentOnline : 0L)
            .lastHourVisits(getLastHourVisits())
            .build();
    }

    // 定时任务：每小时同步Redis数据到MySQL
    @Scheduled(cron = "0 0 * * * ?")
    public void syncRedisToMySQL() {
        log.info("开始同步Redis数据到MySQL");
        // 同步逻辑实现
    }

    // 定时任务：每天凌晨清理过期数据
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpiredData() {
        log.info("开始清理过期数据");
        // 清理30天前的访问记录
        LocalDateTime expireDate = LocalDateTime.now().minusDays(30);
        analyticsMapper.deleteExpiredRecords(expireDate);
    }
}
```

### 5.4 异步处理服务

```java
// AsyncService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {

    private final AnalyticsMapper analyticsMapper;

    @Async
    public void saveVisitRecord(VisitRecordDTO dto) {
        try {
            VisitRecord record = new VisitRecord();
            BeanUtils.copyProperties(dto, record);
            analyticsMapper.insert(record);
        } catch (Exception e) {
            log.error("保存访问记录失败", e);
        }
    }

    @Async
    public void updatePageStats(String pageUrl, Integer dateKey) {
        try {
            // 更新页面统计
            PageStats stats = analyticsMapper.selectPageStats(pageUrl, dateKey);
            if (stats == null) {
                stats = new PageStats();
                stats.setPageUrl(pageUrl);
                stats.setDateKey(dateKey);
                stats.setVisitCount(1);
                stats.setUniqueIpCount(1);
                analyticsMapper.insertPageStats(stats);
            } else {
                stats.setVisitCount(stats.getVisitCount() + 1);
                analyticsMapper.updatePageStats(stats);
            }
        } catch (Exception e) {
            log.error("更新页面统计失败", e);
        }
    }
}
```

### 5.5 IP 工具类

```java
// IPUtil.java
public class IPUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // IPv6 localhost转IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }
}
```

### 5.6 统一响应格式

```java
// ApiResponse.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
```

## 6. 应用配置和部署

### 6.1 application.yml 配置

```yaml
server:
  port: 9050

spring:
  application:
    name: blog-analytics

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:blog_analytics}?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 600000
      connection-timeout: 30000
      max-lifetime: 1800000

  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: -1ms

  task:
    execution:
      pool:
        core-size: 10
        max-size: 50
        queue-capacity: 1000
    scheduling:
      pool:
        size: 5

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.blog.analytics: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/blog-analytics.log
    max-size: 100MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

## 7. 前端集成方案

### 7.1 客户端统计脚本

```javascript
// src/utils/analytics.js
class BlogAnalytics {
    constructor(apiUrl) {
        this.apiUrl = apiUrl;
        this.init();
    }

    init() {
        // 页面加载完成后的统计
        if (document.readyState === 'complete') {
            this.trackPageView();
        } else {
            window.addEventListener('load', () => this.trackPageView());
        }

        // SPA 路由变化监听
        this.observeRouteChanges();
    }

    trackPageView() {
        const visitData = {
            pageUrl: window.location.pathname,
            userAgent: navigator.userAgent,
            referer: document.referrer
        };

        // 异步发送，不阻塞页面
        this.sendData('/api/analytics/visit', visitData);
    }

    sendData(endpoint, data) {
        // 使用 navigator.sendBeacon 或 fetch
        if (navigator.sendBeacon) {
            const blob = new Blob([JSON.stringify(data)], {
                type: 'application/json'
            });
            navigator.sendBeacon(this.apiUrl + endpoint, blob);
        } else {
            fetch(this.apiUrl + endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data),
                keepalive: true
            }).catch(() => {
                // 静默失败，不影响用户体验
            });
        }
    }

    observeRouteChanges() {
        // 监听 pushState 和 replaceState
        const originalPushState = history.pushState;
        const originalReplaceState = history.replaceState;

        history.pushState = function(...args) {
            originalPushState.apply(this, args);
            window.dispatchEvent(new Event('pushstate'));
        };

        history.replaceState = function(...args) {
            originalReplaceState.apply(this, args);
            window.dispatchEvent(new Event('replacestate'));
        };

        window.addEventListener('pushstate', () => this.trackPageView());
        window.addEventListener('replacestate', () => this.trackPageView());
        window.addEventListener('popstate', () => this.trackPageView());
    }
}

// 初始化
export const analytics = new BlogAnalytics(
    import.meta.env.PUBLIC_ANALYTICS_API || 'http://localhost:9050'
);
```

### 7.2 Astro 组件集成

```astro
<!-- src/components/analytics/AnalyticsTracker.astro ---
<script>
  import { analytics } from '../utils/analytics.js';

  // 组件加载时自动开始统计
  analytics.trackPageView();
</script>
```

## 8. 开发优先级建议

### Phase 1: 核心功能 (1-2周)
- [ ] 搭建 Spring Boot 项目框架
- [ ] 配置 MySQL 数据库和 MyBatis-Plus
- [ ] 实现 visit_record 表和相关实体
- [ ] 开发访问记录 API (/api/analytics/visit)
- [ ] 集成 Redis 并实现基础缓存

### Phase 2: 统计查询 (1-2周)
- [ ] 实现今日统计 API (/api/analytics/stats/today)
- [ ] 实现日期范围统计 API (/api/analytics/stats/range)
- [ ] 实现热门页面 API (/api/analytics/stats/hot-pages)
- [ ] 实现实时统计 API (/api/analytics/stats/realtime)
- [ ] 前端集成和测试

### Phase 3: 优化完善 (1周)
- [ ] 添加定时任务（数据同步、清理）
- [ ] 完善日志和监控
- [ ] 性能优化和缓存策略
- [ ] API 文档生成

## 9. 监控和运维

### 9.1 应用监控
Spring Boot Actuator 提供完整的监控端点：
- `/actuator/health` - 健康检查
- `/actuator/metrics` - 性能指标
- `/actuator/info` - 应用信息
- `/actuator/prometheus` - Prometheus 格式指标

### 9.2 日志配置
使用 SLF4J + Logback，支持：
- 日志级别动态调整
- 日志文件自动滚动
- 结构化日志输出
- 敏感信息脱敏

## 10. 部署
使用MCP工具部署