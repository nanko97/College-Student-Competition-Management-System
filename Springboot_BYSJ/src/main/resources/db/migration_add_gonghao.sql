-- 为 jingsaixinxi 表添加工号和教师姓名字段
-- 解决教师新增竞赛时 500 错误的问题

ALTER TABLE `jingsaixinxi` 
ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`,
ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;

-- 说明：
-- 1. 使用 DEFAULT NULL 而不是 NOT NULL，避免对已有数据报错
-- 2. 如果后续需要 NOT NULL 约束，可以在数据迁移完成后添加
-- 3. 此脚本需要在现有数据库上执行
