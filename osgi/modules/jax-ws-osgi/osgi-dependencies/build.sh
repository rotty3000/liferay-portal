#!/bin/bash

## JAXB API 2.2.6
cat > jaxb-api-2.2.6.jar.bnd <<EOF
#-nouses: true
Export-Package: javax.xml.bind*;version=2.2.6
Import-Package: *;resolution:=optional
Bundle-Version: 2.2.6
Bundle-Name: JAXB API 2.2.6 
EOF

java -jar bnd.jar wrap --properties jaxb-api-2.2.6.jar.bnd --output dist/jaxb-api-2.2.6-OSGi.jar jaxb-api-2.2.6.jar
rm jaxb-api-2.2.6.jar.bnd


## JAXB IMPL 2.2.6
cat > jaxb-impl-2.2.6.jar.bnd <<EOF
#-nouses: true
Export-Package: com.sun.xml.bind*;version=2.2.6
Import-Package: *;resolution:=optional
Bundle-Version: 2.2.6
Bundle-Name: JAXB IMPL 2.2.6 
EOF

java -jar bnd.jar wrap --properties jaxb-impl-2.2.6.jar.bnd --output dist/jaxb-impl-2.2.6-OSGi.jar jaxb-impl-2.2.6.jar
rm jaxb-impl-2.2.6.jar.bnd


## SAAJ API 1.3
cat > saaj-api-1.3.bnd <<EOF
#-nouses: true
Export-Package: javax.xml.soap*;version=1.3.0
Import-Package: *;resolution:=optional
Bundle-Version: 1.3.0
Bundle-Name: SOAP with Attachments API for Java
EOF

java -jar bnd.jar wrap --properties saaj-api-1.3.bnd --output dist/saaj-api-1.3-OSGi.jar saaj-api-1.3.jar
rm saaj-api-1.3.bnd


## SAAJ impl 1.3
cat > saaj-impl-1.3.bnd <<EOF
#-nouses: true
Export-Package: com.sun.xml.messaging.saaj*;version=1.3.0
Import-Package: *;resolution:=optional
Bundle-Version: 1.3.0
Bundle-Name: SOAP with Attachments API for Java Implementation
EOF

java -jar bnd.jar wrap --properties saaj-impl-1.3.bnd --output dist/saaj-impl-1.3-OSGi.jar saaj-impl-1.3.jar
rm saaj-impl-1.3.bnd


## OpenSAML 2.5.1

mkdir opensaml-merge
unzip -o openws-1.4.2-1.jar -d opensaml-merge
unzip -o xmltooling-1.3.2-1.jar -d opensaml-merge
unzip -o opensaml-2.5.1-1.jar -d opensaml-merge

cd opensaml-merge
rm META-INF/INDEX.LIST
rm META-INF/MANIFEST.MF

zip ../opensaml-merge.jar -r *
cd ..
rm -r opensaml-merge

cat > opensaml-2.5.1-xmltooling-1.3.2-openws-1.4.2.bnd <<EOF
-nouses: true
Export-Package: org.opensaml*;version="2.5.1",org.opensaml.xml*;version="1.3.2", org.opensaml.ws*;version="1.4.2",org.opensaml.util*;"version=1.4.2"
Import-Package: *;resolution:=optional
Bundle-Version: 1.0.0
Bundle-Name: OpenSAML + xmltooling + OpenSAML WS libraries bundle
EOF

java -jar bnd.jar wrap --properties opensaml-2.5.1-xmltooling-1.3.2-openws-1.4.2.bnd --output dist/opensaml-2.5.1-xmltooling-1.3.2-openws-1.4.2-OSGi.jar opensaml-merge.jar
rm opensaml-2.5.1-xmltooling-1.3.2-openws-1.4.2.bnd
rm opensaml-merge.jar

## OpenSAML xmltooling 1.3.2
#cat > xmltooling-1.3.2-1.bnd <<EOF
#-nouses: true
#Export-Package: org.opensaml.xml*;version="1.3.2"
#Import-Package: *;resolution:=optional
#Bundle-Version: 1.3.2
#Bundle-Name: OpenSAML xmltooling
#EOF
#
#java -jar bnd.jar wrap --properties xmltooling-1.3.2-1.bnd --output dist/xmltooling-1.3.2-1-OSGi.jar xmltooling-1.3.2-1.jar
#rm xmltooling-1.3.2-1.bnd


# OpenSAML WS
#cat > openws-1.4.2-1.bnd <<EOF
##-nouses: true
#Export-Package: org.opensaml.ws*;version=1.4.2,org.opensaml.util*;version=1.4.2
#Import-Package: *;resolution:=optional
#Bundle-Version: 1.4.2
#Bundle-Name: OpenSAML WS
#EOF
#
#java -jar bnd.jar wrap --properties openws-1.4.2-1.bnd --output dist/openws-1.4.2-1-OSGi.jar openws-1.4.2-1.jar
#rm openws-1.4.2-1.bnd


# commons logging 
cat > commons-logging-1.1.1.bnd <<EOF
#-nouses: true
Export-Package: org.apache.commons.logging*;version=1.1.1
Import-Package: *;resolution:=optional
Bundle-Version: 1.1.1
Bundle-Name: Commons logging
EOF

java -jar bnd.jar wrap --properties commons-logging-1.1.1.bnd --output dist/commons-logging-1.1.1-OSGi.jar commons-logging-1.1.1.jar
rm commons-logging-1.1.1.bnd


# WSDL4j
cat > wsdl4j-1.6.3.bnd <<EOF
#-nouses: true
Export-Package: com.ibm.wsdl.*;version=1.6.3,javax.wsdl.*;version=1.6.3,
Import-Package: *;resolution:=optional
Bundle-Version: 1.6.3
Bundle-Name: WSDL4j
EOF

java -jar bnd.jar wrap --properties wsdl4j-1.6.3.bnd --output dist/wsdl4j-1.6.3-OSGi.jar wsdl4j-1.6.3.jar
rm wsdl4j-1.6.3.bnd


# WSS4J ...  change javax.xml.crypto versions - use JDK << configured at Liferay level
# xmlsec-2.0.0.rc1 -> change to 1.9999999.2.0.0.rc-1 to conform the [1.5.6,2) package imports :/

