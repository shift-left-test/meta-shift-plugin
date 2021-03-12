pipeline {
    agent {
        docker {
            image "cart.lge.com/swte/yocto:18.04"
        }
    }
    stages {
        stage("Test") {
            steps {
                sh "mvn verify"
            }
        }
        stage("Report") {
            steps {
                junit "target/surefire-reports/*.xml"
            }
        }
    }
}
