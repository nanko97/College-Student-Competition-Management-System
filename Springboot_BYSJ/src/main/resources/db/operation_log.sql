-- ============================================
-- 操作日志表 - operation_log
-- 用于记录系统所有关键操作
-- 创建时间：2026-05-18
-- ============================================

DROP TABLE IF EXISTS `operation_log`;

CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operator` VARCHAR(100) COMMENT '操作人',
  `operator_role` VARCHAR(50) COMMENT '操作人角色（admin/xuesheng/jiaoshi）',
  `operation_module` VARCHAR(100) COMMENT '操作模块（如：竞赛管理、报名管理等）',
  `operation_type` VARCHAR(50) COMMENT '操作类型（新增/修改/删除/查询/审核等）',
  `operation_desc` VARCHAR(500) COMMENT '操作描述',
  `request_method` VARCHAR(10) COMMENT '请求方法（GET/POST/PUT/DELETE）',
  `request_url` VARCHAR(500) COMMENT '请求URL',
  `request_params` TEXT COMMENT '请求参数（JSON格式）',
  `response_result` TEXT COMMENT '响应结果（JSON格式）',
  `client_ip` VARCHAR(50) COMMENT '客户端IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理信息',
  `execution_time` BIGINT COMMENT '执行耗时（毫秒）',
  `status` VARCHAR(20) DEFAULT '成功' COMMENT '操作状态（成功/失败）',
  `error_msg` TEXT COMMENT '错误信息（如果失败）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator`),
  KEY `idx_operation_module` (`operation_module`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 添加说明：
-- 1. 该表用于存储所有通过@OperationLog注解标记的操作
-- 2. 建议定期清理180天前的日志数据
-- 3. 可以配合定时任务自动清理过期日志
