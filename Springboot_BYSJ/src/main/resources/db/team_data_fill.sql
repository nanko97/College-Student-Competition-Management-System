-- 团队数据联动填充脚本
-- 执行前请确保已有竞赛、赛道、学生基础数据

USE BYSJ_Springboot;

-- 1. 更新团队基本信息（赛道、简介、成员数）
UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'C++组',
  tuandui_jianjie = '专注于算法竞赛，以ACM-ICPC为目标，团队成员具备扎实的编程基础和算法能力。',
  chengyuan_renshu = 3
WHERE id = 1;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'Java组',
  tuandui_jianjie = 'Java技术栈精英团队，专注于企业级应用开发和竞赛。',
  chengyuan_renshu = 3
WHERE id = 2;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = '高教主赛道',
  tuandui_jianjie = '致力于智慧校园解决方案，结合AI和物联网技术。',
  chengyuan_renshu = 4
WHERE id = 3;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = '青年红色筑梦之旅',
  tuandui_jianjie = '关注乡村振兴，利用互联网技术助力农村发展。',
  chengyuan_renshu = 5
WHERE id = 4;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = '本科组',
  tuandui_jianjie = '数学建模爱好者团队，擅长数据分析和模型构建。',
  chengyuan_renshu = 3
WHERE id = 5;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'C/C++程序设计大学A组',
  tuandui_jianjie = '蓝桥杯竞赛团队，专注于算法和数据结构。',
  chengyuan_renshu = 1
WHERE id = 6;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = '电源类题目',
  tuandui_jianjie = '电子设计竞赛团队，专注于电源类设计。',
  chengyuan_renshu = 3
WHERE id = 7;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'Java软件开发大学B组',
  tuandui_jianjie = 'Java开发团队，参与蓝桥杯竞赛。',
  chengyuan_renshu = 1
WHERE id = 8;

-- 2. 更新负责人手机信息
UPDATE jingsai_tuandui SET fuzeren_shouji = '13812345678' WHERE fuzeren_xuehao = '2022001';
UPDATE jingsai_tuandui SET fuzeren_shouji = '13812345679' WHERE fuzeren_xuehao = '2022002';
UPDATE jingsai_tuandui SET fuzeren_shouji = '13812345680' WHERE fuzeren_xuehao = '2022003';
UPDATE jingsai_tuandui SET fuzeren_shouji = '13812345681' WHERE fuzeren_xuehao = '2022004';
UPDATE jingsai_tuandui SET fuzeren_shouji = '13812345682' WHERE fuzeren_xuehao = '2022005';

-- 3. 更新旧数据团队
UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'C++组',
  tuandui_jianjie = 'ACM-ICPC竞赛团队',
  shenhe_zhuangtai = '已通过'
WHERE id = 1001;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = 'Java组',
  tuandui_jianjie = 'Java编程团队',
  shenhe_zhuangtai = '已通过'
WHERE id = 1002;

UPDATE jingsai_tuandui SET 
  saidao_mingcheng = '高教主赛道',
  tuandui_jianjie = '创新创业团队',
  shenhe_zhuangtai = '已通过'
WHERE id = 1003;

-- 4. 补充团队成员信息（如果需要添加更多成员）
-- 团队1：算法先锋队（已有3人）
-- 团队2：Java精英队（已有3人）
-- 团队3：智能校园团队（已有4人）
-- 团队4：乡村振兴队（已有5人）
-- 团队5：数模之星（已有3人）
-- 团队6：编程高手（已有1人，负责人）
-- 团队7：电源大师队（已有1人，负责人）
-- 团队8：Java达人（已有1人，负责人）

-- 验证数据
SELECT '团队数据填充完成！' as message;
SELECT 
  t.id,
  t.tuandui_bianhao,
  t.tuandui_mingcheng,
  t.jingsaimingcheng,
  t.saidao_mingcheng,
  t.fuzeren_xingming,
  t.fuzeren_shouji,
  t.chengyuan_renshu,
  t.shenhe_zhuangtai,
  LEFT(t.tuandui_jianjie, 20) as jianjie_preview
FROM jingsai_tuandui t
ORDER BY t.id;

SELECT 
  COUNT(*) as total_members,
  COUNT(DISTINCT tuandui_id) as total_teams
FROM jingsai_tuandui_chengyuan
WHERE is_active = '是';
