@echo off
title Student Attendance Management System (SAMS)
cd /d "%~dp0"

echo ====================================================
echo   Student Attendance Management System (SAMS)
echo ====================================================
echo Starting application, please wait...
echo.

set "MVN_CMD=%USERPROFILE%\apache-maven\apache-maven-3.9.6\bin\mvn.cmd"

if not exist "%MVN_CMD%" (
    where mvn >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "MVN_CMD=mvn"
    ) else (
        echo [ERROR] Maven not found.
        pause
        exit /b 1
    )
)

"%MVN_CMD%" javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with an error.
    pause
)
