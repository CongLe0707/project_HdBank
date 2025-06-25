pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        echo 'Cloning source code...'
      }
    }

    stage('Build') {
      steps {
        sh './mvnw clean package -DskipTests'
      }
    }

    stage('Test') {
      steps {
        sh './mvnw test'
      }
    }

    stage('Deploy') {
      steps {
        echo 'Deploying application...'
      }
    }
  }
}
