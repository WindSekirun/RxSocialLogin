pipeline {
  agent any
  stages {
    stage('assembleDebug') {
      steps {
        sh './gradlew --no-daemon assembleDebug --stacktrace'
      }
    }
  }
}