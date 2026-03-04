-- =============================================
-- 修复管理员密码脚本
-- 密码：admin123
-- BCrypt 加密后的哈希值（通过 spring-security-crypto 生成）
-- =============================================

USE springbootrd362;

-- 检查 users 表是否存在
DROP TABLE IF EXISTS `users`;

-- 创建 users 表
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `role` varchar(20) DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入/更新管理员账号
-- 密码：admin123 (BCrypt 加密)
INSERT INTO `users` (`username`, `password`, `role`) VALUES 
('admin', '$2a$10$7JeHnYCKMJfMqZTz9vK1x.dQXrNmFqSVMvfLqDhCvRjZk3pWqL1uG', '管理员')
ON DUPLICATE KEY UPDATE 
`password` = '$2a$10$7JeHnYCKMJfMqZTz9vK1x.dQXrNmFqSVMvfLqDhCvRjZk3pWqL1uG',
`role` = '管理员';

-- 验证插入结果
SELECT id, username, role, addtime FROM users WHERE username = 'admin';
