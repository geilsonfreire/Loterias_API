@echo off
:: ----------------------------------------------------------------------------
:: Licensed to the Apache Software Foundation (ASF) under one
:: or more contributor license agreements.  See the NOTICE file
:: distributed with this work for additional information
:: regarding copyright ownership.  The ASF licenses this file
:: to you under the Apache License, Version 2.0 (the
:: "License"); you may not use this file except in compliance
:: with the License.  You may obtain a copy of the License at
::
::    https://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing,
:: software distributed under the License is distributed on an
:: "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
:: KIND, either express or implied.  See the License for the
:: specific language governing permissions and limitations
:: under the License.
:: ----------------------------------------------------------------------------

:: ----------------------------------------------------------------------------
:: Maven Start Up Batch script
::
:: Required ENV vars:
:: ------------------
::   JAVA_HOME - location of a JDK home dir
::
:: Optional ENV vars
:: -----------------
::   M2_HOME - location of maven2's installed home dir
::   MAVEN_OPTS - parameters passed to the Java VM when running Maven
::     e.g. to debug Maven itself, use
::       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
::   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
:: ----------------------------------------------------------------------------

@REM Determine the base dir
set BASE_DIR=%~dp0

@REM Enable MAVEN_SKIP_RC to disable loading of mavenrc files
if not "%MAVEN_SKIP_RC%"=="" goto skipRcPre

@REM Check for pre mavenrc files
if exist "%BASE_DIR%/mavenrc_pre.bat" call "%BASE_DIR%/mavenrc_pre.bat"
if exist "%USERPROFILE%/.mavenrc_pre" call "%USERPROFILE%/.mavenrc_pre"

:skipRcPre

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%BASE_DIR%
if not "%MAVEN_BASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%

@REM Enable MAVEN_SKIP_RC to disable loading of mavenrc files
if not "%MAVEN_SKIP_RC%"=="" goto skipRc

@REM Check for mavenrc files
if exist "%MAVEN_PROJECTBASEDIR%/.mvn/mavenrc.bat" call "%MAVEN_PROJECTBASEDIR%/.mvn/mavenrc.bat"
if exist "%USERPROFILE%/.mavenrc" call "%USERPROFILE%/.mavenrc"

:skipRc

@REM Determine the Java command to use to start the JVM.
if not "%JAVA_HOME%"=="" goto gotJavaHome
set JAVA_EXE=java
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%"=="0" goto gotJava
echo.
echo Error: JAVA_HOME is not defined correctly.
echo We cannot execute %JAVA_EXE%
goto error

:gotJavaHome
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

:gotJava
@REM Determine the location of the wrapper jar file
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%/.mvn/wrapper/maven-wrapper.jar"

@REM Check that the wrapper jar file exists
if exist %WRAPPER_JAR% goto wrapperJarExists
echo.
echo Error: The wrapper jar file is not present: %WRAPPER_JAR%
echo.
echo Please run the following command to create it:
echo.
echo     mvn -N io.takari:maven:wrapper
goto error

:wrapperJarExists
@REM Initialize the arguments for the JVM
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
set WRAPPER_JAR_ARGS=-classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER%

@REM Add default JVM options if MAVEN_OPTS env var is not set
if "%MAVEN_OPTS%"=="" set MAVEN_OPTS=-Xmx512m

@REM Execute the Java command
"%JAVA_EXE%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" %WRAPPER_JAR_ARGS% %*

goto end

:error
exit /b 1

:end
