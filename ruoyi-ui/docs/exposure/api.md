# 曝光模块 API 文档

> 注：前端统一使用 `src/utils/request.js` 发起请求。下面列出前端目前使用与建议的后端接口契约。

## 公共约定
- 数据分页：使用 `page`（页号，1 起）和 `pageSize`。
- 成功响应格式示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "list": [],
    "total": 0
  }
}
```

## 1. 获取自动曝光列表
- URL: `GET /exposure/auto/list`
- 描述: 列出自动曝光配置（支持分页与筛选）
- Query 参数:
  - `page` int
  - `pageSize` int
  - `platform` string 可选（抖音/douyin、快手/kuaishou 等）
  - `configType` string 可选（评论/私信）
- 返回示例:

```json
{
  "code":200,
  "msg":"success",
  "data":{
    "list":[{ "id":1, "name":"自动曝光_1", "platform":"douyin", "account":"acc_1", "configType":"评论", "status":"启用" }],
    "total":1
  }
}
```

## 2. 创建/更新自动曝光配置
- URL: `POST /exposure/auto/save`
- 描述: 保存自动曝光配置对象（create 或 update）。
- 请求体 (JSON):

```json
{
  "id": 123,                // 可选，存在则为更新
  "name": "配置名称",
  "account": "acc_1",
  "platform": "douyin",
  "configType": "评论",
  "searchKeywords": "关键词1,关键词2",
  "commentContent": "示例评论\n换行",
  "dailyLimit": 10,
  "startTime": "09:00",
  "sortOrder": "综合",
  "skipRepeat": false,
  "enabled": true
}
```

- 返回示例:

```json
{ "code":200, "msg":"saved", "data": null }
```

## 3. 获取定向/链接/搜索曝光列表
- URL: `GET /exposure/directional/list`（定向）
- URL: `GET /exposure/link/list`（链接）
- 参数同上（分页/筛选）

## 4. 创建/更新定向曝光配置
- URL: `POST /exposure/directional/save`
- 请求体同自动曝光（可扩展字段，例如 targetAccounts 列表）

## 5. 上传定向/链接曝光目标文件（Excel）
- URL: `POST /exposure/directional/upload`
- 描述: 上传 `.csv|.xls|.xlsx` 文件，后端负责解析并返回解析结果（或解析失败原因）。
- Headers: `Content-Type: multipart/form-data`
- Form field: `file`（文件）
- 返回示例:

```json
{
  "code":200,
  "msg":"parsed",
  "data":{
    "parsed": ["account1","account2"],
    "errors": []
  }
}
```

### 上传解析说明
- CSV 解析：后端以行读取，默认取每行第一列为账号（支持用逗号分隔多列，取第一列）。
- Excel 解析（`.xls` / `.xlsx`）：后端解析第一个 sheet 的第一列，每行作为一个账号项。
- 返回字段说明：`parsed` - 解析出的账号数组；`errors` - 解析警告或失败行信息（含行号）。

### 使用示例（前端）
```js
const form = new FormData();
form.append('file', fileInput.files[0]);
request({ url: '/exposure/directional/upload', method: 'post', data: form, headers: { 'Content-Type': 'multipart/form-data' } })
.then(res => {
  console.log(res.data.parsed, res.data.errors)
})
```

## 6. 自动曝光设置（可选）
- 获取: `GET /exposure/auto/settings` -> 返回 `{ min, max }`
- 保存: `POST /exposure/auto/settings` 请求体 `{ min, max }`

## 7. 错误码约定
- `200` 成功（或 `code:200`）。
- `400` 参数错误，返回 `{ code:400, msg:"bad request", errors: [...] }`。
- `401` 未授权，需要登录/鉴权。
- `413` 上传文件过大（后端应返回或前端限制）。

## 8. 安全性与限制建议
- 文件上传仅允许指定 MIME 与扩展名，限制大小（建议 <= 5MB）。
- 上传接口需后端鉴权并对解析结果做严格校验，防止注入或二次风险。

---
文档由前端自动生成，后端实现需与以上契约对齐。
