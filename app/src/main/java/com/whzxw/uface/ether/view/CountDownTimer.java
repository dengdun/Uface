package com.whzxw.uface.ether.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 倒计时器
 */
public class CountDownTimer extends AppCompatTextView {
    /**
     * 默认计时器的时间
     */
    public final static int DEFAULTCOUNTNUMBER = 15;
    private Disposable disposable;

    public CountDownTimer(Context context) {
        super(context);
        super.setTextSize(20f);
    }

    public CountDownTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setTextSize(20f);
    }

    public CountDownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setTextSize(20f);
    }

    DeadlineListener deadlineListener;

    public void setDeadlineListener(DeadlineListener deadlineListener) {
        this.deadlineListener = deadlineListener;
    }

    public void startCountDown(final int CountdownNumber) {
        disposable = Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .take(CountdownNumber + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return CountdownNumber - aLong;
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

    /**
     * 强制停止计时器
     */
    public void stopCount() {
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }

    /**
     * 计时器时间到了，就回调。
     */
    public interface DeadlineListener {
        public void deadline();
    }
}
