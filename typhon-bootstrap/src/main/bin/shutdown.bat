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

set "EXECUTABLE=%TYPHON_HOME%\bin\typhon.bat"

rem Check that target executable exists
if exist "%EXECUTABLE%" goto okExec
echo Cannot find "%EXECUTABLE%"
echo This file is needed to run this program
goto end
:okExec

rem Get remaining unshifted command line arguments and save them in the
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

call "%EXECUTABLE%" stop %CMD_LINE_ARGS%

:end
