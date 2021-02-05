#!groovy
pipeline {
  agent any

  tools {
    jdk "11.0.8"
  }

  options {
    buildDiscarder logRotator(numToKeepStr: '10')
  }

  environment {
    PROJECT_VERSION = getProjectVersion().replace("-SNAPSHOT", "");
    IS_SNAPSHOT = getProjectVersion().endsWith("-SNAPSHOT");
  }

  stages {
    stage('Update snapshot version') {
      when {
        allOf {
          environment name: 'IS_SNAPSHOT', value: 'true'
        }
      }

      steps {
        sh 'mvn versions:set -DnewVersion="${PROJECT_VERSION}.${BUILD_NUMBER}-SNAPSHOT"';
      }
    }

    stage('Clean') {
      steps {
        sh 'mvn clean';
      }
    }

    stage('Build') {
      steps {
        sh 'mvn package';
      }
    }

    stage('Verify') {
      steps {
        sh 'mvn verify';
      }
    }

    stage('Deploy release') {
      when {
        allOf {
          branch 'stable'
          environment name: 'IS_SNAPSHOT', value: 'false'
        }
      }

      steps {
        echo "Deploy new release...";
        sh 'mvn clean deploy -P deploy';
      }
    }

    stage('Prepare cloud zip') {
      steps {
        echo "Creating cloud zip...";

        sh "rm -rf ReformCloud.zip";
        sh "mkdir -p results";
        sh "cp -r .templates/* results/";
        sh "cp runner/target/runner.jar results/runner.jar";

        zip archive: true, dir: 'results', glob: '', zipFile: 'ReformCloud.zip';

        sh "rm -rf results/";
      }
    }

    stage('Prepare applications zip') {
      steps {
        sh "rm -rf ReformCloud-Applications.zip";
        sh "mkdir -p applications/";

        sh "find applications/ -type f -name \"*.jar\" -and -not -name \"*-sources.jar\" -and -not -name \"*-javadoc.jar\" -exec cp \"{}\" applications/ ';'";
        zip archive: true, dir: 'applications', glob: '', zipFile: 'ReformCloud-Applications.zip'

        sh "rm -rf applications/";
      }
    }

    stage('Prepare plugins zip') {
      steps {
        sh "rm -rf ReformCloud-Plugins.zip";
        sh "mkdir -p plugins/";

        sh "find plugins/ -type f -name \"*.jar\" -and -not -name \"*-sources.jar\" -and -not -name \"*-javadoc.jar\" -exec cp \"{}\" plugins/ ';'";
        zip archive: true, dir: 'plugins', glob: '', zipFile: 'ReformCloud-Plugins.zip'

        sh "rm -rf plugins/";
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'ReformCloud.zip', fingerprint: true
        archiveArtifacts artifacts: 'ReformCloud-Applications.zip', fingerprint: true
        archiveArtifacts artifacts: 'ReformCloud-Plugins.zip', fingerprint: true
        archiveArtifacts artifacts: 'runner/target/runner.jar', fingerprint: true
        archiveArtifacts artifacts: 'node/target/executor.jar', fingerprint: true
        archiveArtifacts artifacts: 'embedded/target/embedded.jar', fingerprint: true
      }
    }
  }

  post {
    success {
      junit allowEmptyResults: true, testResults: 'api/target/surefire-reports/TEST-*.xml'
      junit allowEmptyResults: true, testResults: 'protocol/target/surefire-reports/TEST-*.xml'
      junit allowEmptyResults: true, testResults: 'shared/target/surefire-reports/TEST-*.xml'

      withCredentials([string(credentialsId: 'discord-webhook', variable: 'url')]) {
        discordSend(
          description: "**Build:** ${env.BUILD_NUMBER}\nStatus: Success\n\n**Job-Url**:\n${env.BUILD_URL}",
          footer: 'ReformCloud Jenkins',
          link: "${env.BUILD_URL}",
          successful: true,
          title: "Build success: {env.JOB_NAME}",
          webhookURL: "$url",
          unstable: false,
          result: "SUCCESS"
        )
      }
    }
    failure {
      withCredentials([string(credentialsId: 'discord-webhook', variable: 'url')]) {
        discordSend(
          description: "**Build:** ${env.BUILD_NUMBER}\nStatus: Failure\n\n**Job-Url**:\n${env.BUILD_URL}",
          footer: 'ReformCloud Jenkins',
          link: "${env.BUILD_URL}",
          successful: false,
          title: "Build failure: {env.JOB_NAME}",
          webhookURL: "$url",
          unstable: false,
          result: "FAILURE"
        )
      }
    }
  }
}

def getProjectVersion() {
  return sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout  | tail -1", returnStdout: true)
}
