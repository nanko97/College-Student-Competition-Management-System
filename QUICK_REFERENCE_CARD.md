# BYSJ项目 - 快速参考卡

**最后更新**: 2026-05-15  
**版本**: v1.0

---

## 🚀 快速启动

### 启动后端
```bash
cd Springboot_BYSJ
mvn spring-boot:run
```
访问: http://localhost:9090/BYSJ_Springboot

### 启动前端
```bash
cd new-admin
pnpm run dev
```
访问: http://localhost:8081

---

## 🔑 默认账号

| 角色 | 账号 | 密码 |
|-----|------|------|
| 管理员 | admin | admin |
| 教师 | jiaoshi001 | 123456 |
| 学生 | 2022001 | 123456 |

---

## 📁 项目结构

```
BYSJ/
├── Springboot_BYSJ/          # 后端
│   ├── src/main/java/com/
│   │   ├── controller/       # 控制器（23个）
│   │   ├── service/          # 服务层（26个）
│   │   ├── entity/           # 实体类（20个）
│   │   └── mapper/           # 数据访问（20个）
│   └── src/main/resources/
│       ├── application.yml   # 配置文件
│       └── db/               # SQL脚本
│
├── new-admin/                # 前端
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── views/            # 页面组件
│   │   ├── router/           # 路由配置
│   │   └── utils/            # 工具函数
│   └── package.json
│
└── docs/                     # 文档
    └── *.md                  # 各种文档
```

---

## 🔧 常用命令

### 后端
```bash
# 启动
mvn spring-boot:run

# 打包
mvn clean package

# 测试
mvn test
```

### 前端
```bash
# 开发模式
pnpm run dev

# 构建
pnpm run build

# 预览
pnpm run preview

# 代码检查
pnpm run lint
```

---

## 📊 核心模块

| 模块 | 路径 | 权限 |
|-----|------|------|
| 竞赛信息 | /competition/info | admin/jiaoshi/xuesheng |
| 报名管理 | /competition/registration | admin/jiaoshi/xuesheng |
| 缴费管理 | /competition/fee | admin/jiaoshi/xuesheng |
| 费用配置 | /competition/fee-config | admin |
| 团队列表 | /team/list | admin/jiaoshi/xuesheng |
| 人员变更 | /team/change | admin/jiaoshi/xuesheng |
| 我的团队 | /student/my-team | xuesheng |
| 作品管理 | /work/list | admin/jiaoshi/xuesheng |
| 评分管理 | /score/list | admin/jiaoshi |
| 晋级管理 | /promotion/list | admin/jiaoshi/xuesheng |

---

## 🔌 API接口规范

### 请求格式
```typescript
// GET请求
Alova.Get('/api/path', { 
  params: { key: value },
  meta: { isTransformResponse: false } 
})

// POST请求
Alova.Post('/api/path', data, { 
  meta: { isTransformResponse: false } 
})
```

### 响应格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {},
  "page": {
    "list": [],
    "total": 100,
    "pageSize": 10,
    "currPage": 1
  }
}
```

---

## 🎨 字段命名规范

### ✅ 正确示例（下划线命名）
```typescript
interface TeamData {
  id: number
  tuandui_mingcheng: string      // 团队名称
  fuzeren_xuehao: string         // 负责人学号
  shenhe_zhuangtai: string       // 审核状态
}
```

### ❌ 错误示例（驼峰命名）
```typescript
interface TeamData {
  id: number
  tuanduiMingcheng: string       // 错误！
  fuzerenXuehao: string          // 错误！
  shenheZhuangtai: string        // 错误！
}
```

---

## 🔐 权限控制

### 路由权限
```typescript
{
  path: 'example',
  meta: { 
    roles: ['admin', 'jiaoshi']  // 允许的角色
  }
}
```

### 按钮权限
```vue
<n-button v-if="userRole === 'admin'">
  管理员操作
</n-button>
```

---

## 🐛 常见问题

### Q1: 前端无法连接后端？
**A**: 检查 `vite.config.ts` 中的代理配置
```typescript
server: {
  proxy: {
    '/BYSJ_Springboot': {
      target: 'http://localhost:9090',
      changeOrigin: true
    }
  }
}
```

---

### Q2: Token过期怎么办？
**A**: 重新登录获取新Token
```typescript
localStorage.removeItem('token')
router.push('/login')
```

---

### Q3: 403 Forbidden？
**A**: 检查用户角色是否有权限访问该路由

---

### Q4: 数据不显示？
**A**: 检查以下几点
1. 浏览器控制台是否有错误
2. Network中API请求是否成功
3. 返回的数据格式是否正确
4. 字段名是否与后端一致

---

### Q5: 如何添加新功能？
**A**: 遵循标准流程
1. 后端: Entity → Mapper → Service → Controller
2. 前端: API → Columns → Vue组件
3. 确保字段名统一（下划线命名）
4. 配置路由和权限
5. 测试功能

---

## 📝 开发清单

### 新增CRUD功能
- [ ] 创建Entity类
- [ ] 创建Mapper接口
- [ ] 创建Service接口和实现
- [ ] 创建Controller
- [ ] 编写SQL建表语句
- [ ] 重启后端服务
- [ ] 创建前端API接口
- [ ] 创建Columns定义
- [ ] 创建Vue组件
- [ ] 配置路由
- [ ] 测试功能

---

### 修复Bug
- [ ] 复现问题
- [ ] 定位原因
- [ ] 修复代码
- [ ] 测试验证
- [ ] 记录到文档

---

## 🔍 调试技巧

### 前端调试
```javascript
// 查看请求数据
console.log('Request:', data)

// 查看响应数据
console.log('Response:', response)

// 查看用户信息
console.log('User:', JSON.parse(localStorage.getItem('userInfo')))

// 查看Token
console.log('Token:', localStorage.getItem('token'))
```

### 后端调试
```java
// 日志输出
log.info("参数: {}", param);
log.error("异常: ", e);

// 断点调试
// 在IDEA中设置断点
```

---

## 📚 重要文档

| 文档 | 说明 |
|-----|------|
| FINAL_PROJECT_SUMMARY.md | 项目总结报告 |
| FEATURE_GAP_ANALYSIS.md | 功能缺失分析 |
| FIRST_PHASE_FINAL_COMPLETION.md | 第一阶段完成报告 |
| CHANGE_AUDIT_COMPLETION_REPORT.md | 人员变更完成报告 |
| TEAM_CREATE_COMPLETION_REPORT.md | 团队创建完成报告 |
| FIELD_NAMING_COMPLETION_CONFIRMATION.md | 字段命名确认 |
| PROJECT_STATUS_SUMMARY.md | 项目状态总结 |

---

## 💡 最佳实践

### 1. 代码规范
- ✅ 使用TypeScript类型定义
- ✅ 字段名与后端保持一致
- ✅ 添加必要的注释
- ✅ 遵循组件化设计

### 2. 性能优化
- ✅ 使用懒加载
- ✅ 避免不必要的重渲染
- ✅ 合理使用缓存
- ✅ 优化API请求

### 3. 安全防护
- ✅ 所有敏感操作需要Token
- ✅ 前端进行表单验证
- ✅ 后端进行权限校验
- ✅ XSS防护（HTML转义）

### 4. 用户体验
- ✅ 提供友好的错误提示
- ✅ 显示加载状态
- ✅ 支持键盘操作
- ✅ 响应式设计

---

## 🎯 下一步计划

### 短期（1周内）
- [ ] 系统测试
- [ ] Bug修复
- [ ] 论文修改

### 中期（1个月内）
- [ ] 数据统计API
- [ ] 团队成员管理
- [ ] 变更历史查看

### 长期（持续）
- [ ] 性能优化
- [ ] 功能扩展
- [ ] 技术分享

---

## 📞 联系方式

- **项目地址**: D:\BYSJ\BYSJ
- **前端端口**: 8081
- **后端端口**: 9090
- **数据库**: MySQL 8.0.28

---

**祝开发顺利！** 🚀
