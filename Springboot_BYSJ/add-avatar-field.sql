-- ========================================
-- 添加用户头像字段
-- ========================================

-- 1. 给 users 表添加头像字段（管理员）
ALTER TABLE users ADD COLUMN touxiang VARCHAR(500) DEFAULT NULL COMMENT '头像URL' AFTER role;

-- 2. 给 jiaoshi 表添加头像字段（如果不存在）
-- 注意：jiaoshi表已有zhaopian字段，但为了统一命名，添加touxiang字段
ALTER TABLE jiaoshi ADD COLUMN touxiang VARCHAR(500) DEFAULT NULL COMMENT '头像URL' AFTER zhaopian;

-- ========================================
-- 验证更新结果
-- ========================================
SELECT '========== users表结构 ==========' as info;
DESC users;

SELECT '========== jiaoshi表结构 ==========' as info;
DESC jiaoshi;

SELECT '========== 更新完成 ==========' as info;
