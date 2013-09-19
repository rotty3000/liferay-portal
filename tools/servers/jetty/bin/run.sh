#!/bin/sh

if [ ! $JAVA_HOME ]
then
	echo JAVA_HOME not defined.
	exit
fi

JETTY_HOME="$(cd "$(dirname "$0")/.." && pwd)"

export JAVA_OPTS="-Dfile.encoding=UTF8 ${java.security.manager.option} -Djetty.home=$JETTY_HOME -Djava.security.policy=$JETTY_HOME/lib/policy/server.policy -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=256m"

$JAVA_HOME/bin/java $JAVA_OPTS -jar $JETTY_HOME/start.jar
