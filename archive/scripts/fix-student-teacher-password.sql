-- 修复学生和教师密码：添加缺失的 BCrypt 前缀
-- 原密码: .zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
-- 正确:  $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

-- 修复学生密码
UPDATE xuesheng SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' 
WHERE mima NOT LIKE '$2a%' AND mima LIKE '.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 修复教师密码
UPDATE jiaoshi SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' 
WHERE mima NOT LIKE '$2a%' AND mima LIKE '.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

-- 验证结果
SELECT 'xuesheng' as table_name, xuehao, LEFT(mima, 15) as pwd_prefix 
FROM xuesheng WHERE xuehao IN ('2022001','2022002','2022003','2022004','2022005')
UNION ALL
SELECT 'jiaoshi', gonghao, LEFT(mima, 15) 
FROM jiaoshi WHERE gonghao IN ('T2022001','T2022002','T2022003','T2022004','T2022005');
