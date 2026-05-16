-- ============================================
-- 更新用户和竞赛照片
-- ============================================
-- 说明：
-- 1. 使用 upload/人像.png 作为所有学生、教师、管理员的头像
-- 2. 使用 upload/比赛.png 作为所有竞赛的封面图片
-- 3. 图片路径为相对路径，前端访问时会自动拼接
-- ============================================

USE bysj_springboot;

-- 1. 更新所有学生的照片
UPDATE xuesheng 
SET zhaopian = 'upload/人像.png'
WHERE zhaopian IS NULL OR zhaopian = '';

-- 2. 更新所有教师的照片
UPDATE jiaoshi 
SET zhaopian = 'upload/人像.png'
WHERE zhaopian IS NULL OR zhaopian = '';

-- 3. 更新管理员的照片（users表）
UPDATE users 
SET zhaopian = 'upload/人像.png'
WHERE zhaopian IS NULL OR zhaopian = '';

-- 4. 更新所有竞赛的封面图片
UPDATE jingsaixinxi 
SET fengmiantupian = 'upload/比赛.png'
WHERE fengmiantupian IS NULL OR fengmiantupian = '';

-- 5. 验证更新结果
SELECT '学生照片更新情况：' AS 说明;
SELECT COUNT(*) AS 总人数, 
       SUM(CASE WHEN zhaopian = 'upload/人像.png' THEN 1 ELSE 0 END) AS 已设置照片人数
FROM xuesheng;

SELECT '教师照片更新情况：' AS 说明;
SELECT COUNT(*) AS 总人数, 
       SUM(CASE WHEN zhaopian = 'upload/人像.png' THEN 1 ELSE 0 END) AS 已设置照片人数
FROM jiaoshi;

SELECT '管理员照片更新情况：' AS 说明;
SELECT COUNT(*) AS 总人数, 
       SUM(CASE WHEN zhaopian = 'upload/人像.png' THEN 1 ELSE 0 END) AS 已设置照片人数
FROM users;

SELECT '竞赛封面更新情况：' AS 说明;
SELECT COUNT(*) AS 总竞赛数, 
       SUM(CASE WHEN fengmiantupian = 'upload/比赛.png' THEN 1 ELSE 0 END) AS 已设置封面数
FROM jingsaixinxi;

-- ============================================
-- 完成提示
-- ============================================
SELECT '✅ 照片更新完成！' AS 状态;
SELECT '学生、教师、管理员头像：upload/人像.png' AS 人像图片;
SELECT '竞赛封面图片：upload/比赛.png' AS 比赛图片;
