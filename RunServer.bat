@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  RunServer.bat · 只负责启动专用服务器（交互式控制台）
rem  自动处理 EULA；NeoForge 路径同样先跑 datagen
rem
rem  用法：
rem    双击                  交互菜单
rem    RunServer.bat fabric      直接跑 Fabric
rem    RunServer.bat neoforge    直接跑 NeoForge
rem    RunServer.bat nodata      跳过 datagen
rem ============================================================

rem --console=plain 是必需的：
rem Gradle 的富控制台会重绘进度区，与 MC 服务器的交互提示符互相覆盖，
rem 导致输入行错位、看不到自己敲了什么。
set "GRADLE_EXTRA_ARGS=--console=plain"
set "do_data=1"
set "code=0"
title MultiLoader-Template · 专用服务器

if not exist "gradlew.bat" goto :no_gradlew
if defined JAVA_HOME goto :java_ok
where java >nul 2>nul
if errorlevel 1 goto :no_java
:java_ok

:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="nodata" set "do_data=0" & shift & goto :parse_args
if /i "%~1"=="fabric"   goto :run_fabric
if /i "%~1"=="neoforge" goto :run_neoforge
echo [错误] 未知参数: %~1
echo        可用: fabric ^| neoforge ^| nodata
set "code=2"
goto :end
:args_done

:menu
cls
echo.
echo  ============================================================
echo    启动专用服务器        Minecraft 26.1 · Java 25
echo  ============================================================
echo.
echo    [1]  Fabric        :fabric:runServer
echo    [2]  NeoForge      :neoforge:runData  然后  :neoforge:runServer
echo    [0]  退出   
echo.
set "choice="
set /p "choice=  请选择: "

if "%choice%"=="1" goto :run_fabric
if "%choice%"=="2" goto :run_neoforge
if "%choice%"=="0" goto :end
echo.
echo  [提示] 无效选项。
timeout /t 2 >nul
goto :menu

rem ============================================================
rem  ⚠️ 这两个路径必须与 Gradle 里的 gameDirectory / runDir 一致
rem     NeoForge: multiloader.neoforge.gradle.kts 中 runs/server
rem     Fabric  : Loom 默认 runDir（若改过请同步这里）
rem ============================================================
:run_fabric
set "label=Fabric 服务器"
set "need_data=0"
set "task=:fabric:runServer"
set "run_dir=fabric\run"
goto :prep

:run_neoforge
set "label=NeoForge 服务器"
set "need_data=%do_data%"
set "task=:neoforge:runServer"
set "run_dir=neoforge\runs\server"
goto :prep

:prep
call :ensure_eula "%run_dir%"
if errorlevel 1 ( set "code=1" & goto :end )
goto :exec

:exec
echo.
echo  ============================================================
echo    %label%
echo  ============================================================
if "%need_data%"=="1" (
    echo.
    echo  [1/2] 生成数据 :neoforge:runData
    call gradlew.bat :neoforge:runData %GRADLE_EXTRA_ARGS%
    if errorlevel 1 ( set "code=!errorlevel!" & goto :fail )
    echo.
    echo  [2/2] 启动服务器
)
echo.
echo  ------------------------------------------------------------
echo    控制台操作：
echo      stop  + 回车     正常关闭（保存世界后退出）
echo      Ctrl + C         强制终止（若 stop 无响应）
echo.
echo    若敲 stop 完全没反应，说明 Gradle 未转发标准输入，
echo    此时只能用 Ctrl+C。详见随附说明。
echo  ------------------------------------------------------------
echo.
echo  ^> gradlew.bat %task% %GRADLE_EXTRA_ARGS%
echo.
call gradlew.bat %task% %GRADLE_EXTRA_ARGS%
set "code=!errorlevel!"
if not "!code!"=="0" goto :fail
goto :ok

rem ============================================================
:ensure_eula
set "eula_dir=%~1"
if not exist "%eula_dir%" mkdir "%eula_dir%" >nul 2>nul
if not exist "%eula_dir%\eula.txt" goto :eula_ask
findstr /i /c:"eula=true" "%eula_dir%\eula.txt" >nul 2>nul
if not errorlevel 1 exit /b 0

:eula_ask
echo.
echo  ------------------------------------------------------------
echo    首次启动需要接受 Minecraft EULA
echo    目录：%eula_dir%
echo    协议：https://aka.ms/MinecraftEULA
echo  ------------------------------------------------------------
set "ans="
set /p "ans=  是否同意并写入 eula=true？[Y/N]: "
if /i "!ans!"=="Y"   goto :eula_write
if /i "!ans!"=="yes" goto :eula_write
echo.
echo  [中止] 未接受 EULA，专用服务器无法启动。
exit /b 1

:eula_write
> "%eula_dir%\eula.txt" echo #By changing the setting below to TRUE you are indicating your agreement to our EULA ^(https://aka.ms/MinecraftEULA^).
>>"%eula_dir%\eula.txt" echo eula=true
echo  [OK] 已写入 %eula_dir%\eula.txt
exit /b 0

rem ============================================================
:ok
echo.
echo  [完成] %label% 已关闭。
echo.
goto :end

:fail
echo.
echo  ============================================================
echo    失败：%label%    退出码 !code!
echo  ============================================================
echo.
echo  排查：
echo    1. 日志：%run_dir%\logs\latest.log
echo    2. 崩溃报告：%run_dir%\crash-reports\
echo    3. 端口占用：netstat -ano ^| findstr :25565
echo    4. 内存不足：在 gradle.properties 里调大 run 任务的 -Xmx
echo.
goto :end

:no_gradlew
echo [错误] 找不到 gradlew.bat。本脚本须放在项目根目录。
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