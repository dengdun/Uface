package com.uniubi.uface.etherdemo.view.snowview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.uniubi.uface.etherdemo.EtherApp;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.etherdemo.utils.ShareUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class SnowView extends View {
    private SnowUtils mSnowUtils;

    public static final int MSG_PRODUCE_SNOW = 1;
    public static final int MSG_UPDATE_SNOW = 2;
    public static final int MSG_INVALIDATE_VIEW = 3;

    public static final int SPEED_ACCELARATE = 1;
    public static final int SPEED_DECELARATE = 2;

    public int REFRESH_VIEW_INTERVAL = 30;
    Paint paint = new Paint();
    private CountDownLatch mMeasureLatch = new CountDownLatch(1);
    private Bitmap bitmap;
    private Timer timer;
    private Bitmap Scaledbitmap;

    private String schoolNameLine1 = (String)ShareUtils.get(EtherApp.context,"schooleNameLine1", "湖北省武汉市");
    private String schoolNameLine2 = (String)ShareUtils.get(EtherApp.context,"schooleNameLine2", "高中");
    private SimpleDateFormat simpleDateFormat, simpleDateDateFormat;

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
        simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateDateFormat = new SimpleDateFormat("MM 月 dd 日 E");

        stratTime();

    }
    private int yinzi = 10*8;
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


        paint.setTextSize(40f);
        canvas.drawText(simpleDateFormat.format(new Date()), getWidth()/10*6 , getHeight()/10*9 +10f, paint);
        paint.setTextSize(14f);
        canvas.drawText(simpleDateDateFormat.format(new Date()), getWidth()/10*6 , getHeight()/10*9+30f , paint);
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


    /*private SnowHandler mSnowHandler = new SnowHandler(this);
    public static class SnowHandler extends Handler {
        private WeakReference<SnowView> mSnowView;
        public SnowHandler(SnowView view){
            mSnowView = new WeakReference<SnowView>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PRODUCE_SNOW:
                    if(mSnowView.get() != null){

                        removeMessages(MSG_INVALIDATE_VIEW);
                        sendMessage(obtainMessage(MSG_INVALIDATE_VIEW));
                    }
                    break;
                case MSG_UPDATE_SNOW:

                    break;
                case MSG_INVALIDATE_VIEW:
                    if(mSnowView.get() != null){
                        mSnowView.get().postInvalidateOnAnimation();
                        removeMessages(MSG_INVALIDATE_VIEW);
                        sendMessageDelayed(obtainMessage(MSG_INVALIDATE_VIEW), mSnowView.get().REFRESH_VIEW_INTERVAL);
                    }
                    break;
                default:
                    break;
            }
        }
    }
*/
    /*private void initSnowFlakes(){
        mSnowUtils = new SnowUtils(getContext());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSnowUtils.init(getMeasuredWidth(), getMeasuredHeight());
                mMeasureLatch.countDown();
            }
        });
    }

    public void produceSnowFlake(){
        mSnowUtils.produceSnowFlake();
    }

    private int getProduceSnowInterval(){
        return mSnowUtils.getProduceSnowInterval();
    }

    public void startSnowAnim(int level){
        mSnowUtils.setSnowLevel(level);
        startThread.start();
    }

    private void startSnowAnim(){
        mSnowHandler.removeMessages(MSG_PRODUCE_SNOW);
        mSnowHandler.obtainMessage(MSG_PRODUCE_SNOW).sendToTarget();
    }

    public void stopAnim(){
        mSnowUtils.removeAllSnowFlake();
        mSnowHandler.removeCallbacksAndMessages(null);
    }

    public void changeSnowLevel(int level){
        mSnowUtils.setSnowLevel(level);
        stopAnim();
        startSnowAnim();
    }

    private SnowHandler mSnowHandler = new SnowHandler(this);
    public static class SnowHandler extends Handler {
        private WeakReference<SnowView> mSnowView;
        public SnowHandler(SnowView view){
            mSnowView = new WeakReference<SnowView>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PRODUCE_SNOW:
                    if(mSnowView.get() != null){
                        mSnowView.get().produceSnowFlake();
                        removeMessages(MSG_PRODUCE_SNOW);
                        sendMessageDelayed(obtainMessage(MSG_PRODUCE_SNOW), mSnowView.get().getProduceSnowInterval());

                        removeMessages(MSG_INVALIDATE_VIEW);
                        sendMessage(obtainMessage(MSG_INVALIDATE_VIEW));
                    }
                    break;
                case MSG_UPDATE_SNOW:

                    break;
                case MSG_INVALIDATE_VIEW:
                    if(mSnowView.get() != null){
                        mSnowView.get().postInvalidateOnAnimation();
                        removeMessages(MSG_INVALIDATE_VIEW);
                        sendMessageDelayed(obtainMessage(MSG_INVALIDATE_VIEW), mSnowView.get().REFRESH_VIEW_INTERVAL);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private Thread startThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if(getContext() != null && !((Activity)getContext()).isDestroyed()){
                try {
                    mMeasureLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(getContext() != null && !((Activity)getContext()).isDestroyed()){
                    startSnowAnim();
                }
            }
        }
    });*/
}
