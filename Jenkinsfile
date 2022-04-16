pipeline {
    agent any
    
    tools {
        maven 'M3'
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('SonarQube Analysis') {
  //  def mvn = tool 'M3'; // Replace with Global Tool > Maven > Maven installations > Name
    withSonarQubeEnv() {
      sh 'mvn sonar:sonar'
   /// sh "${mvn}/bin/mvn sonar:sonar"
    }
  }
}
}
