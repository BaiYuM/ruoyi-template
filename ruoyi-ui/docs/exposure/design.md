# 曝光模块 设计文档

## 1. 概览
该模块在前端主要由列表页、右侧配置抽屉、AI 助手对话框、曝光统计弹窗、自动曝光设置弹窗构成。实现须复用项目组件并通过 `src/utils/request.js` 访问后端。

## 2. 路由与菜单
- 建议路由：`/exposure/auto` 对应 `src/views/exposure/autoExposure/index.vue`。
- 菜单项已在 `src/views/exposure/menuTitles.js` 添加 `autoExposure: '自动曝光'`。

## 3. 主要前端组件
- `src/views/exposure/autoExposure/index.vue`（页面）
  - 责任：展示搜索面板、表格（`MyTable`）、工具栏按钮（添加/统计/设置）
  - 状态：`filters`, `pageConfig`, `pagination`, `loading`, `drawerVisible`, `statsVisible`, `settingsVisible`。

- `src/views/exposure/autoExposure/AutoConfigDrawer.vue`（右侧抽屉）
  - 责任：新增/编辑配置表单，触发 `save` 事件回传数据
  - 关键 props/events：`visible` (v-model)、`config`、`platformOptions`、`isEditing`；`save` 事件
  - 校验：`name` 必填、`account` 必填、`dailyLimit` >=1

- `src/views/exposure/autoExposure/AIHelperDialog.vue`（AI 助手）
  - 责任：输入需求并生成文本（前端占位实现），回填至抽屉表单

- `src/views/exposure/autoExposure/ExposureStatsDialog.vue`（统计弹窗）
  - 责任：展示趋势图占位，后续接 ECharts + 后端统计数据

- `src/views/exposure/autoExposure/AutoExposureSettingsDialog.vue`（设置弹窗）
  - 责任：设置曝光间隔范围（min/max），提交后通过 API 或本地保存

## 4. 数据模型建议
- `ExposureConfig`（前端/后端共享字段）
```json
{
  "id": 123,
  "name": "配置名称",
  "platform": "douyin",
  "account": "acc_1",
  "configType": "评论",
  "searchKeywords": "k1,k2",
  "commentContent": "文本1\n文本2",
  "dailyLimit": 10,
  "startTime": "09:00",
  "sortOrder": "综合",
  "skipRepeat": false,
  "enabled": true
}
```

## 5. 交互与 UX 细节
- 抽屉宽度：右侧 `el-drawer` 宽度 520px；在更小屏幕上采用全屏或纵向滚动。
- 文本输入：`commentContent` 使用 textarea，支持多行与换行；“添加一行数据”操作作为快捷行为（可追加或在数组形式中维护）。
- AI 助手：默认以对话形式收集需求并返回候选文本；前端可在接入真实 AI 前使用占位逻辑。
- 文件导入：CSV 在前端通过 FileReader 解析（简单场景）；建议 Excel 通过后端解析并返回数组。

## 6. 样式与响应式
- 复用 `@/assets/styles/page-common.css` 中 `.search-grid.compact` 的布局规则。
- 所有数值输入采用固定宽度（例如 100px）以避免弹窗内溢出；在 <600px 时可使用 media query 改为纵向排列。

## 7. 集成点
- HTTP：使用 `src/utils/request.js`；后端需实现 `/exposure/auto/list`、`/exposure/auto/save`、`/exposure/directional/upload` 等接口。
- 权限：需要在路由/菜单处控制访问权限（与项目现有权限系统一致）。

## 8. 验证与测试
- 单元/集成测试：
  - 表单校验规则覆盖（必填、范围）
  - CSV 上传解析边界（空行、重复、编码）
  - AI 助手回填行为（模拟返回）
  - 弹窗响应式（不同窗口宽度断点测试）

## 9. 部署与运维注意
- 后端解析 Excel 可能占用 CPU/内存，建议限制文件大小并做异步解析或限流。

## 10. 后续扩展建议
- 把 AI 助手抽象为独立服务，前端仅作为 UI，后端负责调用第三方 AI 并记录对话历史。
- 曝光统计接入 ECharts 并提供导出 CSV 的功能。

---
文档生成时间：自动化生成
