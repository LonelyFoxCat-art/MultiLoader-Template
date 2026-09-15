@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  Build.bat · 统一构建 Fabric + NeoForge
rem  默认流程：datagen → build → 汇总产物到 build\outputs
rem
rem  用法：
rem    双击 / Build.bat        完整构建
rem    Build.bat clean         先 clean 再构建（clean 与 build 分两次调用）
rem    Build.bat nodata        跳过 datagen（沿用已有的 generated 资源）
rem    Build.bat clean nodata  组合使用
rem ============================================================

set "GRADLE_EXTRA_ARGS="
set "do_clean=0"
set "do_data=1"
set "code=0"
set "out_dir=build\outputs"
title MultiLoader-Template · 构建

if not exist "gradlew.bat" goto :no_gradlew
if defined JAVA_HOME goto :java_ok
where java >nul 2>nul
if errorlevel 1 goto :no_java
:java_ok

:parse_args
if "%~1"=="" goto :args_done
if /i "%~1"=="clean"  set "do_clean=1" & shift & goto :parse_args
if /i "%~1"=="nodata" set "do_data=0"  & shift & goto :parse_args
if /i "%~1"=="fast"   set "do_data=0"  & shift & goto :parse_args
echo [错误] 未知参数: %~1
echo        可用: clean ^| nodata ^| fast
set "code=2"
goto :end
:args_done

echo.
echo  ============================================================
echo    构建 Fabric + NeoForge     Minecraft 26.1 · Java 25
echo  ============================================================
echo    clean   : %do_clean%
echo    datagen : %do_data%
echo  ============================================================
echo.

rem ---- clean（必须独立调用）----
rem gradlew clean build 在 org.gradle.parallel=true 下有竞态：
rem 某模块的 clean 删除 build/ 时，另一模块的 build 可能正在写入。
if "%do_clean%"=="1" (
    echo  [1/3] 清理旧产物...
    call gradlew.bat clean %GRADLE_EXTRA_ARGS%
    if errorlevel 1 ( set "code=!errorlevel!" & set "stage=clean" & goto :fail )
) else (
    echo  [1/3] 跳过清理
)

rem ---- datagen ----
rem 同样独立调用：runData 写 src\generated\resources，
rem 而 build 的 processResources 读它，合并调用会并发读写。
if "%do_data%"=="1" (
    echo  [2/3] NeoForge 数据生成...
    call gradlew.bat :neoforge:runData %GRADLE_EXTRA_ARGS%
    if errorlevel 1 ( set "code=!errorlevel!" & set "stage=datagen" & goto :fail )
) else (
    echo  [2/3] 跳过数据生成（沿用现有 generated 资源） 
)

rem ---- 3. build ----
echo  [3/3] 编译打包 Fabric + NeoForge...
call gradlew.bat build %GRADLE_EXTRA_ARGS%
if errorlevel 1 ( set "code=!errorlevel!" & set "stage=build" & goto :fail )

goto :collect

rem ============================================================
:collect
echo.
echo  ------------------------------------------------------------
echo    汇总产物到 %out_dir%
echo  ------------------------------------------------------------
if not exist "%out_dir%" mkdir "%out_dir%" >nul 2>nul
del /q "%out_dir%\*.jar" >nul 2>nul

set "found=0"
for %%M in (fabric neoforge) do (
    if exist "%%M\build\libs\" (
        copy /y "%%M\build\libs\*.jar" "%out_dir%\" >nul 2>nul
        set /a found+=1
    ) else (
        echo    [警告] 未找到 %%M\build\libs\
    )
)

rem withSourcesJar() / withJavadocJar() 会额外产出这两个，发布用不到
del /q "%out_dir%\*-sources.jar" >nul 2>nul
del /q "%out_dir%\*-javadoc.jar" >nul 2>nul

if "!found!"=="0" goto :no_jars

echo.
echo  ============================================================
echo    构建成功
echo  ============================================================
echo.
echo    产物目录：%CD%\%out_dir%
echo.
for %%F in ("%out_dir%\*.jar") do (
    set /a "kb=%%~zF / 1024"
    echo      %%~nxF    [!kb! KB]
)
echo.
echo    发布提示：把上面这些 jar 全部上传到 Modrinth / CurseForge 
echo              的同一个 Release 下，平台会按玩家的加载器自动分发。 

echo.
goto :end

:no_jars
echo.
echo  [错误] 构建报告成功，但没找到任何 jar。 
echo         请检查 fabric\build\libs\ 与 neoforge\build\libs\ 
set "code=3"
goto :end

rem ============================================================
:fail
echo.
echo  ============================================================ 
echo    构建失败于 [%stage%] 阶段    退出码 !code! 
echo  ============================================================ 
echo.
echo  排查： 
echo    1. 完整堆栈：Build.bat clean  仍失败则编辑脚本， 
echo       把 GRADLE_EXTRA_ARGS 设为 --stacktrace 
echo    2. 依赖下载失败：确认能访问 maven.neoforged.net / maven.fabricmc.net 
echo    3. 只想快速验证编译：Build.bat nodata 
echo    4. 之前 IDEA 报的 "Cannot resolve resource filtering" 
echo       是无害警告，不会导致构建失败，可忽略 
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