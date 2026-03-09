-- ============================================
-- 自动修复 jingsaixinxi 表结构
-- ============================================

USE springbootrd362;

-- 检查并添加 gonghao 字段
SET @dbname = DATABASE();
SET @tablename = 'jingsaixinxi';
SET @columnname = 'gonghao';

SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE
    TABLE_NAME = @tablename AND TABLE_SCHEMA = @dbname AND COLUMN_NAME = @columnname) > 0,
  'SELECT ''✓ gonghao 字段已存在'' AS message',
  'ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT ''工号'' AFTER `fengmiantupian`'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查并添加 jiaoshixingming 字段
SET @columnname = 'jiaoshixingming';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE
    TABLE_NAME = @tablename AND TABLE_SCHEMA = @dbname AND COLUMN_NAME = @columnname) > 0,
  'SELECT ''✓ jiaoshixingming 字段已存在'' AS message',
  'ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT ''教师姓名'' AFTER `gonghao`'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 显示最终表结构
SELECT '========================================' AS '';
SELECT '修复完成！jingsaixinxi 表结构：' AS message;
SELECT '========================================' AS '';
DESCRIBE jingsaixinxi;
