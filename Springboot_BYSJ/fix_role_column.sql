-- 修复数据库字段缺失问题
USE springbootrd362;

-- 为 xuesheng 表添加 role 字段
ALTER TABLE xuesheng ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '学生' COMMENT '角色';

-- 为 jiaoshi 表添加 role 字段  
ALTER TABLE jiaoshi ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '教师' COMMENT '角色';

-- 验证
SHOW COLUMNS FROM xuesheng LIKE 'role';
SHOW COLUMNS FROM jiaoshi LIKE 'role';

SELECT '数据库修复完成！' AS message;
