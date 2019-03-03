package com.uniubi.uface.etherdemo.activity.core;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uniubi.faceapi.CvFace;
import com.uniubi.uface.ether.base.UfaceEtherImpl;
import com.uniubi.uface.ether.config.ServiceOptions;
import com.uniubi.uface.ether.config.configenum.algorithm.FaceOrientation;
import com.uniubi.uface.ether.config.configenum.service.RecoMode;
import com.uniubi.uface.ether.config.configenum.service.RecoPattern;
import com.uniubi.uface.ether.config.configenum.service.WorkMode;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.uniubi.uface.ether.core.bean.AliveResult;
import com.uniubi.uface.ether.core.bean.CheckFace;
import com.uniubi.uface.ether.core.bean.DataSource;
import com.uniubi.uface.ether.core.bean.IdentifyResult;
import com.uniubi.uface.ether.core.cvhandle.FaceHandler;
import com.uniubi.uface.ether.core.exception.CvFaceException;
import com.uniubi.uface.ether.core.faceprocess.IdentifyResultCallBack;
import com.uniubi.uface.ether.core.utils.CoreUtils;
import com.uniubi.uface.ether.utils.ImageUtils;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.etherdemo.utils.CameraUtils;
import com.uniubi.uface.etherdemo.view.FaceView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qiaopeng
 * @date 2018/08/02
 *
 *  直接把代码改造成识别页.
 */
public class CoreRecoActivity extends AppCompatActivity implements IdentifyResultCallBack, EtherFaceManager.OnServerConnectListener {

//    @BindView(R.id.text_score)
//    TextView textScore;
//    @BindView(R.id.text_alive)
//    TextView textAlive;
    private FaceHandler faceHandler;

    private TextureView textureRGBView;

    private TextureView textureIRView;

    private FaceView faceView;

    private CameraUtils cameraRGB;

    private CameraUtils cameraIR;

    private EtherFaceManager etherFaceManager;

    private ServiceOptions serviceOptions;

    private int deviceType = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coretest);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        serviceOptions = UfaceEtherImpl.getServiceOptions();
        etherFaceManager = EtherFaceManager.getInstance();
        serviceOptions.setRecoMode(RecoMode.LOCALONLY);
        serviceOptions.setRecoPattern(RecoPattern.IDENTIFY);
        serviceOptions.setWorkMode(WorkMode.OFFLINE);
        init();
        initCamera();
        etherFaceManager.startService(this, this, this);
    }

    private void init() {
        faceHandler = new FaceHandler();
        faceHandler.init();

    }

    private void initCamera() {
        textureRGBView = findViewById(R.id.rgb_camera);
        textureIRView = findViewById(R.id.ir_camera);

        faceView = findViewById(R.id.fvRGB);

        switch (deviceType) {
            case 0:
                faceView.initPaint(false, 0);

                cameraRGB = new CameraUtils(this, 0, 0);
                cameraIR = new CameraUtils(this, 1, 0);

                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_UP);
                break;
            case 1:
                faceView.initPaint(false, 0);

                cameraRGB = new CameraUtils(this, 0, 0);
                cameraIR = new CameraUtils(this, 1, 0);
                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_RIGHT);
                break;
            case 2:
                faceView.initPaint(false, 2);

                cameraRGB = new CameraUtils(this, 0, 90);
                cameraIR = new CameraUtils(this, 1, 90);
                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_LEFT);
                break;
            default:
                break;
        }

        cameraRGB.initCamera(0, new CameraUtils.OnCameraDataEnableListener() {
            @Override
            public void onCameraDataCallback(byte[] data, int camId) {
                etherFaceManager.pushRGBFrameData(data);
            }
        });
//        cameraIR.initCamera(1, new CameraUtils.OnCameraDataEnableListener() {
//            @Override
//            public void onCameraDataCallback(byte[] data, int camId) {
//                etherFaceManager.pushIRFrameData(data);
//            }
//        });

        textureRGBView.setSurfaceTextureListener(cameraRGB);
//        textureIRView.setSurfaceTextureListener(cameraIR);
    }


    private void bitmapCompare() {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.famous);
        try {
            CvFace[] cvFaces = faceHandler.detectBGR(bmp, FaceOrientation.CV_FACE_UP);
            byte[] feature = faceHandler.getFeatureBGR(bmp, cvFaces[0]);
            List<DataSource> features = new ArrayList<DataSource>();
            features.add(new DataSource(feature, "ufaceId", "1"));
            etherFaceManager.setVerifyData(features);
            bmp.recycle();
        } catch (CvFaceException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraRGB.closeCamera();
        cameraIR.closeCamera();
    }


    @Override
    public void onFaceIn(CvFace[] cvFaces) {
    }

    @Override
    public void onFaceNull() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceView.setVisibility(View.GONE);
//                textAlive.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onTrackCallBack(final List<CheckFace> checkFaces) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkFaces == null) {
                    faceView.setVisibility(View.GONE);
                } else {
                    faceView.setVisibility(View.VISIBLE);
                    faceView.setFaces(checkFaces);

                }
            }
        });
    }

    @Override
    public void onIdentifySuccess(final IdentifyResult result) {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                textScore.setText(result.getScore() + "分");
//            }
//        });
    }

    @Override
    public void onIdentifyFailed(final IdentifyResult result) {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                textScore.setText(result.getScore() + "分");
//            }
//        });
    }

    @Override
    public void onAliveCallBack(final AliveResult result) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                textAlive.setText("活体" + result.isAlive());
//                textAlive.setVisibility(View.VISIBLE);
//            }
//        });
    }

    @Override
    public void onIrFaceIn(CvFace[] cvFaces) {

    }

    @Override
    public void onWholeIdentifyResult(final IdentifyResult recognition) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textScore.setVisibility(View.VISIBLE);
//                textScore.setText(recognition.getScore()+"");
                Toast.makeText(getApplicationContext(), recognition.getScore() + "", Toast.LENGTH_LONG).show();
                if (recognition.isAlivePass()&&recognition.isVerifyPass()) {
//                    textScore.setText("都通过");
                    return;
                }
                if (recognition.isAlivePass()&&!recognition.isVerifyPass()){
//                    textScore.setText("识别未通过");
                    return;
                }
                if (!recognition.isAlivePass()&&recognition.isVerifyPass()){
//                    textScore.setText("活体未通过");
                    return;
                }
                if (!recognition.isAlivePass()&&!recognition.isVerifyPass()){
//                    textScore.setText("都未通过");
                    return;
                }

            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                textScore.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        etherFaceManager.stopService(this);
    }

    @Override
    public void onConnected() {
        if (serviceOptions.getRecoMode() == RecoMode.LOCALONLY && serviceOptions.getRecoPattern() == RecoPattern.VERIFY) {
            bitmapCompare();
        }
    }

    @Override
    public void onDisconnected() {

    }
}
