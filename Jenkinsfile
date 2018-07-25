#!groovy
// Jenkinsfile (Pipeline Script)

mavenConfiguration = [
    jdk: "jdk8",
    maven: "m3.5",
    mavenLocalRepo: ".repository",
    mavenSettingsConfig: "639c4560-87b7-4502-bb3d-2c44845cd2b5"
]

node {
    stage('Checkout') {
        checkout scm
    }

    withMaven(mavenConfiguration) {
        def mvnParams = "-Dbuild.number=${BUILD_NUMBER}"

        stage('Build') {
            sh "mvn ${mvnParams} clean compile"
        }

        stage('Test') {
            sh "mvn ${mvnParams} test"
        }

        stage('Package') {
            sh "mvn ${mvnParams} -DskipStatic -DskipTests package"
        }

        stage('Deploy') {
            sh "mvn ${mvnParams} -DskipStatic -DskipTests deploy"
            sh "mvn ${mvnParams} scm:tag"
        }
    }
}