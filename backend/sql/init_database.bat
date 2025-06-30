@echo off
chcp 65001 > nul
echo ========================================
echo    在线学习平台数据库初始化脚本
echo ========================================
echo.

REM 设置MySQL连接参数
set MYSQL_USER=root
set MYSQL_PASSWORD=238729046
set MYSQL_HOST=localhost
set MYSQL_PORT=3306

echo 正在连接MySQL服务器...
echo 主机: %MYSQL_HOST%:%MYSQL_PORT%
echo 用户: %MYSQL_USER%
echo.

REM 检查MySQL是否可用
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -h %MYSQL_HOST% -P %MYSQL_PORT% -e "SELECT 1;" > nul 2>&1
if errorlevel 1 (
    echo [错误] 无法连接到MySQL服务器！
    echo 请检查：
    echo 1. MySQL服务是否已启动
    echo 2. 用户名和密码是否正确
    echo 3. 网络连接是否正常
    pause
    exit /b 1
)

echo [成功] MySQL连接正常
echo.

REM 1. 创建数据库
echo [1/3] 正在创建数据库 online_study_dev...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -h %MYSQL_HOST% -P %MYSQL_PORT% < init_database.sql
if errorlevel 1 (
    echo [错误] 数据库创建失败！
    pause
    exit /b 1
)
echo [成功] 数据库创建完成

REM 2. 创建表结构
echo [2/3] 正在创建表结构...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -h %MYSQL_HOST% -P %MYSQL_PORT% < online_study.sql
if errorlevel 1 (
    echo [错误] 表结构创建失败！
    pause
    exit /b 1
)
echo [成功] 表结构创建完成

REM 3. 添加AI功能表
echo [3/3] 正在添加AI功能表...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% -h %MYSQL_HOST% -P %MYSQL_PORT% < ai_features.sql
if errorlevel 1 (
    echo [错误] AI功能表创建失败！
    pause
    exit /b 1
)
echo [成功] AI功能表创建完成

echo.
echo ========================================
echo          数据库初始化完成！
echo ========================================
echo.
echo 数据库名称: online_study_dev
echo 连接信息: 
echo   主机: %MYSQL_HOST%:%MYSQL_PORT%
echo   数据库: online_study_dev
echo   用户: %MYSQL_USER%
echo.
echo 您现在可以启动Spring Boot应用了！
echo.
pause 