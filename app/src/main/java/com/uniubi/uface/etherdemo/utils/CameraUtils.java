package com.uniubi.uface.etherdemo.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by uniubi on 2017/10/23.
 */

public class CameraUtils implements TextureView.SurfaceTextureListener {
    private SurfaceTexture mSurfaceTexture;
    private Camera mCamera;
    private Context mContext;
    private byte[] cameraData = new byte[640 * 480 * 3 / 2];

    public interface OnCameraDataEnableListener{
        void onCameraDataCallback(byte[] data, int camId);
    }

    private OnCameraDataEnableListener listener;
    /**
     * 取流监听
     */
    private Timer timer = new Timer();
    private long count = 0;
    private long oldCount = 0;
    private int camId;
    private int orientation;
    private boolean isCheckCamera = true;


    public CameraUtils(Context mContext, int cameraId, int orientation) {
        this.camId = cameraId;
        this.orientation = orientation;
        this.mContext = mContext;
    }

    public CameraUtils() {
    }

    public CameraUtils(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
        handler.sendEmptyMessageDelayed(0, 300);
//        initCamera(camId);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        closeCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        LogUtils.d("CameraUtils-----------onSurfaceTextureUpdated");
    }

    /**
     * 初始化相机
     */
    public void initCamera(int cameraId,OnCameraDataEnableListener listener) {
        this.listener = listener;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        if (mCamera == null) {
            try {
                mCamera = Camera.open(cameraId);
            } catch (Exception e) {
                e.printStackTrace();
                //相机不可用
                cameraDisable();
            }
        }

        if (mCamera != null) {
            try {
               Camera.Parameters parameters =  mCamera.getParameters();
               parameters.setPreviewSize(640,480);
                String flashMode = parameters.getFlashMode();
                if (flashMode != null) {
                    // FIXME
                    List<String> flashModes = parameters.getSupportedFlashModes();
                    if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                        if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(parameters);

                            } else {
                            }
                    }
                }
                mCamera.setParameters(parameters);
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.setPreviewCallbackWithBuffer(frameCallback);
                mCamera.addCallbackBuffer(cameraData);
                mCamera.setDisplayOrientation(orientation);
                mCamera.startPreview();
                confirmCameraData();
            } catch (Exception e) {
                e.printStackTrace();
                //相机不可用
                cameraDisable();
            }
        } else {
            cameraDisable();
        }
    }

    /**
     * 检查Camera是否有数据
     */
    private void confirmCameraData() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (count > oldCount) {
                    oldCount = count;
                } else {
                        cameraDisable();
                    }
                }
        }, 20 * 1000, 10 * 1000);
    }

    /**
     * 相机不可用
     */
    private void cameraDisable() {
        if(isCheckCamera){
//            CameraErrorRestart.reStartCamera(mContext);
            isCheckCamera = false;
           /* CameraErrorRestart.reStartCamera(mContext);*/
//            if (null != listener){
//                listener.onCameraNoData();
//                isCheckCamera = false;
//            }else {
//                isCheckCamera = true;
//            }
        }
    }

    /**
     * 关闭照相机
     */
    public void closeCamera() {
        handler.removeCallbacksAndMessages(null);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (null != mCamera) {
            //一定要设置为空
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 相机数据回调
     */
    Camera.PreviewCallback frameCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //传递进来的data,默认是YUV420SP的
            if (data != null && data.length > 0) {
                mCamera.addCallbackBuffer(data);
                listener.onCameraDataCallback(data,camId);
                count++;
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initCamera(camId,listener);
                    break;
            }
            super.handleMessage(msg);
        }
    };


}
