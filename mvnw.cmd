@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven start up batch script for Windows
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@REM Check if JAVA_HOME is set
if not "%JAVA_HOME%" == "" goto OkJHome
echo JAVA_HOME not set, using java from PATH
goto findMaven

:OkJHome
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
goto findMaven

:findMaven
@REM Try to find Maven in common locations
set "MAVEN_CMD="
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set "MAVEN_CMD=mvn"
    goto runMaven
)

if exist "C:\Program Files\Maven\bin\mvn.cmd" (
    set "MAVEN_CMD=C:\Program Files\Maven\bin\mvn.cmd"
    goto runMaven
)

@REM Download Maven if not found
echo Maven is not installed. Downloading Apache Maven 3.9.6...
set "MVN_HOME=%USERPROFILE%\.m2\wrapper\apache-maven-3.9.6"
set "MVN_ZIP=%USERPROFILE%\.m2\wrapper\apache-maven-3.9.6-bin.zip"

if exist "%MVN_HOME%\bin\mvn.cmd" (
    set "MAVEN_CMD=%MVN_HOME%\bin\mvn.cmd"
    goto runMaven
)

mkdir "%USERPROFILE%\.m2\wrapper" 2>nul
echo Downloading Maven to %MVN_ZIP%...
powershell -Command "Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile '%MVN_ZIP%' -UseBasicParsing"
if %ERRORLEVEL% neq 0 (
    echo Failed to download Maven. Please install Maven manually from https://maven.apache.org/download.cgi
    echo Then add it to your PATH and try again.
    exit /b 1
)

echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%MVN_ZIP%' -DestinationPath '%USERPROFILE%\.m2\wrapper' -Force"
set "MAVEN_CMD=%MVN_HOME%\bin\mvn.cmd"

:runMaven
echo Using Maven: %MAVEN_CMD%
"%MAVEN_CMD%" %*
