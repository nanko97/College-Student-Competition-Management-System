@echo off
chcp 65001 >nul
echo ========================================
echo 启动后端并诊断数据库
echo ========================================
echo.

cd /d "%~dp0"

echo [1/2] 正在诊断数据库表结构...
echo.

REM 先运行诊断程序
call mvn -q exec:java -Dexec.mainClass=com.utils.QuickDiagnose 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo 诊断失败，请手动检查数据库连接
    echo.
)

echo.
echo ========================================
echo [2/2] 准备启动后端服务...
echo ========================================
echo.
echo 提示：
echo 1. 如果上面显示表结构正确，但新增仍失败，请按 F12 查看错误
echo 2. 诊断文件已保存为：DIAGNOSE_STEPS.md
echo 3. 详细修复说明：README_FIX.md
echo.
pause
