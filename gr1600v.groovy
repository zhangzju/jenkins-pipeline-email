node {
    try {
        docker.image("fd2").inside("-t -i -e TERM=linux -u root -v /var/lib/jenkins/workspace/GR1600vV1_checkout/TP_SDK:/code") {
            // sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 env_build'
            sh 'cd /code;ls -al .; echo "OK"'
            sh 'cd /code/4.16L.05/toolchain ;tar -jxvf crosstools-arm-gcc-4.6-linux-3.4-uclibc-0.9.32-binutils-2.21-NPTL.Rel1.2.tar.bz2'
            sh 'cd /code/build; make MODEL=GR1600vV1 boot_build '
            sh 'cd /code/build; make MODEL=GR1600vV1 kernel_build '
            sh 'cd /code/build; make MODEL=GR1600vV1 modules_build '
            sh 'touch /code/apps/public/iptables-1.4.17/*'
            sh 'cd /code/build; make iptables'
            sh 'chmod +x /code/apps/public/nginx-1.8.0/configure'
            sh 'cd /code/build; make MODEL=GR1600vV1 apps_build '
            sh 'cd /code/build; make fakeroot_clean  '
            sh 'cd /code/build; make hosttools '
            sh 'cd /code/host_tools/fakeroot; rm -rf Makefile'
            sh 'cd /code/host_tools/fakeroot; ./configure --prefix=/usr --disable-nls'
            sh 'cd /code/host_tools/fakeroot; make '
            sh 'cd /code/build; make fakeroot '
            sh 'cd /code/build; make fs_build '
            sh 'cd /code/build; make image_build '
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
      to: 'zhangwei_w8284@tp-link.com.cn chenming@tp-link.com.cn'
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
        <p><b>Trunk URL :</b> https://spcodes.rd.tp-link.net/sw3/GR1600vV1</p>
        <p><b>Docker images :</b> gr1600v:0.1</p>
        <p><b>Console Message :</b> Please check the attachment</p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'zhangwei_w8284@tp-link.com.cn chenming@tp-link.com.cn',
      attachLog: true, compressLog: true
    )
}