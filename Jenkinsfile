#!groovy​
// Jenkinsfile (Pipeline Script)
node {
    // Configure environment
    env.REPO_URL = "repo.koc.se/zutil-java.git" //scm.getUserRemoteConfigs()[0].getUrl()
    env.BUILD_NAME = "BUILD-" + env.BUILD_ID


    checkout scm

    withMaven(JDK: "JDK8") {

        stage('Build') {
            sh 'mvn clean compile'
        }

        stage('Test') {
            sh 'mvn surefire:test failsafe:integration-test'
        }

        stage('Package') {
            sh 'mvn -DskipStatic -DskipTests install'

            // Tag artifact
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'f8e5f6c6-4adb-4ab2-bb5d-1c8535dff491',
                                          usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                sh "git tag ${env.BUILD_NAME}"
                sh "git push 'https://${USERNAME}:${PASSWORD}@${env.REPO_URL}' ${env.BUILD_NAME}"
            }
        }
    }
}

//stage('Deploy') {
//    timeout(time: 5, unit: 'HOURS') {
//        input message: 'Deploy?', submitter: 'ziver'
//        node {
//            sh 'mvn deploy'
//        }
//    }
//}
