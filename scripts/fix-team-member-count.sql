-- ============================================
-- 团队成员数据一致性修复脚本
-- ============================================
-- 问题描述：团队表(jingsai_tuandui)中的chengyuan_renshu字段值
--          与成员表(jingsai_tuandui_chengyuan)中的实际记录数不一致
-- 修复方案：根据成员表的实际记录数，更新团队表的人数统计
-- ============================================

USE bysj_springboot;

-- 1. 先查看数据不一致的团队
SELECT 
    t.id AS 团队ID,
    t.tuandui_bianhao AS 团队编号,
    t.tuandui_mingcheng AS 团队名称,
    t.chengyuan_renshu AS 团队表记录的人数,
    COUNT(c.id) AS 成员表实际人数
FROM jingsai_tuandui t
LEFT JOIN jingsai_tuandui_chengyuan c ON t.id = c.tuandui_id AND c.is_active = '是'
GROUP BY t.id, t.tuandui_bianhao, t.tuandui_mingcheng, t.chengyuan_renshu
HAVING t.chengyuan_renshu != COUNT(c.id)
ORDER BY t.id;

-- 2. 修复数据不一致 - 将团队人数更新为成员表的实际人数
UPDATE jingsai_tuandui t
SET t.chengyuan_renshu = (
    SELECT COUNT(*) 
    FROM jingsai_tuandui_chengyuan c 
    WHERE c.tuandui_id = t.id AND c.is_active = '是'
)
WHERE t.id IN (
    SELECT id FROM (
        SELECT t2.id
        FROM jingsai_tuandui t2
        LEFT JOIN jingsai_tuandui_chengyuan c2 ON t2.id = c2.tuandui_id AND c2.is_active = '是'
        GROUP BY t2.id
        HAVING t2.chengyuan_renshu != COUNT(c2.id)
    ) AS temp
);

-- 3. 验证修复结果
SELECT 
    '修复后检查结果' AS 说明,
    COUNT(*) AS 数据不一致的团队数
FROM (
    SELECT t.id
    FROM jingsai_tuandui t
    LEFT JOIN jingsai_tuandui_chengyuan c ON t.id = c.tuandui_id AND c.is_active = '是'
    GROUP BY t.id, t.chengyuan_renshu
    HAVING t.chengyuan_renshu != COUNT(c.id)
) AS inconsistent_teams;

-- 4. 查看特定团队的成员详情（可选，用于调试）
-- 将下面的团队ID替换为实际要查看的团队ID
-- SELECT * FROM jingsai_tuandui_chengyuan WHERE tuandui_id = 1;
-- SELECT * FROM jingsai_tuandui WHERE id = 1;
