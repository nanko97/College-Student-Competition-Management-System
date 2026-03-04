-- =============================================
-- 用户注册与权限管理系统 - 数据库迁移脚本
-- 版本：V1
-- 日期：2026-03-04
-- =============================================

-- 1. 为 xuesheng 表添加角色字段（如果不存在）
ALTER TABLE xuesheng ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '学生' COMMENT '角色';

-- 2. 为 jiaoshi 表添加角色字段（如果不存在）
ALTER TABLE jiaoshi ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '教师' COMMENT '角色';

-- 3. 创建角色权限表（用于细粒度权限控制）
CREATE TABLE IF NOT EXISTS user_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(20) NOT NULL,
    resource_type VARCHAR(20) NOT NULL, -- MENU/BUTTON/API
    resource_code VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_resource (role_name, resource_type, resource_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

-- 4. 初始化基础权限数据
INSERT INTO user_role_permission (role_name, resource_type, resource_code, description) VALUES
-- 学生权限
('学生', 'API', '/jingsaixinxi/list', '查看竞赛列表'),
('学生', 'API', '/jingsaixinxi/detail', '查看竞赛详情'),
('学生', 'API', '/jingsaibaoming/add', '竞赛报名'),
('学生', 'API', '/jingsaibaoming/my', '查看我的报名'),
('学生', 'API', '/jingsaibaoming/list', '查看报名列表'),
('学生', 'API', '/xuesheng/info', '查看个人信息'),
('学生', 'API', '/xuesheng/update', '更新个人信息'),
('学生', 'API', '/zuopindafen/list', '查看作品评分'),
('学生', 'API', '/file/download', '下载文件'),

-- 教师权限
('教师', 'API', '/jingsaixinxi/*', '竞赛管理所有接口'),
('教师', 'API', '/jingsaibaoming/*', '报名管理所有接口'),
('教师', 'API', '/zuopindafen/*', '作品打分所有接口'),
('教师', 'API', '/xuesheng/list', '查看学生列表'),
('教师', 'API', '/jiaoshi/*', '教师信息管理'),
('教师', 'API', '/file/*', '文件管理'),

-- 管理员权限（所有权限）
('管理员', 'API', '/*', '所有接口权限');

-- 5. 为账号字段添加唯一索引（如果不存在）
CREATE UNIQUE INDEX IF NOT EXISTS idx_xuesheng_xuehao ON xuesheng(xuehao);
CREATE UNIQUE INDEX IF NOT EXISTS idx_jiaoshi_gonghao ON jiaoshi(gonghao);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- 6. 优化 token 表查询性能
CREATE INDEX IF NOT EXISTS idx_token_userid ON token(userid);
CREATE INDEX IF NOT EXISTS idx_token_token ON token(token);
