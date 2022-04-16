node {
  stage('SCM') {
    checkout scm
  }
        stage('Test') {
             def mvn = tool 'M3';
              sh "${mvn}/bin/mvn test'
            }
  stage('SonarQube Analysis') {
    def mvn = tool 'M3'; // Replace with Global Tool > Maven > Maven installations > Name
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn sonar:sonar"
    }
  }
}
