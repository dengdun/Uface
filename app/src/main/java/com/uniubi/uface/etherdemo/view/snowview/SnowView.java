package com.uniubi.uface.etherdemo.view.snowview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.uniubi.uface.etherdemo.EtherApp;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.etherdemo.utils.ShareUtils;

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
    private String schoolName = (String)ShareUtils.get(EtherApp.context,"schooleName", "学校");
    Paint paint = new Paint();


    private CountDownLatch mMeasureLatch = new CountDownLatch(1);
    private Bitmap bitmap;
    private Date currentDate;
    private Timer timer;
    private Bitmap Scaledbitmap;

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
    }
    private int yinzi = 10*8;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Scaledbitmap == null) {
            Scaledbitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
        }
        canvas.drawBitmap(Scaledbitmap, 0,0, paint);

        canvas.drawText(schoolName, 20, getHeight()/10*8, paint);
        currentDate = new Date();

        canvas.drawText(currentDate.getHours() + ":" + currentDate.getMinutes(), 20, getHeight()/10*8, paint);
        canvas.drawText(currentDate.getMonth() + "月" + currentDate.getDate() + "日  星期" + currentDate.getDay(), getWidth()/2 , getHeight()/10*8 , paint);
    }

    public void setSchoolName (String name) {
        this.schoolName = name;
    }

    public void stratTime() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 2000, 6000);
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
