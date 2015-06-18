@echo off
rem Copyright (C) 2012-2013 The Skfiy Open Association.

rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start/Stop Script for the CATALINA Server
rem
rem Environment Variable Prerequisites
rem
rem   TYPHON_HOME   May point at your Catalina "build" directory.
rem
rem   TYPHON_OPTS   (Optional) Java runtime options used when the "start",
rem                   "run" or "debug" command is executed.
rem                   Include here and not in JAVA_OPTS all options, that should
rem                   only be used by Tomcat itself, not by the stop process,
rem                   the version command etc.
rem                   Examples are heap size, GC logging, JMX ports etc.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem                   Required to run the with the "debug" argument.
rem
rem   JRE_HOME        Must point at your Java Runtime installation.
rem                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
rem                   are both set, JRE_HOME is used.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when any command
rem                   is executed.
rem                   Include here and not in TYPHON_OPTS all options, that
rem                   should be used by Tomcat and also by the stop process,
rem                   the version command etc.
rem                   Most options should go into TYPHON_OPTS.
rem
rem   JAVA_ENDORSED_DIRS (Optional) Lists of of semi-colon separated directories
rem                   containing some jars in order to allow replacement of APIs
rem                   created outside of the JCP (i.e. DOM and SAX from W3C).
rem                   It can also be used to update the XML parser implementation.
rem                   Defaults to $TYPHON_HOME/endorsed.
rem
rem   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
rem                   command is executed. The default is "dt_socket".
rem
rem   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. The default is 8000.
rem
rem   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. Specifies whether JVM should suspend
rem                   execution immediately after startup. Default is "y".
rem
rem   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
rem                   and JPDA_SUSPEND are ignored. Thus, all required jpda
rem                   options MUST be specified. The default is:
rem
rem                   -agentlib:jdwp=transport=%JPDA_TRANSPORT%,
rem                       address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%
rem
rem ---------------------------------------------------------------------------
rem Guess TYPHON_HOME if not defined
set "CURRENT_DIR=%cd%"
if not "%TYPHON_HOME%" == "" goto gotHome
set "TYPHON_HOME=%CURRENT_DIR%"
if exist "%TYPHON_HOME%\bin\typhon.bat" goto okHome
cd ..
set "TYPHON_HOME=%cd%"
cd "%CURRENT_DIR%"
:gotHome
if exist "%TYPHON_HOME%\bin\typhon.bat" goto okHome
echo The TYPHON_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

rem Suppress Terminate batch job on CTRL+C

rem Ensure that any user defined CLASSPATH variables are not used on startup,
rem but allow them to be specified in setenv.bat, in rare case when it is needed.
set CLASSPATH=

rem Get standard Java environment variables
if exist "%TYPHON_HOME%\bin\setclasspath.bat" goto okSetclasspath
echo Cannot find "%TYPHON_HOME%\bin\setclasspath.bat"
echo This file is needed to run this program
goto end
:okSetclasspath
call "%TYPHON_HOME%\bin\setclasspath.bat" %1
if errorlevel 1 goto end

rem Add on extra jar file to CLASSPATH
rem Note that there are no quotes as we do not want to introduce random
rem quotes into the CLASSPATH
if "%CLASSPATH%" == "" goto emptyClasspath
set "CLASSPATH=%CLASSPATH%;"
:emptyClasspath
set "CLASSPATH=%CLASSPATH%%TYPHON_HOME%\bin\bootstrap.jar"

if not "%TYPHON_TMPDIR%" == "" goto gotTmpdir
set "TYPHON_TMPDIR=%TYPHON_HOME%\temp"
:gotTmpdir

set JAVA_OPTS=%JAVA_OPTS%

if exist "%TYPHON_HOME%\bin\setenv.bat" goto okSetenv
echo Cannot find "%TYPHON_HOME%\bin\setenv.bat"
echo This file is needed to run this program
goto end
:okSetenv
call "%TYPHON_HOME%\bin\setenv.bat" %1
if errorlevel 1 goto end

rem ----- Execute The Requested Command ---------------------------------------
echo Using TYPHON_HOME:   "%TYPHON_HOME%"
echo Using TYPHON_TMPDIR: "%TYPHON_TMPDIR%"
if ""%1"" == ""debug"" goto use_jdk
echo Using JRE_HOME:      "%JRE_HOME%"
goto java_dir_displayed
:use_jdk
echo Using JAVA_HOME:     "%JAVA_HOME%"
:java_dir_displayed
echo Using CLASSPATH:     "%CLASSPATH%"

set _EXECJAVA=%_RUNJAVA%
set MAINCLASS=org.skfiy.typhon.startup.Bootstrap
set ACTION=start
set SECURITY_POLICY_FILE=
set DEBUG_OPTS=
set JPDA=

if not ""%1"" == ""jpda"" goto noJpda
set JPDA=jpda
if not "%JPDA_TRANSPORT%" == "" goto gotJpdaTransport
set JPDA_TRANSPORT=dt_socket
:gotJpdaTransport
if not "%JPDA_ADDRESS%" == "" goto gotJpdaAddress
set JPDA_ADDRESS=8000
:gotJpdaAddress
if not "%JPDA_SUSPEND%" == "" goto gotJpdaSuspend
set JPDA_SUSPEND=n
:gotJpdaSuspend
if not "%JPDA_OPTS%" == "" goto gotJpdaOpts
set JPDA_OPTS=-agentlib:jdwp=transport=%JPDA_TRANSPORT%,address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%
:gotJpdaOpts
shift
:noJpda

if ""%1"" == ""debug"" goto doDebug
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
if ""%1"" == ""version"" goto doVersion

echo Usage:  typhon ( commands ... )
echo commands:
echo   jpda start        Start Typhon under JPDA debugger
echo   start             Start Typhon in a separate window
echo   stop              Stop Typhon
echo   version           What version of tomcat are you running?
goto end

:doDebug
shift
set _EXECJAVA=%_RUNJDB%
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set "SECURITY_POLICY_FILE=%TYPHON_HOME%\conf\typhon.policy"
goto execCmd

:doStart
shift
if not "%OS%" == "Windows_NT" goto noTitle
if "%TITLE%" == "" set TITLE=Typhon
set _EXECJAVA=start "%TITLE%" %_RUNJAVA%
goto gotTitle
:noTitle
set _EXECJAVA=start %_RUNJAVA%
:gotTitle
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set "SECURITY_POLICY_FILE=%TYPHON_HOME%\conf\typhon.policy"
goto execCmd

:doStop
shift
set ACTION=stop
set CATALINA_OPTS=
goto execCmd

:doVersion
%_EXECJAVA% -classpath "%TYPHON_HOME%\lib\typhon.jar" org.skfiy.typhon.ServerInfo
goto end

:execCmd
rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem Execute Java with the applicable properties
if not "%JPDA%" == "" goto doJpda
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurity
%_EXECJAVA% %JAVA_OPTS% %TYPHON_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Dtyphon.home="%TYPHON_HOME%" -Djava.io.tmpdir="%TYPHON_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doSecurity
%_EXECJAVA% %JAVA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -Dtyphon.home="%TYPHON_HOME%" -Djava.io.tmpdir="%TYPHON_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doJpda
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurityJpda
%_EXECJAVA% %JAVA_OPTS% %CATALINA_OPTS% %JPDA_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Dtyphon.home="%TYPHON_HOME%" -Djava.io.tmpdir="%TYPHON_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doSecurityJpda
%_EXECJAVA% %JAVA_OPTS% %CATALINA_OPTS% %JPDA_OPTS% %DEBUG_OPTS% -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -Dtyphon.home="%TYPHON_HOME%" -Djava.io.tmpdir="%TYPHON_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end

:end