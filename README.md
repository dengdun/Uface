1.添加人脸接口 http://ip:8090/faceCreate  POST 方法
    参数:
            faceId  人脸照片的id
            personId
            imgBase64 照片的base64格式
     正确的话返回:人脸照片添加成功

2.删除人脸接口 http://ip:8090/faceDelete  POST 方法
    参数:
            faceId  人脸照片的id
            personId
    正确的话返回:人脸照片删除成功

3.设置广告页  http://ip:8090/setting  POST 方法   三个要同时设置,不然报错
    参数:urlad 上部分的广告的URL页
         urlad2 识别框旁边的URL地址
         resulturl 发送识别结果的地址

    正确的返回:地址设置成功


4.主动发送网络请求,地址就是步骤3中的resulturl这个地址.  POST 方法
    参数:1.personId
         2.faceId
         3.识别分数


5. 心跳包   http://ip:8090/pong  POST 方法
a.参数  ping  空字符串

返回:pong 成功,其它为失败

6.屏保开关  http://ip:8090/screenSaver  POST 方法
a. 参数  screenSaver    true就是屏保,false关屏保

