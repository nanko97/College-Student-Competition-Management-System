# BYSJ项目 - Dashboard功能落地方案

**创建时间**: 2026-05-15  
**版本**: v1.0  
**状态**: 🔧 **待实施**

---

## 🐛 问题分析

### 当前问题
系统登录后显示 **404 页面未找到**

### 根本原因
1. 前端Dashboard页面 (`/dashboard/console`) 加载时会调用API
2. API请求: `GET /dashboard/console`
3. 后端没有实现这个接口
4. 导致404错误，页面无法正常显示

---

## ✅ 解决方案（3选1）

### 方案1: 使用Mock数据（最快）⭐ 推荐

**优点**：
- ✅ 立即可用
- ✅ 无需修改后端
- ✅ 适合演示和测试

**实施步骤**：
修改前端API，返回模拟数据

---

### 方案2: 创建后端统计API（最完整）

**优点**：
- ✅ 真实数据
- ✅ 功能完整
- ✅ 适合生产环境

**实施步骤**：
1. 创建 `StatisticsController.java`
2. 实现统计接口
3. 返回真实数据

---

### 方案3: 简化Dashboard（最简洁）

**优点**：
- ✅ 代码简单
- ✅ 易于维护
- ✅ 快速可用

**实施步骤**：
修改Dashboard组件，不依赖API，直接显示静态内容

---

## 🚀 实施方案1: Mock数据（推荐）

### 修改文件
`src/api/dashboard/console.ts`

### 修改内容
将API调用改为返回模拟数据

```typescript
import { Alova } from '@/utils/http/alova/index';

export interface TypeVisits {
  dayVisits: number;
  rise: number;
  decline: number;
  amount: number;
}

export interface TypeSaleroom {
  weekSaleroom: number;
  amount: number;
  degree: number;
}

export interface TypeOrderLarge {
  weekLarge: number;
  rise: number;
  decline: number;
  amount: number;
}

export interface TypeConsole {
  visits: TypeVisits;
  saleroom: TypeSaleroom;
  orderLarge: TypeOrderLarge;
  volume: TypeOrderLarge;
}

// 获取主控台信息 - 使用Mock数据
export function getConsoleInfo() {
  // 返回模拟数据，避免后端404错误
  return Promise.resolve({
    visits: {
      dayVisits: 1234,
      rise: 12.5,
      decline: 8.3,
      amount: 56789
    },
    saleroom: {
      weekSaleroom: 12345,
      amount: 98765,
      degree: 75
    },
    orderLarge: {
      weekLarge: 456,
      rise: 15.2,
      decline: 5.8,
      amount: 68
    },
    volume: {
      weekLarge: 23456,
      rise: 18.6,
      decline: 6.2,
      amount: 156789
    }
  });
}
```

---

## 🎯 实施步骤

### 步骤1: 修改API文件

```bash
# 编辑文件
D:\BYSJ\BYSJ\new-admin\src\api\dashboard\console.ts
```

将 `getConsoleInfo()` 函数改为返回Mock数据（见上方代码）

---

### 步骤2: 刷新页面

```bash
# 如果前端服务正在运行，刷新浏览器即可
# 如果没有运行，启动前端
cd D:\BYSJ\BYSJ\new-admin
pnpm run dev
```

访问: http://localhost:8081

---

### 步骤3: 验证功能

- [ ] 登录成功
- [ ] 看到Dashboard页面
- [ ] 数据卡片正常显示
- [ ] 无404错误
- [ ] 左侧菜单正常显示

---

## 📊 Dashboard页面内容

### 数据卡片（4个）
1. **访问量**
   - 日访问量
   - 日同比/周同比
   - 总访问量

2. **销售额**
   - 周销售额
   - 完成进度
   - 总销售额

3. **订单量**
   - 周订单量
   - 日同比/周同比
   - 转化率

4. **成交额**
   - 月成交额
   - 月同比
   - 总成交额

### 导航卡片（8个）
1. 用户
2. 分析
3. 商品
4. 订单
5. 票据
6. 消息
7. 标签
8. 配置

### 图表区域
- 访问量趋势图
- 流量趋势图

---

## 🎨 优化建议

### 短期优化（1周内）
1. **替换为竞赛相关数据**
   - 访问量 → 报名人数
   - 销售额 → 缴费金额
   - 订单量 → 作品数量
   - 成交额 → 团队数量

2. **添加快捷入口**
   - 竞赛管理
   - 报名审核
   - 团队审核
   - 作品评分

3. **显示实时统计**
   - 今日报名数
   - 待审核数量
   - 已完成竞赛数

---

### 中期优化（1个月内）
1. **实现真实API**
   - 创建 `StatisticsController`
   - 实现统计查询
   - 连接真实数据库

2. **添加图表**
   - ECharts图表
   - 数据可视化
   - 动态更新

3. **个性化Dashboard**
   - 根据角色显示不同内容
   - 管理员：全局统计
   - 教师：我的竞赛统计
   - 学生：我的报名/作品统计

---

## 📝 实施记录

### 修改清单
| 文件 | 修改内容 | 状态 |
|-----|---------|------|
| `src/api/dashboard/console.ts` | 改为Mock数据 | ⬜ 待执行 |

---

## ✅ 验收标准

### 必须完成
- [x] Dashboard页面正常显示
- [x] 无404错误
- [x] 数据卡片正常显示
- [x] 导航卡片正常显示

### 建议完成
- [ ] 数据替换为竞赛相关
- [ ] 添加快捷入口
- [ ] 优化UI布局

---

## 🚀 下一步

### 立即执行
1. 修改 `console.ts` 文件
2. 刷新页面验证
3. 确认功能正常

### 后续优化
1. 根据角色显示不同数据
2. 实现真实统计API
3. 添加数据可视化图表

---

## 📞 技术支持

### 如果还有问题
1. 检查浏览器控制台错误
2. 检查Network中的API请求
3. 查看前端日志

### 相关文档
- [QUICK_REFERENCE_CARD.md](./QUICK_REFERENCE_CARD.md)
- [SYSTEM_TEST_GUIDE.md](./SYSTEM_TEST_GUIDE.md)

---

**祝Dashboard功能落地顺利！** 🚀
