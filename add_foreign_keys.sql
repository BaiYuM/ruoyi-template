-- 添加缺失的外键约束
-- 注意：运行前请备份数据库

-- 检查并添加唯一索引（如果不存在）
-- ALTER TABLE sys_user ADD UNIQUE KEY uk_user_name (user_name); -- 已存在，跳过

-- 添加外键：sys_user.dept_id -> sys_dept.dept_id
ALTER TABLE sys_user ADD CONSTRAINT fk_sys_user_dept_id FOREIGN KEY (dept_id) REFERENCES sys_dept(dept_id);

-- 添加外键：friend_add_plan.create_by -> sys_user.user_name
ALTER TABLE friend_add_plan ADD CONSTRAINT fk_friend_add_plan_create_by FOREIGN KEY (create_by) REFERENCES sys_user(user_name);

-- 添加外键：sys_user_role.user_id -> sys_user.user_id
ALTER TABLE sys_user_role ADD CONSTRAINT fk_sys_user_role_user_id FOREIGN KEY (user_id) REFERENCES sys_user(user_id);

-- 添加外键：sys_user_role.role_id -> sys_role.role_id
ALTER TABLE sys_user_role ADD CONSTRAINT fk_sys_user_role_role_id FOREIGN KEY (role_id) REFERENCES sys_role(role_id);

-- 添加外键：sys_role_menu.role_id -> sys_role.role_id
ALTER TABLE sys_role_menu ADD CONSTRAINT fk_sys_role_menu_role_id FOREIGN KEY (role_id) REFERENCES sys_role(role_id);

-- 添加外键：sys_role_menu.menu_id -> sys_menu.menu_id
ALTER TABLE sys_role_menu ADD CONSTRAINT fk_sys_role_menu_menu_id FOREIGN KEY (menu_id) REFERENCES sys_menu(menu_id);

-- 添加外键：sys_user_post.user_id -> sys_user.user_id
ALTER TABLE sys_user_post ADD CONSTRAINT fk_sys_user_post_user_id FOREIGN KEY (user_id) REFERENCES sys_user(user_id);

-- 添加外键：sys_user_post.post_id -> sys_post.post_id
ALTER TABLE sys_user_post ADD CONSTRAINT fk_sys_user_post_post_id FOREIGN KEY (post_id) REFERENCES sys_post(post_id);

-- 注意：sys_logininfor.user_name 有数据冲突（'ruoyi'不存在于sys_user），跳过
-- 注意：sys_dept.parent_id 自引用，有无效值0，跳过