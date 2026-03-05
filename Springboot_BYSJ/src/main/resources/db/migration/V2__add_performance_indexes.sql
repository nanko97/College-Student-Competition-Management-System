-- =============================================
-- 性能优化索引 - 数据库迁移脚本
-- 版本：V2
-- 日期：2026-03-05
-- 说明：本脚本用于优化系统查询性能，添加必要的数据库索引
-- =============================================

-- 1. 竞赛信息表索引
CREATE INDEX IF NOT EXISTS idx_jingsaixinxi_leibie 
    ON jingsaixinxi(jingsaileibie) 
    COMMENT '竞赛类型索引，加速分类查询';

CREATE INDEX IF NOT EXISTS idx_jingsaixinxi_shijian 
    ON jingsaixinxi(jingsaishijian) 
    COMMENT '竞赛时间索引，加速时间排序';

CREATE INDEX IF NOT EXISTS idx_jingsaixinxi_fabu 
    ON jingsaixinxi(fabushijian) 
    COMMENT '发布时间索引，加速最新竞赛查询';

-- 2. 竞赛报名表索引
CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_xuehao 
    ON jingsaibaoming(xuehao) 
    COMMENT '学号索引，加速学生报名查询';

CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_jingsai 
    ON jingsaibaoming(jingsaibianhao) 
    COMMENT '竞赛编号索引，加速竞赛报名统计';

CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_shenhe 
    ON jingsaibaoming(sfsh) 
    COMMENT '审核状态索引，加速待审核查询';

CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_shijian 
    ON jingsaibaoming(baomingshijian) 
    COMMENT '报名时间索引，加速时间排序';

-- 3. 作品打分表索引
CREATE INDEX IF NOT EXISTS idx_zuopindafen_jingsai 
    ON zuopindafen(jingsaibianhao) 
    COMMENT '竞赛编号索引，加速作品查询';

CREATE INDEX IF NOT EXISTS idx_zuopindafen_xuehao 
    ON zuopindafen(xuehao) 
    COMMENT '学号索引，加速学生成绩查询';

CREATE INDEX IF NOT EXISTS idx_zuopindafen_gonghao 
    ON zuopindafen(gonghao) 
    COMMENT '工号索引，加速教师评分查询';

-- 4. 教师表索引
CREATE INDEX IF NOT EXISTS idx_jiaoshi_zhicheng 
    ON jiaoshi(zhicheng) 
    COMMENT '职称索引，加速职称筛选';

-- 5. 组合索引 (根据实际查询场景)
CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_xuehao_status 
    ON jingsaibaoming(xuehao, sfsh) 
    COMMENT '学号 + 审核状态组合索引，加速个人报名状态查询';

CREATE INDEX IF NOT EXISTS idx_jingsaibaoming_jingsai_status 
    ON jingsaibaoming(jingsaibianhao, sfsh) 
    COMMENT '竞赛编号 + 审核状态组合索引，加速竞赛审核统计';

-- 6. 验证索引创建结果
SELECT 
    TABLE_NAME, 
    INDEX_NAME, 
    COLUMN_NAME, 
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME IN ('jingsaixinxi', 'jingsaibaoming', 'zuopindafen', 'jiaoshi')
ORDER BY TABLE_NAME, INDEX_NAME;
