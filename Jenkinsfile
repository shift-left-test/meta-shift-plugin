pipeline {
    agent {
        docker {
            image "cart.lge.com/swte/yocto:18.04"
        }
    }
    stages {
        stage("Javadoc") {
            steps {
                sh "mvn javadoc:javadoc"
            }
        }
        stage("Test") {
            steps {
                sh "mvn verify"
                junit "target/surefire-reports/*.xml"
            }
        }
    }
}
