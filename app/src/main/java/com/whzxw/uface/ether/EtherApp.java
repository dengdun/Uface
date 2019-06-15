package com.whzxw.uface.ether;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.tencent.bugly.crashreport.CrashReport;
import com.uniubi.andserver.EtherAndServerManager;
import com.uniubi.ether.Ether;
import com.uniubi.uface.ether.andserver.handler.AbstractEtherRequestHandler;
import com.uniubi.uface.ether.config.AlgorithmOptions;
import com.uniubi.uface.ether.config.AndServerOptions;
import com.uniubi.uface.ether.config.CommonOptions;
import com.uniubi.uface.ether.config.ServiceOptions;
import com.uniubi.uface.ether.config.configenum.algorithm.DataSourceFormat;
import com.uniubi.uface.ether.config.configenum.algorithm.FaceOrientation;
import com.uniubi.uface.ether.config.configenum.service.AliveLevel;
import com.uniubi.uface.ether.config.configenum.service.RecoMode;
import com.uniubi.uface.ether.config.configenum.service.RecoPattern;
import com.uniubi.uface.ether.config.configenum.service.ScenePhotoRecResult;
import com.uniubi.uface.ether.config.configenum.service.ScenePhotoWholeResult;
import com.uniubi.uface.ether.config.configenum.service.WorkMode;
import com.uniubi.uface.ether.core.cvhandle.FaceHandler;
import com.uniubi.uface.ether.utils.AppLog;
import com.whzxw.uface.ether.activity.SplashActivity;
import com.whzxw.uface.ether.database.DaoMaster;
import com.whzxw.uface.ether.database.DaoSession;
import com.whzxw.uface.ether.serverhandle.ChangeRocModeHandler;
import com.whzxw.uface.ether.serverhandle.FaceAllDeleteHandler;
import com.whzxw.uface.ether.serverhandle.FaceCreateHandler;
import com.whzxw.uface.ether.serverhandle.FaceDeleteHandler;
import com.whzxw.uface.ether.serverhandle.PongHandler;
import com.whzxw.uface.ether.serverhandle.ScreenHandler;
import com.whzxw.uface.ether.serverhandle.SettingHandler;
import com.whzxw.uface.ether.serverhandle.StartRecoHandler;
import com.whzxw.uface.ether.utils.AudioUtils;
import com.whzxw.uface.ether.utils.SerialUtils;
import com.whzxw.uface.ether.utils.ShareferenceManager;
import com.whzxw.uface.ether.utils.XlogUitls;

import java.util.ArrayList;
import java.util.List;
/**
 * description:
 * version:
 * email: cfj950221@163.com
 *
 * @author caojun
 * @date 2018/8/7
 */
public class EtherApp extends Application {
    public static Context context;
    public static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        CrashReport.initCrashReport(getApplicationContext(), "7a0e2c4097", true);
        CrashReport.setIsDevelopmentDevice(context, true);

        AlgorithmOptions algorithmOptions = AlgorithmOptions.newBuilder()
                .withDataSourceFormat(DataSourceFormat.CV_PIX_FMT_NV21)
                .withDataSourceWidth(640)
                .withDataSourceHeight(480)
                .withFaceOrientation(FaceOrientation.CV_FACE_UP)
                .withFilterMaxFace(false)
                .withFilterFaceAngle(true)
                .withFilterOutScreen(true)
                .withFaceVerifyThreshold(62)
                .withMinFaceVerifyScore(57)
                .withFaceDetectThreadNum(2)
                .withMinFacePixel(96)
                .withMaxFaceNumber(-1)
                .withMaxAliveCount(6)
                .withAliveThreadNumber(1)
                .withFaceVerifyDistance(88)
                .withAliveMinFaceSize(88)
                .withVerifyStrangerCount(3)
                .withIsContinueVerify(false)
                .withOccupancyArea(0.4f)
                .withMaxAliveCount(10)
                .build();
        // 自己初始化一个数据库,跟他们的名字不一样吧...
        initGreenDao();
        FaceCreateHandler faceCreateHandler = new FaceCreateHandler("/faceCreate");
        FaceDeleteHandler faceDeleteHandler = new FaceDeleteHandler("/faceDelete");
        ChangeRocModeHandler rocModeHandler = new ChangeRocModeHandler("/changeReco");
        // 删除所有人的
        FaceAllDeleteHandler faceAllDeleteHandler = new FaceAllDeleteHandler("/deleteAll");

        SettingHandler settingHandler = new SettingHandler("/setting");
        PongHandler pongHandler = new PongHandler("/pong");
        ScreenHandler screenHandler = new ScreenHandler("/screenSaver");
        StartRecoHandler startRecoHandler = new StartRecoHandler("/stratApp");
        List<AbstractEtherRequestHandler> handlers = new ArrayList<>();
        handlers.add(faceCreateHandler);
        handlers.add(faceDeleteHandler);
        handlers.add(rocModeHandler);
        handlers.add(settingHandler);
        handlers.add(startRecoHandler);
        handlers.add(faceAllDeleteHandler);

        // 心跳包
        handlers.add(pongHandler);
        // 屏幕保护
        handlers.add(screenHandler);

        ServiceOptions serviceOptions = new ServiceOptions.Builder()
                .withRecoMode(RecoMode.LOCALONLY)
                .withRecoPattern(RecoPattern.IDENTIFY)
                .withAliveLevel(AliveLevel.HARD)
                .withWorkMode(WorkMode.OFFLINE)
                .withScenePhotoRecResult(ScenePhotoRecResult.NON)
                .withScenePhotoWholeResult(ScenePhotoWholeResult.SUCCESS)
                .build();

        AndServerOptions andServerOptions = AndServerOptions.newBuilder()
                .withOfflineServerHandlers(handlers)
                .withOfflineServerPort(8091)
                .withOfflineServerTimeout(15)
                .withIsRegisterWebsite(true)
                .withIsExternalStorage(true)
                .withOfflineServerWebsitePath("").build();


        CommonOptions commonOptions = new CommonOptions(SerialUtils.getDeviceSerial(), "06CA983C214E4FBF82E90A00B05A4822", "025cb4d148f5b71f14dfdcf0e68ae5a9");

        Ether ether = new Ether.Builder(commonOptions)
                .withAlgorithmOptions(algorithmOptions)
                .withServiceOptions(serviceOptions)
                .withAndServerOptions(andServerOptions)
                .build();
        ether.init(this);

        // 如果是第一次进来的话，就设置音量最大
        if(ShareferenceManager.firstRun()){
            AudioUtils.setMaxVolum();
            ShareferenceManager.setFirstRun(false);
        }

        // 启动andserver服务
        EtherAndServerManager.getInstance().startAndServer(this);

        AppLog.e("hwcode "+ FaceHandler.getHwCode());

        // 初始化xlog
        XlogUitls.init(getApplicationContext());

        // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
//            restartApp();// 发生崩溃异常时,重启应用
        }
    };

    public void restartApp(){
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "person_base.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


}
