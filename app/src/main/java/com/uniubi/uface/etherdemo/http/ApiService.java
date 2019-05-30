package com.uniubi.uface.etherdemo.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
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
    @GET("startApp")
    Observable<ResponseEntity> startApp();
}
