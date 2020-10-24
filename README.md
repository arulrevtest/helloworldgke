# Spring Boot Continuous Deployment Pipeline with Jenkins and GKE

## Enhancements in this repository

1. Deploying containers to GKE cluster with kubectl. Please refer to K8s folder and Jenkinsfile for more details
1. Installing Jenkins with helm chart
1. Terraform code to create GKE cluster
1. Testcases are written for Springboot controller


## Prerequisites
1. A Google Cloud Platform Account
1. [Enable the Compute Engine, Container Engine](https://console.cloud.google.com/flows/enableapi?apiid=compute_component,container)

## Setting up Google Cloud Shell
In this section you will start [Google Cloud Shell](https://cloud.google.com/cloud-shell/docs/) and clone the lab code repository to it.

1. Create a new Google Cloud Platform project: [https://console.developers.google.com/project](https://console.developers.google.com/project)

1. Click the Google Cloud Shell icon in the top-right and wait for the shell to open:


1. When the shell is open, set default compute zone:

  ```shell
  $ gcloud config set compute/zone us-east1-d
  ```

1. Clone the lab repository in cloud shell, then `cd` into that dir:

  ```shell
  $ git clone https://github.com/arulrevtest/helloworldgke.git
  Cloning into 'helloworldgke'...
  ...

  $ cd helloworldgke
  ```

## Architecture

![](RevGKEDemo_kubectl.png)


## Create a Kubernetes Cluster
Use Terraform to create and manage Kubernetes cluster.

Update terraform/variables.tf with appropriate project, cluster_name and other parameters

```shell
    $ cd terraform
    $ terraform init
    $ terraform apply -auto-approve
```

Once that terraform apply completes, download the credentials for the cluster using the [gcloud CLI](https://cloud.google.com/sdk/):
```shell
$ gcloud container clusters get-credentials jenkins-cd
Fetching cluster endpoint and auth data.
kubeconfig entry generated for jenkins-cd.
```

Confirm that the cluster is running and `kubectl` is working by listing pods:

```shell
$ kubectl get pods
No resources found.
```
You should see `No resources found.`.

## Install Helm

Using Helm to install Jenkins from the Charts repository.

1. Download and install the helm binary

    ```shell
    wget https://storage.googleapis.com/kubernetes-helm/helm-v2.9.1-linux-amd64.tar.gz
    ```

1. Unzip the file to local system:

    ```shell
    tar zxfv helm-v2.9.1-linux-amd64.tar.gz
    cp linux-amd64/helm .
    ```

1. Add ourself as a cluster administrator in the cluster's RBAC so that you can give Jenkins permissions in the cluster:
    
    ```shell
    kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value account)
    ```

1. Grant Tiller, the server side of Helm, the cluster-admin role in the cluster:

    ```shell
    kubectl create serviceaccount tiller --namespace kube-system
    kubectl create clusterrolebinding tiller-admin-binding --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
    ```

1. Initialize Helm. This ensures that the server side of Helm (Tiller) is properly installed in the cluster.

    ```shell
    ./helm init --service-account=tiller
    ./helm update
    ```

1. Ensure Helm is properly installed by running the following command. You should see versions appear for both the server and the client of ```v2.9.1```:

    ```shell
    ./helm version
    Client: &version.Version{SemVer:"v2.9.1", GitCommit:"20adb27c7c5868466912eebdf6664e7390ebe710", GitTreeState:"clean"}
    Server: &version.Version{SemVer:"v2.9.1", GitCommit:"20adb27c7c5868466912eebdf6664e7390ebe710", GitTreeState:"clean"}
    ```

## Configure and Install Jenkins

1. Use the Helm CLI to deploy the chart with our configuration set.

    ```shell
    ./helm install -n cd stable/jenkins -f jenkins/values.yaml --version 0.16.6 --wait
    ```

1. Run the following command to setup port forwarding to the Jenkins UI from the Cloud Shell

    ```shell
    export POD_NAME=$(kubectl get pods -l "component=cd-jenkins-master" -o jsonpath="{.items[0].metadata.name}")
    kubectl port-forward $POD_NAME 8080:8080 >> /dev/null &
    ```

## Connect to Jenkins

1. The Jenkins chart will automatically create an admin password for you. To retrieve it, run:

    ```shell
    printf $(kubectl get secret cd-jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo    ```

2. To get to the Jenkins user interface, click on the Web Preview button in cloud shell, then click “Preview on port 8080”


### Add service account credentials

1. In the Jenkins UI, Click “Credentials” on the left
1. Click either of the “(global)” links (they both route to the same URL)
1. Click “Add Credentials” on the left
1. From the “Kind” dropdown, select “Google Service Account from metadata”
1. Click “OK”

## Deploying MySQL database

Deployment of mysql db is done outside pipeline using below commands
1. kubectl -n dev create secret generic db-credentials --from-literal=mysql-root-password=admin
1. kubectl -n dev create -f k8s/db/mysql-deployment.yaml
1. kubectl -n dev create -f k8s/db/mysql-service.yaml

## Zero downtime deployment of helloword application

1. Jenkins pipeline job is created with this repo as SCM source
1. Pipeline steps are defined in Jenkinsfile
1. Zero downtime deployment is achieved through RollingUpdate defined in k8s/app/testapp-deployment.yaml


## Testing application

### Create table in RDS mysql instance with mysql client with below ddl command
```
CREATE TABLE users ( id smallint unsigned not null auto_increment, user_name varchar(20), date_of_birth varchar(20), constraint pk_example primary key (id) );

```

### Sample Test Scripts

Replace ip address appropriately

```
Command:
curl -X PUT \
  http://34.244.214.224:8090/hello/Arul \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
    "dateOfBirth": "2007-06-12"
}'
Expected result:
No Response message. Row should be created in users table for Arul

Command:
curl -X GET \
  http://34.244.214.224:8090/hello/Arul \
  -H 'Cache-Control: no-cache'
 Expected Result:
 Hello Arul Your birthday is in N Day(s)

Command:
curl -X PUT \
  http://34.244.214.224:8090/hello/Arulk \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
    "dateOfBirth": "2007-04-24"
}'
Expected result:
No Response message. Row should be created in users table for Arulk

Command:
curl -X GET \
  http://34.244.214.224:8090/hello/Arulk \
  -H 'Cache-Control: no-cache'
 Expected Result:
 Hello Arulk Happy Birthday!

Command:
curl -X PUT \
  http://34.244.214.224:8090/hello/Arulk12 \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
    "dateOfBirth": "2007-04-24"
}'
Expected result:
Arulk12 must contains only letters.

Command:
curl -X PUT \
  http://34.244.214.224:8090/hello/Arulku \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
    "dateOfBirth": "2019-04-30"
}'
Expected result:
Date 2019-04-30 must be a date before the today date

### 24122020   

check-apis