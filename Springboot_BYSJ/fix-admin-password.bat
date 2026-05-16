@echo off
chcp 65001 >nul
echo ========================================
echo 修复管理员密码
echo ========================================
echo.
echo 正在连接数据库并更新管理员密码...
echo.

mysql -u root -p123123 bysj < fix-admin-password.sql

echo.
echo ========================================
echo 完成！
echo ========================================
pause
