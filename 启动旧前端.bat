@echo off
chcp 65001 >nul
echo ========================================
echo   大学生竞赛管理系统 - 旧前端启动
echo   Vue 2.7.14 + Element UI 2.15.14
echo ========================================
echo.

echo [1/2] 正在启动后端服务...
start "后端服务" powershell -NoExit -Command "cd 'D:\BYSJ\BYSJ\Springboot_BYSJ'; Write-Host '========================================' -ForegroundColor Cyan; Write-Host '  后端服务启动中...' -ForegroundColor Green; Write-Host '  端口: 9090' -ForegroundColor Yellow; Write-Host '========================================' -ForegroundColor Cyan; Write-Host ''; mvn spring-boot:run"

echo 等待后端启动（30秒）...
timeout /t 30 /nobreak >nul

echo.
echo [2/2] 正在启动前端服务...
start "前端服务" powershell -NoExit -Command "cd 'D:\BYSJ\BYSJ\Springboot_BYSJ\src\main\resources\admin\admin'; Write-Host '========================================' -ForegroundColor Cyan; Write-Host '  旧前端服务启动中...' -ForegroundColor Green; Write-Host '  Vue 2.7.14 + Element UI 2.15.14' -ForegroundColor Yellow; Write-Host '  端口: 8081' -ForegroundColor Yellow; Write-Host '========================================' -ForegroundColor Cyan; Write-Host ''; npm run serve"

echo 等待前端编译（15秒）...
timeout /t 15 /nobreak >nul

echo.
echo ========================================
echo   服务启动完成！
echo ========================================
echo.
echo 访问地址：http://localhost:8081
echo.
echo 测试账号：
echo   学生：2022001 / 123456
echo   教师：js001 / 123456
echo   管理员：admin / admin
echo.
echo 正在打开浏览器...
start http://localhost:8081

pause
