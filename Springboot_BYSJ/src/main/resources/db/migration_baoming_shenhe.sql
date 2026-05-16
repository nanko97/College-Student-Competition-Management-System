-- ============================================
-- 报名审核驱动缴费流程 - 数据迁移脚本
-- 执行时间：2026-05-04
-- 说明：清理未审核通过的报名对应的缴费记录
-- ============================================

USE BYSJ_Springboot;

-- 步骤1：查看当前数据状态（执行前检查）
SELECT 
    '当前数据统计' AS info,
    (SELECT COUNT(*) FROM jingsaibaoming) AS total_baoming,
    (SELECT COUNT(*) FROM jingsaibaoming WHERE sfsh = '是' OR sfsh = '已通过') AS approved_baoming,
    (SELECT COUNT(*) FROM jingsaibaoming WHERE sfsh != '是' AND sfsh != '已通过') AS pending_baoming,
    (SELECT COUNT(*) FROM jingsai_jiaofei_jilu) AS total_jiaofei;

-- 步骤2：删除未审核通过的报名对应的缴费记录
-- 注意：只删除那些报名状态不是"是"或"已通过"的记录
DELETE j 
FROM jingsai_jiaofei_jilu j
INNER JOIN jingsaibaoming b ON j.baoming_id = b.id
WHERE (b.sfsh != '是' AND b.sfsh != '已通过')
   OR b.sfsh IS NULL;

-- 步骤3：验证删除结果
SELECT 
    '删除后统计' AS info,
    (SELECT COUNT(*) FROM jingsai_jiaofei_jilu) AS remaining_jiaofei,
    (SELECT COUNT(*) FROM jingsai_jiaofei_jilu j 
     INNER JOIN jingsaibaoming b ON j.baoming_id = b.id 
     WHERE b.sfsh != '是' AND b.sfsh != '已通过') AS invalid_jiaofei;

-- 步骤4：更新已审核通过但ispay状态不正确的报名记录
UPDATE jingsaibaoming b
LEFT JOIN jingsai_jiaofei_jilu j ON b.id = j.baoming_id 
SET b.ispay = CASE 
    WHEN j.jiaofei_zhuangtai = '已通过' THEN '已缴费'
    WHEN j.jiaofei_zhuangtai = '已驳回' THEN '已驳回'
    WHEN j.id IS NOT NULL THEN '未缴费'
    ELSE b.ispay  -- 如果没有缴费记录，保持原状态
END
WHERE (b.sfsh = '是' OR b.sfsh = '已通过');

-- 步骤5：最终验证
SELECT 
    '最终数据状态' AS info,
    COUNT(*) AS total,
    SUM(CASE WHEN sfsh = '是' OR sfsh = '已通过' THEN 1 ELSE 0 END) AS approved,
    SUM(CASE WHEN ispay = '已缴费' THEN 1 ELSE 0 END) AS paid,
    SUM(CASE WHEN ispay = '未缴费' THEN 1 ELSE 0 END) AS unpaid
FROM jingsaibaoming;

-- 完成提示
SELECT '数据迁移完成！请重启后端服务并测试新流程。' AS message;
