pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-11'
      args '-v /home/jenkins/.m2:/var/maven/.m2 -v /home/jenkins/.gnupg:/.gnupg -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS=-Duser.home=/var/maven'
    }
  }
  environment {
    GPG_SECRET = credentials('gpg_password')
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn -B clean verify'
      }
    }
    stage('javadoc') {
      steps {
        sh 'mvn -B javadoc:javadoc'
      }
    }
    stage('Deploy SNAPSHOT') {
      steps {
        sh 'mvn -B -Prelease -DskipTests -D-Dgpg.passphrase=${GPG_SECRET} deploy'
      }
    }
  }
}
