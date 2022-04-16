node {
  stage('SCM') {
    checkout scm
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
    def mvn = tool 'M3'; // Replace with Global Tool > Maven > Maven installations > Name
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn sonar:sonar"
    }
  }
}
