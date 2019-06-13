package com.whzxw.uface.ether.view.snowview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.uniubi.uface.ether.BuildConfig;
import com.uniubi.uface.ether.R;
import com.whzxw.uface.ether.EtherApp;
import com.whzxw.uface.ether.utils.ShareUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class SnowView extends View {
    Paint paint = new Paint();
    private Bitmap bitmap;
    private Timer timer;
    private Bitmap Scaledbitmap;

    private String schoolNameLine1 = (String) ShareUtils.get(EtherApp.context,"schooleNameLine1", "湖北省武汉市");
    private String schoolNameLine2 = (String)ShareUtils.get(EtherApp.context,"schooleNameLine2", "高中");
    private SimpleDateFormat simpleDateFormat, simpleDateDateFormat;
    private String versionCode = null;
    public SnowView(Context context) {
        super(context);
        init(context);

    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.face_lock);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);
        paint.setTextSize(20f);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true); // 抗锯齿
        simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateDateFormat = new SimpleDateFormat("MM 月 dd 日 E");

        stratTime();


        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(true);
                    }
                });
            }
        })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Boolean>>() {
                    @Override
                    public void accept(List<Boolean> booleans) throws Exception {
                        if (booleans.size() > 5) {
                            Log.i("canvas", "> 5");
                            versionCode  = BuildConfig.VERSION_CODE +"";
                            invalidate();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        versionCode = null;
                    }
                });

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Scaledbitmap == null) {
            Scaledbitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
        }
        canvas.drawBitmap(Scaledbitmap, 0,0, paint);
        paint.setTextSize(20f);

        canvas.drawText(schoolNameLine1, 10, getHeight()/10*9, paint);
        canvas.drawText(schoolNameLine2, 10, getHeight()/10*9 + 30f, paint);


        if (versionCode != null) {
            paint.setTextSize(10f);
            canvas.drawText(BuildConfig.VERSION_CODE +"", getWidth()/10*6 , getHeight()/10*9 +10f, paint);
            versionCode = null;
        } else {
            paint.setTextSize(40f);
            canvas.drawText(simpleDateFormat.format(new Date()), getWidth()/10*6 , getHeight()/10*9 +10f, paint);
            paint.setTextSize(14f);
            canvas.drawText(simpleDateDateFormat.format(new Date()), getWidth()/10*6 , getHeight()/10*9+30f , paint);
        }
    }

    public void setSchoolName (String schooleNameLine1, String schoolNameLine2) {
        this.schoolNameLine1 = schooleNameLine1;
        this.schoolNameLine2 = schoolNameLine2;
        postInvalidate(getWidth()/2, getHeight()/10*9 - 40, getWidth(), getHeight());
    }

    public void stratTime() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate(getWidth()/2, getHeight()/10*9 - 40, getWidth(), getHeight());
            }
        }, 2000, 60000);
    }



}
