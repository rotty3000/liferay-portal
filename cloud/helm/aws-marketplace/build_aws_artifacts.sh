#!/usr/bin/env bash

export AWS_PROFILE=AWSAdministratorAccess-831926597587

BUILD_SUFFIX=$(date -u +%Y%m%d%H%M%S)
CUSTOM_CHART_VERSION="${HELM_CHART_VERSION}-${BUILD_SUFFIX}"
HELM_CHART_VERSION=$(cat ../default/Chart.yaml | yq .version)
DXP_TAG=${1?"argument specifying the DXP image tag is required"}
CUSTOM_TAG="${DXP_TAG}-os-${BUILD_SUFFIX}"
OPENSEARCH_BUNDLES_DIR=${HOME}/projects/liferay-portal-ee-release-2025.q1/liferay-portal-ee/tools/sdk/dist
TARGET_HELM_REPOSITORY="liferay/charts"
TARGET_IMAGE_REPOSITORY="liferay/containers"
TARGET_REGISTRY="709825985650.dkr.ecr.us-east-1.amazonaws.com"

echo "Publishing custom Helm chart to Registry: ${TARGET_REGISTRY}"
echo "Publishing custom Helm chart into Repository: ${TARGET_HELM_REPOSITORY}"
echo "Publishing custom Helm chart with Version: ${HELM_CHART_VERSION}"
echo "Using Custom Chart Version: ${CUSTOM_CHART_VERSION}"
echo "Publishing custom Docker image into Repository: ${TARGET_IMAGE_REPOSITORY}"
echo "Using DXP Image Tag: ${DXP_TAG}"
echo "Using Custom Image Tag: ${CUSTOM_TAG}"

aws_login() {
	aws sso login
}

build_and_push_custom_chart() {
	curl --fail --location --output helm.tar.gz --show-error --silent https://get.helm.sh/helm-v3.6.3-linux-amd64.tar.gz
	tar --extract --file=helm.tar.gz --gzip --strip-components=1 linux-amd64/helm
	rm helm.tar.gz

	export HELM_EXPERIMENTAL_OCI=1
	aws ecr get-login-password --region us-east-1 | \
		./helm registry login --password-stdin --username AWS ${TARGET_REGISTRY}

	./helm chart save . "${TARGET_REGISTRY}/${TARGET_HELM_REPOSITORY}:${CUSTOM_CHART_VERSION}"
	./helm chart push "${TARGET_REGISTRY}/${TARGET_HELM_REPOSITORY}:${CUSTOM_CHART_VERSION}"

	echo "Contents of ${TARGET_REGISTRY}/${TARGET_HELM_REPOSITORY}:"

	aws --no-cli-pager ecr describe-images --region us-east-1 --registry-id "${TARGET_REGISTRY:0:12}" --repository-name "${TARGET_HELM_REPOSITORY}"
}

build_and_push_custom_image() {
	rm *.jar 2&>/dev/null

	for module in api impl; do
		cp -v ${OPENSEARCH_BUNDLES_DIR}/com.liferay.portal.search.opensearch2.${module}-*.jar com.liferay.portal.search.opensearch2.${module}.jar
	done

	ls -l *.jar

	aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${TARGET_REGISTRY}

	docker build \
		--platform "linux/amd64" \
		--build-arg "DXP_IMAGE_TAG=${DXP_TAG}" \
		--tag "${TARGET_REGISTRY}/${TARGET_IMAGE_REPOSITORY}:${CUSTOM_TAG}" \
		--push .

	echo "Contents of ${TARGET_REGISTRY}/${TARGET_IMAGE_REPOSITORY}:"

	aws --no-cli-pager ecr describe-images --region us-east-1 --registry-id "${TARGET_REGISTRY:0:12}" --repository-name "${TARGET_IMAGE_REPOSITORY}"
}

cleanup() {
	git checkout -- ./Chart.yaml ./values.yaml
	rm helm *.jar 2&>/dev/null
}

configure_mktplc_helm_chart() {
	git checkout -- ./Chart.yaml ./values.yaml

	sed -i "s|repository: file://../aws|repository: oci://us-central1-docker.pkg.dev/liferay-artifact-registry/liferay-helm-chart|" Chart.yaml
	sed -i "s|version: 0.0.0|version: ${HELM_CHART_VERSION}|g" Chart.yaml
	sed -i "s|tag: 0.0.0|tag: ${CUSTOM_TAG}|g" values.yaml
}

main() {
	aws_login

	configure_mktplc_helm_chart

	build_and_push_custom_image

	build_and_push_custom_chart

	cleanup
}

main