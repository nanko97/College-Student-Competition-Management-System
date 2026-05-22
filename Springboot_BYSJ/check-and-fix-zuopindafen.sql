-- ============================================
-- 检查并修复zuopindafen表中的null数据和状态
-- ============================================

-- 1. 检查所有字段为null的记录
SELECT 
    id,
    xuehao,
    xueshengxingming,
    jingsaimingcheng,
    jingsaileixing,
    zuopinpingfen,
    pingjiashijian,
    gonghao,
    jiaoshixingming,
    addtime
FROM zuopindafen
WHERE 
    jingsaileixing IS NULL OR
    pingjiashijian IS NULL OR
    jiaoshixingming IS NULL OR
    xuehao IS NULL OR
    xueshengxingming IS NULL OR
    jingsaimingcheng IS NULL OR
    zuopinpingfen IS NULL OR
    gonghao IS NULL;

-- 2. 检查评分超出正常范围(0-100)的记录
SELECT 
    id,
    zuopinpingfen,
    xuehao,
    jingsaimingcheng
FROM zuopindafen
WHERE zuopinpingfen < 0 OR zuopinpingfen > 100;

-- 3. 检查工号是否存在于教师表
SELECT 
    z.id,
    z.gonghao,
    z.jiaoshixingming,
    t.xingming AS 教师表姓名
FROM zuopindafen z
LEFT JOIN jiaoshi t ON z.gonghao = t.gonghao
WHERE z.gonghao IS NOT NULL AND t.gonghao IS NULL;

-- 4. 检查学号是否存在于学生表
SELECT 
    z.id,
    z.xuehao,
    z.xueshengxingming,
    s.xingming AS 学生表姓名
FROM zuopindafen z
LEFT JOIN xuesheng s ON z.xuehao = s.xuehao
WHERE z.xuehao IS NOT NULL AND s.xuehao IS NULL;

-- ============================================
-- 修复脚本（根据检查结果选择执行）
-- ============================================

-- 修复1：更新空白的jingsaileixing字段
UPDATE zuopindafen 
SET jingsaileixing = '未分类' 
WHERE jingsaileixing IS NULL OR jingsaileixing = '';

-- 修复2：更新空白的pingjiashijian字段（使用addtime）
UPDATE zuopindafen 
SET pingjiashijian = addtime 
WHERE pingjiashijian IS NULL AND addtime IS NOT NULL;

-- 修复3：更新空白的jiaoshixingming字段（从教师表获取）
UPDATE zuopindafen z
INNER JOIN jiaoshi t ON z.gonghao = t.gonghao
SET z.jiaoshixingming = t.xingming
WHERE z.jiaoshixingming IS NULL AND z.gonghao IS NOT NULL;

-- 修复4：更新空白的xueshengxingming字段（从学生表获取）
UPDATE zuopindafen z
INNER JOIN xuesheng s ON z.xuehao = s.xuehao
SET z.xueshengxingming = s.xingming
WHERE z.xueshengxingming IS NULL AND z.xuehao IS NOT NULL;

-- 修复5：修正异常评分（如果存在）
UPDATE zuopindafen 
SET zuopinpingfen = 60 
WHERE zuopinpingfen < 0 OR zuopinpingfen > 100;

-- ============================================
-- 验证修复结果
-- ============================================
SELECT 
    '修复后统计' AS 状态,
    COUNT(*) AS 总记录数,
    SUM(CASE WHEN jingsaileixing IS NULL THEN 1 ELSE 0 END) AS jingsaileixing为null,
    SUM(CASE WHEN pingjiashijian IS NULL THEN 1 ELSE 0 END) AS pingjiashijian为null,
    SUM(CASE WHEN jiaoshixingming IS NULL THEN 1 ELSE 0 END) AS jiaoshixingming为null,
    SUM(CASE WHEN xueshengxingming IS NULL THEN 1 ELSE 0 END) AS xueshengxingming为null
FROM zuopindafen;
