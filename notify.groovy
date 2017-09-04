node {
    def TODAY = sh(script: "date \"+%Y-%m-%d\"",returnStdout: true).trim()
    def YESTERDAY=sh(script: "date -d yesterday \"+%Y-%m-%d\"",returnStdout: true).trim()
    
    echo "get date information (today:${TODAY}, yesterday:${YESTERDAY})"
    def CAUSE = sh(script: "cd /home/spjenkins/VR500newBuildImage/VR500new; git log -p -1|sed ':a;N;$!ba;s/\s+/<\/br>\n/g'",returnStdout: true).trim()
    try {
        docker.image("ubuntu14.04-bcm:v2").inside("-t -i -e TERM=linux -u root -v /home/spjenkins/VR500newBuildImage:/home/bba/git-src") {
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=PVW422T1200ACGV1 env_build'
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=PVW422T1200ACGV1 boot_build'
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=PVW422T1200ACGV1 kernel_build'
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=PVW422T1200ACGV1 modules_build'
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=PVW422T1200ACGV1 apps_build'
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=VR600_TT_V1  cmm -B'            
            sh 'cd /home/bba/git-src/VR500new/build; make MODEL=VR600_TT_V1 fs_build'
            sh 'ls -al /home/bba/git-src; echo "OK"'
        }
        notifySuccessful()
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}

def CAUSE = sh(script: "cd /home/spjenkins/VR500newBuildImage/VR500new; git log -p -1",returnStdout: true).trim()

def to = emailextrecipients([
        [$class: 'CulpritsRecipientProvider'],
        [$class: 'DevelopersRecipientProvider'],
        [$class: 'RequesterRecipientProvider']
])
if(to != null && !to.isEmpty()) {
    mail to: to, subject: "Vagrant Test has finished with ${currentBuild.result}",
            body: "See ${env.BUILD_URL}"
}

def notifySuccessful() { 
    emailext (
      subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'dean.hsiao@tp-link.com zhangwei_w8284@tp-link.com.cn'
    )
 }

def notifyFailed() {
   
   emailext (
      subject: "【Jenkins CI】FAILED: Project '${env.JOB_NAME}'",
      body: """<h3>Build Failed:  '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</h3>
        <p><b>Project Name :</b> '${env.JOB_NAME}'</p>
        <p><b>Build Times :</b> '${env.BUILD_NUMBER}'</p>
        <p><b>Trunk URL :</b> https://spcodes.rd.tp-link.net/VR500/VR500Test.git</p>
        <p><b>Docker images :</b> ubuntu14.04-bcm:v2</p>
        <!--<p><b>The author :</b> '${env.CHANGE_AUTHOR}'</p>
        <p><a href='${env.BUILD_URL}'>Click for details</a></p>-->
        <p><b>Cause :</b> '${CAUSE}'</p>
        <p><b>Console Message :</b> Please check the attachment</p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'raoul.oyang@tp-link.com lj.chu@tp-link.com double.syu@tp-link.com roy.huang@tp-link.com dondum.su@tp-link.com rex.lu@tp-link.com dean.hsiao@tp-link.com zhangwei_w8284@tp-link.com.cn',
      attachLog: true, compressLog: true
    )
}