pipeline {
    agent any

    environment {
        IMAGE_NAME = 'hdbank_project-app'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/CongLe0707/project_HdBank.git'
            }
        }

        stage('Build Jar') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME .'
            }
        }

        stage('Run Docker Compose') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose up -d --build'
            }
        }
    }
}
