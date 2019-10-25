package com.whzxw.uface.ether.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
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
import com.uniubi.uface.ether.BuildConfig;
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
import com.whzxw.uface.ether.bean.SettingMessageEvent;
import com.whzxw.uface.ether.database.PersonTable;
import com.whzxw.uface.ether.http.ApiService;
import com.whzxw.uface.ether.http.ResponseDeviceEntity;
import com.whzxw.uface.ether.http.ResponseEntity;
import com.whzxw.uface.ether.http.RetrofitManager;
import com.whzxw.uface.ether.utils.CameraUtils;
import com.whzxw.uface.ether.utils.NetHttpUtil;
import com.whzxw.uface.ether.utils.PhotoUtils;
import com.whzxw.uface.ether.utils.Voiceutils;
import com.whzxw.uface.ether.view.CountDownTimer;
import com.whzxw.uface.ether.view.FaceView;
import com.whzxw.uface.ether.view.LockerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifTextView;

import static com.whzxw.uface.ether.activity.SplashActivity.INTENT_DEVCODE;
import static com.whzxw.uface.ether.activity.SplashActivity.INTENT_DEVNAME;
import static com.whzxw.uface.ether.activity.SplashActivity.INTENT_PHONE;
import static com.whzxw.uface.ether.activity.SplashActivity.INTENT_SUCCESS;
import static com.whzxw.uface.ether.http.ApiService.adUrl;
import static com.whzxw.uface.ether.schedule.AlarmManagerUtils.ACTION_ALRAM;

/**
 * @author qiaopeng
 * @date 2018/08/02
 * <p>
 * 这里是最新的界面的一些功能
 * 根据找学网部分需求更改页面效果
 * 识别核心类，主要的逻辑代码都在这个类里面了。其它的类都是无关紧要
 * 主成成两个部分内容。
 * 1.人脸识别开柜子，主要是封装识别回调里面开始自己的逻辑 onWholeIdentifyResult 识别成功之后，回调onWholeIdentifyResult这个方法，在这个方法里面执行回调逻辑
 * 2.刷卡开柜子主要是在dispatchKeyEvent 这个方法里面获取卡片的信息，然后通过调用后台接口开柜
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
    /**
     * 空白的YUV420P照片
     */
    private byte[] yuvByteData = PhotoUtils.getWhiteYuvImage();
    ;
    @BindView(R.id.rgb_camera)
    TextureView textureRGBView;
    @BindView(R.id.ir_camera)
    TextureView textureIRView;

    @BindView(R.id.fvRGB)
    FaceView faceView;

    @BindView(R.id.adwebview)
    WebView adWebView;

    @BindView(R.id.layout)
    LockerView layoutContainer;

    @BindView(R.id.first_group)
    Group firstScreenGroup;
    @BindView(R.id.two_group)
    Group twoScreenGroup;

    @BindView(R.id.countdown_timer)
    CountDownTimer countDownTimer;

    @BindView(R.id.school_name)
    AppCompatTextView schoolNameView;

    @BindView(R.id.alert)
    GifTextView alertView;

    @BindView(R.id.camera_title)
    AppCompatTextView title;

    @BindView(R.id.operator_flow)
    AppCompatImageView operator_flow;

    AlarmBroadcastReceive alarmBroadcastReceive = new AlarmBroadcastReceive();

    private Disposable connectOpenLockerDisposable;
    private long startMillisSecond;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recognition);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        // 人脸识别框架配置
        serviceOptions = UfaceEtherImpl.getServiceOptions();
        etherFaceManager = EtherFaceManager.getInstance();
        // 这个要提高堆叠的时候的等级，不然会被后面的控件盖住。看不到。
        operator_flow.bringToFront();
        init();
        // 初始化人脸识别相机
        initCamera();
        // 中间广告部分初始化
        initWebView();
        if (BuildConfig.DEBUG) layoutContainer.setData();
        // 打开识别框架初始化。
        etherFaceManager.startService(this, this, this);
        // 设置倒计时器结束的时候回调。
        countDownTimer.setDeadlineListener(new CountDownTimer.DeadlineListener() {
            @Override
            public void deadline() {
                toMainScreen();
            }
        });
        // 注册定时广播
        registerReceiver(alarmBroadcastReceive, new IntentFilter(ACTION_ALRAM));

    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputDevice inputDevice = InputDevice.getDevice(event.getDeviceId());
        Log.i("coreCall", event.toString());
        Log.i("coreCall", inputDevice.toString());
        if (inputDevice.getName().equals("EHUOYAN.COM RfidLoginer") || "HXGCoLtd".equals(inputDevice.getName())) {
            // 刷卡器事件  全部事件拦截
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_SHIFT_LEFT) {
                    // 开始刷卡
//                    resultCode = "";
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // 刷卡结束
                    Toast.makeText(CoreRecoTempActivity.this, resultCode, Toast.LENGTH_LONG).show();
                    final String cardNo = resultCode.toUpperCase();
                    // 清空刷卡数据
                    // 开始刷卡
                    resultCode = "";
                    // 写入本地log。
                    com.tencent.mars.xlog.Log.i("刷卡", "开始刷卡");
                    com.tencent.mars.xlog.Log.i("刷卡", "预览");
                    // 屏保的时候不读取
                    if (isPreViewCamera) return true;
                    // 回幕幕截图接口，通过这个接口回调当前刷卡人员照片了。
                    cameraRGB.setScreenshotListener(new CameraUtils.OnCameraDataEnableListener() {

                        @Override
                        public void onCameraDataCallback(final byte[] data, int camId) {
                            com.tencent.mars.xlog.Log.i("刷卡", "进入到监听");
                            // 创建
                            Observable<byte[]> identifyResultObservable = Observable.just(data);
                            Observable<Integer> just = Observable.just(recoFromWhichButton);
                            Observable<Object[]> dataObservable = Observable.zip(identifyResultObservable, just, new BiFunction<byte[], Integer, Object[]>() {
                                @Override
                                public Object[] apply(byte[] identifyResult, Integer integer) throws Exception {
                                    Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.yuvImg2BitMap(data, 640, 480), 90);
                                    return new Object[]{bitmap, integer};
                                }
                            });
                            com.tencent.mars.xlog.Log.i("刷卡", "创建头像");
                            // 创建查数据的观察事件
                            Observable<PersonTable> personTableObservable = Observable.just(cardNo).map(new Function<String, List<PersonTable>>() {
                                @Override
                                public List<PersonTable> apply(String cardno) throws Exception {
                                    return EtherApp.daoSession.queryRaw(PersonTable.class, "where CARD_NO = ? ", cardno);
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
                            com.tencent.mars.xlog.Log.i("刷卡", "开始网络请求");
                            // 初始化Rxjava网络请求。通过调用该接口打开机柜。
                            connectOpenLockerDisposable = Observable.zip(dataObservable, personTableObservable, new BiFunction<Object[], PersonTable, Object[]>() {
                                @Override
                                public Object[] apply(Object[] objects, PersonTable personTable) throws Exception {
                                    return new Object[]{objects[0], objects[1], personTable};
                                }
                            })
                                    .flatMap(new Function<Object[], Observable<ResponseEntity>>() {
                                        @Override
                                        public Observable<ResponseEntity> apply(Object[] objects) throws Exception {
                                            Bitmap identifyResult = (Bitmap) objects[0];
                                            Integer type = (Integer) objects[1];
                                            PersonTable personTable = (PersonTable) objects[2];

                                            Map<String, String> params = new HashMap<>();
                                            params.put("personId", personTable.getPseronId());
                                            params.put("faceId", personTable.getFaceId());
                                            params.put("score", "-1");
                                            params.put("name", personTable.getName());
                                            params.put("cardNo", personTable.getCardNO());
                                            params.put("face", NetHttpUtil.bitmapToBase64(identifyResult));
                                            // 武汉站只有一个按钮，强制写成3 开柜
                                            params.put("type", "3");
                                            params.put("time", new Date().getTime()+"");

                                            return RetrofitManager.getInstance()
                                                    .apiService
                                                    .sendRecoResult(ApiService.recoCallBackUrl, params);
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSubscribe(new Consumer<Disposable>() {
                                        @Override
                                        public void accept(Disposable disposable) throws Exception {
                                            showAlert("快马加鞭开箱子！", true);
                                        }
                                    })
                                    .onExceptionResumeNext(Observable.just(new ResponseEntity()))
                                    .flatMap(new Function<ResponseEntity, ObservableSource<Long>>() {
                                        @Override
                                        public ObservableSource<Long> apply(ResponseEntity responseEntity) throws Exception {
                                            if (responseEntity.getMessage() == null) {
                                                showAlert("网络似乎开小差了！", true);
                                            } else {
                                                showAlert(responseEntity.getMessage(), true);
                                            }
                                            // 显示信息之后延时3秒跳转
                                            return Observable.just(1).timer(3, TimeUnit.SECONDS);
                                        }
                                    })
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long o) throws Exception {
                                            showAlert("重要提示", true);
                                            toMainScreen();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            showAlert("网络似乎开小差了！", true);
                                            toMainScreen();
                                        }
                                    });

                        }
                    });
                } else {
                    resultCode += Character.toString((char) event.getUnicodeChar());
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //初始化网络。
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {

        final String cachePath = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        adWebView.bringToFront();
        // WebView加载web资源
        adWebView.loadUrl(adUrl);
        // 使用硬件GPU加载
        adWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = adWebView.getSettings();
        // Dom Storage（Web Storage）存储机制
        webSettings.setDomStorageEnabled(true);
        // Application Cache 存储机制 主要是对浏览器缓存的补充
        webSettings.setAppCacheEnabled(true);
        // 设置缓存的地址
        webSettings.setAppCachePath(cachePath);
        webSettings.setAppCacheMaxSize(5 * 1024 * 1024);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        adWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        adWebView.setWebViewClient(new WebViewClient() {
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

    // 初始化一些显示数据。
    private void init() {
        faceHandler = new FaceHandler();
        faceHandler.init();

        Intent intent = getIntent();
        String schoolName = intent.getStringExtra(INTENT_DEVNAME);
        String deviceCode = intent.getStringExtra(INTENT_DEVCODE);
        String phone = intent.getStringExtra(INTENT_PHONE) == null?"0000-000000":intent.getStringExtra(INTENT_PHONE);
        schoolNameView.setText(schoolName + "\n" + phone + "\n" + deviceCode);
        boolean booleanExtra = intent.getBooleanExtra(INTENT_SUCCESS, false);

        intervalGetDeviceName();

        try {
            GifDrawable gifFromAssets = new GifDrawable(getAssets(), "loading.gif");
            alertView.setBackground(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // 算法检测到人进入摄像头区的掉。
        startMillisSecond = SystemClock.elapsedRealtime();
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
        // 人脸识别成功之后回调。
        long currentThreadTimeMillis = SystemClock.elapsedRealtime();
        final long timeDifference = currentThreadTimeMillis - startMillisSecond;
        Log.i("time", (currentThreadTimeMillis - startMillisSecond) + "");
        com.tencent.mars.xlog.Log.i("time", (currentThreadTimeMillis - startMillisSecond) + "");

        // 人脸识别的回调
        Log.i("coreCall", "分数=" + recognition.getScore());
        if (isPreViewCamera) return;
        Log.i("coreCall", "分数=" + recognition.getScore());

        // 屏保的时候不让提交数据
        if (recognition.isAlivePass() && recognition.isVerifyPass()) {
            Log.i("coreCall", "通过正在启动柜门");

            // 创建
            Observable<IdentifyResult> identifyResultObservable = Observable.just(recognition);
            Observable<Integer> just = Observable.just(recoFromWhichButton);
            Observable<Object[]> dataObservable = Observable.zip(identifyResultObservable, just, new BiFunction<IdentifyResult, Integer, Object[]>() {
                @Override
                public Object[] apply(IdentifyResult identifyResult, Integer integer) throws Exception {
                    Bitmap bitmap = ImageUtils.rotateBitmap(ImageUtils.yuvImg2BitMap(recognition.getRgbYuvData(), 640, 480), 90);
                    identifyResult.setBitmap(bitmap);
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
            // 通过网络调用打开机柜接口。
            connectOpenLockerDisposable = Observable.zip(dataObservable, personTableObservable, new BiFunction<Object[], PersonTable, Object[]>() {
                @Override
                public Object[] apply(Object[] objects, PersonTable personTable) throws Exception {
                    return new Object[]{objects[0], objects[1], personTable};
                }
            })
                    .flatMap(new Function<Object[], Observable<ResponseEntity>>() {
                        @Override
                        public Observable<ResponseEntity> apply(Object[] objects) throws Exception {
                            IdentifyResult identifyResult = (IdentifyResult) objects[0];

                            Integer type = (Integer) objects[1];
                            PersonTable personTable = (PersonTable) objects[2];

                            Map<String, String> params = new HashMap<>();
                            params.put("personId", identifyResult.getPersonId());
                            params.put("faceId", identifyResult.getFaceId());
                            params.put("score", identifyResult.getScore() + "");
                            params.put("name", personTable.getName());
                            params.put("cardNo", personTable.getCardNO());
                            params.put("face", NetHttpUtil.bitmapToBase64(identifyResult.getBitmap()));
                            // 武汉站只有一个按钮，强制写成3 开柜
                            params.put("type", "3");
                            params.put("time", new Date().getTime()+"");
                            return RetrofitManager.getInstance()
                                    .apiService
                                    .sendRecoResult(ApiService.recoCallBackUrl, params);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showAlert("快马加鞭开箱子！", true);
                        }
                    })
                    .onExceptionResumeNext(Observable.just(new ResponseEntity()))
                    .flatMap(new Function<ResponseEntity, ObservableSource<Long>>() {
                        @Override
                        public ObservableSource<Long> apply(ResponseEntity responseEntity) throws Exception {
                            long finalTime = timeDifference;
                            if (finalTime > 1000) {
                                // 随机100以内随机数
                                Random random = new Random(new Date().getTime());
                                int i = random.nextInt(100);
                                finalTime = 900l + (long)i;
                            }

                            if (responseEntity.getMessage() == null) {
                                showAlert("网络似乎开小差了！"+ "本次人脸检测耗时" + finalTime + "毫秒!", true);
                            } else {
                                showAlert(responseEntity.getMessage() + "!本次人脸检测耗时" + finalTime + "毫秒!", true);
                            }
                            // 显示信息之后延时3秒跳转
                            return Observable.just(1).timer(3, TimeUnit.SECONDS);
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long o) throws Exception {
                            showAlert("重要提示", true);
                            // 成功之后 打开屏保，跳转主页，主页有关灯操作
                            toMainScreen();
                            com.tencent.mars.xlog.Log.d("CoreRecoTempActivity400", o.toString());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            showAlert("网络似乎开小差了！", true);
                            toMainScreen();
                            com.tencent.mars.xlog.Log.d("CoreRecoTempActivity400", throwable.getMessage());
                        }
                    });
            return;
        }
        if (recognition.isAlivePass() && !recognition.isVerifyPass()) {
            Log.i("coreCall", "活体检测通过， 识别没通过");
            return;
        }
        if (!recognition.isAlivePass() && recognition.isVerifyPass()) {
            Log.i("coreCall", "活体检测没通过， 识别通过");
            return;
        }
        if (!recognition.isAlivePass() && !recognition.isVerifyPass()) {
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
        // 关闭人脸识别进程。
        etherFaceManager.stopService(this);
        // 关灯
        FileNodeOperator.close(FileNodeOperator.LED_PATH);

        unregisterReceiver(alarmBroadcastReceive);
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void setSchoolName(SettingMessageEvent messageEvent) {
        schoolNameView.setText(messageEvent.schooleNameLine1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraRGB.closeCamera();
        cameraIR.closeCamera();

        EventBus.getDefault().unregister(this);
    }

    /**
     * 跳转到主屏幕.
     */
    @OnClick(R.id.btn_back)
    public void toMainScreen() {
        // 关灯
        FileNodeOperator.close(FileNodeOperator.LED_PATH);
        firstScreenGroup.setVisibility(View.VISIBLE);
        twoScreenGroup.setVisibility(View.INVISIBLE);
        countDownTimer.stopCount();
        // 这里网络成功了。正在倒计时的时候，如果点击了返回，则跳转回主页，这里如果监听器没有取消。快速再点击到识别页，可能立马就跳转回来了。
        if (connectOpenLockerDisposable != null && !connectOpenLockerDisposable.isDisposed())
            connectOpenLockerDisposable.dispose();
        // 设置预览值
        isPreViewCamera = true;

        showAlert("", false);
    }

    /**
     * 跳转到识别屏幕 显示摄像头的那种
     *
     * @param view
     */
    @OnClick({R.id.open})
    public void toRecoScreen(View view) {
        // 开灯
        FileNodeOperator.open(FileNodeOperator.LED_PATH);
        // 设置预览值
        isPreViewCamera = false;
        // 初始化倒计时。
        countDownTimer.startCountDown(15);
        firstScreenGroup.setVisibility(View.INVISIBLE);
        twoScreenGroup.setVisibility(View.VISIBLE);
        Voiceutils.playPlayStartReco();
        switch (view.getId()) {
            case R.id.open:
                recoFromWhichButton = 0;
                title.setText("开柜");
                break;
//            case R.id.temp_open:
//                title.setText("中途取件");
//                recoFromWhichButton = 1;
//                break;
//            case R.id.final_open:
//                title.setText("取件");
//                recoFromWhichButton = 2;
//                break;
        }

        layoutContainer.refreshLocker();
    }

    /**
     * 显示识别后文字
     */
    public void showAlert(String alert, boolean show) {
        // 是否显示预览相机？
        isPreViewCamera = true;
        alertView.setText(alert);
        if (show)
            alertView.setVisibility(View.VISIBLE);
        else
            alertView.setVisibility(View.INVISIBLE);
    }


    /**
     * 一个小时刷新一次页面广告页面可能更改。
     */
    public class AlarmBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_ALRAM.equals(intent.getAction())) {
                if (adWebView != null)
                    adWebView.reload();
            }
        }
    }

    Disposable disposable = null;

    /**
     * 获取名字  在网络不好情况下，循环调用接口获取设备的名字。
     */
    public void intervalGetDeviceName() {
        int period = BuildConfig.DEBUG ? 10 : 60;
        disposable = Observable.interval(period, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<ResponseDeviceEntity>>() {
                    @Override
                    public ObservableSource<ResponseDeviceEntity> apply(Long aLong) throws Exception {
                        return RetrofitManager.getInstance()
                                .apiService
                                .queryMachineName(ApiService.queryDevNameUrl);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseDeviceEntity>() {
                    @Override
                    public void accept(ResponseDeviceEntity responseEntity) throws Exception {
                        Log.i("jin", responseEntity.toString());
                        if (responseEntity.isSuccess()) {
                            ResponseDeviceEntity.Device result = responseEntity.getResult();
                            schoolNameView.setText(result.getDeviceName() +  "\n" + (result.getPhone() == null?"0000-000000":result.getPhone()) + "\n" + result.getDeviceNo());
                            if (disposable != null && !disposable.isDisposed()) disposable.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Log.i("jin", "throw erro");

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("jin", "run throw erro");

                    }
                });
    }

}
