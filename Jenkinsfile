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

        sh "rm -rf ReformCloud2.zip";
        sh "mkdir -p results";
        sh "cp -r .templates/* results/";
        sh "cp reformcloud2-runner/target/runner.jar results/runner.jar";

        zip archive: true, dir: 'results', glob: '', zipFile: 'ReformCloud2.zip';

        sh "rm -rf results/";
      }
    }

    stage('Prepare applications zip') {
      steps {
        sh "rm -rf ReformCloud2-Applications.zip";
        sh "mkdir -p applications/";

        sh "find reformcloud2-applications/ -type f -name \"reformcloud2-default-*.jar\" -and -not -name \"*-sources.jar\" -and -not -name \"*-javadoc.jar\" -exec cp \"{}\" applications/ ';'";
        zip archive: true, dir: 'applications', glob: '', zipFile: 'ReformCloud2-Applications.zip'

        sh "rm -rf applications/";
      }
    }

    stage('Prepare plugins zip') {
      steps {
        sh "rm -rf ReformCloud2-Plugins.zip";
        sh "mkdir -p plugins/";

        sh "find reformcloud2-plugins/ -type f -name \"reformcloud2-default-*.jar\" -and -not -name \"*-sources.jar\" -and -not -name \"*-javadoc.jar\" -exec cp \"{}\" plugins/ ';'";
        zip archive: true, dir: 'plugins', glob: '', zipFile: 'ReformCloud2-Plugins.zip'

        sh "rm -rf plugins/";
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'ReformCloud2.zip', fingerprint: true
        archiveArtifacts artifacts: 'ReformCloud2-Applications.zip', fingerprint: true
        archiveArtifacts artifacts: 'ReformCloud2-Plugins.zip', fingerprint: true
        archiveArtifacts artifacts: 'reformcloud2-runner/target/runner.jar', fingerprint: true
        archiveArtifacts artifacts: 'reformcloud2-node/target/executor.jar', fingerprint: true
        archiveArtifacts artifacts: 'reformcloud2-embedded/target/embedded.jar', fingerprint: true
      }
    }
  }

  post {
    always {
      withCredentials([string(credentialsId: 'discord-webhook', variable: 'url')]) {
        discordSend description: 'New build of ReformCloud', footer: 'Update', link: BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), title: JOB_NAME, webhookURL: url
      }
    }

    success {
      junit allowEmptyResults: true, testResults: 'reformcloud2-executor-api/target/surefire-reports/TEST-*.xml'
      junit allowEmptyResults: true, testResults: 'reformcloud2-protocol/target/surefire-reports/TEST-*.xml'
      junit allowEmptyResults: true, testResults: 'reformcloud2-shared/target/surefire-reports/TEST-*.xml'
    }
  }
}

def getProjectVersion() {
  return sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout  | tail -1", returnStdout: true)
}
