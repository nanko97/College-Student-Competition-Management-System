-- ============================================
-- 修复教师新增竞赛 500 错误 - SQL 执行脚本
-- ============================================
-- 问题：jingsaixinxi 表缺少 gonghao 和 jiaoshixingming 字段
-- 解决方案：添加这两个字段到表中
-- ============================================

USE springbootrd362;

-- 检查字段是否已存在，避免重复执行报错
SELECT '开始检查表结构...' AS message;

-- 添加 gonghao 字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = 'jingsaixinxi';
SET @columnname = 'gonghao';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_NAME = @tablename
      AND TABLE_SCHEMA = @dbname
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT ''字段已存在，无需添加'' AS message',
  'ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT ''工号'' AFTER `fengmiantupian`'
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 jiaoshixingming 字段（如果不存在）
SET @columnname = 'jiaoshixingming';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_NAME = @tablename
      AND TABLE_SCHEMA = @dbname
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT ''字段已存在，无需添加'' AS message',
  'ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT ''教师姓名'' AFTER `gonghao`'
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 验证结果
SELECT '验证表结构...' AS message;
DESCRIBE jingsaixinxi;

SELECT '修复完成！请重启后端服务后测试新增竞赛功能。' AS message;
