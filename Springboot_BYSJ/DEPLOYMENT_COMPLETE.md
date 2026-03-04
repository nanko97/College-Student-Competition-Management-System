# 🎉 竞赛管理系统 - 部署完成指南

## ✅ 部署状态

| 步骤 | 状态 | 说明 |
|------|------|------|
| 1. Node.js 环境检查 | ✅ 完成 | Node.js v22.12.0 已安装 |
| 2. 前端依赖安装 | ✅ 完成 | node_modules 已安装 |
| 3. 前端构建 | ✅ 完成 | dist 目录已生成 |
| 4. 数据库脚本准备 | ✅ 完成 | init.sql 已创建 |
| 5. 后端编译打包 | ✅ 完成 | springbootrd362.jar (78MB) |
| 6. 应用启动 | ⏳ 待执行 | 需要手动启动 |

---

## 📁 已生成的文件

### 后端文件
```
target/
└── springbootrd362.jar (78,317,469 字节)  ✅
```

### 前端文件
```
src/main/resources/admin/admin/dist/
├── index.html
├── favicon.ico
├── css/
├── js/
├── fonts/
└── img/
```

### 配置文件
```
src/main/resources/
├── application.yml              # 应用配置
└── db/
    ├── init.sql                 # 完整数据库初始化脚本
    └── migration/
        └── V1__add_user_roles.sql  # 迁移脚本
```

---

## 🚀 快速启动指南

### 步骤 1: 初始化数据库

**重要**: 首次运行前必须执行此步骤！

#### 方式 A: 使用 MySQL 命令行
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

#### 方式 B: 使用 MySQL Workbench 或其他 GUI 工具
1. 打开 `src/main/resources/db/init.sql`
2. 执行整个 SQL 脚本
3. 验证表是否创建成功

#### 方式 C: 手动执行
```sql
-- 登录 MySQL
mysql -u root -p

-- 执行脚本
source C:/Users/Nanko/Desktop/BYSJ/BYSJ/Springboot_BYSJ/src/main/resources/db/init.sql;
```

### 步骤 2: 启动应用

#### 方式 A: 直接运行 JAR (推荐)
```bash
cd c:\Users\Nanko\Desktop\BYSJ\BYSJ\Springboot_BYSJ
java -jar target/springbootrd362.jar
```

#### 方式 B: 使用 Maven 启动
```bash
cd c:\Users\Nanko\Desktop\BYSJ\BYSJ\Springboot_BYSJ
mvn spring-boot:run
```

#### 方式 C: Windows 批处理启动
创建 `start.bat`:
```batch
@echo off
echo 正在启动竞赛管理系统...
cd /d %~dp0
java -jar target/springbootrd362.jar
pause
```

### 步骤 3: 访问系统

应用启动后，打开浏览器访问:

**登录页面**: 
```
http://localhost:8080/springbootrd362/admin/dist/index.html#/login
```

**注册页面**:
```
http://localhost:8080/springbootrd362/admin/dist/index.html#/register
```

---

## 👤 默认账号

### 管理员账号
- **账号**: admin
- **密码**: admin123
- **角色**: 管理员

### 测试账号 (需手动创建)

可以通过注册页面创建测试账号:
1. 访问注册页面
2. 选择角色 (学生/教师)
3. 填写信息并提交
4. 使用注册的账号登录

---

## 🔧 配置说明

### 数据库配置
编辑 `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/springbootrd362?...
    username: root           # 修改为你的数据库用户名
    password: YOUR_PASSWORD  # 修改为你的数据库密码
```

### 端口配置
```yaml
server:
  port: 8080              # 修改此处可更改端口
  servlet:
    context-path: /springbootrd362
```

---

## 📊 功能验证清单

### 基础功能
- [ ] 能够访问登录页面
- [ ] 能够访问注册页面
- [ ] 管理员可以登录 (admin/admin123)
- [ ] 页面样式正常显示
- [ ] 响应式布局正常

### 注册功能
- [ ] 可以选择学生/教师角色
- [ ] 账号格式验证正常
- [ ] 密码强度验证正常
- [ ] 账号可用性检查正常
- [ ] 注册成功后跳转到登录页

### 登录功能
- [ ] 三角色切换正常
- [ ] 学生可以登录
- [ ] 教师可以登录
- [ ] 管理员可以登录
- [ ] 登录成功后跳转到首页
- [ ] 记住我功能正常

### 权限控制
- [ ] 学生只能访问学生相关接口
- [ ] 教师可以访问教师相关接口
- [ ] 管理员可以访问所有接口
- [ ] 未登录访问返回 401
- [ ] 越权访问返回 403

---

## 🐛 常见问题解决

### 问题 1: 数据库连接失败
**错误**: `Communications link failure`

**解决方案**:
1. 确认 MySQL 服务已启动
2. 检查数据库用户名密码是否正确
3. 确认数据库 `springbootrd362` 已创建

### 问题 2: 端口被占用
**错误**: `Port 8080 was already in use`

**解决方案**:
```yaml
server:
  port: 8081  # 改为其他端口
```

### 问题 3: 前端页面无法加载
**症状**: 空白页或 404 错误

**解决方案**:
1. 确认前端已构建：检查 `target/classes/admin/admin/dist` 目录
2. 重新构建前端：`npm run build`
3. 清理 Maven 缓存：`mvn clean package`

### 问题 4: 注册失败
**错误**: "账号已存在" 或 "注册失败"

**解决方案**:
1. 检查数据库中是否已有该账号
2. 查看应用日志获取详细错误信息
3. 确认数据库表结构正确

### 问题 5: 登录失败
**错误**: "账号或密码不正确"

**解决方案**:
1. 确认使用的是 BCrypt 加密的密码
2. 旧数据需要重置密码
3. 使用默认管理员账号测试：admin/admin123

---

## 📝 日志查看

应用启动后，日志会输出到控制台。如需保存日志:

```bash
java -jar target/springbootrd362.jar > app.log 2>&1
```

日志文件位置：`logs/springbootrd362.log`

---

## 🎯 下一步操作

### 立即开始
1. ✅ 执行数据库初始化脚本
2. ✅ 启动应用
3. ✅ 访问登录页面
4. ✅ 测试注册功能
5. ✅ 测试登录功能

### 后续优化
1. 修改默认管理员密码
2. 创建更多测试账号
3. 配置 Redis 缓存 (可选)
4. 部署到生产环境

---

## 📞 技术支持

如遇到问题，请检查:
1. Java 版本：`java -version` (应为 1.8)
2. Maven 版本：`mvn -version` (应为 3.6+)
3. MySQL 版本：`mysql --version` (应为 5.7+)
4. Node.js 版本：`node -v` (应为 14+)

---

## 🎊 恭喜！

部署完成后，您将拥有一个完整的、现代化的竞赛管理系统，包含:
- ✨ 美观的登录/注册界面
- 🔒 安全的 BCrypt 密码加密
- 🎯 完善的权限控制
- 📱 响应式设计支持移动端
- 🚀 高性能数据库连接池

**祝您使用愉快！** 🎉
