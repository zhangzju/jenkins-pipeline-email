node {
    try {
        docker.image("e48").inside("-t -i -e TERM=linux -u root -v /var/lib/jenkins/workspace/C20(SP)v4_checkout:/home/bba") {
            // sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 env_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build;ls -al .; echo "OK"'
            sh 'cd /home/bba; sh C20.sh'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 env_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 boot_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 kernel_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 modules_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 apps_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 fs_build'
            sh 'cd /home/bba/TP_SDK/BBA_1.5_platform/build; make MODEL=C20V4 image_build'
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
// if(to != null && !to.isEmpty()) {
//     mail to: to, subject: "Vagrant Test has finished with ${currentBuild.result}",
//             body: "See ${env.BUILD_URL}"
// }

def notifySuccessful() {
    emailext (
      subject: "【Jenkins CI】SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'zhangwei_w8284@tp-link.com.cn chenghuazhuo@tp-link.com.cn'
    )
 }

def notifyFailed() {
//    def CAUSE = sh(script: 'cd /var/lib/jenkins/workspace/HomeGatewayCheckout/linux_mtk_VR500vTTHGW; svn log -r \'COMMITTED\' | sed \':a;N;\$!ba;s/\\n/<br\\/>\\n/g\' > /var/lib/jenkins/workspace/HomeGatewayCheckout/linux_mtk_VR500vTTHGW/log.txt;',returnStdout: true).trim()
//    def MyLogTxt=readFile('/var/lib/jenkins/workspace/HomeGatewayCheckout/linux_mtk_VR500vTTHGW/log.txt')
//    echo "${MyLogTxt}"

   emailext (
      subject: "【Jenkins CI】FAILED: Project '${env.JOB_NAME}'",
      body: """<h3>Build Failed:  '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</h3>
        <p><b>Project Name :</b> '${env.JOB_NAME}'</p>
        <p><b>Build Times :</b> '${env.BUILD_NUMBER}'</p>
        <p><b>Trunk URL :</b> https://spcodes.rd.tp-link.net/sw1/Archer_C20SP_V4</p>
        <p><b>Docker images :</b>c20spv4:0.1</p>
        <p><b>Console Message :</b> Please check the attachment</p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'zhangwei_w8284@tp-link.com.cn chenghuazhuo@tp-link.com.cn',
      attachLog: true, compressLog: true
    )
}