package com.uniubi.uface.ether.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.uniubi.faceapi.CvFace;
import com.uniubi.uface.ether.EtherApp;
import com.uniubi.uface.ether.adapter.LockerAdapter;
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
import com.uniubi.uface.ether.outdevice.utils.FileNodeOperator;
import com.uniubi.uface.ether.utils.CameraUtils;
import com.uniubi.uface.ether.utils.ImageUtils;
import com.uniubi.uface.ether.utils.NetUtils;
import com.uniubi.uface.ether.utils.ShareUtils;
import com.uniubi.uface.ether.view.FaceView;
import com.uniubi.uface.etherdemo.R;
import com.uniubi.uface.ether.database.PersonTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qiaopeng
 * @date 2018/08/02
 *
 *  直接把代码改造成识别页.
 *  目前可以识别.
 */
public class CoreRecoTempActivity extends AppCompatActivity implements IdentifyResultCallBack, EtherFaceManager.OnServerConnectListener {

    private FaceHandler faceHandler;

    private TextureView textureRGBView;

    private TextureView textureIRView;

    private FaceView faceView;

    private CameraUtils cameraRGB;

    private CameraUtils cameraIR;

    private EtherFaceManager etherFaceManager;

    private ServiceOptions serviceOptions;
    private String resultCode = "";
    private int deviceType = 2;


    // 正在屏保
    private static boolean isScreenSaver = true;
    private byte[] yuvByteData;

    @BindView(R.id.adwebview)
    WebView adWebView;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recognition);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        serviceOptions = UfaceEtherImpl.getServiceOptions();
        etherFaceManager = EtherFaceManager.getInstance();
        serviceOptions.setRecoMode(RecoMode.LOCALONLY);
        serviceOptions.setRecoPattern(RecoPattern.IDENTIFY);
        serviceOptions.setWorkMode(WorkMode.OFFLINE);

        init();
        initCamera();
        initWebView();
        initRecycleView();
        etherFaceManager.startService(this, this, this);
        initWhiteYuvImage();
    }

    private void initWhiteYuvImage() {
        // 这里是读取assert的保存的一张空白的yuv的图片，保存下来，位了在屏保的时候推送到底层的识别。
        try {
            InputStream inputStream = getApplication().getResources().getAssets().open("white.yuv");
            yuvByteData = new byte[inputStream.available()];
            inputStream.read(yuvByteData);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化显示的空白页
     */
    private void initRecycleView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 10);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
        LockerAdapter lockerAdapter = new LockerAdapter();
        recyclerView.setAdapter(lockerAdapter);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputDevice inputDevice = InputDevice.getDevice(event.getDeviceId());
        if (inputDevice.getName().equals("EHUOYAN.COM RfidLoginer")) {
            // 刷卡器事件  全部事件拦截
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT) {
                    // 开始刷卡
                    resultCode = "";
                } else if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // 刷卡结束
                    Toast.makeText(CoreRecoTempActivity.this, resultCode, Toast.LENGTH_LONG).show();
                    String cardNo = resultCode.toUpperCase();
                    List<PersonTable> personTables = EtherApp.daoSession.queryRaw(PersonTable.class, "where CARD_NO = ? ", cardNo);
                    if (personTables != null && personTables.size() > 0) {
                        final PersonTable personTable = personTables.get(0);
                        // 屏保的时候不传开锁指令。
                        if (!isScreenSaver){
                            cameraRGB.setScreenshotListener(new CameraUtils.OnCameraDataEnableListener() {
                                @Override
                                public void onCameraDataCallback(byte[] data, int camId) {
                                    Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.yuvImg2BitMap(data,640, 480), 90);
                                    NetUtils.sendMessage(personTable.getPseronId(), personTable.getFaceId(), 100f, personTable.getName(), personTable.getCardNO(), bitmap);

                                }
                            });
                        }
                    }
                } else {
                    resultCode += Character.toString((char)event.getUnicodeChar());
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initWebView() {
        final String cachePath = getApplicationContext().getDir("cache",Context.MODE_PRIVATE).getPath();
        // WebView加载web资源
        adWebView.loadUrl((String) ShareUtils.get(getApplicationContext(), "urlad2", "http://home.wuhan.gov.cn/"));
        // 使用硬件GPU加载
        adWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        WebSettings webSettings = adWebView.getSettings();
        // Dom Storage（Web Storage）存储机制
        webSettings.setDomStorageEnabled(true);
        // Application Cache 存储机制 主要是对浏览器缓存的补充
        webSettings.setAppCacheEnabled(true);
        // 设置缓存的地址
        webSettings.setAppCachePath(cachePath);
        webSettings.setAppCacheMaxSize(5*1024*1024);

        webSettings.setMediaPlaybackRequiresUserGesture(false);
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        adWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        adWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // 报错了，继续加载当前页
                view.loadUrl(request.getUrl().toString());
            }
        });


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

        textureRGBView.setSurfaceTextureListener(cameraRGB);
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
    protected void onStart() {
        super.onStart();
        // 注册接收是否屏保的广播
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraRGB.closeCamera();
        cameraIR.closeCamera();
        // 注销接收是否屏保的广播
//        EventBus.getDefault().unregister(this);
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

    }

    @Override
    public void onIdentifyFailed(final IdentifyResult result) {

    }

    @Override
    public void onAliveCallBack(final AliveResult result) {

    }

    @Override
    public void onIrFaceIn(CvFace[] cvFaces) {

    }

    @Override
    public void onWholeIdentifyResult(final IdentifyResult recognition) {
        // 人脸识别的回调
        Log.i("测试1", "分数=" + recognition.getScore());
        if (isScreenSaver) return;
        Log.i("测试2", "分数=" + recognition.getScore());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), recognition.getScore() + "", Toast.LENGTH_LONG).show();
                // 屏保的时候不让提交数据
                if (recognition.isAlivePass()&&recognition.isVerifyPass()) {
                    List<PersonTable> personTables = EtherApp.daoSession.queryRaw(PersonTable.class, "where FACE_ID = ? and PSERON_ID = ?", recognition.getFaceId(), recognition.getPersonId());
                    if (personTables == null || (personTables != null && personTables.size() == 0)) return;
                    PersonTable personTable = personTables.get(0);
                    Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.yuvImg2BitMap(recognition.getRgbYuvData(), 640, 480), 90);
                    NetUtils.sendMessage(recognition.getPersonId(), recognition.getFaceId(), recognition.getScore(), personTable.getName(), personTable.getCardNO(), bitmap);
                    return;
                }
                if (recognition.isAlivePass()&&!recognition.isVerifyPass()){
                    return;
                }
                if (!recognition.isAlivePass()&&recognition.isVerifyPass()){
                    return;
                }
                if (!recognition.isAlivePass()&&!recognition.isVerifyPass()){
                    return;
                }

            }
        });
    }

    @Override
    public void onTrackStart() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        etherFaceManager.stopService(this);
        // 关灯
        FileNodeOperator.close(FileNodeOperator.LED_PATH);
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

//    /**
//     * 接收到订阅消息 设置屏幕保护的监听
//     * @param event
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(ScreenSaverMessageEvent event) {
//        if (event.isScreenSaver) {
//            // 这里显示屏保  这里不能开灯
//            isScreenSaver = true;
//            snowView.setVisibility(View.VISIBLE);
//
//            // 关灯
//            FileNodeOperator.close(FileNodeOperator.LED_PATH);
//        } else {
//            isScreenSaver = false;
//            // 开灯
//            FileNodeOperator.open(FileNodeOperator.LED_PATH);
//
//            snowView.setVisibility(View.INVISIBLE);
//        }
//    }
//    /**
//     * 接收到订阅消息  设置的url的监听
//     * @param event
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onUrlMessageEvent(SettingMessageEvent event) {
//        if (!event.urlad.isEmpty()) {
//            top_webView.loadUrl(event.urlad);
//        }
//
//        if (!event.urlad2.isEmpty()) {
//            bottom_webView.loadUrl(event.urlad2);
//        }
//
//        if (!event.schooleNameLine1.isEmpty()) {
//            snowView.setSchoolName(event.schooleNameLine1, event.schooleNameLine2);
//        }
//    }
}