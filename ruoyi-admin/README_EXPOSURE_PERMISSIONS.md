曝光模块权限与菜单初始化说明

建议步骤：

1. 在测试环境备份数据库。
2. 根据项目中现有 `sys_menu` 与权限表结构，修改并执行示例 SQL（见 `sql/exposure_permissions.sql`）。
3. 在系统管理后台为菜单项分配相应的角色/用户。

权限点建议：
- `exposure:auto:list` - 查看自动曝光列表
- `exposure:auto:add` - 新增/编辑自动曝光配置
- `exposure:dupload` - 上传目标文件（定向/链接曝光）

注意：不同若依版本表结构不同，请参考现有数据库中的 `sys_menu` 与权限表列名并调整 SQL。
