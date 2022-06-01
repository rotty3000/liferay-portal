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

mvn clean package

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
tstamp=$(date +%s)
sed -e "s/\${tstamp}/${tstamp}/g" -e 's/^/    /' configurator/osgi.config.json \
	>> ../../k8s/$ID/extension-configmap.yaml

docker build -t $IMAGE .