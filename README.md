# 大学生竞赛管理系统

基于 Spring Boot + Vue 的大学生竞赛管理平台，支持竞赛发布、学生报名、作品提交、教师打分、缴费审核等全流程管理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.x |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 2.x + Element UI |
| 构建工具 | Maven |

## 快速开始

### 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+
- Node.js 14+

### 后端启动

```bash
# 1. 导入数据库
mysql -u root -p < bysj_backup.sql

# 2. 修改数据库配置
# 编辑 src/main/resources/application.yml 中的数据库连接信息

# 3. 启动项目
cd Springboot_BYSJ
mvn spring-boot:run
```

后端默认运行在 `http://localhost:9090/BYSJ_Springboot`

### 前端启动

```bash
cd old-frontend
npm install
npm run serve
```

前端默认运行在 `http://localhost:8080`

## 测试账号

> 所有账号默认密码：`123456`

### 管理员

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |

### 教师

| 工号 | 姓名 | 密码 |
|------|------|------|
| T2022001 | 李明德 | 123456 |
| T2022002 | 王秀英 | 123456 |
| T2022003 | 张伟强 | 123456 |

### 学生

| 学号 | 姓名 | 密码 |
|------|------|------|
| 2022001 | 张志强 | 123456 |
| 2022002 | 李雨婷 | 123456 |
| 2022003 | 王浩然 | 123456 |
| 2022004 | 刘思琪 | 123456 |
| 2022005 | 陈俊杰 | 123456 |

## 功能模块

- **竞赛管理**：竞赛发布、编辑、删除，支持付费/免费两种缴费模式
- **报名管理**：学生在线报名，教师审核
- **缴费管理**：缴费凭证上传、审核（待审核/已通过/已驳回）
- **作品管理**：作品提交、下载、打分
- **成绩查看**：学生查看各竞赛成绩与排名
- **团队管理**：团队创建、成员变更
- **消息通知**：审核结果、缴费状态等消息推送
- **数据展板**：竞赛统计数据可视化

## 项目结构

```
├── Springboot_BYSJ/        # 后端项目
│   └── src/main/java/com/  # Java 源码
│       ├── controller/     # 控制器
│       ├── service/        # 业务逻辑
│       ├── entity/         # 实体类
│       └── config/         # 配置类
├── old-frontend/           # 前端项目
│   └── src/
│       ├── views/          # 页面组件
│       ├── api/            # API 接口
│       └── components/     # 公共组件
└── deploy/                 # 部署配置
    ├── nginx-bysj.conf     # Nginx 配置
    └── bysj.service        # Systemd 服务文件
```
