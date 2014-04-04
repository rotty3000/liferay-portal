#!/bin/bash

# create keys
keytool -genkey -keyalg RSA -sigalg SHA1withRSA -validity 730 -alias myservicekey -keypass skpass -storepass sspass -keystore serviceKeystore.jks -dname "cn=localhost"
keytool -genkey -keyalg RSA -sigalg SHA1withRSA -validity 730 -alias myclientkey  -keypass ckpass -storepass cspass -keystore clientKeystore.jks -dname "cn=clientuser"

# service will trust client
keytool -export -rfc -keystore clientKeystore.jks -storepass cspass -alias myclientkey -file MyClient.cer
keytool -import -trustcacerts -keystore serviceKeystore.jks -storepass sspass -alias myclientkey -file MyClient.cer -noprompt

# client will trust service
keytool -export -rfc -keystore serviceKeystore.jks -storepass sspass -alias myservicekey -file MyService.cer
keytool -import -trustcacerts -keystore clientKeystore.jks -storepass cspass -alias myservicekey -file MyService.cer -noprompt

# copy keystores into classpath
cp clientKeystore.jks ../src/main/resources
cp serviceKeystore.jks ../src/main/resources

cat > ../src/main/resources/serviceKeystore.properties <<EOF
org.apache.ws.security.crypto.merlin.keystore.file=serviceKeystore.jks
org.apache.ws.security.crypto.merlin.keystore.password=sspass
org.apache.ws.security.crypto.merlin.keystore.type=jks
org.apache.ws.security.crypto.merlin.keystore.alias=myservicekey
EOF

