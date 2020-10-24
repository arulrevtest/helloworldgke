def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
  containerTemplate(name: 'springbuild', image: 'arulkumar1967/build-arul-container:latest', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'kubectl', image: 'gcr.io/cloud-builders/kubectl', command: 'cat', ttyEnabled: true)
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]) {
  node(label) {
    def project = 'arulgkedemo'
    def  appName = 'helloworld'
    def  imageTag = "arulkumar1967/${appName}:${env.BUILD_NUMBER}"
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT
    def gitBranch = myRepo.GIT_BRANCH
    def shortGitCommit = "${gitCommit[0..10]}"
    def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)

    stage('Build') {
        container('springbuild') {
          sh """
            pwd
            echo "GIT_BRANCH=${gitBranch}" >> /etc/environment
            echo "GIT_COMMIT=${gitCommit}" >> /etc/environment
            mvn test
            mvn package
          """
        }
    }
    stage('Build and push image with Container Builder') {
        container('docker') {
          sh "docker build -t ${imageTag} ."
          docker.withRegistry('', 'aruldoccred') {
            sh "docker push ${imageTag}"
        }
      }
    }
    stage('Deploy') {
        container('kubectl') {
          withCredentials([string(credentialsId: 'gke-jenkins-sa-token', variable: 'K8S_TOKEN')]) {
            withEnv([
                // Ensure that kubectl is using our special robot deployer’s kubeconfig
                "KUBECONFIG=/home/[jenkins_user]/.kube/kubernetes_deployment_config",
                "KUBECTL=kubectl --token $K8S_TOKEN",
            ]) {
                // Execute the code that is now wrapped with the correct kubectl
                sh("sed -i.bak 's#arulkumar1967/rev_helloworld_gke_sb#${imageTag}#' ./k8s/app/*.yaml")
                sh("$KUBECTL --namespace=dev apply -f k8s/app")
                sh("echo http://`KUBECTL get service -o jsonpath='{.status.loadBalancer.ingress[0].ip}'`")
            }
         }

        }
    }
  }
}
