package com.whzxw.uface.ether.http;

import com.whzxw.uface.ether.utils.RegularUtils;
import com.whzxw.uface.ether.utils.ShareferenceManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
//                    Cache cache = new Cache(new File(Environment.getDownloadCacheDirectory(), "HttpCache"),
//                            1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder()
                            //设置连接超时时间
                            .connectTimeout(5, TimeUnit.SECONDS)
                            //设置读取超时时间
                            .readTimeout(10, TimeUnit.SECONDS)
                            //设置写入超时时间
                            .writeTimeout(10, TimeUnit.SECONDS)
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
//                            .cache(cache)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    public Single<Response> get(final String url) {
        return Single.create(new SingleOnSubscribe<Response>() {
            @Override
            public void subscribe(final SingleEmitter<Response> emitter) throws Exception {
                Request request = new Request.Builder().url(url).build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        emitter.onSuccess(response);
                    }
                });
            }
        });
    }
}
