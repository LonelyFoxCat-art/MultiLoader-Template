@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  RunClient.bat · 只负责启动客户端
rem  NeoForge 路径会自动先跑 :neoforge:runData（datagen）
rem  Fabric 无 datagen 任务，直接启动
rem
rem  用法：
rem    双击                  交互菜单
rem    RunClient.bat fabric      直接跑 Fabric
rem    RunClient.bat neoforge    直接跑 NeoForge（含 datagen）
rem    RunClient.bat both        依次跑两个
rem    RunClient.bat nodata      跳过 datagen（配合上面的参数）
rem ============================================================

set "GRADLE_EXTRA_ARGS="
set "do_data=1"
set "code=0"
title MultiLoader-Template · 客户端

if not exist "gradlew.bat" goto :no_gradlew
if defined JAVA_HOME goto :java_ok
where java >nul 2>nul
if errorlevel 1 goto :no_java
:java_ok

rem ---- 参数解析 ----
:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="nodata" set "do_data=0" & shift & goto :parse_args
if /i "%~1"=="fabric"   goto :run_fabric
if /i "%~1"=="neoforge" goto :run_neoforge
if /i "%~1"=="both"     goto :run_both
echo [错误] 未知参数: %~1
echo        可用: fabric ^| neoforge ^| both ^| nodata
set "code=2"
goto :end
:args_done

rem ============================================================
:menu
cls
echo.
echo  ============================================================
echo    启动客户端            Minecraft 26.1 · Java 25
echo  ============================================================
echo.
echo    [1]  Fabric        :fabric:runClient
echo    [2]  NeoForge      :neoforge:runData  然后  :neoforge:runClient
echo    [3]  依次运行两者     
echo    [0]  退出
echo.
if "%do_data%"=="0" echo    当前：已禁用 datagen
set "choice="
set /p "choice=  请选择: "

if "%choice%"=="1" goto :run_fabric
if "%choice%"=="2" goto :run_neoforge
if "%choice%"=="3" goto :run_both
if "%choice%"=="0" goto :end
echo.
echo  [提示] 无效选项。
timeout /t 2 >nul
goto :menu

rem ============================================================
:run_fabric
set "label=Fabric 客户端"
set "need_data=0"
set "task=:fabric:runClient"
goto :exec

:run_neoforge
set "label=NeoForge 客户端"
set "need_data=%do_data%"
set "task=:neoforge:runClient"
goto :exec

:run_both
set "label=Fabric + NeoForge 客户端"
echo.
echo  ---------- 第一阶段：Fabric ----------
call gradlew.bat :fabric:runClient %GRADLE_EXTRA_ARGS%
if errorlevel 1 ( set "code=!errorlevel!" & goto :fail )
echo.
echo  ---------- 第二阶段：NeoForge ----------
if "%do_data%"=="1" (
    call gradlew.bat :neoforge:runData %GRADLE_EXTRA_ARGS%
    if errorlevel 1 ( set "code=!errorlevel!" & goto :fail )
)
call gradlew.bat :neoforge:runClient %GRADLE_EXTRA_ARGS%
if errorlevel 1 ( set "code=!errorlevel!" & goto :fail )
goto :ok

rem ---- datagen 与 runClient 必须分两次调用 ----
rem 若合并为一次，org.gradle.parallel=true 会让 processResources
rem 与 runData 并发执行，读到半写入的 src/generated/resources
:exec
echo.
echo  ============================================================
echo    %label%
echo  ============================================================
if "%need_data%"=="1" (
    echo.
    echo  [1/2] 生成数据 :neoforge:runData
    call gradlew.bat :neoforge:runData %GRADLE_EXTRA_ARGS%
    if errorlevel 1 (
        set "code=!errorlevel!"
        echo.
        echo  [中止] datagen 失败，未启动客户端。
        echo         原因：src\generated\resources 可能处于半生成状态，
        echo         此时启动客户端会加载到残缺数据。
        goto :fail
    )
    echo.
    echo  [2/2] 启动客户端
)
echo.
echo  ^> gradlew.bat %task% %GRADLE_EXTRA_ARGS%
echo.
call gradlew.bat %task% %GRADLE_EXTRA_ARGS%
set "code=!errorlevel!"
if not "!code!"=="0" goto :fail
goto :ok

rem ============================================================
:ok
echo.
echo  [完成] %label% 已退出。
echo.
goto :end

:fail
echo.
echo  ============================================================
echo    失败：%label%    退出码 !code!
echo  ============================================================
echo.
echo  排查：
echo    1. 崩溃报告：fabric\run\crash-reports\  或  neoforge\runs\client\crash-reports\
echo    2. 完整堆栈：把脚本顶部 GRADLE_EXTRA_ARGS 设为 --stacktrace
echo    3. 只想跳过 datagen 快速启动：RunClient.bat neoforge nodata
echo.
goto :end

:no_gradlew
echo [错误] 找不到 gradlew.bat。本脚本须放在项目根目录。
echo        当前目录：%CD%
set "code=2"
goto :end

:no_java
echo [错误] 未检测到 Java。运行 gradlew.bat 需要本机 JDK（本项目要求 25）。
set "code=2"
goto :end

:end
echo.
if "%~1"=="" pause
endlocal & exit /b %code%
