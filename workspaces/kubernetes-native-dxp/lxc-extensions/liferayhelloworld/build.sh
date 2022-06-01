#!/bin/bash

cd "$(dirname "$0")"

ID="${PWD##*/}"

if [ ! -z "${EXPECTED_REF}" ]; then
  IMAGE=${EXPECTED_REF}
fi

if [ ! -n "${IMAGE}" ]; then
  IMAGE="${ID}"
fi

eval $(minikube docker-env)

echo "[run_local] Build the $ID PoC"

yarn install && yarn build-local

JS=$(jq '[.files[] | "$[conf:host.service.address]" + select(endswith(".js"))]' build/asset-manifest.json)
CSS=$(jq '[.files[] | "$[conf:host.service.address]" + select(endswith(".css"))]' build/asset-manifest.json)

jq ".[\"com.liferay.remote.app.factory.configuration.v1.RemoteAppFactoryConfiguration~$ID\"].webComponentUrl |= $JS" \
	configurator/osgi.config.json >\
	configurator/osgi.config.json.tmp &&\
	mv configurator/osgi.config.json.tmp configurator/osgi.config.json

jq ".[\"com.liferay.remote.app.factory.configuration.v1.RemoteAppFactoryConfiguration~$ID\"].webComponentCssUrl |= $CSS" \
	configurator/osgi.config.json >\
	configurator/osgi.config.json.tmp &&\
	mv configurator/osgi.config.json.tmp configurator/osgi.config.json

./assemble.sh

## The rest simulates what LCP does

unzip build/libs/*.jar -d build/unzip

docker build -t $IMAGE .

cat << EOF > ../../k8s/$ID/extension-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: $ID-dxp-configs
  labels:
    cloud.liferay.com/serviceId: $ID
    dxp.liferay.com/configs: "true"
  annotations:
    cloud.liferay.com/context-data: '{"domains":["$ID.localdev.me"]}'
data:
  osgi.config.json: |
EOF
sed -e 's/^/    /' build/unzip/OSGI-INF/configurator/osgi.config.json \
	>> ../../k8s/$ID/extension-configmap.yaml
