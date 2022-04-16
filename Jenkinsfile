pipeline {
    agent any
    
    tools {
        maven 'M3'
    }
    
    stages {
          stage("build & SonarQube analysis") {
            agent any
            steps {
              withSonarQubeEnv('sonar') {
                sh 'mvn clean package sonar:sonar'
              }
            }
          }
        }
}
