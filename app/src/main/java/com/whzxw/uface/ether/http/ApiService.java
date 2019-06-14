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
    Observable<Object> sendRecoResult(@Url String url, @Query("personId")  String personId, @Query("faceId")  String faceId,
                                      @Query("score")  Float score, @Query("name")  String name, @Query("cardNo")  String cardNo, @Query("face")  Bitmap face);
}
