pipeline {
  agent any
  stages {
    stage('Touch local.properties') {
      steps {
        sh 'echo "sdk.dir=/opt/android-sdk-linux" >> local.properties'
      }
    }
    stage('assembleDebug') {
      steps {
        sh './gradlew --no-daemon assembleDebug --stacktrace'
      }
    }
  }
}