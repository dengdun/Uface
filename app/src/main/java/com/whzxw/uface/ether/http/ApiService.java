package com.whzxw.uface.ether.http;

import android.graphics.Bitmap;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 接口封装类
 */
public interface ApiService {
    /**
     * 1. 查询设备名称接口
     * 请求地址: http://192.168.10.150:8082/locker/selectDevName
     * 请求参数: 无
     * 返回结果:
     * {
     *     "success": true,
     *     "message": "查询成功",
     *     "result": "开发版@储物柜名称"
     * }
     * 其它说明: 查询异常时success返回false
     */
    public String queryDevNameUrl = "http://192.168.10.150:8082/locker/selectDevName";

    /**
     * 2. 识别回调接口
     * 请求地址: http://192.168.10.150:8082/locker/callback4App
     * 请求参数:
     * personId	人员唯一标识
     * faceId		人脸唯一标识
     * score		识别分数
     * name 		姓名
     * cardNo		一卡通号码
     * face 		人脸识别快照
     * type		操作类型，0. 储物; 1. 临取; 2. 整取;
     */
    public String recoCallBackUrl = "http://192.168.10.150:8082/locker/callback4App";

    /**
     * 通知树莓派开始工作了
     * @param url
     * @return
     */
    @POST
    Observable<ResponseEntity> startApp(@Url String url);

    /**
     * 识别成功之后，把数据给他们
     * @param url
     * @return
     */
    @POST
    Observable<ResponseEntity> sendRecoResult(@Url String url, @Query("personId")  String personId, @Query("faceId")  String faceId,
                                      @Query("score")  Float score, @Query("name")  String name,
                                      @Query("cardNo")  String cardNo, @Query("face")  Bitmap face,
                                      @Query("type") String type);

    /**
     * 查詢设备名字
     * @param url
     * @return
     */
    @POST
    Observable<ResponseEntity> queryMachineName(@Url String url);

}
