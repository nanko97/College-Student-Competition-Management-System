DROP TABLE IF EXISTS `xiaoxi_tongzhi`;

CREATE TABLE `xiaoxi_tongzhi` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `biaoti` VARCHAR(200) NOT NULL,
  `neirong` TEXT,
  `leixing` VARCHAR(50),
  `fasongren` VARCHAR(100),
  `jieshouren_xuehao` VARCHAR(50),
  `jieshouren_gonghao` VARCHAR(50),
  `jieshouren_juese` VARCHAR(20),
  `guanlian_id` BIGINT,
  `guanlian_leixing` VARCHAR(50),
  `is_read` VARCHAR(10),
  `read_time` DATETIME,
  `addtime` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_jieshouren_xuehao` (`jieshouren_xuehao`),
  KEY `idx_jieshouren_gonghao` (`jieshouren_gonghao`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_addtime` (`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
