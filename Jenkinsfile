pipeline {
    agent any
    tools {
        jdk "1.8.0_222"
    }

    stages {
        stage('Update environment') {
            steps {
                script {
                    env.PROJECT_VERSION = readMavenPom().getVersion();
                    env.IS_SNAPSHOT = readMavenPom().getVersion().endsWith("-SNAPSHOT");
                }
            }
        }

        stage('Update snapshot version') {
            when {
                branch 'indev';
            }

            steps {
                sh 'mvn versions:set -DnewVersion="${env.PROJECT_VERSION}-${BUILD_NUMBER}"'
            }
        }

        stage('Validate') {
            steps {
                sh 'mvn validate';
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
                expression {
                    env.BRANCH_NAME == 'master' && IS_SNAPSHOT == false
                }
            }

            steps {
                echo "Deploy new release...";
            }
        }

        stage('Deploy snapshot') {
            when {
                expression {
                    env.BRANCH_NAME == 'indev' && IS_SNAPSHOT == true
                }
            }

            steps {
                echo "Deploy new snapshot...";
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

                sh "find reformcloud2-external/ -type f -name \"reformcloud2-default-*.jar\" -and -not -name \"*-sources.jar\" -and -not -name \"*-javadoc.jar\" -exec cp \"{}\" applications/ ';'";
                zip archive: true, dir: 'applications', glob: '', zipFile: 'ReformCloud2-Applications.zip'

                sh "rm -rf applications/";
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'ReformCloud2.zip'
                archiveArtifacts artifacts: 'ReformCloud2-Applications.zip'
                archiveArtifacts artifacts: 'reformcloud2-runner/target/runner.jar'
            }
        }
    }
}

def getProjectVersion() {
  return sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout  | tail -1", returnStdout: true).trim()
}