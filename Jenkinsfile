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
        stage("StaticAnalysis") {
            steps {
                sh "mvn checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs"
            }
        }
        stage("Test") {
            steps {
                sh "mvn verify"
            }
        }
    }
    post {
        always {
            junit "**/target/surefire-reports/*.xml"
            recordIssues enabledForFailure: true, tools: [mavenConsole(), java(), javaDoc()]
            recordIssues enabledForFailure: true, tool: checkStyle()
            recordIssues enabledForFailure: true, tool: spotBugs()
            recordIssues enabledForFailure: true, tool: cpd(pattern: '**/target/cpd.xml')
            recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
        }
    }
}
