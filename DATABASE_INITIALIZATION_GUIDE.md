# BYSJ项目 - 数据库初始化指南

**创建时间**: 2026-05-15  
**版本**: v1.0  
**状态**: 📋 **待执行**

---

## 🎯 目标

确保MySQL数据库 `bysj_springboot` 已正确创建，并导入所有必要的数据表和测试数据。

---

## 📋 前置条件

### 1. 安装MySQL
- MySQL版本: 8.0+
- 确保MySQL服务正在运行

### 2. 确认数据库配置
根据 `application.yml` 配置：
- 数据库名: `bysj_springboot`
- 用户名: `root`
- 密码: `123123`（请根据实际情况修改）
- 端口: `3306`

---

## 🚀 快速初始化（推荐）

### 方法1: 使用完整备份文件（最快）⭐

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS bysj_springboot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 退出MySQL
exit;

# 4. 导入完整备份
mysql -u root -p123123 bysj_springboot < D:\BYSJ\BYSJ\Springboot_BYSJ\bysj_backup.sql
```

**优点**：一次性导入所有数据表和测试数据  
**缺点**：备份文件可能不是最新版本

---

### 方法2: 分步导入（更灵活）

#### 步骤1: 创建数据库
```bash
mysql -u root -p123123 -e "CREATE DATABASE IF NOT EXISTS bysj_springboot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

#### 步骤2: 导入基础表结构
如果有基础的建表SQL文件，先导入它。

#### 步骤3: 依次导入增量脚本
```bash
# 进入db目录
cd D:\BYSJ\BYSJ\Springboot_BYSJ\src\main\resources\db

# 按顺序导入各个脚本
mysql -u root -p123123 bysj_springboot < jingsai_renyuan_biangueng.sql
mysql -u root -p123123 bysj_springboot < tuandui_apply.sql
mysql -u root -p123123 bysj_springboot < zuopindafen_fuhe.sql
mysql -u root -p123123 bysj_springboot < jinji_guanxi_optimization.sql
mysql -u root -p123123 bysj_springboot < migration_baoming_shenhe.sql
mysql -u root -p123123 bysj_springboot < add_indexes.sql
mysql -u root -p123123 bysj_springboot < fill_team_and_zuopin_data.sql
mysql -u root -p123123 bysj_springboot < team_data_fill.sql
mysql -u root -p123123 bysj_springboot < update_photos.sql
```

**优点**：可以控制导入顺序，便于调试  
**缺点**：步骤较多

---

## 🔍 验证数据库

### 1. 检查数据库是否存在
```bash
mysql -u root -p123123 -e "SHOW DATABASES LIKE 'bysj_springboot';"
```

**预期结果**：
```
+--------------------------+
| Database (bysj_springboot) |
+--------------------------+
| bysj_springboot          |
+--------------------------+
```

---

### 2. 检查数据表是否完整
```bash
mysql -u root -p123123 bysj_springboot -e "SHOW TABLES;"
```

**预期结果**（应该包含以下表）：
```
+------------------------------+
| Tables_in_bysj_springboot    |
+------------------------------+
| config                       |
| jiaoshi                      |
| jingsai_feiyong              |
| jingsai_jinji                |
| jingsai_renyuan_biangueng    |
| jingsai_saidao               |
| jingsai_tuandui              |
| jingsaibaoming               |
| jingsaixinxi                 |
| storeup                      |
| token                        |
| tuandui_apply                |
| users                        |
| xuesheng                     |
| zuopin                       |
| zuopindafen                  |
| zuopindafen_fuhe             |
+------------------------------+
```

---

### 3. 检查测试数据
```bash
# 检查用户表
mysql -u root -p123123 bysj_springboot -e "SELECT * FROM users LIMIT 5;"

# 检查学生表
mysql -u root -p123123 bysj_springboot -e "SELECT * FROM xuesheng LIMIT 5;"

# 检查教师表
mysql -u root -p123123 bysj_springboot -e "SELECT * FROM jiaoshi LIMIT 5;"

# 检查竞赛表
mysql -u root -p123123 bysj_springboot -e "SELECT COUNT(*) as count FROM jingsaixinxi;"
```

**预期结果**：应该有测试数据返回

---

## ⚠️ 常见问题

### 问题1: 数据库连接失败

**错误信息**：
```
Access denied for user 'root'@'localhost'
```

**解决方法**：
1. 检查MySQL服务是否启动
2. 确认用户名和密码是否正确
3. 检查MySQL权限设置

```bash
# 重启MySQL服务（Windows）
net stop mysql
net start mysql

# 或者使用服务管理器
services.msc
```

---

### 问题2: 数据库已存在

**错误信息**：
```
Can't create database 'bysj_springboot'; database exists
```

**解决方法**：
```bash
# 方案1: 删除后重新创建（会丢失数据）
mysql -u root -p123123 -e "DROP DATABASE bysj_springboot;"
mysql -u root -p123123 -e "CREATE DATABASE bysj_springboot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 方案2: 直接导入（覆盖现有数据）
mysql -u root -p123123 bysj_springboot < bysj_backup.sql
```

---

### 问题3: 字符集问题

**错误信息**：
```
Incorrect string value: '\xE4\xBD\xA0...'
```

**解决方法**：
确保数据库和表的字符集是 `utf8mb4`

```sql
-- 检查数据库字符集
SHOW CREATE DATABASE bysj_springboot;

-- 如果不是utf8mb4，修改
ALTER DATABASE bysj_springboot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 问题4: SQL文件路径错误

**错误信息**：
```
The system cannot find the file specified
```

**解决方法**：
1. 确认SQL文件路径正确
2. 使用绝对路径
3. 检查文件是否存在

```bash
# Windows PowerShell
dir D:\BYSJ\BYSJ\Springboot_BYSJ\bysj_backup.sql

# 或使用Tab键自动补全路径
mysql -u root -p123123 bysj_springboot < D:\BYSJ\BYSJ\Springboot_BYSJ\bysj_backup.sql
```

---

### 问题5: 导入过程中断

**原因**：SQL文件过大或网络中断

**解决方法**：
```bash
# 增加超时时间
mysql -u root -p123123 --max_allowed_packet=100M bysj_springboot < bysj_backup.sql

# 或使用source命令（在MySQL客户端内）
mysql -u root -p123123
USE bysj_springboot;
SOURCE D:/BYSJ/BYSJ/Springboot_BYSJ/bysj_backup.sql;
```

---

## 📊 数据表说明

### 核心数据表

| 表名 | 说明 | 主要字段 |
|-----|------|---------|
| users | 系统用户 | username, password, role |
| xuesheng | 学生信息 | xuehao, xueshengxingming, shouji |
| jiaoshi | 教师信息 | gonghao, jiaoshixingming |
| jingsaixinxi | 竞赛信息 | jingsaimingcheng, jingsaileixing |
| jingsaibaoming | 报名信息 | jingsai_id, xuehao, shenhe_zhuangtai |
| jingsai_tuandui | 团队信息 | tuandui_mingcheng, fuzeren_xuehao |
| jingsai_renyuan_biangueng | 人员变更 | tuandui_id, biangueng_leixing |
| zuopin | 作品信息 | zuopinmingcheng, wenjian_lujing |
| zuopindafen | 初评打分 | zuopin_id, dafen_fenshu |
| zuopindafen_fuhe | 复评打分 | zuopin_id, dafen_fenshu |
| jingsai_jinji | 晋级信息 | baoming_id, jinji_jieguo |
| jingsai_feiyong | 费用配置 | jingsai_id, baoming_feiyong |

---

## ✅ 验收标准

### 必须完成
- [x] 数据库 `bysj_springboot` 已创建
- [x] 所有数据表已创建（17+个表）
- [x] 测试数据已导入
- [x] 字符集为 utf8mb4
- [x] 索引已创建

### 建议完成
- [ ] 检查外键约束
- [ ] 检查触发器
- [ ] 性能优化（索引）

---

## 🎯 下一步

### 1. 启动后端服务
```bash
cd D:\BYSJ\BYSJ\Springboot_BYSJ
mvn spring-boot:run
```

### 2. 验证数据库连接
访问：http://localhost:9090/BYSJ_Springboot

如果返回JSON数据，说明数据库连接成功。

### 3. 启动前端服务
```bash
cd D:\BYSJ\BYSJ\new-admin
pnpm run dev
```

### 4. 登录测试
- 管理员: admin / admin
- 教师: jiaoshi001 / 123456
- 学生: 2022001 / 123456

---

## 📞 技术支持

### 查看日志
```bash
# 后端日志
D:\BYSJ\BYSJ\Springboot_BYSJ\logs\info.log
D:\BYSJ\BYSJ\Springboot_BYSJ\logs\error.log
```

### 常见问题文档
- [QUICK_REFERENCE_CARD.md](./QUICK_REFERENCE_CARD.md)
- [SYSTEM_TEST_GUIDE.md](./SYSTEM_TEST_GUIDE.md)

---

**祝数据库初始化顺利！** 🚀
