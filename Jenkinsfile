pipeline {
    agent any
    tools {
        jdk "Java8"
    }

    stages {
        stage('Clean') {
            steps {
                echo "Cleaning up...";
                sh 'mvn clean';
            }
        }

        stage('Build') {
            steps {
                echo "Building project...";
                sh 'mvn package';
            }
        }

        stage('Prepare zip') {
            steps {
                echo "Creating zip...";
                sh "mkdir results";
                sh "cp -r .templates/* results/";
                sh "cp reformcloud2-runner/target/runner.jar results/runner.jar";
                zip archive: true, dir: 'results', glob: '', zipFile: 'ReformCloud2.zip';
                sh "rm -rf results/";
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'reformcloud2-runner/target/runner.jar'
            }
        }
    }
}