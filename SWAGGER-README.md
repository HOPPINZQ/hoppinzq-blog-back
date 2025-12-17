# Swagger API 文档使用指南

## 简介

本项目已集成Swagger 3.0.0，提供完整的API文档和测试界面。

## 访问地址

启动应用后，可以通过以下地址访问Swagger文档：

- **Swagger UI**: http://localhost:9050/swagger-ui/
- **API JSON**: http://localhost:9050/v2/api-docs

## 主要功能

### 1. API文档浏览
- 自动生成的API文档
- 详细的接口说明
- 请求参数和响应示例
- HTTP状态码说明

### 2. 在线测试
- 直接在浏览器中测试API
- 支持各种HTTP方法（GET、POST、PUT、DELETE）
- 参数验证和错误提示

### 3. 交互式文档
- 展开/折叠接口分组
- 搜索接口功能
- 下载API文档

## API分组

所有API接口都归类在 **"博客分析API"** 分组下，包含以下主要接口：

### 访问记录
- `POST /api/analytics/visit` - 记录页面访问

### 统计查询
- `GET /api/analytics/stats/today` - 获取今日统计
- `GET /api/analytics/stats/range` - 获取日期范围统计
- `GET /api/analytics/stats/hot-pages` - 获取热门页面
- `GET /api/analytics/stats/realtime` - 获取实时统计
- `GET /api/analytics/stats/hourly` - 获取小时统计
- `GET /api/analytics/stats/page` - 获取页面统计
- `GET /api/analytics/stats/region` - 获取地域统计
- `GET /api/analytics/stats/browser` - 获取浏览器统计
- `GET /api/analytics/stats/os` - 获取操作系统统计
- `GET /api/analytics/stats/referer` - 获取来源统计

### 工具接口
- `GET /api/analytics/health` - 健康检查
- `GET /api/analytics/info` - API信息

## 使用示例

### 1. 记录页面访问

在Swagger UI中：

1. 展开 **"博客分析API"** 分组
2. 点击 **"记录页面访问"** 接口
3. 点击 **"Try it out"** 按钮
4. 修改请求体示例：
```json
{
  "pageUrl": "/posts/java-tutorial",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "referer": "https://google.com"
}
```
5. 点击 **"Execute"** 执行请求
6. 查看响应结果

### 2. 查询今日统计

1. 展开 **"博客分析API"** 分组
2. 点击 **"获取今日统计"** 接口
3. 点击 **"Try it out"** 按钮
4. 点击 **"Execute"** 执行请求
5. 查看统计数据

## 响应格式

所有API都返回统一的响应格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": { /* 具体数据 */ },
  "timestamp": 1702915200000,
  "code": 200
}
```

## 错误处理

当API调用失败时，会返回相应的错误信息：

```json
{
  "success": false,
  "message": "错误描述",
  "timestamp": 1702915200000,
  "code": 400
}
```

常见错误码：
- `200`: 成功
- `400`: 参数错误
- `500`: 服务器内部错误

## 注解说明

### Controller注解
- `@Api(tags = "分组名称", description = "分组描述")` - 控制器类注解
- `@ApiOperation(value = "接口名称", notes = "接口详细描述")` - 接口方法注解
- `@ApiResponses({ @ApiResponse(...) })` - 响应状态码注解
- `@ApiImplicitParam` - 请求参数注解

### Model注解
- `@ApiModel(description = "模型描述")` - DTO类注解
- `@ApiModelProperty(value = "字段描述", example = "示例值")` - 字段注解

## 配置说明

### application.yml 配置
```yaml
springfox:
  documentation:
    swagger:
      v2:
        enabled: true
    swagger-ui:
      enabled: true
      base-url: /swagger-ui/
```

### 生产环境配置
在生产环境中，可以通过配置禁用Swagger：

```yaml
springfox:
  documentation:
    swagger:
      v2:
        enabled: false
    swagger-ui:
      enabled: false
```

## 高级功能

### 1. 请求认证
如果API需要认证，可以在Swagger中配置：
- 点击接口右上角的锁形图标
- 输入认证信息
- 后续请求会自动携带认证头

### 2. 下载API文档
- 点击页面顶部的API文档名称
- 选择下载格式（JSON、YAML等）

### 3. 全局搜索
- 使用页面顶部的搜索框
- 输入关键词搜索接口

## 注意事项

1. **开发环境**：默认启用Swagger UI
2. **生产环境**：建议禁用Swagger UI
3. **性能影响**：Swagger对应用性能影响很小
4. **版本兼容**：支持Java 8+和Spring Boot 2.x

## 故障排查

### Swagger UI无法访问
1. 确认应用已正常启动
2. 检查端口配置（默认9050）
3. 确认Swagger依赖已正确引入

### API文档不显示
1. 检查Controller包扫描路径
2. 确认Swagger注解配置正确
3. 查看应用日志中的错误信息

### 接口测试失败
1. 检查请求参数格式
2. 确认服务依赖（数据库、Redis）正常
3. 查看应用日志中的详细错误信息

## 相关链接

- [Swagger官方文档](https://swagger.io/)
- [SpringFox文档](https://springfox.github.io/springfox/)
- [Swagger UI使用指南](https://swagger.io/tools/swagger-ui/)