#!groovy
// Jenkinsfile (Pipeline Script)
withMaven(jdk: "jdk8", maven: "m3.5", mavenLocalRepo: ".repository", mavenSettingsConfig: "639c4560-87b7-4502-bb3d-2c44845cd2b5") {
    node {
        // Configure environment
        env.REPO_URL = "repo.koc.se/zutil-java.git" //scm.getUserRemoteConfigs()[0].getUrl()
        env.BUILD_NAME = "BUILD-" + env.BUILD_ID

        stage('Checkout') {
            git url: "https://" + env.REPO_URL
        }

        stage('Build') {
            sh 'mvn clean compile'
        }

        stage('Test') {
            sh 'mvn test'
        }

        stage('Package') {
            sh 'mvn -DskipStatic -DskipTests package'

            // Tag artifact
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'f8e5f6c6-4adb-4ab2-bb5d-1c8535dff491',
                              usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                sh "git tag ${env.BUILD_NAME}"
                sh "git push 'https://${USERNAME}:${PASSWORD}@${env.REPO_URL}' ${env.BUILD_NAME}"
            }
        }

        stage('Deploy') {
            sh 'mvn -DskipStatic -DskipTests deploy'
        }
    }

    stage('Release') {
        timeout(time: 1, unit: 'HOURS') {
            input message: 'Deploy?', submitter: 'ziver'
            node {
                sh 'mvn --batch-mode -DskipStatic -DskipTests release:prepare release:perform'
            }
        }
    }
}