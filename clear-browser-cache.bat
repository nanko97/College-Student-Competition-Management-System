@echo off
chcp 65001 >nul
echo ========================================
echo    彻底清除浏览器缓存工具
echo ========================================
echo.
echo 正在停止所有浏览器进程...
echo.

taskkill /F /IM chrome.exe >nul 2>&1
taskkill /F /IM msedge.exe >nul 2>&1
taskkill /F /IM firefox.exe >nul 2>&1

echo ✅ 已停止所有浏览器进程
echo.
echo 正在清除浏览器缓存...
echo.

REM 清除 Chrome 缓存
del /q /s "%LOCALAPPDATA%\Google\Chrome\User Data\Default\Cache\*" >nul 2>&1
del /q /s "%LOCALAPPDATA%\Google\Chrome\User Data\Default\Application Cache\*" >nul 2>&1
del /q /s "%LOCALAPPDATA%\Google\Chrome\User Data\Default\Service Worker\*" >nul 2>&1
echo ✅ 已清除 Chrome 缓存

REM 清除 Edge 缓存
del /q /s "%LOCALAPPDATA%\Microsoft\Edge\User Data\Default\Cache\*" >nul 2>&1
del /q /s "%LOCALAPPDATA%\Microsoft\Edge\User Data\Default\Application Cache\*" >nul 2>&1
del /q /s "%LOCALAPPDATA%\Microsoft\Edge\User Data\Default\Service Worker\*" >nul 2>&1
echo ✅ 已清除 Edge 缓存

echo.
echo ========================================
echo   缓存清除完成！
echo ========================================
echo.
echo 请按以下步骤操作：
echo.
echo 1. 关闭所有浏览器窗口（已完成）
echo 2. 重新打开浏览器
echo 3. 访问：http://localhost:8081/
echo 4. 按 Ctrl + Shift + Delete
echo 5. 勾选"缓存的图片和文件"
echo 6. 点击"清除数据"
echo 7. 按 Ctrl + F5 强制刷新
echo.
echo 或者直接访问：
echo http://localhost:8081/?t=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
echo.
pause

start http://localhost:8081/?t=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
