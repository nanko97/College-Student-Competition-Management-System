-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: bysj_springboot
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `name` varchar(100) NOT NULL COMMENT '??????',
  `value` varchar(100) DEFAULT NULL COMMENT '?????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COMMENT='????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (7,'picture1','http://localhost:8081/BYSJ_Springboot/upload/jingsaixinxi_fengmiantupian1.jpg'),(8,'picture2','http://localhost:8081/BYSJ_Springboot/upload/jingsaixinxi_fengmiantupian2.jpg'),(9,'picture3','http://localhost:8081/BYSJ_Springboot/upload/jingsaixinxi_fengmiantupian3.jpg'),(10,'homepage',NULL);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jiaoshi`
--

DROP TABLE IF EXISTS `jiaoshi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jiaoshi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `gonghao` varchar(200) NOT NULL COMMENT '??',
  `mima` varchar(200) NOT NULL COMMENT '??',
  `jiaoshixingming` varchar(200) NOT NULL COMMENT '????',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '??',
  `xueyuanmingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `zhicheng` varchar(200) DEFAULT NULL COMMENT '??',
  `shouji` varchar(200) DEFAULT NULL COMMENT '??',
  `youxiang` varchar(200) DEFAULT NULL COMMENT '??',
  `zhaopian` varchar(200) DEFAULT NULL COMMENT '??',
  `role` varchar(20) DEFAULT '??' COMMENT '??',
  PRIMARY KEY (`id`),
  UNIQUE KEY `gonghao` (`gonghao`),
  KEY `idx_jiaoshi_zhicheng` (`zhicheng`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='??';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jiaoshi`
--

LOCK TABLES `jiaoshi` WRITE;
/*!40000 ALTER TABLE `jiaoshi` DISABLE KEYS */;
INSERT INTO `jiaoshi` VALUES (1,'2026-04-25 07:29:38','T2022001','$2a$10$xQH7mItPEGLR6r.mkNLEJOqhYW9DZkp35ics7ihqN9ShZ5o9M78q.','李明德','男','计算机科学与技术学院','教授','13900001001','limingde@edu.cn','upload/1777913870064_人像.png','教师'),(2,'2026-04-25 07:29:38','T2022002','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','王秀英','女','计算机科学与技术学院','副教授','13900001002','wangxiuying@edu.cn','upload/人像.png','教师'),(3,'2026-04-25 07:29:38','T2022003','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','张伟强','男','软件学院','讲师','13900001003','zhangweiqiang@edu.cn','upload/人像.png','教师'),(4,'2026-04-25 07:29:38','T2022004','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','刘芳','女','信息工程学院','副教授','13900001004','liufang@edu.cn','upload/人像.png','教师'),(5,'2026-04-25 07:29:38','T2022005','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','陈建国','男','电子工程学院','教授','13900001005','chenjianguo@edu.cn','upload/人像.png','教师');
/*!40000 ALTER TABLE `jiaoshi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_feiyong`
--

DROP TABLE IF EXISTS `jingsai_feiyong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_feiyong` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `baomingfei` decimal(10,2) DEFAULT '0.00' COMMENT '?????',
  `shifou_shoufei` varchar(10) DEFAULT '是' COMMENT '????(?/?)',
  `jiaofei_jiezhi_riqi` datetime DEFAULT NULL COMMENT '??????',
  `beizhu` text COMMENT '????',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_jingsai_id` (`jingsai_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_feiyong`
--

LOCK TABLES `jingsai_feiyong` WRITE;
/*!40000 ALTER TABLE `jingsai_feiyong` DISABLE KEYS */;
INSERT INTO `jingsai_feiyong` VALUES (1,1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'是','2026-06-01 23:59:59','每队50元，含报名费和资料费','2026-04-25 07:29:38'),(2,2,'中国国际\"互联网+\"大学生创新创业大赛',0.00,'否',NULL,'本竞赛免费参赛','2026-04-25 07:29:38'),(3,3,'全国大学生数学建模竞赛',100.00,'是','2026-09-01 23:59:59','每队100元，含评审费和证书费','2026-04-25 07:29:38'),(4,4,'蓝桥杯全国软件和信息技术专业人才大赛',300.00,'是','2026-04-20 23:59:59','个人赛300元/人','2026-04-25 07:29:38'),(5,5,'全国大学生电子设计竞赛',150.00,'是','2026-08-01 23:59:59','每队150元，含材料费','2026-04-25 07:29:38'),(6,100,'校级程序设计竞赛',20.00,'是','2026-05-15 23:59:59',NULL,'2026-04-28 14:09:16'),(7,101,'校级数学建模竞赛',30.00,'是','2026-07-15 23:59:59',NULL,'2026-04-28 14:09:16'),(8,102,'校级电子设计竞赛',50.00,'是','2026-08-15 23:59:59',NULL,'2026-04-28 14:09:16'),(9,103,'校级蓝桥杯选拔赛',20.00,'是','2026-03-15 23:59:59',NULL,'2026-04-28 14:09:16'),(10,104,'校级创新创业大赛',0.00,'否',NULL,NULL,'2026-04-28 14:09:16'),(11,200,'省级程序设计竞赛（ACM-ICPC）',80.00,'是','2026-07-01 23:59:59',NULL,'2026-04-28 14:09:16'),(12,201,'省级数学建模竞赛',120.00,'是','2026-09-15 23:59:59',NULL,'2026-04-28 14:09:16'),(13,202,'省级电子设计竞赛',180.00,'是','2026-08-20 23:59:59',NULL,'2026-04-28 14:09:16'),(14,203,'省级蓝桥杯竞赛',350.00,'是','2026-04-25 23:59:59',NULL,'2026-04-28 14:09:16'),(15,204,'省级创新创业大赛',0.00,'否',NULL,NULL,'2026-04-28 14:09:16');
/*!40000 ALTER TABLE `jingsai_feiyong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_jiaofei_jilu`
--

DROP TABLE IF EXISTS `jingsai_jiaofei_jilu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_jiaofei_jilu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `baoming_id` bigint NOT NULL COMMENT '??ID',
  `xuehao` varchar(20) NOT NULL COMMENT '??',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT '????',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `jiaofei_jine` decimal(10,2) DEFAULT '0.00' COMMENT '????',
  `jiaofei_zhuangtai` varchar(20) DEFAULT '待缴费' COMMENT '????(???/???/???/???/???)',
  `jiaofei_shijian` datetime DEFAULT NULL COMMENT '????',
  `shenhe_ren` varchar(50) DEFAULT NULL COMMENT '???',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT '????',
  `shenhe_yijian` text COMMENT '????',
  `zhifufangshi` varchar(50) DEFAULT NULL COMMENT '????(??/???/???/??)',
  `jiaoyi_hao` varchar(100) DEFAULT NULL COMMENT '???',
  `pingzheng_tupian` varchar(500) DEFAULT NULL COMMENT '????(???????)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_baoming_id` (`baoming_id`),
  KEY `idx_xuehao` (`xuehao`),
  KEY `idx_jingsai_id` (`jingsai_id`),
  KEY `idx_zhuangtai` (`jiaofei_zhuangtai`)
) ENGINE=InnoDB AUTO_INCREMENT=839501119351820289 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_jiaofei_jilu`
--

LOCK TABLES `jingsai_jiaofei_jilu` WRITE;
/*!40000 ALTER TABLE `jingsai_jiaofei_jilu` DISABLE KEYS */;
INSERT INTO `jingsai_jiaofei_jilu` VALUES (1,1,'2022001','张志强',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已通过','2026-04-10 10:30:00','李明德','2026-04-25 17:40:23','通过','微信支付','ORDER20260410001','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(2,2,'2022003','王浩然',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已通过','2026-04-10 10:35:00','李明德','2026-04-25 18:10:34','通过','支付宝','ORDER20260410002','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(3,3,'2022005','陈俊杰',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已驳回','2026-04-10 10:40:00','李明德','2026-04-25 17:40:32','驳回','微信支付','ORDER20260410003','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(4,4,'2022002','李雨婷',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已缴费','2026-04-11 09:30:00',NULL,NULL,NULL,'支付宝','ORDER20260411001','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(5,5,'2022004','刘思琪',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已缴费','2026-04-11 09:35:00',NULL,NULL,NULL,'微信支付','ORDER20260411002','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(6,6,'2022001','张志强',1,'全国大学生程序设计竞赛（ACM-ICPC）',50.00,'已缴费','2026-04-11 09:40:00',NULL,NULL,NULL,'支付宝','ORDER20260411003','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(7,12,'2022005','陈俊杰',3,'全国大学生数学建模竞赛',100.00,'已缴费','2026-04-14 09:30:00',NULL,NULL,NULL,'微信支付','ORDER20260414001','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(8,13,'2022001','张志强',3,'全国大学生数学建模竞赛',100.00,'已缴费','2026-04-14 09:35:00',NULL,NULL,NULL,'支付宝','ORDER20260414002','upload/1777960708491_缴费凭证图样.png','2026-04-25 07:29:38'),(9,14,'2022003','王浩然',3,'全国大学生数学建模竞赛',100.00,'已缴费','2026-04-14 09:40:00',NULL,NULL,NULL,'微信支付','ORDER20260414003','upload/1777960738033_缴费凭证图样.png','2026-04-25 07:29:38'),(10,15,'2022001','张志强',4,'蓝桥杯全国软件和信息技术专业人才大赛',300.00,'已缴费','2026-04-15 11:30:00',NULL,NULL,NULL,'支付宝','ORDER20260415001','upload/1777960738033_缴费凭证图样.png','2026-04-25 07:29:38'),(11,1,'2022005','陈俊杰',5,'全国大学生电子设计竞赛',200.00,'已缴费','2026-04-20 10:00:00',NULL,NULL,NULL,NULL,'ORDER20260416001',NULL,'2026-04-25 07:29:38'),(12,4,'2022002','李雨婷',4,'蓝桥杯全国软件和信息技术专业人才大赛',300.00,'已缴费','2026-04-16 14:00:00',NULL,NULL,NULL,'微信支付','ORDER20260416002','upload/1777960738033_缴费凭证图样.png','2026-04-25 07:29:38'),(13,1,'2022001','张志强',100,'校级程序设计竞赛',50.00,'已通过','2026-04-15 10:30:00','admin','2026-04-15 14:20:00','审核通过','微信支付','WX202604150001','upload/1777960738033_缴费凭证图样.png','2026-04-25 17:37:19'),(14,2,'2022001','张志强',103,'校级蓝桥杯选拔赛',80.00,'已通过','2026-04-16 11:00:00','admin','2026-04-16 15:30:00','审核通过','支付宝','ALI202604160001','upload/1777960746300_缴费凭证图样.png','2026-04-25 17:37:19'),(15,3,'2022001','张志强',101,'校级数学建模竞赛',60.00,'待审核','2026-04-17 09:20:00',NULL,NULL,NULL,'银行转账','BNK202604170001','upload/1777960746300_缴费凭证图样.png','2026-04-25 17:37:19'),(16,6,'2022001','张志强',102,'校级电子设计竞赛',70.00,'已驳回','2026-04-29 01:53:42','t2022001','2026-05-05 00:58:15','不清晰','微信支付','WX202604180001','upload/1777398822391_缴费凭证图样.png','2026-04-25 17:37:19'),(17,8,'2022001','张志强',104,'校级创新创业大赛',100.00,'已通过','2026-04-19 10:00:00','admin','2026-04-19 17:00:00','审核通过','支付宝','ALI202604190001','upload/1777960746300_缴费凭证图样.png','2026-04-25 17:37:19'),(18,13,'2022001','张志强',1,'全国大学生程序设计竞赛（ACM-ICPC）',150.00,'待审核','2026-04-20 11:30:00',NULL,NULL,NULL,'微信支付','WX202604200001','upload/1777960746300_缴费凭证图样.png','2026-04-25 17:37:19'),(19,15,'2022001','张志强',4,'蓝桥杯全国软件和信息技术专业人才大赛',120.00,'已通过','2026-04-21 09:00:00','admin','2026-04-21 16:30:00','审核通过','银行转账','BNK202604210001','upload/1777960746300_缴费凭证图样.png','2026-04-25 17:37:19'),(20,1,'2022001','张志强',3,'全国大学生数学建模竞赛',200.00,'已驳回','2026-04-29 01:53:31','t2022001','2026-05-05 00:58:33','不清晰',NULL,NULL,'upload/1777398811218_缴费凭证图样.png','2026-04-25 17:37:19'),(838956778870935552,838956778787049472,'2022002','李雨婷',1,'全国大学生程序设计竞赛（ACM-ICPC）',0.00,'已驳回','2026-05-05 00:34:19','t2022001','2026-05-05 00:58:25','不清晰',NULL,NULL,'upload/1777912459278_缴费凭证图样.png','2026-05-03 17:54:43'),(839501054675652608,839501054591766528,'20020907','范佳欣',1,'全国大学生程序设计竞赛（ACM-ICPC）',0.00,'已通过','2026-05-05 13:58:28','t2022001','2026-05-05 14:35:31','通过',NULL,NULL,'upload/1777960708491_缴费凭证图样.png','2026-05-05 05:57:29'),(839501062653218816,839501062602887168,'20020907','范佳欣',2,'中国国际\"互联网+\"大学生创新创业大赛',0.00,'已通过','2026-05-05 13:58:58','t2022002','2026-05-05 14:42:43','通过',NULL,NULL,'upload/1777960738033_缴费凭证图样.png','2026-05-05 05:57:31'),(839501073164144640,839501073113812992,'20020907','范佳欣',4,'蓝桥杯全国软件和信息技术专业人才大赛',0.00,'已通过','2026-05-05 13:59:06','t2022003','2026-05-05 14:43:10','通过',NULL,NULL,'upload/1777960746300_缴费凭证图样.png','2026-05-05 05:57:33'),(839501119351820288,839501119301488640,'20020907','范佳欣',3,'全国大学生数学建模竞赛',0.00,'已通过','2026-05-05 14:44:44','t2022005','2026-05-05 14:44:25','驳回',NULL,NULL,'upload/1777963483781_缴费凭证图样.png','2026-05-05 05:57:44');
/*!40000 ALTER TABLE `jingsai_jiaofei_jilu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_jibie_banben`
--

DROP TABLE IF EXISTS `jingsai_jibie_banben`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_jibie_banben` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `jingsai_nianfen` int NOT NULL COMMENT '????',
  `jibie` varchar(20) DEFAULT NULL COMMENT '????(A?/B?/C?/??/????)',
  `renzheng_jigou` varchar(100) DEFAULT NULL COMMENT '????(????/????)',
  `wenjian_hao` varchar(100) DEFAULT NULL COMMENT '????',
  `shengxiao_riqi` date NOT NULL COMMENT '????',
  `shixiao_riqi` date DEFAULT NULL COMMENT '????(NULL??????)',
  `beizhu` text COMMENT '????',
  `caozuo_ren` varchar(50) DEFAULT NULL COMMENT '???',
  `is_current` varchar(10) DEFAULT '否' COMMENT '??????(?/?)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_jingsai_id` (`jingsai_id`),
  KEY `idx_nianfen` (`jingsai_nianfen`),
  KEY `idx_shengxiao` (`shengxiao_riqi`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_jibie_banben`
--

LOCK TABLES `jingsai_jibie_banben` WRITE;
/*!40000 ALTER TABLE `jingsai_jibie_banben` DISABLE KEYS */;
INSERT INTO `jingsai_jibie_banben` VALUES (1,1,'全国大学生程序设计竞赛（ACM-ICPC）',2026,'国家级','教育部计算机教指委','教指委[2026]1号','2026-01-01','2026-12-31','国际顶级算法竞赛','admin','是','2026-04-25 07:29:38'),(2,2,'中国国际\"互联网+\"大学生创新创业大赛',2026,'国家级','教育部','教高厅函[2026]2号','2026-01-01','2026-12-31','国家级顶级赛事','admin','是','2026-04-25 07:29:38'),(3,3,'全国大学生数学建模竞赛',2026,'国家级','教育部','教高厅函[2026]3号','2026-01-01','2026-12-31','规模最大建模竞赛','admin','是','2026-04-25 07:29:38'),(4,4,'蓝桥杯全国软件和信息技术专业人才大赛',2026,'国家级','工信部人才交流中心','工信部人才[2026]4号','2026-01-01','2026-12-31','行业认可度高','admin','是','2026-04-25 07:29:38'),(5,5,'全国大学生电子设计竞赛',2026,'国家级','工信部','工信厅联科[2026]5号','2026-01-01','2026-12-31','电子设计权威赛事','admin','是','2026-04-25 07:29:38'),(6,100,'校级程序设计竞赛',2026,'校级','学校教务处','校教发[2026]1号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(7,101,'校级数学建模竞赛',2026,'校级','学校教务处','校教发[2026]2号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(8,102,'校级电子设计竞赛',2026,'校级','学校教务处','校教发[2026]3号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(9,103,'校级蓝桥杯选拔赛',2026,'校级','学校教务处','校教发[2026]4号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(10,104,'校级创新创业大赛',2026,'校级','学校教务处','校教发[2026]5号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(11,200,'省级程序设计竞赛（ACM-ICPC）',2026,'省级','省教育厅','教厅函[2026]1号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(12,201,'省级数学建模竞赛',2026,'省级','省教育厅','教厅函[2026]2号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(13,202,'省级电子设计竞赛',2026,'省级','省教育厅','教厅函[2026]3号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(14,203,'省级蓝桥杯竞赛',2026,'省级','省教育厅','教厅函[2026]4号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44'),(15,204,'省级创新创业大赛',2026,'省级','省教育厅','教厅函[2026]5号','2026-01-01','2026-12-31',NULL,NULL,'是','2026-04-28 17:27:44');
/*!40000 ALTER TABLE `jingsai_jibie_banben` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_jinji_guanxi`
--

DROP TABLE IF EXISTS `jingsai_jinji_guanxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_jinji_guanxi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `fu_jingsai_id` bigint NOT NULL COMMENT '????ID(???)',
  `zi_jingsai_id` bigint NOT NULL COMMENT '????ID(???)',
  `fu_jibie` varchar(20) DEFAULT '??' COMMENT '????(??/??/???)',
  `zi_jibie` varchar(20) DEFAULT '??' COMMENT '????(??/??/???)',
  `jinji_type` varchar(20) DEFAULT '????' COMMENT '????(????/????)',
  `jinji_rule` text COMMENT '??????',
  `zuidi_fenshu` int DEFAULT NULL COMMENT '??????',
  `zuidi_mingci` int DEFAULT NULL COMMENT '??????',
  `is_active` varchar(10) DEFAULT '?' COMMENT '????(?/?)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fu_zi` (`fu_jingsai_id`,`zi_jingsai_id`),
  KEY `idx_fu_jibie` (`fu_jibie`),
  KEY `idx_zi_jibie` (`zi_jibie`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_jinji_guanxi`
--

LOCK TABLES `jingsai_jinji_guanxi` WRITE;
/*!40000 ALTER TABLE `jingsai_jinji_guanxi` DISABLE KEYS */;
INSERT INTO `jingsai_jinji_guanxi` VALUES (11,100,200,'校级','省级','自动晋级','校级比赛前3名且分数85分可晋级省级',85,3,'是','2026-05-05 11:19:25'),(12,200,1,'省级','国家级','自动晋级','省级比赛前5名且分数90分可晋级国家级',90,5,'是','2026-05-05 11:19:25'),(13,101,201,'校级','省级','自动晋级','校级比赛前3名且分数85分可晋级省级',85,3,'是','2026-05-05 11:19:25'),(14,201,3,'省级','国家级','自动晋级','省级比赛前5名且分数90分可晋级国家级',90,5,'是','2026-05-05 11:19:25'),(15,102,202,'校级','省级','自动晋级','校级比赛前3名且分数85分可晋级省级',85,3,'是','2026-05-05 11:19:25'),(16,202,5,'省级','国家级','自动晋级','省级比赛前5名且分数90分可晋级国家级',90,5,'是','2026-05-05 11:19:25'),(17,103,203,'校级','省级','自动晋级','校级比赛前3名且分数85分可晋级省级',85,3,'是','2026-05-05 11:19:25'),(18,203,4,'省级','国家级','自动晋级','省级比赛前5名且分数90分可晋级国家级',90,5,'是','2026-05-05 11:19:25'),(19,104,204,'校级','省级','自动晋级','校级比赛前3名且分数85分可晋级省级',85,3,'是','2026-05-05 11:19:25'),(20,204,2,'省级','国家级','自动晋级','省级比赛前5名且分数90分可晋级国家级',90,5,'是','2026-05-05 11:19:25');
/*!40000 ALTER TABLE `jingsai_jinji_guanxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_jinji_jilu`
--

DROP TABLE IF EXISTS `jingsai_jinji_jilu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_jinji_jilu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `yuan_baoming_id` bigint NOT NULL COMMENT '???ID',
  `xin_baoming_id` bigint DEFAULT NULL COMMENT '???ID(??????)',
  `xuehao` varchar(20) NOT NULL COMMENT '??',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT '????',
  `yuan_jingsai_id` bigint NOT NULL COMMENT '???ID',
  `yuan_jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '?????',
  `yuan_jibie` varchar(20) DEFAULT NULL COMMENT '???',
  `xin_jingsai_id` bigint NOT NULL COMMENT '???ID',
  `xin_jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '?????',
  `xin_jibie` varchar(20) DEFAULT NULL COMMENT '???',
  `jinji_jibie` varchar(20) DEFAULT NULL COMMENT '????(???/???/???)',
  `yuan_chengji` decimal(10,2) DEFAULT NULL COMMENT '???',
  `yuan_mingci` int DEFAULT NULL COMMENT '???',
  `jinji_yuanyin` text COMMENT '????',
  `shenhe_ren` varchar(50) DEFAULT NULL COMMENT '???',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT '????',
  `jinji_zhuangtai` varchar(20) DEFAULT '???' COMMENT '????(???/???/???)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_yuan_baoming` (`yuan_baoming_id`),
  KEY `idx_xin_baoming` (`xin_baoming_id`),
  KEY `idx_xuehao` (`xuehao`),
  KEY `idx_zhuangtai` (`jinji_zhuangtai`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_jinji_jilu`
--

LOCK TABLES `jingsai_jinji_jilu` WRITE;
/*!40000 ALTER TABLE `jingsai_jinji_jilu` DISABLE KEYS */;
INSERT INTO `jingsai_jinji_jilu` VALUES (1,1,NULL,'2022001','张志强',100,'校级程序设计竞赛','校级',200,'省级程序设计竞赛（ACM-ICPC）','省级','省级',92.50,5,'校赛成绩优异，排名前10%','李明德','2026-04-22 10:00:00','已通过','2026-04-25 07:29:38'),(2,2,NULL,'2022003','王浩然',100,'校级程序设计竞赛','校级',200,'省级程序设计竞赛（ACM-ICPC）','省级','省级',89.00,12,'校赛表现良好','李明德','2026-04-22 10:00:00','已通过','2026-04-25 07:29:38'),(3,3,NULL,'2022005','陈俊杰',100,'校级程序设计竞赛','校级',200,'省级程序设计竞赛（ACM-ICPC）','省级','省级',87.50,18,'符合晋级条件','李明德','2026-04-22 10:00:00','已通过','2026-04-25 07:29:38'),(4,4,NULL,'2022002','李雨婷',100,'校级程序设计竞赛','校级',200,'省级程序设计竞赛（ACM-ICPC）','省级','省级',85.00,25,'达到晋级标准','李明德','2026-04-22 10:00:00','审核中','2026-04-25 07:29:38'),(5,12,NULL,'2022005','陈俊杰',200,'省级程序设计竞赛（ACM-ICPC）','省级',1,'全国大学生程序设计竞赛（ACM-ICPC）','国家级','国家级',94.00,3,'省赛一等奖，评委强烈推荐','陈建国','2026-04-20 14:00:00','已通过','2026-04-25 07:29:38'),(6,13,NULL,'2022001','张志强',200,'省级程序设计竞赛（ACM-ICPC）','省级',1,'全国大学生程序设计竞赛（ACM-ICPC）','国家级','国家级',91.50,8,'省赛二等奖，具备晋级潜力','陈建国','2026-04-20 14:00:00','已通过','2026-04-25 07:29:38'),(7,14,NULL,'2022003','王浩然',200,'省级程序设计竞赛（ACM-ICPC）','省级',1,'全国大学生程序设计竞赛（ACM-ICPC）','国家级','国家级',88.00,15,'等待最终评审','陈建国','2026-04-20 14:00:00','审核中','2026-04-25 07:29:38'),(8,15,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',90.00,7,'校赛成绩突出','张伟强','2026-05-05 09:00:00','已通过','2026-04-25 07:29:38'),(9,1,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',95.50,3,'在校级竞赛中表现优异，成绩排名前列','李明德','2026-04-18 10:00:00','已通过','2026-04-25 17:37:19'),(10,2,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',88.00,5,'选拔赛成绩优秀，获得晋级资格','李明德','2026-04-18 10:00:00','待审核','2026-04-25 17:37:19'),(11,3,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',92.30,2,'数学建模能力突出','李明德','2026-04-18 10:00:00','已通过','2026-04-25 17:37:19'),(12,6,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',85.00,8,'电子设计基础扎实','李明德','2026-04-18 10:00:00','已驳回','2026-04-25 17:37:19'),(13,8,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','省级',90.00,4,'创新项目具有潜力','admin','2026-04-25 16:00:00','已通过','2026-04-25 17:37:19'),(14,13,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','国家级',98.00,1,'省级竞赛一等奖，直接晋级国赛','李明德','2026-04-18 10:00:00','待审核','2026-04-25 17:37:19'),(15,15,NULL,'2022001','张志强',201,'省级数学建模竞赛','省级',3,'全国大学生数学建模竞赛','国家级','国家级',96.50,2,'省级竞赛特等奖','admin','2026-04-26 09:00:00','已通过','2026-04-25 17:37:19');
/*!40000 ALTER TABLE `jingsai_jinji_jilu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_renyuan_biangueng`
--

DROP TABLE IF EXISTS `jingsai_renyuan_biangueng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_renyuan_biangueng` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `tuandui_id` bigint NOT NULL COMMENT '??ID',
  `tuandui_bianhao` varchar(50) NOT NULL COMMENT '????',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `biangueng_leixing` varchar(20) NOT NULL COMMENT '????(????/????/?????)',
  `caozuo_qian` text COMMENT '?????(JSON??)',
  `caozuo_hou` text COMMENT '?????(JSON??)',
  `caozuo_ren_xuehao` varchar(20) NOT NULL COMMENT '?????',
  `caozuo_ren_xingming` varchar(50) NOT NULL COMMENT '?????',
  `caozuo_yuanyin` text COMMENT '????',
  `shenhe_ren` varchar(50) DEFAULT NULL COMMENT '???',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT '????',
  `shenhe_zhuangtai` varchar(20) DEFAULT '???' COMMENT '????(???/???/???)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_jingsai_id` (`jingsai_id`),
  KEY `idx_caozuo_ren` (`caozuo_ren_xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=5002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='?????????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_renyuan_biangueng`
--

LOCK TABLES `jingsai_renyuan_biangueng` WRITE;
/*!40000 ALTER TABLE `jingsai_renyuan_biangueng` DISABLE KEYS */;
INSERT INTO `jingsai_renyuan_biangueng` VALUES (1,1,'TD2026001',1,'添加成员',NULL,'新增队员：赵明（2022006）','2022001','张志强','原队员因个人原因退出，补充新成员','李明德','2026-04-15 10:00:00','已通过','2026-04-25 07:29:38'),(2,2,'TD2026002',1,'删除成员','移除队员：刘思琪（2022004）',NULL,'2022002','李雨婷','队员课程冲突，无法继续参赛','李明德','2026-04-16 14:00:00','已通过','2026-04-25 07:29:38'),(3,3,'TD2026003',2,'更换负责人','原负责人：王浩然（2022003）','新负责人：张志强（2022001）','2022003','王浩然','原负责人学业压力大，转由张志强负责','王秀英','2026-04-18 09:00:00','已通过','2026-04-25 07:29:38'),(4,4,'TD2026004',2,'添加成员',NULL,'新增队员：孙丽（2022007）','2022004','刘思琪','增加团队成员，增强项目实力','李明德','2026-04-26 03:03:56','已通过','2026-04-25 07:29:38'),(5,5,'TD2026005',3,'删除成员','移除队员：张志强（2022001）',NULL,'2022005','陈俊杰','队员参加其他竞赛，时间冲突','陈建国','2026-04-20 15:00:00','已通过','2026-04-25 07:29:38'),(6,7,'TD2026007',5,'添加成员',NULL,'新增队员：周杰（2022008）','2022005','陈俊杰','补充电子技术专业成员','陈建国','2026-04-22 10:00:00','审核中','2026-04-25 07:29:38'),(5001,1001,'TD2024001',1,'添加成员',NULL,NULL,'2021001','张三','需要更多成员','李老师',NULL,'已通过','2026-05-04 17:44:48');
/*!40000 ALTER TABLE `jingsai_renyuan_biangueng` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_saidao`
--

DROP TABLE IF EXISTS `jingsai_saidao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_saidao` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `saidao_mingcheng` varchar(100) NOT NULL COMMENT '????(????/???/???)',
  `saidao_bianma` varchar(50) DEFAULT NULL COMMENT '????',
  `cansai_leixing` varchar(50) DEFAULT '???' COMMENT '????(???/???)',
  `tuandui_renshu_min` int DEFAULT '1' COMMENT '???????',
  `tuandui_renshu_max` int DEFAULT '5' COMMENT '???????',
  `baoming_shuoming` text COMMENT '????',
  `pingshen_biaozhun` text COMMENT '????',
  `is_active` varchar(10) DEFAULT '?' COMMENT '????(?/?)',
  `sort_order` int DEFAULT '0' COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_jingsai_id` (`jingsai_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_saidao`
--

LOCK TABLES `jingsai_saidao` WRITE;
/*!40000 ALTER TABLE `jingsai_saidao` DISABLE KEYS */;
INSERT INTO `jingsai_saidao` VALUES (1,1,'全国大学生程序设计竞赛（ACM-ICPC）','C++组','SD001','团队赛',3,3,'需熟练掌握C++编程语言和常用算法','解题数量、用时、代码质量','是',1,'2026-04-25 07:29:38'),(2,1,'全国大学生程序设计竞赛（ACM-ICPC）','Java组','SD002','团队赛',3,3,'需熟练掌握Java编程语言','解题数量、用时、代码质量','是',2,'2026-04-25 07:29:38'),(3,1,'全国大学生程序设计竞赛（ACM-ICPC）','Python组','SD003','团队赛',3,3,'需熟练掌握Python编程语言','解题数量、用时、代码质量','是',3,'2026-04-25 07:29:38'),(4,2,'中国国际\"互联网+\"大学生创新创业大赛','高教主赛道','SD004','团队赛',3,5,'面向普通高校学生，注重技术创新','创新性、商业价值、团队能力','是',1,'2026-04-25 07:29:38'),(5,2,'中国国际\"互联网+\"大学生创新创业大赛','青年红色筑梦之旅','SD005','团队赛',3,5,'服务乡村振兴、社区治理等公益项目','社会价值、创新性、可持续性','是',2,'2026-04-25 07:29:38'),(6,4,'蓝桥杯全国软件和信息技术专业人才大赛','C/C++程序设计大学A组','SD006','个人赛',1,1,'985/211院校学生参加','解题正确率、运行效率','是',1,'2026-04-25 07:29:38'),(7,4,'蓝桥杯全国软件和信息技术专业人才大赛','Java软件开发大学B组','SD007','个人赛',1,1,'普通本科院校学生参加','解题正确率、运行效率','是',2,'2026-04-25 07:29:38'),(8,4,'蓝桥杯全国软件和信息技术专业人才大赛','Python程序设计','SD008','个人赛',1,1,'不限院校','解题正确率、运行效率','是',3,'2026-04-25 07:29:38'),(9,5,'全国大学生电子设计竞赛','电源类题目','SD009','团队赛',3,3,'设计开关电源、逆变电源等','技术指标、创新性、实用性','是',1,'2026-04-25 07:29:38'),(10,5,'全国大学生电子设计竞赛','控制类题目','SD010','团队赛',3,3,'设计自动控制系统','控制精度、响应速度、稳定性','是',2,'2026-04-25 07:29:38');
/*!40000 ALTER TABLE `jingsai_saidao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_tuandui`
--

DROP TABLE IF EXISTS `jingsai_tuandui`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_tuandui` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `tuandui_bianhao` varchar(50) NOT NULL COMMENT '????',
  `jingsai_id` bigint NOT NULL COMMENT '??ID',
  `jingsaimingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `saidao_id` bigint DEFAULT NULL COMMENT '??ID',
  `saidao_mingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `tuandui_mingcheng` varchar(100) NOT NULL COMMENT '????',
  `fuzeren_xuehao` varchar(20) NOT NULL COMMENT '?????',
  `fuzeren_xingming` varchar(50) NOT NULL COMMENT '?????',
  `fuzeren_shouji` varchar(20) DEFAULT NULL COMMENT '?????',
  `chengyuan_renshu` int DEFAULT '1' COMMENT '????',
  `zuopin_mingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `zuopin_jieshao` text COMMENT '????',
  `shenhe_zhuangtai` varchar(20) DEFAULT '???' COMMENT '????(???/???/???)',
  `shenhe_yijian` text COMMENT '????',
  `tuandui_jianjie` text COMMENT '团队简介',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tuandui_bianhao` (`tuandui_bianhao`),
  KEY `idx_jingsai_id` (`jingsai_id`),
  KEY `idx_saidao_id` (`saidao_id`),
  KEY `idx_fuzeren` (`fuzeren_xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='?????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_tuandui`
--

LOCK TABLES `jingsai_tuandui` WRITE;
/*!40000 ALTER TABLE `jingsai_tuandui` DISABLE KEYS */;
INSERT INTO `jingsai_tuandui` VALUES (1,'TD2026001',1,'全国大学生程序设计竞赛（ACM-ICPC）',1,'C++组','算法先锋队','2022001','张志强','13812345678',3,NULL,NULL,'已通过',NULL,'专注于算法竞赛，以ACM-ICPC为目标，团队成员具备扎实的编程基础和算法能力。','2026-04-25 07:29:38'),(2,'TD2026002',1,'全国大学生程序设计竞赛（ACM-ICPC）',2,'Java组','Java精英队','2022002','李雨婷','13812345679',3,NULL,NULL,'已通过',NULL,'Java技术栈精英团队，专注于企业级应用开发和竞赛。','2026-04-25 07:29:38'),(3,'TD2026003',2,'中国国际\"互联网+\"大学生创新创业大赛',4,'高教主赛道','智能校园团队','2022003','王浩然','13812345680',4,NULL,NULL,'待审核',NULL,'致力于智慧校园解决方案，结合AI和物联网技术。','2026-04-25 07:29:38'),(4,'TD2026004',2,'中国国际\"互联网+\"大学生创新创业大赛',5,'青年红色筑梦之旅','乡村振兴队','2022004','刘思琪','13812345681',5,NULL,NULL,'已通过',NULL,'关注乡村振兴，利用互联网技术助力农村发展。','2026-04-25 07:29:38'),(5,'TD2026005',3,'全国大学生数学建模竞赛',NULL,'本科组','数模之星','2022005','陈俊杰','13812345682',3,NULL,NULL,'已通过',NULL,'数学建模爱好者团队，擅长数据分析和模型构建。','2026-04-25 07:29:38'),(6,'TD2026006',4,'蓝桥杯全国软件和信息技术专业人才大赛',6,'C/C++程序设计大学A组','编程高手','2022001','张志强','13812345678',1,NULL,NULL,'已通过',NULL,'蓝桥杯竞赛团队，专注于算法和数据结构。','2026-04-25 07:29:38'),(7,'TD2026007',5,'全国大学生电子设计竞赛',9,'电源类题目','电源大师队','2022005','陈俊杰','13812345682',3,NULL,NULL,'待审核',NULL,'电子设计竞赛团队，专注于电源类设计。','2026-04-25 07:29:38'),(8,'TD2026008',4,'蓝桥杯全国软件和信息技术专业人才大赛',7,'Java软件开发大学B组','Java达人','2022002','李雨婷','13812345679',1,NULL,NULL,'已通过',NULL,'Java开发团队，参与蓝桥杯竞赛。','2026-04-25 07:29:38');
/*!40000 ALTER TABLE `jingsai_tuandui` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsai_tuandui_chengyuan`
--

DROP TABLE IF EXISTS `jingsai_tuandui_chengyuan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsai_tuandui_chengyuan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `tuandui_id` bigint NOT NULL COMMENT '??ID',
  `tuandui_bianhao` varchar(50) NOT NULL COMMENT '????',
  `xuehao` varchar(20) NOT NULL COMMENT '??',
  `xueshengxingming` varchar(50) NOT NULL COMMENT '????',
  `juese` varchar(20) DEFAULT '??' COMMENT '??(???/??)',
  `xueyuan` varchar(100) DEFAULT NULL COMMENT '??',
  `zhuanye` varchar(100) DEFAULT NULL COMMENT '??',
  `banji` varchar(50) DEFAULT NULL COMMENT '??',
  `shouji` varchar(20) DEFAULT NULL COMMENT '??',
  `youxiang` varchar(100) DEFAULT NULL COMMENT '??',
  `ruzui_shijian` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `is_active` varchar(10) DEFAULT '?' COMMENT '????(?/?)',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tuandui_xuehao` (`tuandui_id`,`xuehao`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_xuehao` (`xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=2008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsai_tuandui_chengyuan`
--

LOCK TABLES `jingsai_tuandui_chengyuan` WRITE;
/*!40000 ALTER TABLE `jingsai_tuandui_chengyuan` DISABLE KEYS */;
INSERT INTO `jingsai_tuandui_chengyuan` VALUES (1,1,'TD2026001','2022001','张志强','负责人','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(2,1,'TD2026001','2022003','王浩然','队员','软件学院','软件工程','软件工程2202班','13812345680','wanghaoran@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(3,1,'TD2026001','2022005','陈俊杰','队员','电子工程学院','电子信息工程','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(4,2,'TD2026002','2022002','李雨婷','负责人','计算机科学与技术学院','软件工程','软件工程2201班','13812345679','liyuting@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(5,2,'TD2026002','2022004','刘思琪','队员','信息工程学院','信息安全','信息安全2201班','13812345681','liusiqi@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(6,2,'TD2026002','2022001','张志强','队员','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(7,3,'TD2026003','2022003','王浩然','负责人','软件学院','软件工程','软件工程2202班','13812345680','wanghaoran@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(8,3,'TD2026003','2022001','张志强','队员','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(9,3,'TD2026003','2022002','李雨婷','队员','计算机科学与技术学院','软件工程','软件工程2201班','13812345679','liyuting@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(10,3,'TD2026003','2022005','陈俊杰','队员','电子工程学院','电子信息工程','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(11,4,'TD2026004','2022004','刘思琪','负责人','信息工程学院','信息安全','信息安全2201班','13812345681','liusiqi@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(12,4,'TD2026004','2022002','李雨婷','队员','计算机科学与技术学院','软件工程','软件工程2201班','13812345679','liyuting@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(13,4,'TD2026004','2022003','王浩然','队员','软件学院','软件工程','软件工程2202班','13812345680','wanghaoran@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(14,4,'TD2026004','2022001','张志强','队员','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(15,4,'TD2026004','2022005','陈俊杰','队员','电子工程学院','电子信息工程','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(16,5,'TD2026005','2022005','陈俊杰','负责人','电子工程学院','电子信息工程','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(17,5,'TD2026005','2022001','张志强','队员','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(18,5,'TD2026005','2022003','王浩然','队员','软件学院','软件工程','软件工程2202班','13812345680','wanghaoran@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(19,6,'TD2026006','2022001','张志强','负责人','计算机科学与技术学院','计算机科学与技术','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(20,7,'TD2026007','2022005','陈俊杰','负责人','电子工程学院','电子信息工程','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','2026-04-25 07:29:38','是','2026-04-25 07:29:38'),(2001,1001,'TD2024001','2021001','ZhangSan','Leader',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2002,1001,'TD2024001','2021002','LiSi','Member',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2003,1001,'TD2024001','2021003','WangWu','Member',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2004,1002,'TD2024002','2021003','WangWu','Leader',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2005,1002,'TD2024002','2021004','ZhaoLiu','Member',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2006,1003,'TD2024003','2021005','SunQi','Leader',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02'),(2007,1003,'TD2024003','2021006','ZhouBa','Member',NULL,NULL,NULL,NULL,NULL,'2026-05-04 17:44:02','Yes','2026-05-04 17:44:02');
/*!40000 ALTER TABLE `jingsai_tuandui_chengyuan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsaibaoming`
--

DROP TABLE IF EXISTS `jingsaibaoming`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsaibaoming` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `gonghao` varchar(200) DEFAULT NULL COMMENT '??',
  `jiaoshixingming` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsaimingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsai_id` bigint DEFAULT NULL COMMENT '??ID',
  `jingsaileixing` varchar(200) DEFAULT NULL COMMENT '????',
  `saidao_id` bigint DEFAULT NULL COMMENT '??ID',
  `saidao_mingcheng` varchar(100) DEFAULT NULL COMMENT '????',
  `tuandui_id` bigint DEFAULT NULL COMMENT '??ID',
  `tuandui_bianhao` varchar(50) DEFAULT NULL COMMENT '????',
  `cansaileixing` varchar(200) DEFAULT NULL COMMENT '????',
  `cansairenyuan` varchar(200) DEFAULT NULL COMMENT '????',
  `cansaizuopin` varchar(200) DEFAULT NULL COMMENT '????',
  `cansaixuanyan` longtext COMMENT '????',
  `shenqingriqi` date DEFAULT NULL COMMENT '????',
  `xuehao` varchar(200) DEFAULT NULL COMMENT '??',
  `xueshengxingming` varchar(200) DEFAULT NULL COMMENT '????',
  `sfsh` varchar(200) DEFAULT '?' COMMENT '????',
  `shhf` longtext COMMENT '????',
  `ispay` varchar(200) DEFAULT '???' COMMENT '????',
  `jinji_jibie` varchar(20) DEFAULT NULL COMMENT '????(??/??/???)',
  `yuan_baoming_id` bigint DEFAULT NULL COMMENT '???ID(???????)',
  PRIMARY KEY (`id`),
  KEY `idx_jingsaibaoming_xuehao` (`xuehao`),
  KEY `idx_jingsaibaoming_mingcheng` (`jingsaimingcheng`),
  KEY `idx_jingsaibaoming_shenhe` (`sfsh`),
  KEY `idx_jingsaibaoming_gonghao` (`gonghao`),
  KEY `idx_jingsaibaoming_xuehao_status` (`xuehao`,`sfsh`),
  KEY `idx_jingsaibaoming_gonghao_status` (`gonghao`,`sfsh`),
  KEY `idx_saidao_id` (`saidao_id`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_jingsai_id` (`jingsai_id`),
  KEY `idx_jinji_jibie` (`jinji_jibie`),
  KEY `idx_sfsh` (`sfsh`)
) ENGINE=InnoDB AUTO_INCREMENT=839501119301488641 DEFAULT CHARSET=utf8mb3 COMMENT='????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsaibaoming`
--

LOCK TABLES `jingsaibaoming` WRITE;
/*!40000 ALTER TABLE `jingsaibaoming` DISABLE KEYS */;
INSERT INTO `jingsaibaoming` VALUES (1,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',1,'C++组',NULL,'TD2026001','团队赛','张志强','upload/zuopin/2022001_1_1778429924038.doc',NULL,'2024-03-01','2022001','张志强','通过','符合参赛条件','已缴费',NULL,NULL),(2,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',1,'C++组',NULL,'TD2026001','团队赛','王浩然',NULL,NULL,'2026-04-10','2022003','王浩然','是','符合参赛条件','已支付',NULL,NULL),(3,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',1,'C++组',NULL,'TD2026001','团队赛','陈俊杰',NULL,NULL,'2026-04-10','2022005','陈俊杰','是','符合参赛条件','已驳回',NULL,NULL),(4,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',2,'Java组',NULL,'TD2026002','团队赛','李雨婷',NULL,NULL,'2026-04-11','2022002','李雨婷','是','符合参赛条件','已支付',NULL,NULL),(5,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',2,'Java组',NULL,'TD2026002','团队赛','刘思琪',NULL,NULL,'2026-04-11','2022004','刘思琪','是','符合参赛条件','已支付',NULL,NULL),(6,'2026-04-25 07:29:38',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',2,'Java组',NULL,'TD2026002','团队赛','张志强','upload/zuopin/2022001_6_data_analysis.docx',NULL,'2024-03-05','2022001','张志强','通过','符合参赛条件','已缴费',NULL,NULL),(7,'2026-04-25 07:29:38',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',4,'高教主赛道',NULL,'TD2026003','个人赛','王浩然',NULL,NULL,'2026-04-12','2022003','王浩然','否',NULL,'已支付',NULL,NULL),(8,'2026-04-25 07:29:38',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',4,'高教主赛道',NULL,'TD2026003','个人赛','张志强','upload/zuopin/2022001_8_innovation_plan.pptx',NULL,'2024-03-10','2022001','张志强','通过',NULL,'已缴费',NULL,NULL),(9,'2026-04-25 07:29:38',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',4,'高教主赛道',NULL,'TD2026003','个人赛','李雨婷',NULL,NULL,'2026-04-12','2022002','李雨婷','否',NULL,'已支付',NULL,NULL),(10,'2026-04-25 07:29:38',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',4,'高教主赛道',NULL,'TD2026003','个人赛','陈俊杰',NULL,NULL,'2026-04-12','2022005','陈俊杰','否',NULL,'已支付',NULL,NULL),(11,'2026-04-25 07:29:38',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',5,'青年红色筑梦之旅',NULL,'TD2026004','个人赛','刘思琪',NULL,NULL,'2026-04-13','2022004','刘思琪','是','项目具有社会价值','已支付',NULL,NULL),(12,'2026-04-25 07:29:38',NULL,NULL,'全国大学生数学建模竞赛',3,'数学建模',NULL,NULL,NULL,'TD2026005','个人赛','陈俊杰',NULL,NULL,'2026-04-14','2022005','陈俊杰','是','符合参赛条件','未支付',NULL,NULL),(13,'2026-04-25 07:29:38',NULL,NULL,'全国大学生数学建模竞赛',3,'数学建模',NULL,NULL,NULL,'TD2026005','个人赛','张志强','upload/zuopin/2022001_13_math_model.zip',NULL,'2024-03-15','2022001','张志强','通过','符合参赛条件','已缴费',NULL,NULL),(14,'2026-04-25 07:29:38',NULL,NULL,'全国大学生数学建模竞赛',3,'数学建模',NULL,NULL,NULL,'TD2026005','个人赛','王浩然',NULL,NULL,'2026-04-14','2022003','王浩然','是','符合参赛条件','未支付',NULL,NULL),(15,'2026-04-25 07:29:38',NULL,NULL,'蓝桥杯全国软件和信息技术专业人才大赛',4,'软件开发',6,'C/C++程序设计大学A组',NULL,'TD2026006','团队赛','张志强','upload/zuopin/2022001_15_code_project.rar',NULL,'2024-03-20','2022001','张志强','通过','符合参赛条件','已缴费',NULL,NULL),(3001,'2026-05-04 17:44:14',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'Innovation',1,NULL,1001,'TD2024001','Team','ZhangSan,LiSi,WangWu','upload/zuopin/team_alpha_project.pdf','AI for Good','2026-03-15','2021001','张三','已通过',NULL,'已支付',NULL,NULL),(3002,'2026-05-04 17:44:14',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'Innovation',1,NULL,1002,'TD2024002','Team','WangWu,ZhaoLiu','upload/zuopin/team_beta_iot.docx','Smart Future','2026-03-15','2021003','王五','已通过',NULL,'已支付',NULL,NULL),(3003,'2026-05-04 17:44:14',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'Algorithm',2,NULL,1003,'TD2024003','Team','SunQi,ZhouBa','upload/zuopin/team_gamma_algo.zip','Code Warriors','2026-03-15','2021005','孙七','已通过',NULL,'已支付',NULL,NULL),(838956778787049472,'2026-05-03 17:54:43',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',NULL,NULL,NULL,NULL,'个人赛','2022002',NULL,NULL,'2026-05-04','2022002','李雨婷','待审核',NULL,'已驳回',NULL,NULL),(839501054591766528,'2026-05-05 05:57:28',NULL,NULL,'全国大学生程序设计竞赛（ACM-ICPC）',1,'算法竞赛',NULL,NULL,NULL,NULL,'个人赛','范佳欣','upload/zuopin/20020907_839501054591766528_1777981369440.doc',NULL,'2026-05-05','20020907','范佳欣','通过','审核通过，请按时缴费并提交作品','已支付',NULL,NULL),(839501062602887168,'2026-05-05 05:57:30',NULL,NULL,'中国国际\"互联网+\"大学生创新创业大赛',2,'创新创业',NULL,NULL,NULL,NULL,'个人赛','范佳欣','upload/zuopin/20020907_839501062602887168_1777981813751.doc',NULL,'2026-05-05','20020907','范佳欣','通过','审核通过，请按时缴费并提交作品','已支付',NULL,NULL),(839501073113812992,'2026-05-05 05:57:33',NULL,NULL,'蓝桥杯全国软件和信息技术专业人才大赛',4,'软件开发',NULL,NULL,NULL,NULL,'个人赛','范佳欣','upload/zuopin/20020907_839501073113812992_1777981821006.doc',NULL,'2026-05-05','20020907','范佳欣','通过','审核通过，请按时缴费并提交作品','已支付',NULL,NULL),(839501119301488640,'2026-05-05 05:57:44',NULL,NULL,'全国大学生数学建模竞赛',3,'数学建模',NULL,NULL,NULL,NULL,'个人赛','范佳欣','upload/zuopin/20020907_839501119301488640_1777981828367.doc',NULL,'2026-05-05','20020907','范佳欣','通过','审核通过，请按时缴费并提交作品','已缴费',NULL,NULL);
/*!40000 ALTER TABLE `jingsaibaoming` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jingsaixinxi`
--

DROP TABLE IF EXISTS `jingsaixinxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jingsaixinxi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `jingsaimingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsaileixing` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsai_jibie` varchar(20) DEFAULT NULL COMMENT '????(A?/B?/C?)',
  `nianfen` int DEFAULT '2026' COMMENT '??',
  `jingsaididian` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsaiguize` longtext COMMENT '????',
  `jingsaijiangli` longtext COMMENT '????',
  `jingsaishijian` datetime DEFAULT NULL COMMENT '????',
  `moshi` varchar(200) DEFAULT NULL COMMENT '??',
  `shifou_you_saidao` varchar(10) DEFAULT '?' COMMENT '?????(?/?)',
  `shifou_xuyao_jinji` varchar(10) DEFAULT '?' COMMENT '??????(?/?)',
  `fengmiantupian` varchar(200) DEFAULT NULL COMMENT '????',
  `gonghao` varchar(200) DEFAULT NULL COMMENT '??',
  `jiaoshixingming` varchar(200) DEFAULT NULL COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_jingsaixinxi_leixing` (`jingsaileixing`),
  KEY `idx_jingsaixinxi_shijian` (`jingsaishijian`),
  KEY `idx_jingsaixinxi_gonghao` (`gonghao`)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb3 COMMENT='????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jingsaixinxi`
--

LOCK TABLES `jingsaixinxi` WRITE;
/*!40000 ALTER TABLE `jingsaixinxi` DISABLE KEYS */;
INSERT INTO `jingsaixinxi` VALUES (1,'2026-04-25 07:29:38','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛','国家级',2026,'线上初赛+线下决赛','团队赛（3人），5小时解决10-12道算法题','国家级金奖5000元/队，银奖3000元/队','2026-06-15 09:00:00','线上线下结合','是','是','upload/比赛.png','T2022001','李明德'),(2,'2026-04-25 07:29:38','中国国际\"互联网+\"大学生创新创业大赛','创新创业','国家级',2026,'北京国家会议中心','团队赛（3-5人），提交商业计划书和路演PPT','国家级金奖获得投资对接机会','2026-07-20 14:00:00','线下','是','是','upload/比赛.png','T2022002','王秀英'),(3,'2026-04-25 07:29:38','全国大学生数学建模竞赛','数学建模','国家级',2026,'全国各地高校同步进行','团队赛（3人），72小时内完成建模论文','国家一等奖、二等奖，省级奖项','2026-09-10 08:00:00','线下','否','否','upload/比赛.png','T2022005','陈建国'),(4,'2026-04-25 07:29:38','蓝桥杯全国软件和信息技术专业人才大赛','软件开发','国家级',2026,'线上考试','个人赛，C/C++、Java、Python等语言','国家一二三等奖，企业offer直通卡','2026-09-05 09:00:00','线上','否','否','upload/比赛.png','T2022003','张伟强'),(5,'2026-04-25 07:29:38','全国大学生电子设计竞赛','电子设计','国家级',2026,'南京航空航天大学','团队赛（3人），4天3夜完成作品','国家一二三等奖，省级奖项','2026-08-15 08:00:00','线下','是','否','upload/比赛.png','T2022005','陈建国'),(100,'2026-04-25 14:26:42','校级程序设计竞赛','算法竞赛','校级',2026,'线上考试','??????3?????8????','???3????+??1000??????6????+??500??????10?????','2026-08-15 09:00:00','线上','否','否','upload/比赛.png','T2022001','李明德'),(101,'2026-04-25 14:26:42','校级数学建模竞赛','数学建模','校级',2026,'机房201-203','3????48?????????????','???5????+??2000?/??????10????+??1000?/??????15?????','2026-08-30 09:00:00','线上','否','否','upload/比赛.png','T2022008','王秀英'),(102,'2026-04-25 14:26:42','校级电子设计竞赛','电子设计','校级',2026,'实验楼E座','4????3?2????????????','???3????+??3000?/??????6????+??1500?/??????10?????','2026-06-01 09:00:00','线下','是','否','upload/比赛.png','T2022005','陈建国'),(103,'2026-04-25 14:26:42','校级蓝桥杯选拔赛','程序设计','校级',2026,'计算机学院','????C/C++?Java????4??','???5????+??1500??????10????+??800??????15?????','2026-08-20 09:00:00','线上','否','否','upload/比赛.png','T2022001','李明德'),(104,'2026-04-25 14:26:42','校级创新创业大赛','创新创业','校级',2026,'创新创业中心','???????+????','??3????+??5000?/?????6????+??2000?/?????10????+??1000?/??','2026-08-25 09:00:00','线下','否','是','upload/比赛.png','T2022006','张伟强'),(200,'2026-04-28 13:42:33','省级程序设计竞赛（ACM-ICPC）','算法竞赛','省级',2026,'线上初赛+线下决赛',NULL,NULL,'2026-06-20 09:00:00','线上线下结合','是','是','upload/比赛.png','T2022001','李明德'),(201,'2026-04-28 13:42:33','省级数学建模竞赛','数学建模','省级',2026,'各地高校同步进行',NULL,NULL,'2026-09-15 08:00:00','线下比赛','否','是','upload/比赛.png','T2022005','陈建国'),(202,'2026-04-28 13:42:33','省级电子设计竞赛','电子设计','省级',2026,'南京航空航天大学',NULL,NULL,'2026-08-20 08:00:00','线下比赛','是','是','upload/比赛.png','T2022005','陈建国'),(203,'2026-04-28 13:42:33','省级蓝桥杯竞赛','程序设计','省级',2026,'线上考试',NULL,NULL,'2026-09-10 09:00:00','线上比赛','否','是','upload/比赛.png','T2022003','张伟强'),(204,'2026-04-28 13:42:33','省级创新创业大赛','创新创业','省级',2026,'北京国家会议中心',NULL,NULL,'2026-07-25 14:00:00','线上线下结合','是','是','upload/比赛.png','T2022002','王秀英');
/*!40000 ALTER TABLE `jingsaixinxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `userid` bigint NOT NULL COMMENT '??id',
  `username` varchar(100) NOT NULL COMMENT '???',
  `tablename` varchar(100) DEFAULT NULL COMMENT '??',
  `role` varchar(100) DEFAULT NULL COMMENT '??',
  `token` varchar(200) NOT NULL COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb3 COMMENT='token?';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (122,1,'2022001','xuesheng','学生','qbihwh471djx3ztd4k4jpejijjik51np','2026-05-12 16:12:14','2026-05-15 07:53:39');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tuandui_apply`
--

DROP TABLE IF EXISTS `tuandui_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tuandui_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tuandui_id` bigint NOT NULL,
  `tuandui_mingcheng` varchar(100) DEFAULT NULL,
  `jingsai_id` bigint DEFAULT NULL,
  `jingsaimingcheng` varchar(100) DEFAULT NULL,
  `xuehao` varchar(20) NOT NULL,
  `xueshengxingming` varchar(50) NOT NULL,
  `apply_type` varchar(20) NOT NULL,
  `apply_reason` text,
  `apply_status` varchar(20) DEFAULT '待审核',
  `shenhe_yijian` text,
  `shenhe_xuehao` varchar(20) DEFAULT NULL,
  `shenhe_xingming` varchar(50) DEFAULT NULL,
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `shenhe_time` timestamp NULL DEFAULT NULL,
  `addtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tuandui_id` (`tuandui_id`),
  KEY `idx_xuehao` (`xuehao`),
  KEY `idx_apply_status` (`apply_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tuandui_apply`
--

LOCK TABLES `tuandui_apply` WRITE;
/*!40000 ALTER TABLE `tuandui_apply` DISABLE KEYS */;
/*!40000 ALTER TABLE `tuandui_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'admin',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$JnrrzNaUyNh2uSJOEapku.6AmN7/R0jE1gRkC6Du109ojVFtPB9Kq','管理员','2026-04-24 19:08:09'),(2,'20020907','$2a$10$AJ8RPvNEttXGdsSPs/QZnu8zn3Pwce8HaTEIUO3o3dyT.KeE6TGFa','学生','2026-05-04 18:39:33');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xuesheng`
--

DROP TABLE IF EXISTS `xuesheng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xuesheng` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `xuehao` varchar(200) NOT NULL COMMENT '??',
  `mima` varchar(200) NOT NULL COMMENT '??',
  `xueshengxingming` varchar(200) NOT NULL COMMENT '????',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '??',
  `xueyuanmingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `banji` varchar(200) DEFAULT NULL COMMENT '??',
  `shouji` varchar(200) DEFAULT NULL COMMENT '??',
  `youxiang` varchar(200) DEFAULT NULL COMMENT '??',
  `zhaopian` varchar(200) DEFAULT NULL COMMENT '??',
  `role` varchar(20) DEFAULT '??' COMMENT '??',
  PRIMARY KEY (`id`),
  UNIQUE KEY `xuehao` (`xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=839330447447166977 DEFAULT CHARSET=utf8mb3 COMMENT='??';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xuesheng`
--

LOCK TABLES `xuesheng` WRITE;
/*!40000 ALTER TABLE `xuesheng` DISABLE KEYS */;
INSERT INTO `xuesheng` VALUES (1,'2026-04-25 07:29:38','2022001','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','张志强','男','计算机科学与技术学院','计算机科学与技术2201班','13812345678','zhangzhiqiang@stu.edu.cn','upload/1777400100337_人像.png','学生'),(2,'2026-04-25 07:29:38','2022002','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','李雨婷','女','计算机科学与技术学院','软件工程2201班','13812345679','liyuting@stu.edu.cn','upload/人像.png','学生'),(3,'2026-04-25 07:29:38','2022003','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','王浩然','男','软件学院','软件工程2202班','13812345680','wanghaoran@stu.edu.cn','upload/人像.png','学生'),(4,'2026-04-25 07:29:38','2022004','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','刘思琪','女','信息工程学院','信息安全2201班','13812345681','liusiqi@stu.edu.cn','upload/人像.png','学生'),(5,'2026-04-25 07:29:38','2022005','$2a$10$6gePRyrwza8JHhay2hmbg.ThEA8ILKVl0wg4V9AuSa/4muJORrX5q','陈俊杰','男','电子工程学院','电子信息工程2201班','13812345682','chenjunjie@stu.edu.cn','upload/人像.png','学生'),(6,'2026-05-04 17:43:23','2021001','123456','张三','男','计算机学院','计算机2101班','13900000001',NULL,NULL,'??'),(7,'2026-05-04 17:43:23','2021002','123456','李四','女','软件学院','软件2101班','13900000002',NULL,NULL,'??'),(8,'2026-05-04 17:43:23','2021003','123456','王五','男','计算机学院','计算机2101班','13900000003',NULL,NULL,'??'),(9,'2026-05-04 17:43:23','2021004','123456','赵六','男','信息学院','信息2101班','13900000004',NULL,NULL,'??'),(10,'2026-05-04 17:43:23','2021005','123456','孙七','女','计算机学院','计算机2101班','13900000005',NULL,NULL,'??'),(11,'2026-05-04 17:43:23','2021006','123456','周八','男','软件学院','软件2101班','13900000006',NULL,NULL,'??'),(12,'2026-05-04 17:43:23','2021007','123456','吴九','女','信息学院','信息2101班','13900000007',NULL,NULL,'??'),(13,'2026-05-04 17:43:23','2021008','123456','郑十','男','计算机学院','计算机2101班','13900000008',NULL,NULL,'??'),(14,'2026-05-04 17:43:23','2021009','123456','陈十一','女','软件学院','软件2101班','13900000009',NULL,NULL,'??'),(15,'2026-05-04 17:43:23','2021010','123456','刘十二','男','信息学院','信息2101班','13900000010',NULL,NULL,'??'),(839330447447166976,'2026-05-04 18:39:33','20020907','$2a$10$J/HhI4hiaAFe8SpIkHnXNu76SyDJVpEsYDooLHbk93uhMMA7iQtuO','范佳欣','男','人工智能学院','24软件本1班','13257926308','1367019690@qq.com',NULL,'学生');
/*!40000 ALTER TABLE `xuesheng` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zuopindafen`
--

DROP TABLE IF EXISTS `zuopindafen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zuopindafen` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `xuehao` varchar(200) DEFAULT NULL COMMENT '??',
  `xueshengxingming` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsaimingcheng` varchar(200) DEFAULT NULL COMMENT '????',
  `jingsaileixing` varchar(200) DEFAULT NULL COMMENT '????',
  `zuopinpingfen` int DEFAULT NULL COMMENT '????',
  `pingjianeirong` longtext COMMENT '????',
  `pingjiashijian` date DEFAULT NULL COMMENT '????',
  `gonghao` varchar(200) DEFAULT NULL COMMENT '??',
  `jiaoshixingming` varchar(200) DEFAULT NULL COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_zuopindafen_xuehao` (`xuehao`),
  KEY `idx_zuopindafen_gonghao` (`gonghao`),
  KEY `idx_zuopindafen_mingcheng` (`jingsaimingcheng`)
) ENGINE=InnoDB AUTO_INCREMENT=839590070628519937 DEFAULT CHARSET=utf8mb3 COMMENT='????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zuopindafen`
--

LOCK TABLES `zuopindafen` WRITE;
/*!40000 ALTER TABLE `zuopindafen` DISABLE KEYS */;
INSERT INTO `zuopindafen` VALUES (1,'2026-04-25 07:29:38','2022001','张志强','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛',96,'算法设计优秀，代码效率高','2026-04-20','T2022001','李明德'),(2,'2026-04-25 07:29:38','2022003','王浩然','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛',92,'代码规范，注释清晰','2026-04-20','T2022001','李明德'),(3,'2026-04-25 07:29:38','2022002','李雨婷','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛',85,'基本正确，有优化空间','2026-04-20','T2022001','李明德'),(4,'2026-04-25 07:29:38','2022004','刘思琪','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛',95,'代码结构良好','2026-04-20','T2022001','李明德'),(5,'2026-04-25 07:29:38','2022005','陈俊杰','全国大学生数学建模竞赛','数学建模',93,'模型合理，假设恰当','2026-04-18','T2022005','陈建国'),(6,'2026-04-25 07:29:38','2022001','张志强','全国大学生数学建模竞赛','数学建模',95,'论文写作规范，逻辑清晰','2026-04-18','T2022005','陈建国'),(7,'2026-04-25 07:29:38','2022003','王浩然','全国大学生数学建模竞赛','数学建模',89,'分析深入，结论可靠','2026-04-18','T2022005','陈建国'),(8,'2026-04-25 07:29:38','2022001','张志强','蓝桥杯全国软件和信息技术专业人才大赛','软件开发',95,'完成7/10题','2026-04-25','T2022003','张伟强'),(9,'2026-04-25 07:29:38','2022001','张志强','蓝桥杯全国软件和信息技术专业人才大赛','软件开发',90,'大部分题目运行时间在限制内','2026-04-25','T2022003','张伟强'),(10,'2026-04-25 07:29:38','2022005','陈俊杰','全国大学生电子设计竞赛','电子设计',0,'作品尚未提交','2026-04-25','T2022005','陈建国'),(4001,'2026-05-04 17:44:24','2021001','张三','全国大学生程序设计竞赛（ACM-ICPC）','Innovation',92,'Excellent innovation and teamwork','2026-04-25','T2022001','李明德'),(4002,'2026-05-04 17:44:24','2021003','王五','全国大学生程序设计竞赛（ACM-ICPC）','Innovation',88,'Good project with room for improvement','2026-04-25','T2022001','李明德'),(4003,'2026-05-04 17:44:24','2021005','孙七','中国国际\"互联网+\"大学生创新创业大赛','Algorithm',95,'Outstanding algorithm design','2026-04-25','T2022002','王秀英'),(839590070628519936,'2026-05-05 11:51:11','20020907','范佳欣','全国大学生程序设计竞赛（ACM-ICPC）','算法竞赛',70,'测试评分',NULL,'t2022001',NULL);
/*!40000 ALTER TABLE `zuopindafen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zuopindafen_fuhe`
--

DROP TABLE IF EXISTS `zuopindafen_fuhe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zuopindafen_fuhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'zhu jian id',
  `zuopindafen_id` bigint DEFAULT NULL COMMENT 'zuo pin da fen ji lu ID',
  `jingsaimingcheng` varchar(200) DEFAULT NULL COMMENT 'jing sai ming cheng',
  `yuan_chengji` varchar(50) DEFAULT NULL COMMENT 'yuan cheng ji',
  `xuehao` varchar(50) DEFAULT NULL COMMENT 'xue hao',
  `xueshengxingming` varchar(50) DEFAULT NULL COMMENT 'xue sheng xing ming',
  `fuhe_reason` text COMMENT 'fu he li you',
  `fuhe_status` varchar(50) DEFAULT 'dai shen he' COMMENT 'fu he zhuang tai',
  `xin_chengji` varchar(50) DEFAULT NULL COMMENT 'xin cheng ji',
  `shenhe_yijian` text COMMENT 'shen he yi jian',
  `shenhe_gonghao` varchar(50) DEFAULT NULL COMMENT 'shen he jiao shi gong hao',
  `shenhe_jiaoshi` varchar(50) DEFAULT NULL COMMENT 'shen he jiao shi xing ming',
  `shenqing_shijian` datetime DEFAULT NULL COMMENT 'shen qing shi jian',
  `shenhe_shijian` datetime DEFAULT NULL COMMENT 'shen he shi jian',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='zuo pin da fen fu he shen qing biao';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zuopindafen_fuhe`
--

LOCK TABLES `zuopindafen_fuhe` WRITE;
/*!40000 ALTER TABLE `zuopindafen_fuhe` DISABLE KEYS */;
INSERT INTO `zuopindafen_fuhe` VALUES (1,1,'全国大学生程序设计竞赛（ACM-ICPC）','96','2022001','张志强','复核功能测试，查看是否功能实地落地','已驳回','96','成绩无异常驳回申请，保持原成绩','T2022001','李明德','2026-05-04 00:32:08','2026-05-04 00:38:18'),(2,8,'蓝桥杯全国软件和信息技术专业人才大赛','85','2022001','张志强','成绩测试，这次将分数改为95分','已通过','95','测试通过功能，将成绩改为95分','t2022001','t2022001','2026-05-04 01:35:20','2026-05-04 01:36:01'),(3,9,'蓝桥杯全国软件和信息技术专业人才大赛','87','2022001','张志强','再次测试成绩复核通过，将成绩改为90分','已通过','90','成绩通过测试将成绩改为90分','t2022001','t2022001','2026-05-04 01:39:56','2026-05-04 01:40:30'),(6,839590070628520000,'全国大学生程序设计竞赛（ACM-ICPC）','70','20020907','范佳欣','测试复核成绩是否成功','待审核',NULL,NULL,NULL,NULL,'2026-05-06 01:14:08',NULL),(7,6,'全国大学生数学建模竞赛','91','2022001','张志强','不满意，要求重新审核','已通过','95','成绩复核成功，成绩改为95分','T2022001','李明德','2026-05-11 00:19:48','2026-05-11 00:23:14');
/*!40000 ALTER TABLE `zuopindafen_fuhe` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-15 17:01:57
