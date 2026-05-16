-- 创建成绩复核申请表
CREATE TABLE IF NOT EXISTS `zuopindafen_fuhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `zuopindafen_id` bigint DEFAULT NULL COMMENT '作品打分记录ID',
  `jingsaimingcheng` varchar(200) DEFAULT NULL COMMENT '竞赛名称',
  `yuan_chengji` varchar(50) DEFAULT NULL COMMENT '原成绩',
  `xuehao` varchar(50) DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `fuhe_reason` text COMMENT '复核理由',
  `fuhe_status` varchar(50) DEFAULT '待审核' COMMENT '复核状态（待审核/已通过/已驳回）',
  `xin_chengji` varchar(50) DEFAULT NULL COMMENT '新成绩',
  `shenhe_yijian` text COMMENT '审核意见',
  `shenhe_gonghao` varchar(50) DEFAULT NULL COMMENT '审核教师工号',
  `shenhe_jiaoshi` varchar(50) DEFAULT NULL COMMENT '审核教师姓名',
  `shenqing_shijian` datetime DEFAULT NULL COMMENT '申请时间',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品打分复核申请表';
