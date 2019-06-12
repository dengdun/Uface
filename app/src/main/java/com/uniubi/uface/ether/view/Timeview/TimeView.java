package com.uniubi.uface.ether.view.Timeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 显示时间的控件  使用Rx封装了
 */
public class TimeView extends android.support.v7.widget.AppCompatTextView {

    private SimpleDateFormat simpleDateFormat;
    private Paint paint;
    private Disposable mDisposable;

    public TimeView(Context context) {
        super(context);
        init();
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  \nHH:mm:ss");
        paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);
        super.setText(simpleDateFormat.format(new Date()));
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable=disposable;

                    }

                    @Override
                    public void onNext(Long aLong) {
                        setText(simpleDateFormat.format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
