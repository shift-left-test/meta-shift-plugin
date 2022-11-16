pipeline {
    agent {
        docker {
            image "cart.lge.com/swte/jenkins-dev:latest"
        }
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage("Javadoc") {
            steps {
                sh "mvn javadoc:javadoc"
            }
        }
        stage("StaticAnalysis") {
            steps {
                sh "mvn checkstyle:checkstyle jxr:jxr pmd:pmd pmd:cpd spotbugs:spotbugs"
            }
        }
        stage("Test") {
            steps {
                sh "HOME=${env.WORKSPACE}/home mvn verify"
            }
        }
        stage("Coverage") {
            steps {
                sh "mvn jacoco:report"
            }
        }
        stage("MutationTest") {
            steps {
                sh "mvn org.pitest:pitest-maven:mutationCoverage"
            }
        }
    }
    post {
        always {
            recordIssues enabledForFailure: true, tools: [mavenConsole(), java(), javaDoc()]
            recordIssues enabledForFailure: true, tool: checkStyle()
            recordIssues enabledForFailure: true, tool: spotBugs()
            recordIssues enabledForFailure: true, tool: cpd(pattern: '**/target/cpd.xml')
            recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
            junit "**/target/surefire-reports/*.xml"
            jacoco()
            pitmutation killRatioMustImprove: false, minimumKillRatio: 50.0, mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
            cleanWs disableDeferredWipeout: true
        }
    }
}
