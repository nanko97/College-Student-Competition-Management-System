# 🚨 紧急修复步骤 - 教师新增竞赛 500 错误

## 问题原因
数据库表 `jingsaixinxi` 缺少 `gonghao`（工号）和 `jiaoshixingming`（教师姓名）字段。

## ✅ 解决方案（3 选 1）

### 方案 1：使用 Navicat/MySQL Workbench 执行 SQL（推荐）

1. 打开 Navicat 或 MySQL Workbench
2. 连接到本地 MySQL 数据库
3. 数据库信息：
   - 主机：127.0.0.1
   - 端口：3306
   - 用户名：root
   - 密码：123123
   - 数据库名：springbootrd362

4. 新建查询，执行以下 SQL：

```sql
USE springbootrd362;

-- 添加工号字段
ALTER TABLE `jingsaixinxi` 
ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`;

-- 添加教师姓名字段
ALTER TABLE `jingsaixinxi` 
ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;

-- 验证是否成功
DESCRIBE jingsaixinxi;
```

5. 如果看到 `gonghao` 和 `jiaoshixingming` 字段，说明成功！

---

### 方案 2：使用命令行（如果安装了 MySQL）

1. 按 `Win + R`，输入 `cmd`，回车
2. 进入 MySQL bin 目录（根据实际安装路径修改）：
```cmd
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
```

3. 登录 MySQL：
```cmd
mysql -u root -p123123 springbootrd362
```

4. 执行 SQL：
```sql
ALTER TABLE `jingsaixinxi` 
ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`,
ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;

SHOW COLUMNS FROM jingsaixinxi;
EXIT;
```

---

### 方案 3：使用项目自带的脚本（如果有 MySQL 命令行工具）

在项目根目录执行：
```bash
cd Springboot_BYSJ
mysql -h 127.0.0.1 -P 3306 -u root -p123123 springbootrd362 < src/main/resources/db/fix_jingsaixinxi.sql
```

---

## ✅ 修复后测试

### 1. 重启后端服务
```bash
cd Springboot_BYSJ
mvn spring-boot:run
```

等待看到 `Started SpringbootSchemaApplication` 表示启动成功。

### 2. 重新编译前端（可选）
```bash
cd Springboot_BYSJ/src/main/resources/admin/admin
npm run serve
```

### 3. 测试步骤
1. 打开浏览器，访问：http://localhost:8080/springbootrd362/admin/dist/index.html
2. 使用教师账号登录
3. 进入"竞赛信息"菜单
4. 点击"新增"按钮
5. 填写表单并提交
6. 应该显示"操作成功"，不再出现 500 错误！

---

## 🔍 验证修复是否成功

### 方法 1：查看表结构
在数据库中执行：
```sql
DESCRIBE jingsaixinxi;
```

应该看到以下字段：
- ✅ gonghao (varchar(20))
- ✅ jiaoshixingming (varchar(50))

### 方法 2：查看后端日志
启动后端后，观察控制台是否有以下日志：
```
INFO  ... JingsaixinxiController - 保存竞赛请求数据：...
INFO  ... JingsaixinxiController - 教师 XXX 发布新竞赛：XXX
INFO  ... JingsaixinxiController - 保存竞赛信息成功
```

### 方法 3：F12 控制台
提交表单时，按 F12 打开开发者工具：
- Network 标签页应该看到 `save` 请求返回 `{code: 0, msg: "保存成功"}`
- Console 标签页应该看到 `自动填充工号：XXX` 的日志

---

## ❌ 如果还是不行

### 检查点 1：数据库是否正确
确认连接的是 `springbootrd362` 数据库，不是其他数据库。

### 检查点 2：字段是否已存在
如果执行 SQL 时报错"字段已存在"，说明之前已经添加过了，可以直接跳过此步骤。

### 检查点 3：后端是否重启
修改数据库后必须重启后端服务才能生效！

### 检查点 4：Token 是否有效
清除浏览器缓存，重新登录后再试。

---

## 📞 需要帮助？

如果以上步骤都无法解决问题，请提供：
1. 执行 SQL 时的错误信息（截图）
2. 后端控制台的完整日志（特别是错误堆栈）
3. F12 控制台的 Network 请求详情（红色错误的 Response）

---

**最后更新**: 2026-03-08
**修复文件**: 
- `src/main/resources/db/migration_add_gonghao.sql`
- `src/main/java/com/controller/JingsaixinxiController.java`
- `src/main/resources/admin/admin/src/views/modules/jingsaixinxi/add-or-update.vue`
