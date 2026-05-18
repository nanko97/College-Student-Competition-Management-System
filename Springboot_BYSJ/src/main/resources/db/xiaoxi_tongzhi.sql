-- ============================================
-- 消息通知表 - xiaoxi_tongzhi
-- 用于站内消息推送功能
-- 创建时间：2026-05-18
-- ============================================

DROP TABLE IF EXISTS `xiaoxi_tongzhi`;

CREATE TABLE `xiaoxi_tongzhi` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biaoti` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `neirong` TEXT COMMENT '消息内容',
  `leixing` VARCHAR(50) DEFAULT '系统通知' COMMENT '消息类型（系统通知/审核结果/成绩通知/截止提醒等）',
  `fasongren` VARCHAR(100) DEFAULT '系统' COMMENT '发送人',
  `jieshouren_xuehao` VARCHAR(50) COMMENT '接收人学号（学生）',
  `jieshouren_gonghao` VARCHAR(50) COMMENT '接收人工号（教师）',
  `jieshouren_juese` VARCHAR(20) COMMENT '接收人角色（xuesheng/jiaoshi/admin）',
  `guanlian_id` BIGINT COMMENT '关联业务ID（如报名ID、竞赛ID等）',
  `guanlian_leixing` VARCHAR(50) COMMENT '关联业务类型（baoming/jingsai/pingfen等）',
  `is_read` VARCHAR(10) COMMENT '是否已读（已读/未读）',
  `read_time` DATETIME COMMENT '阅读时间',
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_jieshouren_xuehao` (`jieshouren_xuehao`),
  KEY `idx_jieshouren_gonghao` (`jieshouren_gonghao`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_addtime` (`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';


