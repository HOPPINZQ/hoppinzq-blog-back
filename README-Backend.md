# 博客访问统计后端服务

## 项目简介

这是一个基于Spring Boot的博客访问统计后端服务，提供完整的网站访问数据收集、统计和分析功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0+
- **缓存**: Redis 7+
- **ORM**: MyBatis-Plus 3.5.5
- **连接池**: Druid 1.2.20
- **构建工具**: Maven
- **JDK版本**: Java 8+

## 功能特性

### 核心功能
- ✅ 访问记录收集（页面URL、IP地址、UserAgent、来源等）
- ✅ 实时访问统计
- ✅ 今日/历史数据统计
- ✅ 热门页面排行
- ✅ 小时/日期范围统计
- ✅ 地域访问分析
- ✅ 浏览器/操作系统统计
- ✅ 访问来源分析

### 高级功能
- ✅ Redis缓存加速
- ✅ 异步数据处理
- ✅ 定时任务（数据同步、清理）
- ✅ 统一API响应格式
- ✅ 完整的异常处理
- ✅ 健康检查接口

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 7+

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE blog_analytics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p blog_analytics < src/main/resources/db/schema.sql
```

### 配置文件

修改 `application.yml` 中的数据库和Redis连接配置：

```yaml
spring:
  datasource:
    url: jdbc://localhost:3306/blog_analytics
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/blog-analytics-1.0.0.jar
```

应用将在 `http://localhost:9050` 启动。

## API接口文档

### 基础信息

- **Base URL**: `http://localhost:9050/api/analytics`
- **Content-Type**: `application/json`

### 主要接口

#### 1. 记录页面访问
```http
POST /api/analytics/visit
Content-Type: application/json

{
  "pageUrl": "/posts/article-slug",
  "userAgent": "Mozilla/5.0...",
  "referer": "https://google.com"
}
```

#### 2. 获取今日统计
```http
GET /api/analytics/stats/today
```

响应：
```json
{
  "success": true,
  "data": {
    "date": "2025-12-12",
    "totalVisits": 1250,
    "uniqueIps": 890
  }
}
```

#### 3. 获取日期范围统计
```http
GET /api/analytics/stats/range?startDate=2025-12-01&endDate=2025-12-12
```

#### 4. 获取热门页面
```http
GET /api/analytics/stats/hot-pages?days=7&limit=10
```

#### 5. 获取实时统计
```http
GET /api/analytics/stats/realtime
```

#### 6. 获取小时统计
```http
GET /api/analytics/stats/hourly?date=2025-12-12
```

### 其他统计接口

- `/api/analytics/stats/page` - 页面统计
- `/api/analytics/stats/region` - 地域统计
- `/api/analytics/stats/browser` - 浏览器统计
- `/api/analytics/stats/os` - 操作系统统计
- `/api/analytics/stats/referer` - 来源统计

### 工具接口

- `/api/analytics/health` - 健康检查
- `/api/analytics/info` - API信息

## 前端集成

### JavaScript集成示例

```javascript
class BlogAnalytics {
    constructor(apiUrl = 'http://localhost:9050') {
        this.apiUrl = apiUrl;
        this.init();
    }

    init() {
        // 页面加载时统计
        if (document.readyState === 'complete') {
            this.trackPageView();
        } else {
            window.addEventListener('load', () => this.trackPageView());
        }

        // 监听SPA路由变化
        this.observeRouteChanges();
    }

    trackPageView() {
        const visitData = {
            pageUrl: window.location.pathname,
            userAgent: navigator.userAgent,
            referer: document.referrer
        };

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
const analytics = new BlogAnalytics();
```

### React集成示例

```jsx
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

function AnalyticsTracker() {
    const location = useLocation();

    useEffect(() => {
        const trackVisit = () => {
            fetch('http://localhost:9050/api/analytics/visit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    pageUrl: location.pathname,
                    userAgent: navigator.userAgent,
                    referer: document.referrer
                }),
                keepalive: true
            }).catch(() => {
                // 静默失败
            });
        };

        trackVisit();
    }, [location]);

    return null;
}

export default AnalyticsTracker;
```

## 部署说明

### Docker部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:8-jre-slim

WORKDIR /app
COPY target/blog-analytics-1.0.0.jar app.jar

EXPOSE 9050
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
# 构建镜像
docker build -t blog-analytics .

# 运行容器
docker run -d \
  --name blog-analytics \
  -p 9050:9050 \
  -e DB_HOST=localhost \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=localhost \
  blog-analytics
```

### 环境变量配置

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `DB_HOST` | localhost | 数据库主机 |
| `DB_PORT` | 3306 | 数据库端口 |
| `DB_NAME` | blog_analytics | 数据库名称 |
| `DB_USERNAME` | root | 数据库用户名 |
| `DB_PASSWORD` | password | 数据库密码 |
| `REDIS_HOST` | localhost | Redis主机 |
| `REDIS_PORT` | 6379 | Redis端口 |
| `REDIS_PASSWORD` | (空) | Redis密码 |

## 监控和运维

### 健康检查

- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/metrics` - 性能指标

### 日志配置

日志文件位置：`logs/blog-analytics.log`

日志级别配置：
- `com.blog.analytics: DEBUG` - 应用日志
- `org.springframework.web: INFO` - Spring Web日志
- `org.springframework.data.redis: INFO` - Redis日志

### 定时任务

系统配置了以下定时任务：

1. **每小时同步Redis数据** - `0 5 * * * ?`
2. **每日统计同步** - `0 30 0 * * ?`
3. **数据清理** - `0 0 2 * * ?`
4. **日报生成** - `0 0 1 * * ?`
5. **周报生成** - `0 0 2 ? * MON`
6. **月报生成** - `0 0 3 1 * ?`

## 性能优化

### Redis缓存策略

- 访问统计数据缓存7天
- 在线用户数据缓存2小时
- 实时统计数据缓存1小时

### 数据库优化

- 合理的索引设计
- 分页查询支持
- 定期数据清理

### 异步处理

- 访问记录异步写入数据库
- 统计数据异步计算
- 不阻塞用户请求

## 故障排查

### 常见问题

1. **数据库连接失败**
   - 检查数据库配置
   - 确认数据库服务状态
   - 检查网络连接

2. **Redis连接失败**
   - 检查Redis配置
   - 确认Redis服务状态
   - 检查防火墙设置

3. **统计数据显示异常**
   - 检查定时任务是否正常执行
   - 查看Redis中的数据
   - 检查数据库数据

### 日志查看

```bash
# 查看应用日志
tail -f logs/blog-analytics.log

# 查看错误日志
grep ERROR logs/blog-analytics.log
```

## 扩展开发

### 添加新的统计接口

1. 在 `AnalyticsService` 中定义方法
2. 在 `AnalyticsServiceImpl` 中实现逻辑
3. 在 `AnalyticsController` 中添加接口
4. 如需数据库操作，在 `VisitRecordMapper` 中添加方法

### 自定义定时任务

1. 在 `ScheduledService` 中定义接口
2. 在 `ScheduledServiceImpl` 中实现逻辑
3. 添加 `@Scheduled` 注解配置执行时间

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。