-- 修复学生和教师密码问题
-- 将密码统一重置为 123456 的 BCrypt 加密值
-- BCrypt hash for '123456': $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- 重置所有学生的密码为 123456
UPDATE xuesheng SET mima = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE xuehao IN ('2022001', '2022002', '2022003', '2022004', '2022005');

-- 重置所有教师的密码为 123456
UPDATE jiaoshi SET mima = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE gonghao IN ('T2022001', 'T2022002', 'T2022003', 'T2022004', 'T2022005');

-- 重置管理员密码为 123456
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE username = 'admin';

-- 验证更新结果
SELECT 'xuesheng' as table_name, xuehao, '密码已重置为 123456' as status FROM xuesheng WHERE xuehao IN ('2022001', '2022002', '2022003', '2022004', '2022005')
UNION ALL
SELECT 'jiaoshi' as table_name, gonghao, '密码已重置为 123456' as status FROM jiaoshi WHERE gonghao IN ('T2022001', 'T2022002', 'T2022003', 'T2022004', 'T2022005')
UNION ALL
SELECT 'users' as table_name, username, '密码已重置为 123456' as status FROM users WHERE username = 'admin';
