// Jenkinsfile (Pipeline Script)
node {
    // Select JDK8
    env.JAVA_HOME = tool name: 'JDK8'
    echo "JDK installation path is: ${env.JAVA_HOME}"

    checkout scm


    stage('Build') {
        sh 'ant clean'
        sh 'ant build'
    }

    stage('Test') {
        try {
            sh 'ant test'
        } finally {
            step([$class: 'JUnitResultArchiver', testResults: 'build/reports/*.xml'])
        }
    }


    stage('Package') {
        sh 'ant package'
        archiveArtifacts artifacts: 'build/release/Zutil.jar', fingerprint: true

        // Tag artifact
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'f8e5f6c6-4adb-4ab2-bb5d-1c8535dff491',
                                      usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            tag = "BUILD-" + env.BUILD_ID
            sh "git tag ${tag}"
            sh "git push 'https://${USERNAME}:${PASSWORD}@repo.koc.se/hal.git' ${tag}"
        }
    }


    //stage('Deploy') {
    //    if (currentBuild.result == 'SUCCESS') {
    //        input 'Deploy?', submitter 'Administrator'
    //        sh 'ant deploy'
    //    }
    //}
}

