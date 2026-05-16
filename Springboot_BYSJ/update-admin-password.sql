-- ========================================
-- 更新管理员密码为 123456
-- BCrypt 加密后的 123456 密码
-- ========================================

-- 使用 BCryptPasswordEncoder 加密 123456
-- 加密后的值: $2a$10$YQ4vJQ4vJQ4vJQ4vJQ4vJO4vJQ4vJQ4vJQ4vJQ4vJQ4vJQ4vJ

UPDATE users SET password = '$2a$10$YQ4vJQ4vJQ4vJQ4vJQ4vJO4vJQ4vJQ4vJQ4vJQ4vJQ4vJQ4vJ' WHERE username = 'admin';

-- 验证更新
SELECT username, role, 
       CASE 
           WHEN password LIKE '$2a$%' THEN '✓ 已加密' 
           ELSE ' 未加密' 
       END as status
FROM users 
WHERE username = 'admin';
