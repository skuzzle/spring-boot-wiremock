pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-11'
      args '-v $HOME/.m2:/root/.m2 -v $HOME/.gnupg:/root/.gnupg -u 0:0'
    }
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('javadoc') {
      steps {
        sh 'mvn javadoc:javadoc'
      }
    }
  }
}
