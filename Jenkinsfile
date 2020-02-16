pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'chmod +x gradlew'
        sh './gradlew clean build --no-daemon'
      }
    }

    stage('Artifact') {
      steps {
        archiveArtifacts 'build/libs/**.jar'
      }
    }

  }
}