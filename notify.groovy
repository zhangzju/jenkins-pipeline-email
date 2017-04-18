node {
    try {
        docker.image("ericchu0302/ubuntu14.04-mtk:v2").inside("-t -i -e TERM=linux -u root -v /var/lib/jenkins/workspace/HomeGatewayCheckout/linux_mtk_VR500vTTHGW:/home/bba/git-src") {
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 env_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 boot_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 kernel_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 modules_build'
            //sh 'mkdir -p /home/bba/git-src/work/20170316_HGW/linux_mtk_VR500vTTHGW/apps/public/zebra-0.95a; touch /home/bba/git-src/work/20170316_HGW/linux_mtk_VR500vTTHGW/apps/public/zebra-0.95a/missing'
            //sh 'mkdir -p /mnt/hgfs/share/mySvn/linux/zebra; touch /mnt/hgfs/share/mySvn/linux/zebra/missing'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 apps_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 fs_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 mkkernel_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 image_build'
            sh 'ls -al /home/bba/git-src; echo "OK"'
        }
        notifySuccessful()
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}

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
        <p>Project Name：'${env.JOB_NAME}'</p>
        <p>Build Times:'${env.BUILD_NUMBER}'</p>
        <p>Docker images：ericchu0302/ubuntu14.04-mtk:v2</p>
        <p>The author：'${env.CHANGE_AUTHOR}'</p>
        <p><a href='${env.BUILD_URL}'>Click for details</a></p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'dean.hsiao@tp-link.com zhangwei_w8284@tp-link.com.cn'
    )
}