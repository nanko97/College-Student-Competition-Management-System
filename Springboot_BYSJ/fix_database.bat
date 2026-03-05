@echo off
chcp 65001 >nul
echo =============================================
echo 数据库修复工具 - 添加 role 字段
echo =============================================
echo.
echo 请在 MySQL 命令行或图形化工具中执行以下 SQL:
echo.
echo USE springbootrd362;
echo.
echo ALTER TABLE xuesheng ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '学生' COMMENT '角色';
echo ALTER TABLE jiaoshi ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT '教师' COMMENT '角色';
echo.
echo =============================================
echo 或者打开 fix_database.sql 文件，复制内容到 MySQL 执行
echo =============================================
pause
