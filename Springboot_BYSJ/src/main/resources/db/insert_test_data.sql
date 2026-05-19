-- ============================================
-- Test Data Insert Script
-- Created: 2026-05-19
-- ============================================

-- 1. Students (Skip if exists)
INSERT IGNORE INTO `xuesheng` (`id`, `addtime`, `xuehao`, `mima`, `xueshengxingming`, `xingbie`, `xueyuanmingcheng`, `banji`, `shouji`, `youxiang`, `zhaopian`, `role`) VALUES
(6, NOW(), '2022006', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Zhao Yaqin', 'Female', 'Computer Science', 'CS2203', '13800001006', 'zhao@edu.cn', 'upload/avatar.png', 'Student'),
(7, NOW(), '2022007', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Sun Minghui', 'Male', 'Software Engineering', 'SE2201', '13800001007', 'sun@edu.cn', 'upload/avatar.png', 'Student'),
(8, NOW(), '2022008', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Zhou Xuemei', 'Female', 'Information Engineering', 'IE2202', '13800001008', 'zhou@edu.cn', 'upload/avatar.png', 'Student'),
(9, NOW(), '2022009', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Wu Jianguo', 'Male', 'Electronic Engineering', 'EE2201', '13800001009', 'wu@edu.cn', 'upload/avatar.png', 'Student'),
(10, NOW(), '2022010', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Zheng Tingting', 'Female', 'Computer Science', 'CS2204', '13800001010', 'zheng@edu.cn', 'upload/avatar.png', 'Student');

-- 2. Teachers (Skip if exists)
INSERT IGNORE INTO `jiaoshi` (`id`, `addtime`, `gonghao`, `mima`, `jiaoshixingming`, `xingbie`, `xueyuanmingcheng`, `zhicheng`, `shouji`, `youxiang`, `zhaopian`, `role`) VALUES
(6, NOW(), 'T2022006', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Huang Wenbin', 'Male', 'Software Engineering', 'Associate Professor', '13900001006', 'huang@edu.cn', 'upload/avatar.png', 'Teacher'),
(7, NOW(), 'T2022007', '$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q', 'Lin Xiaohong', 'Female', 'Information Engineering', 'Lecturer', '13900001007', 'lin@edu.cn', 'upload/avatar.png', 'Teacher');

-- 3. Competition Registration (Skip if exists)
INSERT IGNORE INTO `jingsaibaoming` (`id`, `addtime`, `jingsai_id`, `jingsaimingcheng`, `xuehao`, `xueshengxingming`, `cansaileixing`, `tuandui_id`, `cansaizuopin`, `sfsh`, `shhf`, `shenqingriqi`) VALUES
(20, NOW(), 1, 'ACM-ICPC Programming Contest', '2022006', 'Zhao Yaqin', 'Individual', NULL, '', 'Approved', '', CURDATE()),
(21, NOW(), 2, 'Internet+ Innovation Contest', '2022006', 'Zhao Yaqin', 'Team', 1, 'upload/zuopin/project.pptx', 'Approved', 'Excellent work', CURDATE()),
(22, NOW(), 3, 'Mathematical Modeling Contest', '2022006', 'Zhao Yaqin', 'Team', 2, '', 'Pending', '', CURDATE()),
(23, NOW(), 4, 'Lanqiao Cup', '2022007', 'Sun Minghui', 'Individual', NULL, '', 'Approved', '', CURDATE()),
(24, NOW(), 1, 'ACM-ICPC Programming Contest', '2022007', 'Sun Minghui', 'Individual', NULL, 'upload/zuopin/code.zip', 'Approved', 'Submitted', CURDATE()),
(25, NOW(), 5, 'Electronic Design Contest', '2022008', 'Zhou Xuemei', 'Team', 3, '', 'Approved', '', CURDATE()),
(26, NOW(), 2, 'Internet+ Innovation Contest', '2022008', 'Zhou Xuemei', 'Team', 1, 'upload/zuopin/plan.docx', 'Approved', 'Complete materials', CURDATE()),
(27, NOW(), 3, 'Mathematical Modeling Contest', '2022009', 'Wu Jianguo', 'Team', 2, 'upload/zuopin/model.pdf', 'Approved', 'Good model', CURDATE()),
(28, NOW(), 5, 'Electronic Design Contest', '2022009', 'Wu Jianguo', 'Team', 3, '', 'Pending', '', CURDATE()),
(29, NOW(), 1, 'ACM-ICPC Programming Contest', '2022010', 'Zheng Tingting', 'Individual', NULL, 'upload/zuopin/solution.pdf', 'Approved', '', CURDATE()),
(30, NOW(), 4, 'Lanqiao Cup', '2022010', 'Zheng Tingting', 'Individual', NULL, '', 'Rejected', 'Not eligible', CURDATE());

-- 4. Messages (Skip if exists)
INSERT IGNORE INTO `xiaoxi_tongzhi` (`id`, `addtime`, `biaoti`, `neirong`, `leixing`, `fasongren`, `jieshouren_xuehao`, `jieshouren_gonghao`, `jieshouren_juese`, `is_read`, `guanlian_id`, `guanlian_leixing`) VALUES
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

-- 5. Teams - Skipped (table structure different)
-- 6. Work Scores - Skipped (table structure different)

SELECT 'Test data inserted successfully!' AS message;
SELECT 'Inserted: 5 students, 2 teachers, 11 registrations, 14 messages' AS summary;
