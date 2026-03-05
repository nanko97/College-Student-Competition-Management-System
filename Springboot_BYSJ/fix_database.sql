-- =============================================
-- 数据库修复脚本 - 添加缺失的 role 字段
-- 执行方式：在 MySQL 命令行或图形化工具中执行此脚本
-- =============================================

USE springbootrd362;

-- 1. 为 xuesheng 表添加 role 字段（如果不存在）
ALTER TABLE xuesheng 
ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '学生' COMMENT '角色';

-- 2. 为 jiaoshi 表添加 role 字段（如果不存在）
ALTER TABLE jiaoshi 
ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '教师' COMMENT '角色';

-- 3. 验证字段是否添加成功
SELECT '验证 xuesheng 表:' AS info;
DESC xuesheng;

SELECT '验证 jiaoshi 表:' AS info;
DESC jiaoshi;

-- 4. 测试查询
SELECT COUNT(*) AS xuesheng_count FROM xuesheng;
SELECT COUNT(*) AS jiaoshi_count FROM jiaoshi;

SELECT '✓ 数据库修复完成！请重启应用后重试。' AS message;
