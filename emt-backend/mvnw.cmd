@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script (official)
@REM ----------------------------------------------------------------------------

@ECHO OFF
SETLOCAL EnableExtensions EnableDelayedExpansion

SET "BASE_DIR=%~dp0"
SET "WRAPPER_DIR=%BASE_DIR%.mvn\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
SET "PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties"

IF NOT EXIST "%WRAPPER_JAR%" (
  IF NOT EXIST "%WRAPPER_DIR%" MKDIR "%WRAPPER_DIR%"
  IF EXIST "%PROPS_FILE%" (
    FOR /F "usebackq tokens=1,* delims==" %%A IN ("%PROPS_FILE%") DO (
      IF /I "%%~A"=="wrapperUrl" SET "WRAP_URL=%%~B"
    )
  )
  IF "!WRAP_URL!"=="" SET "WRAP_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
  ECHO Downloading Maven Wrapper from: !WRAP_URL!
  POWERSHELL -NoLogo -NoProfile -Command "Invoke-WebRequest -UseBasicParsing -Uri '!WRAP_URL!' -OutFile '%WRAPPER_JAR%'" || (
    ECHO Failed to download maven-wrapper.jar 1>&2
    EXIT /B 1
  )
)

IF NOT "%JAVA_HOME%"=="" (
  SET "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) ELSE (
  SET "JAVA_CMD=java.exe"
)

"%JAVA_CMD%" -version >NUL 2>&1
IF NOT %ERRORLEVEL%==0 (
  ECHO Java not found in PATH and JAVA_HOME not set. 1>&2
  EXIT /B 1
)

"%JAVA_CMD%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

ENDLOCAL
EXIT /B %ERRORLEVEL%


