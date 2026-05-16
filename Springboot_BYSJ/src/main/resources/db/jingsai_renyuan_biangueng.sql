-- 创建团队人员变更申请表
CREATE TABLE IF NOT EXISTS `jingsai_renyuan_biangueng` (
  `id` bigint NOT NULL COMMENT '主键id',
  `tuandui_id` bigint DEFAULT NULL COMMENT '团队ID',
  `tuandui_bianhao` varchar(50) DEFAULT NULL COMMENT '团队编号',
  `jingsai_id` bigint DEFAULT NULL COMMENT '竞赛ID',
  `biangueng_leixing` varchar(50) DEFAULT NULL COMMENT '变更类型（添加成员/移除成员/更换负责人）',
  `caozuo_qian` text COMMENT '操作前数据（JSON格式）',
  `caozuo_hou` text COMMENT '操作后数据（JSON格式）',
  `caozuo_ren_xuehao` varchar(50) DEFAULT NULL COMMENT '操作人学号',
  `caozuo_ren_xingming` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `caozuo_yuanyin` text COMMENT '变更原因',
  `shenhe_ren` varchar(50) DEFAULT NULL COMMENT '审核人',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT '审核时间',
  `shenhe_zhuangtai` varchar(50) DEFAULT '待审核' COMMENT '审核状态（待审核/已通过/已驳回）',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_shenhe_zhuangtai` (`shenhe_zhuangtai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛人员变更申请表';
