-- ========================================
-- 数据库索引优化脚本
-- 目的：提升常用查询性能
-- 执行时间：2026-05-05
-- 注意：执行前请先检查索引是否已存在，避免重复创建
-- ========================================

-- 1. 报名表索引优化
-- 学号查询（学生查看自己的报名）
CREATE INDEX idx_xuehao ON jingsaibaoming(xuehao);

-- 审核状态查询（筛选通过/待审核/驳回的报名）
CREATE INDEX idx_sfsh ON jingsaibaoming(sfsh);

-- 竞赛ID查询（查看某个竞赛的所有报名）
CREATE INDEX idx_jingsai_id ON jingsaibaoming(jingsai_id);

-- 组合索引：学号+审核状态（学生查询自己的已通过报名）
CREATE INDEX idx_xuehao_sfsh ON jingsaibaoming(xuehao, sfsh);

-- 组合索引：竞赛ID+审核状态（教师查看某个竞赛的已通过报名）
CREATE INDEX idx_jingsai_sfsh ON jingsaibaoming(jingsai_id, sfsh);

-- 2. 作品评分表索引优化
-- 学号查询（学生查看自己的成绩）
CREATE INDEX idx_xuehao ON zuopindafen(xuehao);

-- 工号查询（教师查看自己评分的记录）
CREATE INDEX idx_gonghao ON zuopindafen(gonghao);

-- 组合索引：学号+竞赛名称（学生查看特定竞赛成绩）
CREATE INDEX idx_xuehao_jingsai ON zuopindafen(xuehao, jingsaimingcheng);

-- 3. 团队表索引优化
-- 负责人学号查询（查找学生创建的团队）
CREATE INDEX idx_fuzeren_xuehao ON jingsai_tuandui(fuzeren_xuehao);

-- 竞赛ID查询（查看某个竞赛的所有团队）
CREATE INDEX idx_jingsai_id ON jingsai_tuandui(jingsai_id);

-- 4. 团队成员表索引优化
-- 团队ID查询（查看团队的所有成员）
CREATE INDEX idx_tuandui_id ON jingsai_tuandui_chengyuan(tuandui_id);

-- 学号查询（查找学生加入的所有团队）
CREATE INDEX idx_xuehao ON jingsai_tuandui_chengyuan(xuehao);

-- 组合索引：团队ID+学号（检查学生是否在某个团队）
CREATE INDEX idx_tuandui_xuehao ON jingsai_tuandui_chengyuan(tuandui_id, xuehao);

-- 5. 竞赛信息表索引优化
-- 竞赛类型查询（筛选特定类型的竞赛）
CREATE INDEX idx_jingsaileixing ON jingsaixinxi(jingsaileixing);

-- 审核状态查询（查看已通过审核的竞赛）
CREATE INDEX idx_sfsh ON jingsaixinxi(sfsh);

-- 6. 成绩复核表索引优化
-- 学号查询（学生查看自己的复核申请）
CREATE INDEX idx_xuehao ON zuopindafen_fuhe(xuehao);

-- 审核状态查询（教师查看待审核的复核）
CREATE INDEX idx_shenhe_zhuangtai ON zuopindafen_fuhe(shenhe_zhuangtai);

-- 7. 人员变更表索引优化
-- 团队ID查询（查看团队的所有变更记录）
CREATE INDEX idx_tuandui_id ON jingsai_renyuan_biangueng(tuandui_id);

-- 审核状态查询（教师查看待审核的变更）
CREATE INDEX idx_shenhe_zhuangtai ON jingsai_renyuan_biangueng(shenhe_zhuangtai);

-- 8. Token表索引优化（高频查询）
-- Token查询（登录验证）
CREATE INDEX idx_token ON token(token);

-- 过期时间查询（清理过期token）
CREATE INDEX idx_expiratedtime ON token(expiratedtime);

-- ========================================
-- 索引创建完成
-- 验证方法：SHOW INDEX FROM 表名;
-- ========================================
