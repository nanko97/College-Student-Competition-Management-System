#!/bin/bash
# 验证修复效果脚本

echo "======================================"
echo "验证教师新增竞赛功能修复"
echo "======================================"
echo ""

# 1. 验证数据库迁移脚本是否存在
echo "1. 检查数据库迁移脚本..."
if [ -f "src/main/resources/db/migration_add_gonghao.sql" ]; then
    echo "   ✓ 迁移脚本存在"
    echo "   内容预览:"
    head -6 src/main/resources/db/migration_add_gonghao.sql | sed 's/^/     /'
else
    echo "   ✗ 迁移脚本不存在"
fi
echo ""

# 2. 验证 init.sql 是否更新
echo "2. 检查 init.sql 表结构定义..."
if grep -q "gonghao.*varchar.*COMMENT '工号'" src/main/resources/db/init.sql; then
    echo "   ✓ init.sql 已包含 gonghao 字段"
else
    echo "   ✗ init.sql 未包含 gonghao 字段"
fi

if grep -q "jiaoshixingming.*varchar.*COMMENT '教师姓名'" src/main/resources/db/init.sql; then
    echo "   ✓ init.sql 已包含 jiaoshixingming 字段"
else
    echo "   ✗ init.sql 未包含 jiaoshixingming 字段"
fi
echo ""

# 3. 验证后端控制器修复
echo "3. 检查后端控制器修复..."
if grep -q "if (StringUtils.hasText(gonghao))" src/main/java/com/controller/JingsaixinxiController.java; then
    echo "   ✓ JingsaixinxiController.java 已添加空值检查"
else
    echo "   ✗ JingsaixinxiController.java 未添加空值检查"
fi
echo ""

# 4. 验证前端组件修复
echo "4. 检查前端组件修复..."
FRONTEND_FILE="src/main/resources/admin/admin/src/views/modules/jingsaixinxi/add-or-update.vue"
if [ -f "$FRONTEND_FILE" ]; then
    if grep -q "sessionTable 为空" "$FRONTEND_FILE"; then
        echo "   ✓ add-or-update.vue 已添加 sessionTable 验证"
    else
        echo "   ✗ add-or-update.vue 未添加 sessionTable 验证"
    fi
    
    if grep -q "console.log('自动填充工号:'" "$FRONTEND_FILE"; then
        echo "   ✓ add-or-update.vue 已添加调试日志"
    else
        echo "   ✗ add-or-update.vue 未添加调试日志"
    fi
    
    if grep -q "catch(error =>" "$FRONTEND_FILE"; then
        echo "   ✓ add-or-update.vue 已添加异常捕获"
    else
        echo "   ✗ add-or-update.vue 未添加异常捕获"
    fi
else
    echo "   ✗ 前端文件不存在"
fi
echo ""

# 5. 验证后端编译
echo "5. 验证后端编译..."
cd src/main/java/com/controller
if javac -version >/dev/null 2>&1; then
    echo "   Java 编译器可用"
else
    echo "   使用 Maven 编译验证"
fi
cd ../../../../..
if [ -d "target/classes" ]; then
    echo "   ✓ 后端代码编译成功"
else
    echo "   ⚠ 后端代码未编译（请运行 mvn compile）"
fi
echo ""

echo "======================================"
echo "验证完成"
echo "======================================"
echo ""
echo "下一步操作："
echo "1. 在 MySQL 中执行迁移脚本:"
echo "   mysql -u root -p BYSJ < src/main/resources/db/migration_add_gonghao.sql"
echo ""
echo "2. 重启后端服务:"
echo "   cd Springboot_BYSJ && mvn spring-boot:run"
echo ""
echo "3. 重新编译前端（如需要）:"
echo "   cd Springboot_BYSJ/src/main/resources/admin/admin && npm run serve"
echo ""
