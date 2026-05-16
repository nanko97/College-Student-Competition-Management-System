-- ============================================
-- 数据联动批量修复脚本
-- ============================================
-- 功能：修复现有数据不一致问题
-- 将各表中冗余的学生/教师信息更新为最新值
-- ============================================

USE bysj_springboot;

-- ============================================
-- 1. 修复团队成员表的学生信息
-- ============================================
UPDATE jingsai_tuandui_chengyuan c
INNER JOIN xuesheng x ON c.xuehao = x.xuehao
SET 
    c.xueshengxingming = x.xueshengxingming,
    c.xueyuan = x.xueyuanmingcheng,
    c.banji = x.banji,
    c.shouji = x.shouji,
    c.youxiang = x.youxiang
WHERE c.is_active = '是';

SELECT '团队成员表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 2. 修复竞赛报名表的学生姓名
-- ============================================
UPDATE jingsaibaoming b
INNER JOIN xuesheng x ON b.xuehao = x.xuehao
SET b.xueshengxingming = x.xueshengxingming;

SELECT '竞赛报名表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 3. 修复作品打分表的学生姓名
-- ============================================
UPDATE zuopindafen z
INNER JOIN xuesheng x ON z.xuehao = x.xuehao
SET z.xueshengxingming = x.xueshengxingming;

SELECT '作品打分表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 4. 修复作品复核表的学生姓名
-- ============================================
UPDATE zuopindafen_fuhe f
INNER JOIN xuesheng x ON f.xuehao = x.xuehao
SET f.xueshengxingming = x.xueshengxingming;

SELECT '作品复核表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 5. 修复缴费记录表的学生姓名
-- ============================================
UPDATE jingsai_jiaofei_jilu j
INNER JOIN xuesheng x ON j.xuehao = x.xuehao
SET j.xueshengxingming = x.xueshengxingming;

SELECT '缴费记录表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 6. 修复团队申请表的学生姓名
-- ============================================
UPDATE tuandui_apply t
INNER JOIN xuesheng x ON t.xuehao = x.xuehao
SET t.xueshengxingming = x.xueshengxingming;

SELECT '团队申请表修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 7. 修复竞赛报名表的教师姓名
-- ============================================
UPDATE jingsaibaoming b
INNER JOIN jiaoshi j ON b.gonghao = j.gonghao
SET b.jiaoshixingming = j.jiaoshixingming;

SELECT '竞赛报名表教师信息修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 8. 修复作品打分表的教师姓名
-- ============================================
UPDATE zuopindafen z
INNER JOIN jiaoshi j ON z.gonghao = j.gonghao
SET z.jiaoshixingming = j.jiaoshixingming;

SELECT '作品打分表教师信息修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 9. 修复竞赛信息表的教师姓名
-- ============================================
UPDATE jingsaixinxi jx
INNER JOIN jiaoshi j ON jx.gonghao = j.gonghao
SET jx.jiaoshixingming = j.jiaoshixingming;

SELECT '竞赛信息表教师信息修复完成' AS 状态, ROW_COUNT() AS 更新行数;

-- ============================================
-- 10. 验证修复结果 - 检查数据一致性
-- ============================================

-- 检查团队成员表
SELECT 
    '团队成员表' AS 表名,
    COUNT(*) AS 总记录数,
    SUM(CASE WHEN c.xueshengxingming != x.xueshengxingming THEN 1 ELSE 0 END) AS 不一致记录数
FROM jingsai_tuandui_chengyuan c
LEFT JOIN xuesheng x ON c.xuehao = x.xuehao
WHERE c.is_active = '是';

-- 检查竞赛报名表
SELECT 
    '竞赛报名表' AS 表名,
    COUNT(*) AS 总记录数,
    SUM(CASE WHEN b.xueshengxingming != x.xueshengxingming THEN 1 ELSE 0 END) AS 不一致记录数
FROM jingsaibaoming b
LEFT JOIN xuesheng x ON b.xuehao = x.xuehao;

-- 检查作品打分表
SELECT 
    '作品打分表' AS 表名,
    COUNT(*) AS 总记录数,
    SUM(CASE WHEN z.xueshengxingming != x.xueshengxingming THEN 1 ELSE 0 END) AS 不一致记录数
FROM zuopindafen z
LEFT JOIN xuesheng x ON z.xuehao = x.xuehao;

-- ============================================
-- 执行完成提示
-- ============================================
SELECT '✅ 所有数据联动修复完成！' AS 执行结果;
