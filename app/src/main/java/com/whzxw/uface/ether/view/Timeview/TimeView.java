package com.whzxw.uface.ether.view.Timeview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 显示时间的控件  使用Rx封装了
 */
public class TimeView extends android.support.v7.widget.AppCompatTextView {

    private SimpleDateFormat simpleDateFormat, simpleTimeDateFormat;
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

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleTimeDateFormat = new SimpleDateFormat("HH:mm:ss");

        paint = new Paint();
        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        super.setTextColor(Color.BLACK);
        setGravity(Gravity.RIGHT);
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
                        Date date = new Date();
                        setText(simpleDateFormat.format(date) + "\n" + simpleTimeDateFormat.format(date));

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
