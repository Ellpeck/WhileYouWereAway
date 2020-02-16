pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
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