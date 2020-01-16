pipeline {
    agent any
    tools {
        jdk "1.8.0_222"
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

        stage('Prepare addons zip') {
            steps {
                sh "rm -rf ReformCloud2-Addons.zip";
                sh "mkdir -p addons/";



                zip archive: true, dir: 'addons', glob: '', zipFile: 'ReformCloud2-Addons.zip'

                sh "rm -rf addons/";
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'ReformCloud2.zip'
                archiveArtifacts artifacts: 'reformcloud2-runner/target/runner.jar'
                //archiveArtifacts artifacts: 'ReformCloud2-Addons.zip'
            }
        }
    }
}