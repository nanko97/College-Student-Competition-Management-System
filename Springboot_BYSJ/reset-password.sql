-- ========================================
-- 批量重置所有用户密码为 123456
-- BCrypt加密后的密码哈希值
-- ========================================
-- 原始密码: 123456
-- BCrypt哈希: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- ========================================

-- 1. 更新管理员密码 (users表)
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 2. 更新所有学生密码 (xuesheng表)
-- 学生账号范围: 2022001 - 2022009
UPDATE xuesheng 
SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
WHERE xuehao LIKE '202200%';

-- 3. 更新所有教师密码 (jiaoshi表)
-- 教师账号范围: T2022001 - T2022009
UPDATE jiaoshi 
SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
WHERE gonghao LIKE 'T202200%';

-- ========================================
-- 验证更新结果
-- ========================================

SELECT '========== 管理员账号 ==========' as info;
SELECT id, username, '管理员' as role, 
       CASE WHEN password LIKE '$2a$10$%' THEN '✓ 已加密' ELSE '✗ 未加密' END as status
FROM users;

SELECT '========== 学生账号 ==========' as info;
SELECT id, xuehao as username, '学生' as role,
       CASE WHEN mima LIKE '$2a$10$%' THEN '✓ 已加密' ELSE '✗ 未加密' END as status
FROM xuesheng
WHERE xuehao LIKE '202200%'
ORDER BY xuehao;

SELECT '========== 教师账号 ==========' as info;
SELECT id, gonghao as username, '教师' as role,
       CASE WHEN mima LIKE '$2a$10$%' THEN '✓ 已加密' ELSE '✗ 未加密' END as status
FROM jiaoshi
WHERE gonghao LIKE 'T202200%'
ORDER BY gonghao;

SELECT '========== 更新完成 ==========' as info;
SELECT '总密码: 123456' as note;
SELECT '管理员: admin' as accounts;
SELECT '学生: 2022001-2022009' as accounts;
SELECT '教师: T2022001-T2022009' as accounts;
