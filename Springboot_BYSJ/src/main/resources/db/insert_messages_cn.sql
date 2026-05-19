DELETE FROM xiaoxi_tongzhi WHERE id >= 10;

INSERT INTO `xiaoxi_tongzhi` (`id`, `addtime`, `biaoti`, `neirong`, `leixing`, `fasongren`, `jieshouren_xuehao`, `jieshouren_gonghao`, `jieshouren_juese`, `is_read`, `guanlian_id`, `guanlian_leixing`) VALUES
(10, NOW(), '系统维护通知', '系统将于本周末进行例行维护，预计停机2小时，请提前保存数据。', '系统通知', '管理员', NULL, NULL, 'all', '未读', NULL, NULL),
(11, NOW(), '新版本上线', '竞赛管理系统V2.0已上线，新增了团队管理和在线评分功能，欢迎体验！', '系统通知', '管理员', NULL, NULL, 'all', '未读', NULL, NULL),
(12, NOW(), '报名审核通过', '您的全国大学生程序设计竞赛（ACM-ICPC）报名申请已审核通过，请按时提交作品。', '审核结果', '李明德', '2022001', NULL, 'xuesheng', '已读', 1, 'jingsaibaoming'),
(13, NOW(), '缴费审核通过', '您的竞赛缴费申请已审核通过，缴费金额：50元。', '审核结果', '李明德', '2022001', NULL, 'xuesheng', '已读', 13, 'jingsai_jiaofei_jilu'),
(14, NOW(), '作品提交通知', '您已成功提交作品，作品文件：solution.zip，请等待评审。', '系统通知', '系统', '2022001', NULL, 'xuesheng', '未读', NULL, NULL),
(15, NOW(), '报名审核通过', '您的全国大学生程序设计竞赛（ACM-ICPC）报名申请已审核通过。', '审核结果', '王秀英', '20020907', NULL, 'xuesheng', '未读', 839501054591766528, 'jingsaibaoming'),
(16, NOW(), '缴费审核通过', '您的缴费申请已审核通过，感谢您的配合。', '审核结果', '王秀英', '20020907', NULL, 'xuesheng', '未读', 839501054675652608, 'jingsai_jiaofei_jilu'),
(17, NOW(), '报名提醒', '中国国际"互联网+"大学生创新创业大赛即将截止报名，请尽快提交申请材料。', '截止提醒', '系统', '20020907', NULL, 'xuesheng', '未读', NULL, NULL),
(18, NOW(), '报名审核通过', '您的全国大学生程序设计竞赛（ACM-ICPC）报名申请已审核通过。', '审核结果', '李明德', '2022006', NULL, 'xuesheng', '未读', 20, 'jingsaibaoming'),
(19, NOW(), '作品评分通知', '您提交的蓝桥杯作品已获得评分：85分，等级：一等奖。', '成绩通知', '张伟强', '2022007', NULL, 'xuesheng', '未读', NULL, NULL),
(20, NOW(), '缴费截止提醒', '全国大学生数学建模竞赛缴费即将截止，请尽快完成缴费。', '截止提醒', '系统', '2022008', NULL, 'xuesheng', '未读', NULL, NULL),
(21, NOW(), '报名审核不通过', '您的蓝桥杯竞赛报名申请未通过审核，原因：不符合参赛条件。', '审核结果', '陈建国', '2022010', NULL, 'xuesheng', '未读', 30, 'jingsaibaoming'),
(22, NOW(), '待审核报名提醒', '您有5条报名申请待审核，请及时处理。', '系统通知', '系统', NULL, 'T2022001', 'jiaoshi', '未读', NULL, NULL),
(23, NOW(), '待审核缴费提醒', '您有3条缴费申请待审核，请及时处理。', '系统通知', '系统', NULL, 'T2022002', 'jiaoshi', '未读', NULL, NULL);

SELECT '中文消息数据插入成功！' AS message;
