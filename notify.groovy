node {
    try {
        docker.image("${dockerimage}").inside("-t -i -e TERM=linux -u root -v /var/lib/jenkins/workspace/HomeGatewayCheckout:/home/bba/git-src") {
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 env_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 boot_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 kernel_build'
            sh 'cd /home/bba/git-src/build; make MODEL=VR600_TT_V1 modules_build'
            sh 'mkdir -p /home/bba/git-src/work/20170316_HGW/linux_mtk_VR500vTTHGW/apps/public/zebra-0.95a; touch /home/bba/git-src/work/20170316_HGW/linux_mtk_VR500vTTHGW/apps/public/zebra-0.95a/missing'
            sh 'mkdir -p /mnt/hgfs/share/mySvn/linux/zebra; touch /mnt/hgfs/share/mySvn/linux/zebra/missing'
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

def dockerimage = "ericchu0302/ubuntu14.04-mtk:v2"

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
      subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>构建失败: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>项目：'${env.JOB_NAME}'</p>
        <p>第'${env.BUILD_NUMBER}'次构建</p>
        <p>镜像：'${dockerimage}'</p>
        <p><a href='${env.BUILD_URL}'>点击查看详细内容</a></p>""",
      recipientProviders: [[$class: 'DevelopersRecipientProvider']],
      to: 'dean.hsiao@tp-link.com zhangwei_w8284@tp-link.com.cn'
    )
}