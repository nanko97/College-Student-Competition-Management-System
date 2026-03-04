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
  `jingsaileibie` varchar(50) DEFAULT NULL COMMENT '竞赛类别',
  `jingsaishijian` datetime DEFAULT NULL COMMENT '竞赛时间',
  `jieshi` text COMMENT '介绍',
  `fengmian` varchar(200) DEFAULT NULL COMMENT '封面',
  `lianxiren` varchar(50) DEFAULT NULL COMMENT '联系人',
  `lianxidianhua` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `sfsh` varchar(10) DEFAULT '否' COMMENT '是否审核',
  `shhf` text COMMENT '审核回复',
  `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
  `thumbsupnum` int(11) DEFAULT '0' COMMENT '赞',
  `crazilynum` int(11) DEFAULT '0' COMMENT '踩',
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

-- 9. 班级类型表
DROP TABLE IF EXISTS `banjileixing`;
CREATE TABLE `banjileixing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `banjileixing` varchar(50) DEFAULT NULL COMMENT '班级类型',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级类型表';

-- 10. 配置表
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

-- 完成提示
SELECT '数据库初始化完成！' AS message;
