def updateGitHubCommitStatus(Map args) {
	String message = args.name;
	String state = args.state;

	step([
		$class: "GitHubCommitStatusSetter",
		reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/geckoprojects-org/org.gecko.graphql"],
		errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
		statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
	]);
}

pipeline {
	agent any
	
	tools {
		jdk 'OpenJDK17'
	}

	options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
	}
	
	post {
		always {
			cleanWs()
		}
	}

	stages {
	
		stage('Clean workspace and checkout') {
			steps {
				deleteDir()
				checkout scm
			}
		}

		stage('App build') {

			steps {
				echo "I am building app on branch: ${env.GIT_BRANCH}"

				updateGitHubCommitStatus(name: 'App build', state: 'PENDING')

				sh "./gradlew clean build -x itest --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
			}

			post {
				success {
					updateGitHubCommitStatus(name: 'App build', state: 'SUCCESS')
				}
				
				failure {
					updateGitHubCommitStatus(name: 'App build', state: 'FAILURE')
				}
			}
		}

		stage('Integration Tests') {

			steps {
				echo "I am running integration tests on branch: ${env.GIT_BRANCH}"

				updateGitHubCommitStatus(name: 'Integration Tests', state: 'PENDING')				

				sh "./gradlew clean build itest --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"

				junit '**/generated/test-reports/**/*.xml'
			}

			post {
				success {
					updateGitHubCommitStatus(name: 'Integration Tests', state: 'SUCCESS')
				}
				
				failure {
					updateGitHubCommitStatus(name: 'Integration Tests', state: 'FAILURE')
				}
			}
		}

		stage('Release') {
			when {
				branch 'master'
			}

			steps {
				echo "I am creating a release on branch: ${env.GIT_BRANCH}"

				updateGitHubCommitStatus(name: 'Release', state: 'PENDING')
				
				sh "./gradlew clean build release -Drelease.dir=$JENKINS_HOME/repo.gecko/release/geckoGraphQL -x itest --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"		
			}
			
			post {
				success {
					updateGitHubCommitStatus(name: 'Release', state: 'SUCCESS')
				}
				
				failure {
					updateGitHubCommitStatus(name: 'Release', state: 'FAILURE')
				}
			}
		}

		stage('Snapshot release') {
			when {
				branch 'develop'
			}
			
			steps  {
				echo "I am creating a release on branch: ${env.GIT_BRANCH}"
				
				updateGitHubCommitStatus(name: 'Snapshot release', state: 'PENDING')
				
                sh "./gradlew clean release -x itest --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
                sh "mkdir -p $JENKINS_HOME/repo.gecko/geckoGraphQL"
                sh "rm -rf $JENKINS_HOME/repo.gecko/geckoGraphQL/*"
                sh "cp -r cnf/release/* $JENKINS_HOME/repo.gecko/geckoGraphQL"
            }

			post {
				success {
					updateGitHubCommitStatus(name: 'Snapshot release', state: 'SUCCESS')
				}
				
				failure {
					updateGitHubCommitStatus(name: 'Snapshot release', state: 'FAILURE')
				}
			}
		}
	}
}
