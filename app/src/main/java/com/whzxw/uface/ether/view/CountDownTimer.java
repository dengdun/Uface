package com.whzxw.uface.ether.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 倒计时器
 */
public class CountDownTimer extends AppCompatTextView {
    public CountDownTimer(Context context) {
        super(context);
        startCountDown
                ();
    }

    public CountDownTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        startCountDown();
    }

    public CountDownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startCountDown();
    }

    DeadlineListener deadlineListener;

    public void setDeadlineListener(DeadlineListener deadlineListener) {
        this.deadlineListener = deadlineListener;
    }

    public void startCountDown() {
        final int countDown = 15;
        Observable
                .interval(0, 1 , TimeUnit.SECONDS)
                .take(countDown + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return countDown - aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        CountDownTimer.super.setText(aLong + "s");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (deadlineListener != null)
                            deadlineListener.deadline();
                    }
                });

    }


    public interface DeadlineListener {
        public void deadline();
    }
}
