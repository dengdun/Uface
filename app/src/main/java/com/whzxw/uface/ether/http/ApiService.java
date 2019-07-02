package com.whzxw.uface.ether.http;

import com.uniubi.uface.ether.BuildConfig;
import com.whzxw.uface.ether.activity.SplashActivity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 接口封装类
 */
public interface ApiService {

    public String baseUrl = BuildConfig.DEBUG?"http://gbr8tx.natappfree.cc":"http://192.168.10.150:8082";

    /**
     * 1. 查询设备名称接口
     * 请求地址: /locker/selectDevName
     * 请求参数: 无
     * 返回结果:
     * {
     *     "success": true,
     *     "message": "查询成功",
     *     "result": "开发版@储物柜名称"
     * }
     * 其它说明: 查询异常时success返回false
     */
    public String queryDevNameUrl = baseUrl + "/locker/selectDevName";

    /**
     * 2. 识别回调接口
     * 请求地址: http://192.168.10.150:8082/locker/callback4App
     * 请求参数:
     *
     * personId	人员唯一标识
     * faceId		人脸唯一标识
     * score		识别分数
     * name 		姓名
     * cardNo		一卡通号码
     * face 		人脸识别快照
     * type		操作类型，0. 储物; 1. 临取; 2. 整取;
     */
    public String recoCallBackUrl = baseUrl + "/locker/callback4App";

    /**
     * 3. 查询箱位状态接口
     * 请求地址: http://192.168.10.150:8082/locker/selectBoxStatus
     * 请求参数: 无
     * 返回结果:
     * {
     *     "success": true,
     *     "message": "查询成功",
     *     "result": [
     *         {
     *             "id": 1,
     *             "sarkCode": "face-dev-02",
     *             "no": 1,
     *             "used": 1,
     *             "usable": 0,
     *             "sno": "0301",
     *             "owner": null
     *         },
     *         {
     *             "id": 2,
     *             "sarkCode": "face-dev-02",
     *             "no": 2,
     *             "used": 1,
     *             "usable": 0,
     *             "sno": "0001",
     *             "owner": null
     *         }
     *     ]
     * }
     * used为0表示未存放，1表示已存放；
     */
    public String queryCabinetUrl = baseUrl + "/locker/selectBoxStatus";

    public String adUrl = baseUrl + "/locker/toAd4App";

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
    @FormUrlEncoded
    Observable<ResponseEntity> sendRecoResult(@Url String url, @FieldMap Map<String, String> Msg);
    /**
     * 查詢设备名字
     * @param url
     * @return
     */
    @POST
    Observable<ResponseDeviceEntity> queryMachineName(@Url String url);

    /**
     * 查询柜子的状态
     * @param u
     * @return
     */
    @POST
    Observable<ResponseCabinetEntity> queryCabinet(@Url String u);

    /**
     * 查询柜子的状态
     * @param u
     * @return
     */
    @GET
    Observable<SplashActivity.Diooo> test(@Url String u);
}
