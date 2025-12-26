# RuoYi 桌面自动化应用

本项目是 RuoYi 系统的桌面自动化组件，支持通过后端 API 控制桌面应用执行自动化任务。

## 功能特性

- 通过 HTTP API 接收后端指令
- 支持自动化任务执行
- 支持无头模式（后台运行）
- 支持定时任务和循环任务
- 支持任务状态监控

## API 接口

### 1. 启动自动化任务
- **URL**: `POST /api/automation/start`
- **请求体**: 
```json
{
  "url": "http://localhost:8080",
  "headless": false,
  "screenshot": true,
  "delay": 1000,
  "tasks": []
}
```
- **响应**:
```json
{
  "success": true,
  "message": "自动化任务已启动",
  "data": { ... }
}
```

### 2. 停止自动化任务
- **URL**: `POST /api/automation/stop`
- **响应**:
```json
{
  "success": true,
  "message": "自动化任务已停止",
  "data": { ... }
}
```

### 3. 查询自动化状态
- **URL**: `GET /api/automation/status`
- **响应**:
```json
{
  "success": true,
  "data": {
    "isRunning": true,
    "browserStatus": "运行中",
    "logs": [ ... ]
  }
}
```

### 4. 清除日志
- **URL**: `POST /api/automation/clear-logs`
- **响应**:
```json
{
  "success": true,
  "message": "日志已清除",
  "data": { ... }
}
```

## 环境变量

- `DESKTOP_HTTP_PORT`: HTTP 服务端口（默认 9876）
- `NODE_ENV`: 环境模式（production/development）

## 启动方式

1. 直接启动：`npm start`
2. 开发模式：`npm run dev`

## 与后端集成

后端通过 HTTP API 与桌面应用通信，实现自动化任务的远程控制和监控。

## 任务配置说明

- `url`: 目标页面 URL
- `headless`: 是否启用无头模式（后台运行）
- `screenshot`: 是否启用截图
- `delay`: 任务间延迟时间（毫秒）
- `tasks`: 自动化任务列表（暂未实现具体任务）

## 注意事项

1. 确保端口 9876 未被占用
2. 桌面应用需要与后端服务在同一台机器上运行
3. 启动后端服务时会自动启动桌面应用