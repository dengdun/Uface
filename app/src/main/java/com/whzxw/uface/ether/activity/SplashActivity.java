package com.whzxw.uface.ether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uniubi.uface.ether.BuildConfig;
import com.whzxw.uface.ether.http.ApiService;
import com.whzxw.uface.ether.http.ResponseDeviceEntity;
import com.whzxw.uface.ether.http.RetrofitManager;
import com.whzxw.uface.ether.utils.Voiceutils;
import com.whzxw.uface.ether.utils.XlogUitls;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 正在启动界面
 */
public class SplashActivity extends AppCompatActivity {

    public static final String INTENT_DEVNAME = "devName";
    public static final String INTENT_DEVCODE = "devCode";
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Voiceutils.init();
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText("正在启动...");
        textView.setTextSize(40f);

        linearLayout.addView(textView);
        setContentView(linearLayout);
        /**
         * 查询成功，前置机启动了。
         */
        disposable = RetrofitManager.getInstance()
                .apiService
                .queryMachineName(ApiService.queryDevNameUrl)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (BuildConfig.DEBUG)  {
                                    return Observable.error(new Throwable(""));
                                } else {
                                    // 测试的时候注释上
                                    if (throwable instanceof IOException) {
                                        return Observable.just(1).delay(2, TimeUnit.SECONDS);
                                    } else {
                                        return Observable.error(new Throwable(""));
                                    }
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseDeviceEntity>() {
                    @Override
                    public void accept(ResponseDeviceEntity responseEntity) throws Exception {
                        Log.i("jin", "get result");
                        Intent intent = new Intent(getApplicationContext(), CoreRecoTempActivity.class);
                        ResponseDeviceEntity.Device result = responseEntity.getResult();
                        intent.putExtra(INTENT_DEVNAME, result.getDeviceName());
                        intent.putExtra(INTENT_DEVCODE, result.getDeviceNo());
                        startActivity(intent);
                        finish();
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Intent intent = new Intent(getApplicationContext(), CoreRecoTempActivity.class);
                        intent.putExtra(INTENT_DEVNAME, "什么机子");
                        startActivity(intent);
                        finish();
                        Log.i("jin", "throw erro");

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("jin", "run throw erro");

                    }
                });
        XlogUitls.init(getApplication());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }
}
