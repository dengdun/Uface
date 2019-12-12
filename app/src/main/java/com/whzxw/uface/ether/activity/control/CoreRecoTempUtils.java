package com.whzxw.uface.ether.activity.control;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.whzxw.uface.ether.http.RetrofitManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

/**
 * @author dengdun on 2019-12-11.
 * 工具类
 */
public class CoreRecoTempUtils {
    private static final String TAG = "CoreRecoTempUtils";

    @SuppressLint("CheckResult")
    public static void loadImage(final ImageView imageView, String url) {
        Log.i(TAG, "loadImage url:" + url);
        RetrofitManager.getInstance().get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Response, Bitmap>() {
                    @Override
                    public Bitmap apply(Response res) throws Exception {
                        byte[] bytes = res.body().bytes();
                        Log.d(TAG, "loadimage:" + bytes);
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        Log.i(TAG, "bitmap:" + bitmap);
                        if (imageView != null && bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "load bitmap error", throwable);
                    }
                });
    }
}
