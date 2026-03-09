# 🔍 问题诊断清单

## 请按顺序执行以下步骤，并告诉我每一步的结果

### 步骤 1：验证数据库表结构

在 Navicat 或 MySQL Workbench 中执行：

```sql
USE springbootrd362;
DESCRIBE jingsaixinxi;
```

**检查是否有以下两个字段**：
- [ ] `gonghao` (varchar(20))
- [ ] `jiaoshixingming` (varchar(50))

如果**没有**这两个字段，请执行：

```sql
ALTER TABLE `jingsaixinxi` 
ADD COLUMN `gonghao` varchar(20) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`,
ADD COLUMN `jiaoshixingming` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;
```

---

### 步骤 2：重启后端服务

1. 停止当前运行的后端服务（Ctrl+C）
2. 重新编译并启动：

```bash
cd c:\Users\Nanko\Desktop\BYSJ\BYSJ\Springboot_BYSJ
mvn clean spring-boot:run
```

3. 等待看到 `Started SpringbootSchemaApplication` 表示启动成功

---

### 步骤 3：清除浏览器缓存

1. 按 `Ctrl + Shift + Delete`
2. 选择"全部时间"
3. 勾选"缓存的图片和文件"
4. 点击"清除数据"
5. **关闭浏览器重新打开**

---

### 步骤 4：重新登录

1. 访问：http://localhost:8080/springbootrd362/admin/dist/index.html
2. 使用教师账号登录
3. 登录后，按 F12 打开开发者工具
4. 切换到 **Console** 标签页

---

### 步骤 5：测试新增竞赛

1. 进入"竞赛信息"菜单
2. 点击"新增"按钮
3. 填写表单（**只填写竞赛名称即可**）
4. 点击"提交"

---

### 步骤 6：查看错误信息

#### A. Console 标签页
截图或复制所有**红色**的错误信息

#### B. Network 标签页
1. 点击 Network 标签
2. 找到红色的 `save` 请求（应该是 POST 请求）
3. 点击它
4. 查看 **Response** 标签页
5. 复制所有内容

#### C. 后端日志
查看 IDEA 控制台的输出，找到类似以下的日志：

```
INFO  ... JingsaixinxiController - 保存竞赛请求数据：...
INFO  ... JingsaixinxiController - 当前会话信息 - tableName: ..., username: ...
ERROR ... JingsaixinxiController - 保存竞赛信息异常：...
```

---

## 📋 请提供以下信息

完成后请告诉我：

1. **步骤 1 的结果**：表中是否有 `gonghao` 和 `jiaoshixingming` 字段？
2. **Console 错误**：F12 Console 标签页的完整错误信息
3. **Network 响应**：save 请求的 Response 内容
4. **后端日志**：IDEA 控制台的错误堆栈

---

## 🐛 常见错误及解决方案

### 错误 1：401 Unauthorized
**原因**：Token 失效或未登录  
**解决**：清除缓存，重新登录

### 错误 2：500 Internal Server Error
**原因**：数据库字段不存在或后端代码问题  
**解决**：确认步骤 1 已正确执行，后端已重启

### 错误 3：AMap is not defined
**原因**：高德地图 API 加载失败（与竞赛功能无关）  
**解决**：忽略此错误，不影响竞赛功能

### 错误 4：Request failed with status code 500
**原因**：后端处理出错  
**解决**：查看后端日志，找到具体错误原因

---

**请先完成上述步骤，然后提供错误信息，我会帮你进一步诊断！**
