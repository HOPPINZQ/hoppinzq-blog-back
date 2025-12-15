# 项目启动验证步骤

## 1. 环境检查

确保以下环境已正确安装：

```bash
# 检查Java版本
java -version
# 应该显示 Java 8 或更高版本

# 检查Maven版本
mvn -version
# 应该显示 Maven 3.6+ 版本
```

## 2. 数据库准备

### MySQL设置
```sql
-- 创建数据库
CREATE DATABASE blog_analytics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE blog_analytics;

-- 执行初始化脚本（运行 src/main/resources/db/schema.sql 中的内容）
```

### Redis设置
确保Redis服务正在运行：
```bash
# Windows
redis-server

# Linux/Mac
redis-server
# 或者
sudo systemctl start redis
```

## 3. 配置验证

检查 `src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc://localhost:3306/blog_analytics  # 确认数据库地址
    username: root                             # 确认用户名
    password: your_password                    # 确认密码

  redis:
    host: localhost                            # 确认Redis地址
    port: 6379                                # 确认Redis端口
    password: (如果没有密码就留空)             # 确认Redis密码
```

## 4. 编译和启动

### 方式一：使用Maven
```bash
# 编译项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

### 方式二：使用启动脚本
```bash
# Windows
start.bat

# Linux/Mac
./start.sh
```

### 方式三：打包后运行
```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/blog-analytics-1.0.0.jar
```

## 5. 启动验证

### 检查启动日志
应该看到类似以下输出：
```
Starting BlogAnalyticsApplication on ...
Started BlogAnalyticsApplication on port 8080
```

### 健康检查
访问以下URL验证服务是否正常：

```bash
# 健康检查（应该返回成功状态）
curl http://localhost:8080/api/analytics/health

# API信息（应该返回API列表）
curl http://localhost:8080/api/analytics/info
```

### 浏览器访问
- 健康检查: http://localhost:8080/api/analytics/health
- API信息: http://localhost:8080/api/analytics/info

## 6. 功能测试

### 使用HTTP文件测试
1. 在IDE中打开 `test-api.http` 文件
2. 使用IDE的HTTP Client功能执行请求
3. 或者使用curl命令测试

### 使用curl测试
```bash
# 测试记录访问
curl -X POST http://localhost:8080/api/analytics/visit \
  -H "Content-Type: application/json" \
  -d '{"pageUrl":"/test","userAgent":"test-agent","referer":"direct"}'

# 测试获取今日统计
curl http://localhost:8080/api/analytics/stats/today

# 测试获取实时统计
curl http://localhost:8080/api/analytics/stats/realtime
```

## 7. 常见问题排查

### 端口占用
如果8080端口被占用：
```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

### 数据库连接失败
1. 检查MySQL服务是否运行
2. 确认数据库配置是否正确
3. 确认数据库用户权限

### Redis连接失败
1. 检查Redis服务是否运行
2. 确认Redis配置是否正确
3. 检查防火墙设置

### Maven编译失败
1. 检查Java版本兼容性
2. 清理Maven缓存：`mvn clean`
3. 重新下载依赖：`mvn dependency:resolve`

## 8. 监控和维护

### 查看应用日志
```bash
# 实时查看日志
tail -f logs/blog-analytics.log

# Windows
type logs\blog-analytics.log
```

### 数据库监控
```sql
-- 查看访问记录数量
SELECT COUNT(*) FROM visit_record;

-- 查看今日统计
SELECT * FROM visit_record WHERE date_key = 20251212 LIMIT 10;
```

### Redis监控
```bash
# 连接Redis
redis-cli

# 查看所有键
KEYS blog:analytics:*

# 查看特定键的值
GET blog:analytics:visit:count:20251212
```

## 9. 性能验证

### 压力测试（可选）
```bash
# 使用ab工具进行简单压力测试
ab -n 1000 -c 10 http://localhost:8080/api/analytics/health
```

### 内存和CPU监控
- Windows: 任务管理器
- Linux/Mac: `top` 或 `htop`

## 10. 成功标志

如果看到以下情况，说明项目启动成功：

1. ✅ 应用成功启动，没有错误日志
2. ✅ 健康检查接口返回正常
3. ✅ 能够成功记录访问数据
4. ✅ 能够查询统计数据
5. ✅ Redis和MySQL连接正常

如果遇到问题，请查看日志文件中的错误信息，或根据常见问题排查部分进行检查。