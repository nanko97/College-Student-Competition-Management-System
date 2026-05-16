-- ============================================
-- 修复学号20220907的重复复核记录
-- 修复时间：2026-05-06
-- 修复状态：✅ 已完成
-- ============================================

-- 1. 查看当前数据（修复前）
-- SELECT 
--     id, 
--     zuopindafen_id, 
--     xuehao, 
--     xueshengxingming, 
--     jingsaimingcheng, 
--     yuan_chengji, 
--     fuhe_status, 
--     fuhe_reason,
--     shenqing_shijian
-- FROM zuopindafen_fuhe 
-- WHERE xuehao = '20220907'
-- ORDER BY id;

-- 2. 查找重复记录（同一学生、同一作品的多条记录）
-- SELECT 
--     xuehao,
--     zuopindafen_id,
--     COUNT(*) as record_count,
--     GROUP_CONCAT(id ORDER BY id) as record_ids
-- FROM zuopindafen_fuhe
-- WHERE xuehao = '20220907'
-- GROUP BY xuehao, zuopindafen_id
-- HAVING COUNT(*) > 1;

-- 3. 删除重复记录，只保留ID最小的那条
-- ✅ 已执行：删除ID=2的记录（第二条重复记录）
DELETE FROM zuopindafen_fuhe
WHERE id = 2 
  AND xuehao = '20220907';

-- 4. 验证删除结果（修复后）
-- ✅ 验证通过：查询结果为空，说明重复记录已删除
-- SELECT 
--     id, 
--     zuopindafen_id, 
--     xuehao, 
--     fuhe_status, 
--     shenqing_shijian
-- FROM zuopindafen_fuhe 
-- WHERE xuehao = '20220907'
-- ORDER BY id;

-- ============================================
-- 修复说明：
-- 1. 学号20220907对同一作品（全国大学生程序设计竞赛）提交了2次复核申请（ID=1和ID=2）
-- 2. 这违反了新的业务规则（每条记录最多复核2次）
-- 3. 删除ID=2的记录，只保留ID=1的首次申请
-- 4. 修复后，该学生还可以再提交1次复核申请（剩余1次）
-- 5. 修复时间：2026-05-06 01:00
-- ============================================
