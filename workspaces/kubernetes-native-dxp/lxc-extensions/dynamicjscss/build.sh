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

./assemble.sh

## The rest simulates what LCP does

unzip build/libs/*.jar -d build/unzip

(cd build/unzip && docker build -t $IMAGE .)

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

