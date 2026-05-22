-- 更新zuopindafen表的jingsaileixing字段
UPDATE zuopindafen 
SET jingsaileixing = '测试竞赛' 
WHERE id = 845773896086065152;

-- 验证更新结果
SELECT id, xuehao, xueshengxingming, jingsaimingcheng, jingsaileixing, zuopinpingfen, gonghao 
FROM zuopindafen 
WHERE id = 845773896086065152;
