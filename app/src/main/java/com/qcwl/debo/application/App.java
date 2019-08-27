package com.qcwl.debo.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.alivc.player.AliVcMediaPlayer;
import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.utils.VcPlayerLog;
import com.antfortune.freeline.FreelineCore;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
//import com.huawei.android.hms.agent.HMSAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.found.joke.ruidong_video.FaceHandler;
import com.qcwl.debo.ui.found.joke.ruidong_video.SdkHandler;
import com.qcwl.debo.widget.DemoHelper;
import com.rd.vecore.RdVECore;
import com.rd.vecore.RdVECoreHelper;
import com.rd.veuisdk.MyCrashHandler;
import com.rd.veuisdk.SdkEntry;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.util.Iterator;
import java.util.List;


public class App extends Application {
    private static App mAppApplication;
    private Display display;

    //锐动天地视频录制
    /**
     * 已获取的AppKey
     */
    public static final String APP_KEY = "506bb7417204ea1b";
    /**
     * 锐动已获取的AppSecret
     */
    public static final String APP_SECRET = "27920158a77003257c07f5656bf4e999OJ37Gk8tXlmHmP4x4fDXvZ3xAxlpniHLBKIbpq0KhBP39ljVBh/yGJxtfiAeeVEGZVJ1M8ULAJTBRu0OZcHhGfck2gtcm6NKsUc+PL/baaI=";
    private String mStrCustomPath;


    //微信
    public static final String WEIXIN_APPID = "wxc49702817b48864f";//wxb6c875d9338f44d9

    {
        //友盟分享
        PlatformConfig.setWeixin(WEIXIN_APPID,"3883fddcdba1ace9e9d9d4a74275fd67");
		/*PlatformConfig.setSinaWeibo("2330390931",
				"9259a2ad480866f4cd3a740c3f5edbe3","http://sns.whalecloud.com");*/
        PlatformConfig.setQQZone("1106288673", "P2tMiB0RP08islA7");
    }

    @Override
    public void onCreate() {
        //加速编译初始化
        FreelineCore.init(this);
        super.onCreate();

        //视频录制初始化
        initializeSdk();
        Log.e("Application","initCrashReport");
        //tencent bugly日志上传初始化  下面还有一段bugly初始化代码initBugly()
        CrashReport.initCrashReport(getApplicationContext(), "0076563954", true);
        //友盟分享
        UMConfigure.init(this,"5a504b88a40fa355590002b3"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        Log.e("Application","Application被启动？");
        oneInit();
        Log.e("Application","Application被启动成功");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
        mAppApplication = this;
        /*UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);*/
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        DemoHelper.getInstance().init(this);
        //极光推送帐号
        //每次都进行授权配置
        //初始化热更新
        initBugly();
        //初始化地图
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.GCJ02);
        //UMShareAPI.get(this);
        // red packet code : 初始化红包上下文，开启日志输出开关
//		RedPacket.getInstance().initContext(this);
//		RedPacket.getInstance().setDebugMode(true);

   /*     EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);*/

        //EMClient.getInstance().init(this, options);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();

        // 这样自定义设置后就可以管理二级缓存和三级缓存了
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
                this).memoryCacheSizePercentage(20)
                // 设置占用内存的百分比
                .discCacheFileCount(100)
                // 设置最大下载图片数
                .discCacheSize(5 * 1024 * 1024)
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(configuration);

        if (display == null) {
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }
        //RefWatcher refWatcher = LeakCanary.install(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        Intent intent = new Intent();
        intent.setAction("com.debo.groupList");
        sendBroadcast(intent);


        //视频播放相关
        VcPlayerLog.enableLog();

        AliVcMediaPlayer.init(getApplicationContext(),"");


        //设置保存密码。此密码如果更换，则之前保存的视频无法播放
        AliyunDownloadConfig config1 = new AliyunDownloadConfig();
        config1.setSecretImagePath("/mnt/sdcard/aliyun/encryptedApp.dat");
//        config.setDownloadPassword("123456789");
        //设置保存路径。请确保有SD卡访问权限。
        config1.setDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/debo_aliy_save/");
        //设置同时下载个数
        config1.setMaxNums(2);
        AliyunDownloadManager.getInstance(this).setDownloadConfig(config1);

        Fresco.initialize(this);

//        //异常崩溃捕获
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        catchHandler.init(getApplicationContext());


        // 初始化华为 HMS 推送服务, 需要在SDK初始化后执行
        //boolean b = HMSAgent.init(this);
        //Log.e("Application","Application   b="+b);
        //HMSPushHelper.getInstance().initHMSAgent(mAppApplication);
    }

    public final static String PKG_NAME = "com.qcwl.debo";

    /**
     * 单次初始化
     */
    public void oneInit() {
        Log.e("Application","Application想启动被跳过");

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.i("hx","processAppName---" + processAppName);
        //默认的app会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就return掉
        if (processAppName == null ||!processAppName.equalsIgnoreCase(PKG_NAME)) {
            Log.i("hx","enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }

    private void initBugly() {
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Beta.autoInit = true;
        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(MainActivity.class);
        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        /*在application中初始化时设置监听，监听策略的收取*/
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                Log.i("App","..........UPGRADE="+strategy);
                if (strategy != null) {
                    Intent intent = new Intent();
                    intent.setAction("com.debo.update");
                    sendBroadcast(intent);
                } else {
                    //Toast.makeText(mAppApplication, "没有更新", Toast.LENGTH_LONG).show();
                }
            }
        };
        Bugly.init(this, "774e020991", true);
        //Bugly.init(this, "4473df0423", true);

    }

    /**
     * 初始化SDK
     */
    public void initializeSdk() {
        // 这里确定是否启用日志，在调试阶段可开启该选项，方便定位问题。
        SdkEntry.enableDebugLog(true);
        // 自定义根目录，如果为空则默认为/sdcard/Android/data/com.rd.ve.demo/files/rdve
        mStrCustomPath = getExternalFilesDirEx(this, "rdve").getAbsolutePath();
        // sdk初始
        SdkEntry.initialize(this, mStrCustomPath, APP_KEY, APP_SECRET,
                new SdkHandler().getCallBack());
        // 自定义Crash handler,实际项目可不加入

        MyCrashHandler.getInstance().init(this);
        FaceHandler.initialize(this);
    }
    /**
     * 获取自定义根目录
     *
     */
    public String getCustomPath() {
        return mStrCustomPath;
    }

    private File getExternalFilesDirEx(Context context, String type) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File ef = context.getExternalFilesDir(type);
            if (ef != null && ef.isDirectory()) {
                return ef;
            }
        }
        return new File(Environment.getExternalStorageDirectory(), type);
    }
    /**
     * 获取Application
     */
    public static App getApp() {
        return mAppApplication;
    }

    /**
     * @return
     * @Description： 获取当前屏幕1/4宽度
     */
    public int getQuarterWidth() {
        return display.getWidth() / 4;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //安装tinker
        Beta.installTinker(this);
    }

    public String getCachePath() {
        File cacheDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

}
