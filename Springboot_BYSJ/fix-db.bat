@echo off
chcp 65001 >nul
echo ========================================
echo 修复 jingsaixinxi 表结构
echo ========================================
echo.

REM MySQL 配置
set MYSQL_HOST=127.0.0.1
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PWD=123123
set MYSQL_DB=springbootrd362

echo 正在查找 MySQL 命令行工具...
where mysql >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo ✓ 找到 MySQL 客户端
    echo.
    echo 正在执行 SQL 迁移...
    mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < src\main\resources\db\fix_jingsaixinxi.sql
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ========================================
        echo ✅ 数据库修复成功！
        echo ========================================
        echo.
        echo 下一步操作：
        echo 1. 重启后端服务
        echo 2. 测试新增竞赛功能
        echo.
    ) else (
        echo.
        echo ========================================
        echo ❌ SQL 执行失败
        echo ========================================
        echo.
        echo 请手动执行以下 SQL:
        echo USE %MYSQL_DB%;
        echo ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar^(20^) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`;
        echo ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar^(50^) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;
        echo.
    )
) else (
    echo ✗ 未找到 MySQL 客户端
    echo.
    echo ========================================
    echo 请使用以下方式之一执行修复：
    echo ========================================
    echo.
    echo 方式 1：使用 Navicat/MySQL Workbench
    echo   1. 打开 Navicat 或 MySQL Workbench
    echo   2. 连接到本地 MySQL
    echo   3. 执行以下 SQL:
    echo.
    echo   USE %MYSQL_DB%;
    echo   ALTER TABLE `jingsaixinxi` ADD COLUMN `gonghao` varchar^(20^) DEFAULT NULL COMMENT '工号' AFTER `fengmiantupian`;
    echo   ALTER TABLE `jingsaixinxi` ADD COLUMN `jiaoshixingming` varchar^(50^) DEFAULT NULL COMMENT '教师姓名' AFTER `gonghao`;
    echo.
    echo 方式 2：安装 MySQL 客户端后重新运行此脚本
    echo   下载地址：https://dev.mysql.com/downloads/mysql/
    echo.
    echo 详细说明请查看：FIX_DATABASE.md
    echo.
)

pause
