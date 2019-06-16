package com.whzxw.uface.ether.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.whzxw.uface.ether.http.ApiService;
import com.whzxw.uface.ether.http.ResponseEntity;
import com.whzxw.uface.ether.http.RetrofitManager;
import com.whzxw.uface.ether.utils.Config;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 遍历文件 传输文件
 */
public class TransformService extends Service {
    public TransformService() {
    }


    @Override
    public IBinder onBind(Intent intent) {


        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sendFaceFile() {
        // 定时任务
        File[] files = new File(Config.BASE_DIR).listFiles();
        Observable.fromArray(files)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<File, ObservableSource<ResponseEntity>>() {
                    @Override
                    public ObservableSource<ResponseEntity> apply(File file) throws Exception {
                        return RetrofitManager.getInstance()
                                .apiService
                                .uploadFile(ApiService.uploadFileUrl, file);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseEntity>() {
                    @Override
                    public void accept(ResponseEntity o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
