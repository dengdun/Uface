package com.uniubi.uface.etherdemo.http;

import android.os.Environment;

import com.uniubi.uface.ether.network.LoggerIntercept;
import com.uniubi.uface.etherdemo.EtherApp;
import com.uniubi.uface.etherdemo.utils.RegularUtils;
import com.uniubi.uface.etherdemo.utils.ShareUtils;
import com.uniubi.uface.etherdemo.utils.ShareferenceManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络封装
 */
public class RetrofitManager {

    private static RetrofitManager instace = null;
    private static OkHttpClient mOkHttpClient;
    public final ApiService apiService;


    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(initOkHttpClient())
                .baseUrl(RegularUtils.getHost(ShareferenceManager.getStartAppUrl()))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static RetrofitManager getInstance() {
        synchronized (RetrofitManager.class) {
            if (instace == null)
                synchronized (RetrofitManager.class) {
                    instace = new RetrofitManager();
                }
        }
        return instace;
    }


    /**
     * 单例模式获取 OkHttpClient
     */
    private static OkHttpClient initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(Environment.getDownloadCacheDirectory(), "HttpCache"),
                            1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder()
                            //设置连接超时时间
                            .connectTimeout(30, TimeUnit.SECONDS)
                            //设置读取超时时间
                            .readTimeout(300, TimeUnit.SECONDS)
                            //设置写入超时时间
                            .writeTimeout(300, TimeUnit.SECONDS)
                            //默认重试一次
                            .retryOnConnectionFailure(true)
                            //添加请求头拦截器
//                            .addInterceptor(InterceptorHelper.getHeaderInterceptor())
                            //添加日志拦截器
                            .addInterceptor(httpLoggingInterceptor)
                            //添加缓存拦截器
//                            .addInterceptor(InterceptorHelper.getCacheInterceptor())
                            //添加重试拦截器
//                            .addInterceptor(InterceptorHelper.getRetryInterceptor())

                            //缓存
                            .cache(cache)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

}
