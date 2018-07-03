node {
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        checkout scm
        //gradleHome = tool 'gradle-3'
    }
    stage('Build') {
        // Run the gradle assemble
        echo 'Building'
        sh "./gradlew assemble"
    }
    stage('Deploy') {
        sh 'echo branch $BRANCH_NAME'
        sh 'git name-rev --name-only HEAD > GIT_BRANCH'
        sh 'cat GIT_BRANCH'
        git_branch = readFile('GIT_BRANCH').trim()
        if (git_branch == 'remotes/origin/dev') {
            sh "./gradlew --gradle-user-home=/homes/sub_adm/secrets -PsshKeyFile=/var/lib/jenkins/.ssh/id_rsa -Penv=dev deployJar"
        } else if (git_branch == 'remotes/origin/master') {
            sh "./gradlew --gradle-user-home=/homes/sub_adm/secrets -PsshKeyFile=/var/lib/jenkins/.ssh/id_rsa -Penv=test deployJar"
        }
        cleanWs()
    }
}
