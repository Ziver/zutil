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
    }


    //stage('Deploy') {
    //    if (currentBuild.result == 'SUCCESS') {
    //        input 'Deploy?', submitter 'Administrator'
    //        sh 'ant deploy'
    //    }
    //}
}

