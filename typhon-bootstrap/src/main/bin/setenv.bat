
@rem 设置logback日志文件目录
if exist "%TYPHON_HOME%\conf\logback.xml" goto okLogback
echo Cannot find "%TYPHON_HOME%\conf\logback.xml"
echo This file is needed to run this program
goto end
:okLogback
set JAVA_OPTS=%JAVA_OPTS% -Dlogback.configurationFile="%TYPHON_HOME%\conf\logback.xml"

:end