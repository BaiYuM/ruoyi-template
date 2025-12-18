# 抖音开放API SDK 使用指南

## 概述

`douyin-open-api-sdk` 是抖音开放平台的Java SDK，用于在应用中集成抖音的各种API功能。本项目已集成此SDK，可用于处理抖音webhook消息和基础API调用。

**重要说明**: 当前使用的SDK版本(0.0.3)主要支持webhook消息处理和基础服务功能。视频API、私信API等高级功能可能需要更高版本的SDK或不同的实现方式。

## 主要API功能

### 1. 服务配置管理
- **切换配置**: `switchover(clientKey)` - 切换到指定的应用配置
- **配置检查**: `checkConfiguration(clientKey)` - 验证配置是否有效

### 2. Webhook消息处理
- **签名验证**: `checkWebhookSignature(signature, body)` - 验证webhook请求签名
- **消息解析**: `TtOpWebhookMessage.fromJson(jsonBody)` - 解析webhook消息
- **事件处理**: 处理各种webhook事件（验证、授权、取消授权等）

### 3. 基础服务功能
- **服务状态**: 检查SDK服务是否正常初始化
- **消息路由**: 通过 `TtOpWebhookMessageRouter` 路由消息到不同处理器

## 配置要求

在使用API前，需要在 `application.yml` 中配置抖音应用的密钥：

```yaml
tt:
  op:
    useRedis: true
    configs:
      - clientKey: your_real_client_key    # 替换为真实的clientKey
        clientSecret: your_real_client_secret  # 替换为真实的clientSecret
```

### 获取密钥步骤：
1. 访问 [抖音开放平台](https://open.douyin.com/)
2. 创建应用，获取 `clientKey` 和 `clientSecret`
3. 配置到项目中

## 使用示例

### 基本使用流程

```java
@Autowired
private ITtOpBaseService ttOpBaseService;

// 切换到指定配置
boolean switched = ttOpBaseService.switchover("your_client_key");
if (!switched) {
    throw new IllegalArgumentException("配置切换失败");
}

// 验证webhook签名
boolean isValid = ttOpBaseService.checkWebhookSignature(signature, body);

// 解析webhook消息
TtOpWebhookMessage message = TtOpWebhookMessage.fromJson(jsonBody);
```

### Webhook消息处理示例

```java
// 解析消息
TtOpWebhookMessage message = TtOpWebhookMessage.fromJson(requestBody);

// 检查事件类型
String eventType = message.getEvent();
switch (eventType) {
    case TtOpConst.WebhookEventType.VERIFY_WEBHOOK:
        // 处理验证事件，返回challenge
        return message.getContent();
    case "user_authorize":
        // 处理用户授权事件
        break;
    case "user_unauthorize":
        // 处理用户取消授权事件
        break;
}
```

## 测试运行

运行测试类验证API调用：

```bash
# 运行所有测试
mvn test -Dtest=DouyinApiExampleTest

# 运行特定测试方法
mvn test -Dtest=DouyinApiExampleTest#testCheckConfiguration
```

## 注意事项

1. **SDK版本限制**: 当前版本主要支持webhook处理，视频API等功能可能不可用
2. **权限要求**: 不同操作需要相应的应用权限
3. **频率限制**: webhook消息处理有频率限制
4. **数据安全**: 妥善保管 `clientSecret`，不要在代码中硬编码
5. **错误处理**: API调用可能失败，需要适当的异常处理

## 常见问题

### Q: 为什么没有视频API？
A: 当前SDK版本(0.0.3)主要专注于webhook消息处理。视频API可能在更高版本中提供，或者需要使用HTTP客户端直接调用抖音API。

### Q: 如何获取用户的openId？
A: 通过抖音授权流程获取，具体参考抖音开放平台文档。

### Q: webhook回调如何处理？
A: 查看 `CallbackController.java`，它处理抖音推送的消息并通过路由器分发到不同处理器。

## 实际应用场景

### 1. 自动回复机器人
- 接收用户私信webhook消息
- 自动回复预设内容

### 2. 内容监控
- 监控用户发布内容
- 自动审核或处理

### 3. 用户管理
- 处理用户授权/取消授权事件
- 维护用户状态

## 相关文件

- `DouyinApiExample.java`: API使用示例
- `DouyinApiExampleTest.java`: 测试类
- `TtOpConfiguration.java`: SDK配置
- `CallbackController.java`: webhook处理控制器
- `application.yml`: 应用配置

## 更多资源

- [抖音开放平台文档](https://open.douyin.com/)
- [SDK源码](https://github.com/gadfly361/douyin-open-api-sdk)

## 升级建议

如果需要更完整的API功能（如视频管理、私信发送等），建议：
1. 升级到最新版本的SDK
2. 或直接使用HTTP客户端调用抖音REST API
3. 参考抖音开放平台最新文档