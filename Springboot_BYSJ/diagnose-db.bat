@echo off
chcp 65001 >nul
echo ========================================
echo 诊断 jingsaixinxi 表结构
echo ========================================
echo.

cd /d "%~dp0"

REM 使用 Maven 运行
call mvn exec:java -Dexec.mainClass=com.utils.DatabaseDiagnoseUtil

echo.
pause
