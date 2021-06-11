pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-11'
      args '-v /home/jenkins/.m2:/var/maven/.m2 -v /home/jenkins/.gnupg:/.gnupg -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS=-Duser.home=/var/maven'
    }
  }
  environment {
    COVERALLS_REPO_TOKEN = credentials('coveralls_repo_token_spring_boot_wiremock')
    GPG_SECRET = credentials('gpg_password')
  }
  stages {
    stage ('Build Spring-Boot GA 2.3.12') {
      steps {
        sh 'mvn -B clean verify -Dversion.spring-boot=2.3.12.RELEASE'
      }
    }
    stage ('Build Spring-Boot GA 2.4.7') {
      steps {
        sh 'mvn -B clean verify -Dversion.spring-boot=2.4.7'
      }
    }
    stage ('Build Spring-Boot Current 2.5.1') {
      steps {
        sh 'mvn -B clean verify -Dversion.spring-boot=2.5.1'
      }
    }
    stage('Build Final') {
      steps {
        sh 'mvn -B clean verify'
      }
    }
    stage('Coverage') {
      steps {
        sh 'mvn -B jacoco:report jacoco:report-integration coveralls:report -DrepoToken=$COVERALLS_REPO_TOKEN'
      }
    }
    stage('javadoc') {
      steps {
        sh 'mvn -B javadoc:javadoc'
      }
    }
    stage('Deploy SNAPSHOT') {
      when {
        branch 'dev'
      }
      steps {
        sh 'mvn -B -Prelease -DskipTests -Dgpg.passphrase=${GPG_SECRET} deploy'
      }
    }
  }
}
