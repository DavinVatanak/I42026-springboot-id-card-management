pipeline {
    agent any

    triggers {
        // Poll GitHub every 5 minutes to detect changes automatically
        pollSCM('H/5 * * * *')
    }

    environment {
        // Defines the recipient email for failure notifications
        ADMIN_EMAIL = 'srengty@gmail.com'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot project using Maven...'
                // Clean the project and package it into a JAR, skipping tests for speed
                // Using ./mvnw (Maven Wrapper) to fix the "mvn: command not found" error
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests against SQLite database...'
                // Run tests using the 'test' profile to ensure isolation from production DB
                sh './mvnw test -Dspring.profiles.active=test'
            }
        }

        stage('Quality Check') {
            steps {
                echo 'Performing quality checks...'
                // Ensure the build directory actually contains a compiled jar
                sh '''
                    if [ ! -d "target" ]; then
                        echo "Target directory not found! Build failed."
                        exit 1
                    fi
                '''
                echo 'Build is stable and ready for deployment.'
            }
        }

        stage('Deployment') {
            steps {
                echo 'Triggering Ansible for Continuous Deployment (CD)...'
                // Connect to the web server and deploy the code via SSH
                sh 'ansible-playbook -i ansible/inventory.ini ansible/deploy.yml'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully! Application has been deployed via Ansible.'
        }
        failure {
            echo 'Pipeline failed! Sending email notifications...'
            script {
                // Get the commit author's email dynamically from Git metadata
                def authorEmail = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                def commitId = sh(script: "git log -1 --pretty=format:'%h'", returnStdout: true).trim()
                def authorName = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()

                // Send email using Jenkins Email Extension Plugin
                emailext (
                    subject: "BUILD FAILED: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
                    body: """
                        <p><strong>Build Status:</strong> FAILED</p>
                        <p><strong>Commit ID:</strong> ${commitId}</p>
                        <p><strong>Author:</strong> ${authorName}</p>
                        <p>Check console output at: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    """,
                    to: "${authorEmail}, ${ADMIN_EMAIL}",
                    replyTo: "${ADMIN_EMAIL}",
                    mimeType: 'text/html'
                )
            }
        }
    }
}
