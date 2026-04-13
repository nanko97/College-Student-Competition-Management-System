-- =============================================
-- 检查竞赛数据诊断脚本
-- =============================================

-- 1. 查看 jingsaixinxi 表的所有数据
SELECT 
    id,
    jingsaimingcheng AS 竞赛名称，
    jingsaileixing AS 竞赛类型，
    jingsaididian AS 竞赛地点，
    gonghao AS 工号，
    jiaoshixingming AS 教师姓名，
    addtime AS 创建时间
FROM jingsaixinxi
ORDER BY id DESC;

-- 2. 统计记录数
SELECT COUNT(*) AS 总记录数 FROM jingsaixinxi;

-- 3. 查看特定工号的竞赛
SELECT 
    gonghao AS 工号，
    COUNT(*) AS 竞赛数量
FROM jingsaixinxi
GROUP BY gonghao;
