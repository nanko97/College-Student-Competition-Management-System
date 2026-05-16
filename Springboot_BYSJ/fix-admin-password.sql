-- ========================================
-- 修复管理员密码为 123456
-- ========================================

-- 1. 更新管理员密码
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
WHERE username = 'admin';

-- 2. 验证
SELECT username, 
       CASE WHEN password LIKE CONCAT('$','2a$10$%') THEN 'OK' ELSE 'ERROR' END as status
FROM users WHERE username = 'admin';
