-- 修复所有学生和教师密码为正确的 BCrypt 格式（密码: 123456）
-- BCrypt hash for '123456': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

-- 1. 修复所有学生密码
UPDATE xuesheng SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 2. 修复所有教师密码
UPDATE jiaoshi SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 3. 修复管理员密码
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 验证修复结果
SELECT 'xuesheng' as table_name, xuehao as account, LEFT(mima, 15) as pwd_prefix FROM xuesheng LIMIT 3;
SELECT 'jiaoshi' as table_name, gonghao as account, LEFT(mima, 15) as pwd_prefix FROM jiaoshi LIMIT 3;
SELECT 'users' as table_name, username as account, LEFT(password, 15) as pwd_prefix FROM users LIMIT 3;
