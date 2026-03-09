@echo off
chcp 65001 >nul
echo ========================================
echo 启动后端服务并自动修复数据库
echo ========================================
echo.

cd /d "%~dp0"

echo [提示] 后端启动时会自动检查并修复数据库表结构
echo [提示] 按 Ctrl+C 可以停止后端服务
echo.
echo 正在启动...
echo.

REM 使用 Maven 启动 Spring Boot
call mvn spring-boot:run

pause
