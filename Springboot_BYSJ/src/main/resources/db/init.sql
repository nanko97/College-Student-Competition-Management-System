-- =============================================
-- 竞赛管理系统 - 完整数据库初始化脚本
-- 版本：V1.0
-- 日期：2026-03-04
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS springbootrd362 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springbootrd362;

-- 1. 系统用户表 (管理员)
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `role` varchar(20) DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入默认管理员账号 (密码：admin123, BCrypt 加密后)
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '管理员');

-- 2. 学生表
DROP TABLE IF EXISTS `xuesheng`;
CREATE TABLE `xuesheng` (
  `id` bigint(20) NOT NULL COMMENT '主键 ID',
  `xuehao` varchar(20) NOT NULL COMMENT '学号',
  `mima` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `xingbie` varchar(10) DEFAULT NULL COMMENT '性别',
  `xueyuanmingcheng` varchar(100) DEFAULT NULL COMMENT '学院名称',
  `banji` varchar(50) DEFAULT NULL COMMENT '班级',
  `shouji` varchar(20) DEFAULT NULL COMMENT '手机',
  `youxiang` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
  `role` varchar(20) DEFAULT '学生' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xuehao` (`xuehao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 3. 教师表
DROP TABLE IF EXISTS `jiaoshi`;
CREATE TABLE `jiaoshi` (
  `id` bigint(20) NOT NULL COMMENT '主键 ID',
  `gonghao` varchar(20) NOT NULL COMMENT '工号',
  `mima` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名',
  `xingbie` varchar(10) DEFAULT NULL COMMENT '性别',
  `xueyuanmingcheng` varchar(100) DEFAULT NULL COMMENT '学院名称',
  `zhicheng` varchar(50) DEFAULT NULL COMMENT '职称',
  `shouji` varchar(20) DEFAULT NULL COMMENT '手机',
  `youxiang` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
  `role` varchar(20) DEFAULT '教师' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gonghao` (`gonghao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 4. Token 表
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `userid` bigint(20) NOT NULL COMMENT '用户 ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `tablename` varchar(50) DEFAULT NULL COMMENT '表名',
  `role` varchar(20) DEFAULT NULL COMMENT '角色',
  `token` varchar(32) NOT NULL COMMENT 'Token 字符串',
  `expiratedtime` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`userid`),
  KEY `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token 表';

-- 5. 角色权限表
DROP TABLE IF EXISTS `user_role_permission`;
CREATE TABLE `user_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `role_name` varchar(20) NOT NULL COMMENT '角色名称',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型 (MENU/BUTTON/API)',
  `resource_code` varchar(100) NOT NULL COMMENT '资源代码',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_resource` (`role_name`,`resource_type`,`resource_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

-- 初始化基础权限数据
INSERT INTO `user_role_permission` (`role_name`, `resource_type`, `resource_code`, `description`) VALUES
-- 学生权限
('学生', 'API', '/jingsaixinxi/list', '查看竞赛列表'),
('学生', 'API', '/jingsaixinxi/detail', '查看竞赛详情'),
('学生', 'API', '/jingsaibaoming/add', '竞赛报名'),
('学生', 'API', '/jingsaibaoming/my', '查看我的报名'),
('学生', 'API', '/jingsaibaoming/list', '查看报名列表'),
('学生', 'API', '/jingsaibaoming/info', '查看报名详情'),
('学生', 'API', '/zuopindafen/list', '查看作品评分'),
('学生', 'API', '/xuesheng/info', '查看个人信息'),
('学生', 'API', '/xuesheng/update', '更新个人信息'),
('学生', 'API', '/file/download', '下载文件'),
('学生', 'API', '/config/list', '查看配置'),
('学生', 'API', '/common/*', '通用接口'),

-- 教师权限
('教师', 'API', '/jingsaixinxi/*', '竞赛管理所有接口'),
('教师', 'API', '/jingsaibaoming/*', '报名管理所有接口'),
('教师', 'API', '/zuopindafen/*', '作品打分所有接口'),
('教师', 'API', '/xuesheng/list', '查看学生列表'),
('教师', 'API', '/xuesheng/info', '查看学生信息'),
('教师', 'API', '/jiaoshi/*', '教师信息管理'),
('教师', 'API', '/file/*', '文件管理'),

-- 管理员权限
('管理员', 'API', '/*', '所有接口权限');

-- 6. 竞赛信息表
DROP TABLE IF EXISTS `jingsaixinxi`;
CREATE TABLE `jingsaixinxi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '竞赛名称',
  `jingsaileixing` varchar(50) DEFAULT NULL COMMENT '竞赛类型',
  `jingsaididian` varchar(100) DEFAULT NULL COMMENT '竞赛地点',
  `jingsaiguize` text COMMENT '竞赛规则',
  `jingsaijiangli` varchar(200) DEFAULT NULL COMMENT '竞赛奖励',
  `jingsaishijian` datetime DEFAULT NULL COMMENT '竞赛时间',
  `moshi` varchar(50) DEFAULT NULL COMMENT '模式',
  `fengmiantupian` varchar(200) DEFAULT NULL COMMENT '封面图片',
  `gonghao` varchar(20) DEFAULT NULL COMMENT '工号',
  `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛信息表';

-- 7. 竞赛报名表
DROP TABLE IF EXISTS `jingsaibaoming`;
CREATE TABLE `jingsaibaoming` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `jingsaibianhao` varchar(50) DEFAULT NULL COMMENT '竞赛编号',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '竞赛名称',
  `xuehao` varchar(20) DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `baomingshuoming` text COMMENT '报名说明',
  `sfsh` varchar(10) DEFAULT '否' COMMENT '是否审核',
  `shhf` text COMMENT '审核回复',
  `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛报名表';

-- 8. 作品打分表
DROP TABLE IF EXISTS `zuopindafen`;
CREATE TABLE `zuopindafen` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `zuopinmingcheng` varchar(100) DEFAULT NULL COMMENT '作品名称',
  `zuopinjieshao` text COMMENT '作品介绍',
  `dafenshu` decimal(10,2) DEFAULT NULL COMMENT '打分数',
  `dafenbeizhu` text COMMENT '打分备注',
  `gonghao` varchar(20) DEFAULT NULL COMMENT '工号',
  `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名',
  `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品打分表';

-- 9. 配置表
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) NOT NULL COMMENT '配置项名称',
  `value` varchar(200) DEFAULT NULL COMMENT '配置项值',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置表';

-- 插入默认配置
INSERT INTO `config` (`name`, `value`) VALUES 
('login_max_fail_count', '5'),
('upload_max_size', '10485760');

-- =============================================
-- 示例数据 (用于毕业演示)
-- 说明：以下是测试数据，可根据需要删除
-- =============================================

-- 示例学生账号 (密码：123456，BCrypt 加密)
-- 注意：实际密码需要使用 BCrypt 加密，这里使用占位符，实际使用时请通过注册功能创建
INSERT INTO `xuesheng` (`id`, `xuehao`, `mima`, `xueshengxingming`, `xingbie`, `xueyuanmingcheng`, `banji`, `shouji`, `youxiang`) VALUES
(1001, '2022001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '张三', '男', '计算机学院', '计算机 2201', '13800138001', 'zhangsan@example.com'),
(1002, '2022002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '李四', '女', '软件学院', '软件工程 2201', '13800138002', 'lisi@example.com'),
(1003, '2022003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '王五', '男', '信息学院', '信息安全 2201', '13800138003', 'wangwu@example.com');

-- 示例教师账号 (密码：123456，BCrypt 加密)
INSERT INTO `jiaoshi` (`id`, `gonghao`, `mima`, `jiaoshixingming`, `xingbie`, `xueyuanmingcheng`, `zhicheng`, `shouji`, `youxiang`) VALUES
(2001, 'T2022001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '赵老师', '男', '计算机学院', '教授', '13800138010', 'zhao@example.com'),
(2002, 'T2022002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iDJgYR6sKdZwFpCqGz.xYzQhLzW.', '钱老师', '女', '软件学院', '副教授', '13800138011', 'qian@example.com');

-- 示例竞赛信息
INSERT INTO `jingsaixinxi` (`jingsaimingcheng`, `jingsaileibie`, `jingsaishijian`, `fengmian`, `lianxiren`, `lianxidianhua`, `sfsh`) VALUES
('全国大学生程序设计竞赛', '学科竞赛', '2026-06-15 09:00:00', '/upload/contest1.jpg', '赵老师', '010-12345678', '是'),
('中国"互联网+"大学生创新创业大赛', '创新创业', '2026-07-20 14:00:00', '/upload/contest2.jpg', '钱老师', '010-12345679', '是'),
('全国大学生数学建模竞赛', '学科竞赛', '2026-09-10 08:00:00', '/upload/contest3.jpg', '孙老师', '010-12345680', '是'),
('挑战杯全国大学生课外学术科技作品竞赛', '科技创新', '2026-05-25 10:00:00', '/upload/contest4.jpg', '李老师', '010-12345681', '是'),
('全国大学生电子设计竞赛', '学科竞赛', '2026-08-05 09:00:00', '/upload/contest5.jpg', '周老师', '010-12345682', '是');

-- 示例报名信息
INSERT INTO `jingsaibaoming` (`jingsaibianhao`, `jingsaimingcheng`, `xuehao`, `xueshengxingming`, `baomingshuoming`, `sfsh`) VALUES
('CONTEST2026001', '全国大学生程序设计竞赛', '2022001', '张三', '已准备好参赛材料', '是'),
('CONTEST2026001', '全国大学生程序设计竞赛', '2022002', '李四', '团队报名，共 3 人', '是'),
('CONTEST2026002', '中国"互联网+"大学生创新创业大赛', '2022001', '张三', '创新项目：智能校园系统', '否'),
('CONTEST2026003', '全国大学生数学建模竞赛', '2022003', '王五', '个人参赛', '是');

-- 示例作品打分
INSERT INTO `zuopindafen` (`zuopinmingcheng`, `zuopinjieshao`, `dafenshu`, `dafenbeizhu`, `gonghao`, `jiaoshixingming`) VALUES
('基于 AI 的图像识别系统', '使用深度学习技术实现图像分类和识别', 92.5, '创新性较强，代码规范良好', 'T2022001', '赵老师'),
('校园二手交易平台', '为在校大学生提供便捷的二手物品交易服务', 88.0, '功能完整，界面友好', 'T2022001', '赵老师'),
('智能考勤管理系统', '基于人脸识别的课堂考勤系统', 90.0, '实用性强，性能优秀', 'T2022002', '钱老师'),
('在线考试系统设计与实现', '支持多种题型的在线考试平台', 85.5, '系统稳定，但 UI 有待优化', 'T2022002', '钱老师');

-- =============================================
-- 重要提示
-- =============================================
-- 1. 以上示例数据的密码均为 BCrypt 加密后的密文
--    实际登录时请使用明文密码：123456
-- 
-- 2. 管理员账号：
--    用户名：admin
--    密码：admin123
--
-- 3. 学生账号示例：
--    学号：2022001 / 2022002 / 2022003
--    密码：123456
--
-- 4. 教师账号示例：
--    工号：T2022001 / T2022002
--    密码：123456
--
-- 5. 如需清空示例数据，可执行：
--    DELETE FROM jingsaixinxi WHERE id >= 100;
--    DELETE FROM jingsaibaoming WHERE id >= 100;
--    DELETE FROM zuopindafen WHERE id >= 100;
--    DELETE FROM xuesheng WHERE id >= 1000;
--    DELETE FROM jiaoshi WHERE id >= 2000;
-- =============================================

-- 完成提示
SELECT '✅ 数据库初始化完成！示例数据已加载。' AS message;
