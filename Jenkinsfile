// Jenkinsfile (Pipeline Script)
node {
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

