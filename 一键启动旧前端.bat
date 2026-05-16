@echo off
chcp 65001 >nul
title 旧前端启动器 - Vue 2 + Element UI

echo.
echo ========================================
echo   大学生竞赛管理系统 - 旧前端启动器
echo   Vue 2.7.14 + Element UI 2.15.14
echo ========================================
echo.
echo 正在清理环境...
taskkill /F /IM node.exe /T >nul 2>&1
taskkill /F /IM java.exe /T >nul 2>&1
timeout /t 3 /nobreak >nul
echo ✓ 环境清理完成
echo.

echo [步骤 1/3] 启动后端服务（端口 9090）
echo ----------------------------------------
start "后端服务 - 请等待60秒" powershell -NoExit -Command "cd 'D:\BYSJ\BYSJ\Springboot_BYSJ'; Write-Host '' -ForegroundColor Cyan; Write-Host '========================================' -ForegroundColor Cyan; Write-Host '  后端服务启动中...' -ForegroundColor Green; Write-Host '  端口: 9090' -ForegroundColor Yellow; Write-Host '  预计时间: 60秒' -ForegroundColor Yellow; Write-Host '========================================' -ForegroundColor Cyan; Write-Host ''; mvn spring-boot:run"

echo 等待后端启动（60秒）...
timeout /t 60 /nobreak

echo.
echo [步骤 2/3] 检查后端状态
echo ----------------------------------------
netstat -ano | findstr ":9090" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 后端服务已启动
) else (
    echo ⚠ 后端可能还在启动中，请稍候...
)

echo.
echo [步骤 3/3] 启动旧前端服务（端口 8081）
echo ----------------------------------------
start "旧前端服务 - Vue 2 + Element UI" powershell -NoExit -Command "cd 'D:\BYSJ\BYSJ\Springboot_BYSJ\src\main\resources\admin\admin'; Write-Host '' -ForegroundColor Cyan; Write-Host '========================================' -ForegroundColor Cyan; Write-Host '  旧前端服务启动中...' -ForegroundColor Green; Write-Host '  Vue 2.7.14 + Element UI 2.15.14' -ForegroundColor Yellow; Write-Host '  端口: 8081' -ForegroundColor Yellow; Write-Host '  预计时间: 30秒' -ForegroundColor Yellow; Write-Host '========================================' -ForegroundColor Cyan; Write-Host ''; npm run serve"

echo 等待前端编译（30秒）...
timeout /t 30 /nobreak

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo 📌 重要提示：
echo   1. 请在浏览器中访问：http://localhost:8081
echo   2. 如果看到新前端，请按 Ctrl+F5 硬刷新
echo   3. 或使用无痕模式打开
echo.
echo 🔑 测试账号：
echo   学生：2022001 / 123456
echo   教师：js001 / 123456
echo   管理员：admin / admin
echo.
echo ✅ 如何确认是旧前端？
echo   - 登录页面有科技风格渐变背景
echo   - 使用 Element UI 组件
echo   - 页面标题：竞赛后台管理系统
echo.

choice /C YN /M "是否立即打开浏览器访问旧前端"
if errorlevel 2 goto end
if errorlevel 1 goto openbrowser

:openbrowser
echo.
echo 正在打开浏览器...
start http://localhost:8081
echo ✓ 浏览器已打开
echo.

:end
echo.
echo ========================================
echo   两个PowerShell窗口正在运行：
echo   - 后端服务（端口 9090）
echo   - 旧前端服务（端口 8081）
echo.
echo   关闭窗口即可停止服务
echo ========================================
echo.
pause
