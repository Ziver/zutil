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
        stage('Build') {
            sh 'mvn clean compile'
        }

        stage('Test') {
            sh 'mvn test'
        }

        stage('Package') {
            sh 'mvn -DskipStatic -DskipTests package'
        }

        stage('Deploy') {
            sh 'mvn -DskipStatic -DskipTests deploy'
        }
    }
}

stage('Release') {
    timeout(time: 1, unit: 'HOURS') {
        input message: 'Deploy?', submitter: 'ziver'
        node {
            withMaven(mavenConfiguration) {
                sh 'mvn --batch-mode -DskipStatic -DskipTests release:prepare release:perform'
            }
        }
    }
}