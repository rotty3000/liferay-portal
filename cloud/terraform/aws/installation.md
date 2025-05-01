# Preparing the Infrastructure to Install the AWS Marketplace Chart

This installation guide is intended for use with the specialized Liferay AWS Marketplace Helm chart located at:

`oci://709825985650.dkr.ecr.us-east-1.amazonaws.com/liferay/charts`.

## Prerequisites

1. Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) and configure with [IAM credentials](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-quickstart.html).

1. Install [Terraform CLI](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli).

1. Install [Git CLI](https://git-scm.com/downloads).

1. Install [Helm CLI](https://helm.sh/docs/intro/install/).

1. Install [kubectl CLI](https://kubernetes.io/docs/tasks/tools/).

1. Install [EKS CLI](https://eksctl.io/installation/).

## AWS

1. Export your profile for AWS SDK and its tools.

   ```bash
   export AWS_PROFILE=[profile]
   ```

1. Log into AWS CLI.

   ```bash
   aws sso login
   ```

## Installation

1. Clone the terraform files from the repository:

   ```bash
   git clone -n --depth=1 --filter=tree:0 https://github.com/liferay/liferay-portal.git liferay-aws-terraform
   cd liferay-aws-terraform
   git sparse-checkout set --no-cone /cloud/terraform/aws
   git checkout
   cd cloud/terraform/aws
   ```

Once the repository has been cloned, you have two choices:

1. Create a new EKS cluster. If you want to create a new EKS cluster complete with VPC and networking, follow [Create a new EKS cluster](#create-a-new-eks-cluster).

1. Use an existing EKS cluster. If you have an existing EKS cluster, follow [Create dependent services](#create-dependent-services).

## Create a new EKS cluster

1. Navigate to the `eks` directory.

1. Edit `terraform.tfvars` to configure your infrastructure. Variables are defined in the `variables.tf` file. By default, the system deploys an EKS cluster in the US West (Oregon) region (us-west-2) spanning two availability zones.

1. Run the following commands:

   ```bash
   terraform init
   ```

   ```bash
   terraform apply
   ```

   You are prompted to apply the changes.

1. Write the result of `terraform output` to the `../dependencies/terraform.tfvars` file in the `dependencies` directory:

   ```bash
   terraform output > ../dependencies/terraform.tfvars
   ```

## Create Dependent Services

1. Navigate to the `dependencies` directory.

1. Update the `terraform.tfvars` file to configure your infrastructure. Variables are defined in `variables.tf` file. If you followed [Create a new EKS cluster](#create-a-new-eks-cluster), this file is already populated.

1. Run the following commands:

   ```bash
   terraform init
   ```

   ```bash
   terraform apply
   ```

   You are prompted to apply the changes.

## Helm Launch Instructions

To use Helm you must use the `aws` CLI to set up `kubectl`.

1. Navigate to the `dependencies` directory.

1. Run the command below:

   ```bash
   aws eks update-kubeconfig \
      --name $(terraform output -raw cluster_name) \
      --region $(terraform output -raw region)
   ```

1. Test that `kubectl cluster-info` works.


1. Create the service account by executing the following command:

   ```shell
   eksctl create iamserviceaccount \
      --cluster $(terraform output -raw cluster_name) \
      --name liferay-default \
      --namespace liferay-system \
      --region $(terraform output -raw region) \
      --attach-role-arn $(terraform output -raw liferay_sa_role) \
      --approve \
      --override-existing-serviceaccounts
   ```

1. On the AWS "Launch instructions" page skip "Step 1" (since we executed it above) and execute "Step 2" with the following modification. In the `helm install` command add the following `--set` arguments:

   ```bash
    --set "liferay-aws.liferay-default.serviceAccount.create=false" \
    --set "liferay-aws.liferay-default.serviceAccount.name=liferay-default" \
   ```

1. Once "Step 2" has been executed successfully execute the following command to watch for the application to become ready:

   ```bash
   kubectl -n liferay-system get statefulset liferay-default --watch
   ```
