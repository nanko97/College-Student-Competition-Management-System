-- ============================================
-- 为教师 text002 插入 zuopindafen 测试数据
-- ============================================
-- 说明：教师 text002 在 zuopindafen 表中没有打分记录，
-- 导致"我的打分"页面显示为空。
-- 此脚本插入 5 条测试打分记录，验证修复效果。
-- ============================================

USE bysj_springboot;

-- 先查看 text002 创建了哪些竞赛（可选，用于参考）
-- SELECT id, jingsaimingcheng, gonghao FROM jingsaixinxi WHERE gonghao = 'text002';

-- 插入测试打分数据（gonghao = 'text002'）
-- 使用系统中已有的竞赛名称和学号
INSERT INTO zuopindafen (id, addtime, xuehao, xueshengxingming, jingsaimingcheng, jingsaileixing, zuopinpingfen, pingjianeirong, pingjiashijian, gonghao, jiaoshixingming) VALUES
(9001, NOW(), '2022001', '张志强', '全国大学生程序设计竞赛（ACM-ICPC）', '算法竞赛', 88, '算法实现基本正确，优化空间较大', '2026-05-22', 'text002', '测试教师'),
(9002, NOW(), '2022003', '王浩然', '全国大学生程序设计竞赛（ACM-ICPC）', '算法竞赛', 82, '代码风格良好，边界处理需改进', '2026-05-22', 'text002', '测试教师'),
(9003, NOW(), '2022001', '张志强', '全国大学生数学建模竞赛', '数学建模', 90, '模型建立合理，论文表达清晰', '2026-05-21', 'text002', '测试教师'),
(9004, NOW(), '2022005', '陈俊杰', '全国大学生数学建模竞赛', '数学建模', 85, '数据分析完整，结论有待验证', '2026-05-21', 'text002', '测试教师'),
(9005, NOW(), '2022002', '李雨婷', '蓝桥杯全国软件和信息技术专业人才大赛', '软件开发', 78, '基础题目完成良好，进阶题目有待提升', '2026-05-20', 'text002', '测试教师');

-- 验证插入结果
SELECT COUNT(*) as text002_dafen_count FROM zuopindafen WHERE gonghao = 'text002';
SELECT id, xuehao, xueshengxingming, jingsaimingcheng, zuopinpingfen, gonghao FROM zuopindafen WHERE gonghao = 'text002';

-- ============================================
-- 执行完此脚本后：
-- 1. 刷新前端页面（教师text002登录后的"我的打分"页面）
-- 2. 应能看到 5 条打分记录
-- 3. 如需清除测试数据，执行下面的删除语句：
--    DELETE FROM zuopindafen WHERE gonghao = 'text002' AND id BETWEEN 9001 AND 9005;
-- ============================================