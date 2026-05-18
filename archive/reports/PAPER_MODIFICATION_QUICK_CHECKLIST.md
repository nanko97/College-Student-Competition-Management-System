# 论文修改快速检查表

**打印此表，逐项勾选！** ✅

---

## 🔴 第一阶段：字段命名修正（必须完成）

### 第3章 系统分析 - 数据字典

- [ ] 报名记录表：`shenheZhuangtai` → `sfsh`
- [ ] 报名记录表：`isPay` → `ispay`
- [ ] 报名记录表：`jinjiJibie` → `jinji_jibie`
- [ ] 报名记录表：`teacherNo` → `gonghao`
- [ ] 报名记录表：`teacherName` → `jiaoshixingming`
- [ ] 报名记录表：`studentName` → `xueshengxingming`
- [ ] 报名记录表：`cansaiLeixing` → `canzhaleixing`

- [ ] 团队信息表：`jingsaiId` → `jingsai_id`
- [ ] 团队信息表：`saidaoId` → `saidao_id`
- [ ] 团队信息表：`tuanduiBianhao` → `tuandui_bianhao`
- [ ] 团队信息表：`tuanduiMingcheng` → `tuandui_mingcheng`
- [ ] 团队信息表：`fuzerenXuehao` → `fuzeren_xuehao`
- [ ] 团队信息表：`chengyuanRenshu` → `chengyuan_renshu`
- [ ] 团队信息表：`shenheZhuangtai` → `shenhe_zhuangtai`

- [ ] 团队成员表：`tuanduiId` → `tuandui_id`
- [ ] 团队成员表：`xueshengXingming` → `xueshengxingming`
- [ ] 团队成员表：`ruzuiShijian` → `ruzui_shijian`
- [ ] 团队成员表：`isActive` → `is_active`

- [ ] 作品打分表：`zuopinPingfen` → `zuopinpingfen`
- [ ] 作品打分表：`pingjiaNeirong` → `pingjianeirong`
- [ ] 作品打分表：`teacherNo` → `gonghao`
- [ ] 作品打分表：`teacherName` → `jiaoshixingming`

- [ ] 晋级记录表：`yuanBaomingId` → `yuan_baoming_id`
- [ ] 晋级记录表：`yuanJinjiJibie` → `yuan_jinji_jibie`
- [ ] 晋级记录表：`jinjiJibie` → `jinji_jibie`
- [ ] 晋级记录表：`shenheZhuangtai` → `shenhe_zhuangtai`

---

### 第4章 系统设计 - 接口描述

- [ ] 报名接口参数：所有驼峰命名改为下划线
- [ ] 团队接口参数：所有驼峰命名改为下划线
- [ ] 作品接口参数：所有驼峰命名改为下划线
- [ ] 晋级接口参数：所有驼峰命名改为下划线

---

### 第5章 系统实现 - 代码示例

- [ ] Java代码中的字段名全部改为下划线
- [ ] TypeScript代码中的字段名全部改为下划线
- [ ] SQL语句中的字段名全部改为下划线
- [ ] JSON示例中的字段名全部改为下划线

---

### 第6章 系统测试 - 测试用例

- [ ] 所有测试用例中的字段名改为下划线
- [ ] 测试数据的字段名改为下划线
- [ ] 预期结果的字段名改为下划线

---

## 🟡 第二阶段：功能补充

### 团队创建功能

- [ ] 补充团队编号生成规则（T+时间戳）
- [ ] 补充四大业务规则说明
- [ ] 补充数据库操作SQL示例
- [ ] 补充业务流程图

---

### RBAC权限控制

- [ ] 补充角色定义（admin/jiaoshi/xuesheng）
- [ ] 补充三级权限控制说明
- [ ] 补充权限工具函数代码
- [ ] 补充权限控制流程图

---

### XSS防护和Token管理

- [ ] 补充XSS转义规则说明
- [ ] 补充HTML实体转义代码
- [ ] 补充Token管理机制说明
- [ ] 补充Token认证流程图

---

## 🟢 第三阶段：图表更新

- [ ] ER图中所有字段名改为下划线
- [ ] 类图中所有字段名改为下划线
- [ ] 时序图补充团队创建流程
- [ ] 补充安全防护体系架构图
- [ ] 检查所有图表编号和引用

---

## 🔍 第四阶段：最终检查

### 全局搜索替换确认

在Word中按Ctrl+F搜索以下关键词，确认已全部替换：

- [ ] 搜索 "shenheZhuangtai" → 应为 0 处
- [ ] 搜索 "isPay" → 应为 0 处
- [ ] 搜索 "jinjiJibie" → 应为 0 处
- [ ] 搜索 "yuanBaomingId" → 应为 0 处
- [ ] 搜索 "saidaoId" → 应为 0 处
- [ ] 搜索 "tuanduiId" → 应为 0 处
- [ ] 搜索 "jingsaiId" → 应为 0 处
- [ ] 搜索 "teacherNo" → 应为 0 处
- [ ] 搜索 "teacherName" → 应为 0 处
- [ ] 搜索 "studentName" → 应为 0 处
- [ ] 搜索 "zuopinPingfen" → 应为 0 处
- [ ] 搜索 "pingjiaNeirong" → 应为 0 处
- [ ] 搜索 "cansaiLeixing" → 应为 0 处

---

### 格式检查

- [ ] 字体统一（中文宋体/黑体，英文Times New Roman）
- [ ] 字号统一（正文小四，标题按要求）
- [ ] 行距统一（1.5倍或固定值）
- [ ] 段落缩进统一（首行缩进2字符）
- [ ] 图表标题格式统一
- [ ] 参考文献格式统一

---

### 内容检查

- [ ] 章节编号连续
- [ ] 图表编号连续
- [ ] 公式编号连续
- [ ] 引用标注正确
- [ ] 术语使用一致
- [ ] 语句通顺无错别字

---

## 📊 修改进度跟踪

| 阶段 | 计划开始时间 | 计划完成时间 | 实际完成时间 | 备注 |
|-----|------------|------------|------------|------|
| 第一阶段：字段命名修正 | | | | |
| 第二阶段：功能补充 | | | | |
| 第三阶段：图表更新 | | | | |
| 第四阶段：最终检查 | | | | |

---

## ⏰ 时间安排建议

### Day 1（4小时）
- 上午（2小时）：第3章数据字典字段名修正
- 下午（2小时）：第4章接口描述字段名修正

### Day 2（4小时）
- 上午（2小时）：第5章代码示例字段名修正
- 下午（2小时）：第6章测试用例字段名修正

### Day 3（4小时）
- 上午（2小时）：功能补充（团队创建、RBAC、XSS）
- 下午（2小时）：图表更新和最终检查

---

## 💡 修改技巧

### 1. Word批量替换技巧

1. 按 `Ctrl+H` 打开替换对话框
2. 点击"更多"按钮
3. 勾选"区分大小写"
4. 输入查找内容和替换内容
5. 点击"全部替换"

### 2. 验证替换结果

替换后立即搜索原关键词，确认数量为0：
- 按 `Ctrl+F` 打开查找
- 输入原关键词（如 shenheZhuangtai）
- 查看搜索结果数量

### 3. 备份原始文档

每次大规模替换前：
1. 另存为新版本（如 v1.1、v1.2）
2. 保留原始文档作为备份
3. 记录修改内容

---

## ✅ 完成标志

当以下所有条件满足时，表示修改完成：

- [ ] 所有字段名已改为下划线命名
- [ ] 所有功能已补充完整
- [ ] 所有图表已更新
- [ ] 全局搜索无遗留的驼峰命名
- [ ] 格式检查通过
- [ ] 内容检查通过
- [ ] 已备份最终版本

---

## 🎯 关键记忆点

```
审核: sfsh (不是 shenheZhuangtai)
支付: ispay (不是 isPay)
晋级: jinji_jibie (不是 jinjiJibie)
工号: gonghao (不是 teacherNo)
教师: jiaoshixingming (不是 teacherName)
学生: xueshengxingming (不是 studentName)
评分: zuopinpingfen (不是 score)
评价: pingjianeirong (不是 comment)
类型: canzhaleixing (不是 type)
赛道: saidao_id (不是 saidaoId)
团队: tuandui_id (不是 tuanduiId)
竞赛: jingsai_id (不是 jingsaiId)
```

---

**打印此表，每完成一项就打勾！** 📌

**祝修改顺利！** ✨
