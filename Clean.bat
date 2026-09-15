@echo off
:: 切换到脚本所在目录，确保无论从哪里双击运行路径都正确
cd /d "%~dp0"
chcp 65001 >nul

echo ========================================
echo   MultiLoader-Template 深度清理工具
echo ========================================
echo.

:: 尝试停止 Gradle Daemon，防止 .gradle 文件夹被占用导致无法删除
if exist "gradlew.bat" (
    echo [1/5] 正在停止 Gradle 守护进程...
    call gradlew.bat --stop >nul 2>&1
)

echo [2/5] 正在清理根目录缓存 (.gradle, build, run, runs)...
if exist ".gradle" rd /s /q ".gradle"
if exist "build" rd /s /q "build"
if exist "run" rd /s /q "run"
if exist "runs" rd /s /q "runs"

echo [3/5] 正在清理 Common 模块...
if exist "common\build" rd /s /q "common\build"
if exist "common\run" rd /s /q "common\run"
if exist "common\runs" rd /s /q "common\runs"

echo [4/5] 正在清理 Fabric 和 NeoForge 模块...
if exist "fabric\build" rd /s /q "fabric\build"
if exist "fabric\run" rd /s /q "fabric\run"
if exist "fabric\runs" rd /s /q "fabric\runs"

if exist "neoforge\build" rd /s /q "neoforge\build"
if exist "neoforge\run" rd /s /q "neoforge\run"
if exist "neoforge\runs" rd /s /q "neoforge\runs"

echo [5/5] 正在清理 BuildManager 构建逻辑缓存...
if exist "BuildManager\.gradle" rd /s /q "BuildManager\.gradle"
if exist "BuildManager\.kotlin" rd /s /q "BuildManager\.kotlin"
if exist "BuildManager\build" rd /s /q "BuildManager\build"

echo.
echo ========================================
echo   清理完成！环境已重置。
echo ========================================
pause