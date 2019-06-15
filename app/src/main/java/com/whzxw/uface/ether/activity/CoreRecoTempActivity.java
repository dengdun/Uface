package com.whzxw.uface.ether.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
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
import com.uniubi.uface.ether.R;
import com.uniubi.uface.ether.base.UfaceEtherImpl;
import com.uniubi.uface.ether.config.ServiceOptions;
import com.uniubi.uface.ether.config.configenum.algorithm.FaceOrientation;
import com.uniubi.uface.ether.core.EtherFaceManager;
import com.uniubi.uface.ether.core.bean.AliveResult;
import com.uniubi.uface.ether.core.bean.CheckFace;
import com.uniubi.uface.ether.core.bean.IdentifyResult;
import com.uniubi.uface.ether.core.cvhandle.FaceHandler;
import com.uniubi.uface.ether.core.faceprocess.IdentifyResultCallBack;
import com.uniubi.uface.ether.outdevice.utils.FileNodeOperator;
import com.uniubi.uface.ether.utils.ImageUtils;
import com.whzxw.uface.ether.EtherApp;
import com.whzxw.uface.ether.adapter.GridItemDecoration;
import com.whzxw.uface.ether.adapter.LockerAdapter;
import com.whzxw.uface.ether.database.PersonTable;
import com.whzxw.uface.ether.http.ApiService;
import com.whzxw.uface.ether.http.ResponseEntity;
import com.whzxw.uface.ether.http.RetrofitManager;
import com.whzxw.uface.ether.utils.CameraUtils;
import com.whzxw.uface.ether.utils.NetHttpUtil;
import com.whzxw.uface.ether.utils.ShareUtils;
import com.whzxw.uface.ether.view.CountDownTimer;
import com.whzxw.uface.ether.view.FaceView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.whzxw.uface.ether.activity.SplashActivity.INTENT_DEVNAME;

/**
 * @author qiaopeng
 * @date 2018/08/02
 *
 *  这里是最新的界面的一些功能
 *
 */
public class CoreRecoTempActivity extends AppCompatActivity implements IdentifyResultCallBack, EtherFaceManager.OnServerConnectListener {

    private FaceHandler faceHandler;



    private CameraUtils cameraRGB;

    private CameraUtils cameraIR;

    private EtherFaceManager etherFaceManager;

    private ServiceOptions serviceOptions;
    private String resultCode = "";
    private int deviceType = 2;
    /**
     * 跳转识别是哪个按钮
      */
    int recoFromWhichButton = -1;
    // 是否正在显示屏保
    private static boolean isPreViewCamera = true;
    private byte[] yuvByteData;
    @BindView(R.id.rgb_camera)
    TextureView textureRGBView;
    @BindView(R.id.ir_camera)
    TextureView textureIRView;

    @BindView(R.id.fvRGB)
    FaceView faceView;

    @BindView(R.id.adwebview)
    WebView adWebView;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.first_group)
    Group firstScreenGroup;
    @BindView(R.id.two_group)
    Group twoScreenGroup;

    @BindView(R.id.countdown_timer)
    CountDownTimer countDownTimer;

    @BindView(R.id.school_name)
    AppCompatTextView schoolNameView;

    @BindView(R.id.alert)
    AppCompatTextView alertView;

    @BindView(R.id.camera_title)
    AppCompatTextView title;



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


        init();
        initCamera();
        initWebView();
        initRecycleView();

        initWhiteYuvImage();
        etherFaceManager.startService(this, this, this);

        countDownTimer.setDeadlineListener(new CountDownTimer.DeadlineListener() {
            @Override
            public void deadline() {
                toMainScreen();
            }
        });
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

        GridItemDecoration gridItemDecoration = new GridItemDecoration();
        recyclerView.addItemDecoration(gridItemDecoration);
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
                        if (!isPreViewCamera){
                            cameraRGB.setScreenshotListener(new CameraUtils.OnCameraDataEnableListener() {
                                @Override
                                public void onCameraDataCallback(byte[] data, int camId) {
                                    Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.yuvImg2BitMap(data,640, 480), 90);
                                    NetHttpUtil.sendMessage(personTable.getPseronId(), personTable.getFaceId(), 100f, personTable.getName(), personTable.getCardNO(), bitmap);

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


        Intent intent = getIntent();
        String schoolName = intent.getStringExtra(INTENT_DEVNAME);
        schoolNameView.setText(schoolName);
    }

    private void initCamera() {
        switch (deviceType) {
            case 0:
                faceView.initPaint(false, 0);

                cameraRGB = new CameraUtils(this, 0, 0);
                cameraIR = new CameraUtils(this, 1, 0);
                cameraIR.initCamera(1, new CameraUtils.OnCameraDataEnableListener() {
                    @Override
                    public void onCameraDataCallback(byte[] data, int camId) {
                        etherFaceManager.pushIRFrameData(data);
                    }
                });
                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_UP);
                break;
            case 1:
                faceView.initPaint(false, 0);

                cameraRGB = new CameraUtils(this, 0, 0);
                cameraIR = new CameraUtils(this, 1, 0);
                cameraIR.initCamera(1, new CameraUtils.OnCameraDataEnableListener() {
                    @Override
                    public void onCameraDataCallback(byte[] data, int camId) {
                        etherFaceManager.pushIRFrameData(data);
                    }
                });
                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_RIGHT);
                break;
            case 2:
                faceView.initPaint(false, 2);

                cameraRGB = new CameraUtils(this, 0, 90);
                cameraIR = new CameraUtils(this, 1, 90);
                cameraIR.initCamera(1, new CameraUtils.OnCameraDataEnableListener() {
                    @Override
                    public void onCameraDataCallback(byte[] data, int camId) {
                        etherFaceManager.pushIRFrameData(data);
                    }
                });
                UfaceEtherImpl.getAlgorithmOptions().setFaceOrientation(FaceOrientation.CV_FACE_LEFT);
                break;
            default:
                break;
        }

        cameraRGB.initCamera(0, new CameraUtils.OnCameraDataEnableListener() {
            @Override
            public void onCameraDataCallback(byte[] data, int camId) {
                // 判断是否屏保
                if (isPreViewCamera) {
                    if (yuvByteData != null)
                        etherFaceManager.pushRGBFrameData(yuvByteData);
                } else {
                    etherFaceManager.pushRGBFrameData(data);
                }

            }
        });

        textureRGBView.setSurfaceTextureListener(cameraRGB);
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
        Log.i("coreCall", "分数=" + recognition.getScore());
        if (isPreViewCamera) return;
        Log.i("coreCall", "分数=" + recognition.getScore());

        // 屏保的时候不让提交数据
        if (recognition.isAlivePass()&&recognition.isVerifyPass()) {
            Log.i("coreCall", "通过正在启动柜门");

            // 创建
            Observable<IdentifyResult> identifyResultObservable = Observable.just(recognition);
            Observable<Integer> just = Observable.just(recoFromWhichButton);
            Observable<Object[]> dataObservable = Observable.zip(identifyResultObservable, just, new BiFunction<IdentifyResult, Integer, Object[]>() {
                @Override
                public Object[] apply(IdentifyResult identifyResult, Integer integer) throws Exception {
                    return new Object[]{identifyResult, integer};
                }
            });
            // 创建查数据的观察事件
            Observable<PersonTable> personTableObservable = Observable.just(recognition).map(new Function<IdentifyResult, List<PersonTable>>() {
                @Override
                public List<PersonTable> apply(IdentifyResult identifyResult) throws Exception {
                    return EtherApp.daoSession.queryRaw(PersonTable.class, "where FACE_ID = ? and PSERON_ID = ?", recognition.getFaceId(), recognition.getPersonId());
                }
            }).filter(new Predicate<List<PersonTable>>() {
                @Override
                public boolean test(List<PersonTable> personTables) throws Exception {
                    return personTables != null && personTables.size() != 0;
                }
            }).map(new Function<List<PersonTable>, PersonTable>() {
                @Override
                public PersonTable apply(List<PersonTable> personTables) throws Exception {
                    return personTables.get(0);
                }
            });
            Observable.zip(dataObservable, personTableObservable, new BiFunction<Object[], PersonTable, Object[]>() {
                @Override
                public Object[] apply(Object[] objects, PersonTable personTable) throws Exception {

                    return new Object[]{objects[0], objects[1], personTable};
                }
            }).flatMap(new Function<Object[], Observable<ResponseEntity>>() {
                @Override
                public Observable<ResponseEntity> apply(Object[] objects) throws Exception {
                    IdentifyResult identifyResult = (IdentifyResult) objects[0];
                    Integer type = (Integer) objects[1];
                    PersonTable personTable = (PersonTable) objects[2];
                    return RetrofitManager.getInstance()
                            .apiService
                            .sendRecoResult(ApiService.recoCallBackUrl, identifyResult.getPersonId(),
                                    identifyResult.getFaceId(), identifyResult.getScore(),
                                    personTable.getName(), personTable.getCardNO(),
                                    identifyResult.getBitmap(), type + "");
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showAlert("快马加鞭开箱子！", true);
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread()) // 指定线程之后，线程调用的是上游的回调在哪个线程中。
                    .subscribe(new Consumer<ResponseEntity>() {
                @Override
                public void accept(ResponseEntity responseEntity) throws Exception {
                    showAlert(responseEntity.getMessage(), true);
                    countDownTimer.stopCount();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    showAlert("破网络失败了", true);
                }
            });

            return;
        }
        if (recognition.isAlivePass()&&!recognition.isVerifyPass()){
            Log.i("coreCall", "活体检测通过， 识别没通过");
            return;
        }
        if (!recognition.isAlivePass()&&recognition.isVerifyPass()){
            Log.i("coreCall", "活体检测没通过， 识别通过");
            return;
        }
        if (!recognition.isAlivePass()&&!recognition.isVerifyPass()){
            Log.i("coreCall", "活体检测没通过， 识别没通过");
            return;
        }
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
//        if (serviceOptions.getRecoMode() == RecoMode.LOCALONLY && serviceOptions.getRecoPattern() == RecoPattern.VERIFY) {
//            bitmapCompare();
//        }
    }

    @Override
    public void onDisconnected() {

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraRGB.closeCamera();
        cameraIR.closeCamera();
    }

    /**
     * 跳转到主屏幕
     */
    @OnClick(R.id.btn_back)
    public void toMainScreen() {

        firstScreenGroup.setVisibility(View.VISIBLE);
        twoScreenGroup.setVisibility(View.INVISIBLE);
        countDownTimer.stopCount();
        // 设置预览值
        isPreViewCamera = true;

        showAlert("", false);
    }

    /**
     * 跳转到识别屏幕 显示摄像头的那种
     * @param view
     */
    @OnClick({R.id.open, R.id.temp_open, R.id.final_open})
    public void toRecoScreen(View view) {
        // 设置预览值
        isPreViewCamera = false;

        countDownTimer.startCountDown(15);
        firstScreenGroup.setVisibility(View.INVISIBLE);
        twoScreenGroup.setVisibility(View.VISIBLE);

        switch (view.getId()) {
            case R.id.open:
                recoFromWhichButton = 0;
                title.setText("存件");
                break;
            case R.id.temp_open:
                title.setText("中途取件");
                recoFromWhichButton = 1;
                break;
            case R.id.final_open:
                title.setText("取件");
                recoFromWhichButton = 2;
                break;
        }
    }

    /**
     * 显示识别后文字
     */
    public void showAlert(String alert, boolean show) {
        isPreViewCamera = true;
        alertView.setText(alert);
        if (show)
            alertView.setVisibility(View.VISIBLE);
        else
            alertView.setVisibility(View.INVISIBLE);
    }

}
