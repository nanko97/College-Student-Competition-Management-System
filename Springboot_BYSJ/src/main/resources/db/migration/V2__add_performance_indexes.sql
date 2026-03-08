-- =============================================
-- 性能优化索引 - 数据库迁移脚本
-- 版本：V2
-- 日期：2026-03-06
-- 说明：本脚本用于优化系统查询性能，添加必要的数据库索引
-- =============================================

-- 1. 竞赛信息表索引
-- jingsaileixing: 竞赛类型索引，加速分类查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaixinxi' AND index_name = 'idx_jingsaixinxi_leixing');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaixinxi_leixing already exists" AS msg', 
                   'CREATE INDEX idx_jingsaixinxi_leixing ON jingsaixinxi(jingsaileixing)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- jingsaishijian: 竞赛时间索引，加速时间排序
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaixinxi' AND index_name = 'idx_jingsaixinxi_shijian');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaixinxi_shijian already exists" AS msg', 
                   'CREATE INDEX idx_jingsaixinxi_shijian ON jingsaixinxi(jingsaishijian)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- gonghao: 工号索引，加速教师查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaixinxi' AND index_name = 'idx_jingsaixinxi_gonghao');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaixinxi_gonghao already exists" AS msg', 
                   'CREATE INDEX idx_jingsaixinxi_gonghao ON jingsaixinxi(gonghao)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 竞赛报名表索引
-- xuehao: 学号索引，加速学生报名查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_xuehao');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_xuehao already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_xuehao ON jingsaibaoming(xuehao)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- jingsaimingcheng: 竞赛名称索引，加速竞赛报名统计
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_mingcheng');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_mingcheng already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_mingcheng ON jingsaibaoming(jingsaimingcheng)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- sfsh: 审核状态索引，加速待审核查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_shenhe');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_shenhe already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_shenhe ON jingsaibaoming(sfsh)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- gonghao: 工号索引，加速教师查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_gonghao');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_gonghao already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_gonghao ON jingsaibaoming(gonghao)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 作品打分表索引
-- xuehao: 学号索引，加速学生成绩查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'zuopindafen' AND index_name = 'idx_zuopindafen_xuehao');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_zuopindafen_xuehao already exists" AS msg', 
                   'CREATE INDEX idx_zuopindafen_xuehao ON zuopindafen(xuehao)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- gonghao: 工号索引，加速教师评分查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'zuopindafen' AND index_name = 'idx_zuopindafen_gonghao');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_zuopindafen_gonghao already exists" AS msg', 
                   'CREATE INDEX idx_zuopindafen_gonghao ON zuopindafen(gonghao)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- jingsaimingcheng: 竞赛名称索引，加速作品查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'zuopindafen' AND index_name = 'idx_zuopindafen_mingcheng');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_zuopindafen_mingcheng already exists" AS msg', 
                   'CREATE INDEX idx_zuopindafen_mingcheng ON zuopindafen(jingsaimingcheng)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 教师表索引
-- zhicheng: 职称索引，加速职称筛选
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jiaoshi' AND index_name = 'idx_jiaoshi_zhicheng');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jiaoshi_zhicheng already exists" AS msg', 
                   'CREATE INDEX idx_jiaoshi_zhicheng ON jiaoshi(zhicheng)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. 组合索引 (根据实际查询场景)
-- xuehao + sfsh: 学号 + 审核状态组合索引，加速个人报名状态查询
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_xuehao_status');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_xuehao_status already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_xuehao_status ON jingsaibaoming(xuehao, sfsh)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- gonghao + sfsh: 工号 + 审核状态组合索引，加速竞赛审核统计
SET @exist := (SELECT COUNT(*) FROM information_schema.statistics 
               WHERE table_schema = DATABASE() AND table_name = 'jingsaibaoming' AND index_name = 'idx_jingsaibaoming_gonghao_status');
SET @sqlstmt := IF(@exist > 0, 'SELECT "Index idx_jingsaibaoming_gonghao_status already exists" AS msg', 
                   'CREATE INDEX idx_jingsaibaoming_gonghao_status ON jingsaibaoming(gonghao, sfsh)');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

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
