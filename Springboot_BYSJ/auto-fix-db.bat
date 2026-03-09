@echo off
chcp 65001 >nul
echo ========================================
echo 自动修复数据库表结构
echo ========================================
echo.

cd /d "%~dp0"

REM 设置 Maven 路径
set MAVEN_PATH=D:\Java_object\apache-maven-3.9.12-bin\apache-maven-3.9.12\bin\mvn.cmd

if exist "%MAVEN_PATH%" (
    echo 使用 Maven: %MAVEN_PATH%
    call "%MAVEN_PATH%" -q exec:java -Dexec.mainClass=com.utils.AutoFixDatabase -Dexec.classpathScope=compile
) else (
    echo 使用 mvn 命令...
    call mvn -q exec:java -Dexec.mainClass=com.utils.AutoFixDatabase -Dexec.classpathScope=compile
)

echo.
pause
