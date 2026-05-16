-- 团队加入/退出申请表
CREATE TABLE IF NOT EXISTS `tuandui_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tuandui_id` bigint(20) NOT NULL COMMENT '团队ID',
  `tuandui_mingcheng` varchar(100) DEFAULT NULL COMMENT '团队名称',
  `jingsai_id` bigint(20) DEFAULT NULL COMMENT '竞赛ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '竞赛名称',
  `xuehao` varchar(20) NOT NULL COMMENT '申请人学号',
  `xueshengxingming` varchar(50) NOT NULL COMMENT '申请人姓名',
  `apply_type` varchar(20) NOT NULL COMMENT '申请类型：加入/退出',
  `apply_reason` text COMMENT '申请理由',
  `apply_status` varchar(20) DEFAULT '待审核' COMMENT '申请状态：待审核/已通过/已驳回',
  `shenhe_yijian` text COMMENT '审核意见',
  `shenhe_xuehao` varchar(20) DEFAULT NULL COMMENT '审核人学号（负责人）',
  `shenhe_xingming` varchar(50) DEFAULT NULL COMMENT '审核人姓名',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `shenhe_time` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_xuehao` (`xuehao`),
  KEY `idx_apply_status` (`apply_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队加入/退出申请表';
