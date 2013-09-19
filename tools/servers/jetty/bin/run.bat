@echo off

if "" == "%JAVA_HOME%" goto errorJavaHome

pushd "%~p0" & pushd .. & set "JETTY_HOME=%cd%" & popd & popd

set "JAVA_OPTS=-Dfile.encoding=UTF8 ${java.security.manager.option} -Djetty.home=%JETTY_HOME% -Djava.security.policy=%JETTY_HOME%\lib\policy\jetty.policy -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=256m"

"%JAVA_HOME%/bin/java" %JAVA_OPTS% -jar %JETTY_HOME%/start.jar

goto end

:errorJavaHome
	echo JAVA_HOME not defined.

	goto end

:end