package com.qcwl.debo.ui.found.joke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.ksyun.media.shortvideo.utils.AuthInfoManager;
import com.qcwl.debo.R;
import com.qcwl.debo.application.App;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.fans.CameraActivity;
import com.qcwl.debo.ui.found.jinshanyun.ConfigActivity;
import com.qcwl.debo.ui.found.jinshanyun.ReleasePassageActivity;
import com.qcwl.debo.ui.found.jinshanyun.util.HttpRequestTask;
import com.qcwl.debo.ui.found.joke.ruidong_video.AudioConfigDialog;
import com.qcwl.debo.ui.found.joke.ruidong_video.CameraWatermarkBuilder;
import com.qcwl.debo.ui.found.joke.ruidong_video.ConfigData;
import com.qcwl.debo.ui.found.joke.ruidong_video.SDKUtils;
import com.qcwl.debo.ui.found.joke.ruidong_video.authpack;
import com.qcwl.debo.ui.my.ad.MyPagerAdapter;
import com.qcwl.debo.view.BottomAnimDialog;
import com.rd.vecore.VirtualVideo;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.listener.ExportListener;
import com.rd.vecore.models.Trailer;
import com.rd.vecore.models.VideoConfig;
import com.rd.vecore.models.Watermark;
import com.rd.veuisdk.SdkEntry;
import com.rd.veuisdk.SdkService;
import com.rd.veuisdk.callback.ICompressVideoCallback;
import com.rd.veuisdk.manager.CameraConfiguration;
import com.rd.veuisdk.manager.EditObject;
import com.rd.veuisdk.manager.ExportConfiguration;
import com.rd.veuisdk.manager.FaceuInfo;
import com.rd.veuisdk.manager.TrimConfiguration;
import com.rd.veuisdk.manager.UIConfiguration;
import com.rd.veuisdk.manager.VEOSDBuilder;
import com.rd.veuisdk.manager.VideoMetadataRetriever;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.rd.veuisdk.SdkEntry.editMedia;
import static com.rd.veuisdk.SdkEntry.getSdkService;
import static com.rd.veuisdk.SdkEntry.trimVideo;
import static com.rd.veuisdk.manager.CameraConfiguration.SQUARE_SCREEN_CAN_CHANGE;

public class JokeActivity extends BaseActivity {
    @Bind(R.id.status_bar)
    View statusBar;
    @Bind(R.id.image_back)
    ImageView imageBack;
    @Bind(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @Bind(R.id.image_shoot)
    ImageView imageShoot;
    @Bind(R.id.layout_title)
    LinearLayout layoutTitle;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private String[] titles = null;
    private List<Fragment> fragments;

    private double latitude, longitude;
    private  String city;
    private Handler mMainHandler;
    private MyPagerAdapter pagerAdapter;
    private BottomAnimDialog dialog;
    private HttpRequestTask.HttpResponseListener mAuthResponse;
    private static String TAG = "JokeActivity1";
    private int mRetryCount = 0;
    private static int MAX_RETRY_COUNT = 3;  //若AKSK请求失败尝试3次
    private HttpRequestTask mAuthTask;  //SDK鉴权异步任务
    public static String AUTH_SERVER_URI = "http://ksvs-demo.ks-live.com:8321/Auth";//the uri of your appServer
    //跳转配置
    public static final int CODE_CAMERA = 200;
    /**
     * REQUEST_CODE定义：<br>
     * 录制
     */
    private final int CAMERA_REQUEST_CODE = 100;

    /**
     * REQUEST_CODE定义：<br>
     * 相册
     */
    private final int ALBUM_REQUEST_CODE = 101;

    /**
     * 从相册选折要压缩的文件
     */
    private final int ALBUM_COMPRESS_REQUEST_CODE = 1011;
    /**
     * 从相册选折要播放的视频
     */
    private final int ALBUM_PLAYER_REQUEST_CODE = 1012;

    /**
     * 防篡改录制演示
     */
    private final int CAMERA_ANTI_CHANGE_REQUEST_CODE = 1013;


    /**
     * 动画演示
     */
    private final int ALBUM_ANIMATION_REQUEST_CODE = 1014;

    /**
     * 异形显示
     */
    private final int ALBUM_POINTF_REQUEST_CODE = 1020;

    /**
     * 动画导出完成
     */
    private final int ANIMATION_RESULT_CODE = 1015;

    /**
     * 从相册选择设置音效的视频
     */
    private final int ALBUM_SOUND_EFFECT_REQUEST_CODE = 1016;

    /**
     * 从相册选择简影视频
     */
    private final int ALBUM_SILHOUETT_REQUEST_CODE = 1017;

    /**
     * REQUEST_CODE定义：<br>
     * 视频编辑
     */
    private final int EDIT_REQUEST_CODE = 102;
    /**
     * REQUEST_CODE定义：<br>
     * 读取外置存储
     */
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 1;

    /**
     * REQUEST_CODE定义：<br>
     * 视频截取
     */
    private final int TRIM_REQUEST_CODE = 103;
    /**
     * REQUEST_CODE定义：<br>
     * 视频截取相册选择
     */
    private final int TRIM_ALBUM_REQUEST_CODE = 104;

    /**
     * 短视频录制演示request_code
     */
    private final int SHORTVIDEO_CAMERA_REQUEST_CODE = 110;
    /**
     * 短视频录制进入相册演示request_code
     */
    private final int SHORTVIDEO_ALBUM_REQUEST_CODE = 111;
    /**
     * 短视频录制进入相册进入截取演示request_code
     */
    private final int SHORTVIDEO_TRIM_REQUEST_CODE = 112;


    /**
     * 选折导出的文件(仅视频)
     */
    private final int ALBUM_REQUEST_EXPORT_CODE = 114;
    private ConfigData configData;

    private String EDIT_PICTURE_PATH = Environment
            .getExternalStorageDirectory() + "/android.jpg";
    /**
     * 导出的横向16:9视频
     */
    private String EDIT_L_VIDEO_PATH = Environment
            .getExternalStorageDirectory() + "/demoVideo1.mp4";
    /**
     * 测试用1：1方型视频
     */
    private String EDIT_S_VIDEO_PATH = Environment
            .getExternalStorageDirectory() + "/demoVideo2.mp4";
    /**
     * 测试用竖向9:16视频
     */
    private String EDIT_P_VIDEO_PATH = Environment
            .getExternalStorageDirectory() + "/demoVideo3.mp4";
    /**
     * 测试用水印图片
     */
    private String EDIT_WATERMARK_PATH = Environment
            .getExternalStorageDirectory() + "/watermark.png";
    private String BASE_MEDIA_PATH = Environment
            .getExternalStorageDirectory() + "/baseMedia.png";

    private String MV_SKY_PATH = Environment
            .getExternalStorageDirectory() + "/jy_sky.zip";

    private String MV_AD_PATH = Environment
            .getExternalStorageDirectory() + "/jy_ad.zip";

    private String MEDIA_SKY = Environment
            .getExternalStorageDirectory() + "/jy_sky_asset4.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        ButterKnife.bind(this);
        initView();


        EDIT_PICTURE_PATH = ((App) getApplication()).getCustomPath() + "/android.jpg";
        EDIT_L_VIDEO_PATH = ((App) getApplication()).getCustomPath() + "/demoVideo1.mp4";
        EDIT_S_VIDEO_PATH = ((App) getApplication()).getCustomPath() + "/demoVideo2.mp4";
        EDIT_P_VIDEO_PATH = ((App) getApplication()).getCustomPath() + "/demoVideo3.mp4";
        EDIT_WATERMARK_PATH = ((App) getApplication()).getCustomPath() + "/watermark.png";

        restoreConfigInstanceState();
        registerAllResultHandlers();
    }

    private void initView() {
        titles = new String[]{"关注", "发现", "同城"};
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.getStatusHeight(this)));
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new BottomAnimDialog(JokeActivity.this, "发布段子", "我的段子", "取消");
                dialog.setClickListener(new BottomAnimDialog.BottonAnimDialogListener() {
                    @Override
                    public void onItem1Listener() {
                        mMainHandler = new Handler();
                        checkAuth();
                        /*Intent intent = new Intent(JokeActivity.this, ConfigActivity.class);
                        startActivity(intent);*/
                        //startActivityForResult(new Intent(JokeActivity.this, CameraActivity.class).putExtra("is_cycle", 2),CODE_CAMERA);

                        SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                        CameraWatermarkBuilder.setText("");// 可自定义水印显示文本
                        initCameraConfig(CameraConfiguration.ONLY_WIDE_SCREEN);

                        SdkEntry.record(JokeActivity.this, CAMERA_REQUEST_CODE);
                        dialog.dismiss();
                    }

                    @Override
                    public void onItem2Listener() {
                        //我的段子
                        Intent intent = new Intent();
                        intent.setClass(JokeActivity.this, MyJokeVideo.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                    @Override
                    public void onItem3Listener() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        initViewPager();
        initMagicIndicator();
    }
    private void initViewPager() {
        fragments = new ArrayList<>();
        JokeFragment fragment = null;
        Bundle bundle = null;
        for (int i = 0; i < 3; i++) {
            fragment = new JokeFragment();
            bundle = new Bundle();
            bundle.putInt("key", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewPager.setAdapter(pagerAdapter=new MyPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        super.onStart();
        StatService.onPageStart(this, "启动段子页面");
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        //commonNavigator.setAdjustMode(false);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(getColorInfo(R.color.white));//circle_name
                titleView.setSelectedColor(getColorInfo(R.color.white));
                titleView.setTextSize(18);
                titleView.setText(titles[index]);
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
//              indicator.setBackgroundColor(Color.WHITE);
                indicator.setLineHeight(dp2px(2f));
                indicator.setLineWidth(dp2px(20f));//60px---xxhdpi   //commonNavigator.setAdjustMode(false);
                indicator.setColors(new Integer(getColorInfo(R.color.white)));
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        viewPager.setCurrentItem(1);
    }
   /*   *//*
       * 初始化短视频页面
       *//*
    private void initShortView() {
        AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                //设置录制分辨率，目前支持360p，480p，540p，720p
                .setResulutionMode(3)
                //设置视频比例，目前支持1:1,3:4,9:16
                .setRatioMode(2)
                .setRecordMode(2) //设置录制模式，目前支持按录，点录和混合模式
                //  .setFilterList() //设置滤镜地址列表,具体滤镜接口接收的是一个滤镜数组
                .setBeautyLevel(30) //设置美颜度
                .setBeautyStatus(true) //设置美颜开关
                .setCameraType(CameraType.FRONT) //设置前后置摄像头
                .setFlashType(FlashType.ON) // 设置闪光灯模式
                .setNeedClip(true) //设置是否需要支持片段录制
                .setMaxDuration(30000) //设置最大录制时长 单位毫秒
                .setMinDuration(2000) //设置最小录制时长 单位毫秒
                .setVideQuality(VideoQuality.HD) //设置视频质量
                .setGop(5) //设置关键帧间隔
                .setCropMode(ScaleMode.PS)
                .setMinVideoDuration(3000) //设置过滤的视频最小长度 单位毫秒
                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                .setCropUseGPU(true) //设置裁剪方式，是否使用gpu进行裁剪，不设置则默认使用cpu来裁剪
               // .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
             //   .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                .build();
        AliyunVideoRecorder.startRecordForResult(this,REQUEST_RECORD,recordParam);
        dialog.dismiss();
    }*/

  /*
   * 初始化短视频裁剪页面
   *//*
    private void initShortCrop(){
        AliyunSnapVideoParam mCropParam = new AliyunSnapVideoParam.Builder()
                .setFrameRate(25) //设置帧率
                .setGop(5) //设置关键帧间隔
                .setCropMode(ScaleMode.PS) //设置裁剪模式，目前支持有黑边和无黑边两种
                .setVideQuality(VideoQuality.HD) //设置裁剪质量
                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                .setCropUseGPU(true) //设置裁剪方式，是否使用gpu进行裁剪，不设置则默认使用cpu来裁剪
                .setResulutionMode(0) //设置分辨率，目前支持360p，480p，540p，720p
                .setRatioMode(2)//设置裁剪比例 目前支持1:1,3:4,9:16
                .setNeedRecord(true)  //设置是否需要开放录制入口
                .setMinVideoDuration(4000) //设置过滤的视频最小长度 单位毫秒
                .setMaxVideoDuration(29 * 1000) //设置过滤的视频最大长度 单位毫秒
                .setMinCropDuration(3000) //设置视频最小裁剪时间 单位毫秒
             // .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                .setFilterList(null)
                .build();
                AliyunVideoCrop.startCropForResult(this,REQUEST_CROP,mCropParam);
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }

        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask.release();
            mAuthTask = null;
        }
        StatService.onPageEnd(this,"结束段子页面");

        AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
    }

  /*  protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode ==  REQUEST_RECORD){
            if(resultCode == Activity.RESULT_OK && data!= null){
                int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE,0);
                if(type ==  AliyunVideoRecorder.RESULT_TYPE_CROP){
                    String path = data.getStringExtra("crop_path");
                 *//*   Toast.makeText(this,"文件路径为 "+ path + " 时长为 " +
                            data.getLongExtra("duration",0),Toast.LENGTH_SHORT).show();*//*

                    Intent intent = new Intent();
                    intent.setClass(JokeActivity.this,PushVideoActivity.class);
                    intent.putExtra("filepath",data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    startActivity(intent);

                }else if(type ==  AliyunVideoRecorder.RESULT_TYPE_RECORD){

                    //跳转到上传视频页面'
                   // initShortCrop();
                    Intent intent = new Intent();
                    intent.setClass(JokeActivity.this,PushVideoActivity.class);
                    intent.putExtra("filepath",data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    startActivity(intent);
                  *//*  Toast.makeText(this,"文件路径为 "+
                            data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH),Toast.LENGTH_SHORT).show();*//*
                *//*  Intent intent = new Intent();
                    intent.setClass(JokeActivity.this,PushVideoActivity.class);
                    intent.putExtra("filepath",data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    startActivity(intent);*//*
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                dialog.dismiss();
            }
        }
        if(requestCode == REQUEST_CROP){
            if(resultCode == Activity.RESULT_OK && data!= null){
                int type = data.getIntExtra(MediaActivity.RESULT_TYPE,0);
                if(type ==  MediaActivity.RESULT_TYPE_CROP){
                 *//*   String path = data.getStringExtra("crop_path");
                    Toast.makeText(this,"文件路径为 "+ path + " 时长为 " +
                            data.getLongExtra("duration",0),Toast.LENGTH_SHORT).show();*//*
                    Intent intent = new Intent();
                    intent.setClass(JokeActivity.this,PushVideoActivity.class);
                 //   intent.putExtra("filepath",data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    intent.putExtra("filepath",data.getStringExtra("crop_path"));
                    startActivity(intent);
                }else if(type ==  MediaActivity.RESULT_TYPE_RECORD){
                 *//*   Toast.makeText(this,"文件路径为 "+
                            data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH),Toast.LENGTH_SHORT).show();*//*
                    Intent intent = new Intent();
                    intent.setClass(JokeActivity.this,PushVideoActivity.class);
                    intent.putExtra("filepath",data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    startActivity(intent);
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                dialog.dismiss();
            }
        }
    }*/

    public int getColorInfo(int id) {
        return getResources().getColor(id);
    }

    private int dp2px(float dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }

    /**
     * SDK鉴权
     */
    private void checkAuth() {
        String token = null;
        try {
            InputStream in = getResources().getAssets().open("AuthForTest.pkg");

            int length = in.available();

            byte[] buffer = new byte[length];

            in.read(buffer);

            token = EncodingUtils.getString(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(token)) {
            AuthInfoManager.getInstance().setAuthInfo(token);
            AuthInfoManager.getInstance().checkAuth();
            if (AuthInfoManager.getInstance().getAuthState()) {
               /* Toast.makeText(JokeActivity.this, "Auth Success", Toast.LENGTH_SHORT)
                        .show();*/
                Intent intent = new Intent(JokeActivity.this, ConfigActivity.class);
                startActivity(intent);
                dialog.dismiss();
            } else {
                /*Toast.makeText(JokeActivity.this, "Auth Failed", Toast.LENGTH_SHORT)
                        .show();*/
            }
        } else {
            AuthInfoManager.getInstance().addAuthResultListener(new AuthInfoManager.CheckAuthResultListener() {
                @Override
                public void onAuthResult(int i) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
                            if (AuthInfoManager.getInstance().getAuthState()) {
                                Intent intent = new Intent(JokeActivity.this, ConfigActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(JokeActivity.this, "鉴权失败", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            });
            if (mAuthResponse == null) {
                mAuthResponse = new HttpRequestTask.HttpResponseListener() {
                    @Override
                    public void onHttpResponse(int responseCode, String response) {
                        //params response
                        boolean authResult = false;
                        if (responseCode == 200) {
                            try {
                                JSONObject temp = new JSONObject(response);
                                JSONObject data = temp.getJSONObject("Data");
                                int result = data.getInt("RetCode");
                                if (result == 0) {
                                    String authInfo = data.getString("Authorization");
                                    String date = data.getString("x-amz-date");
                                    //初始化鉴权信息
                                    AuthInfoManager.getInstance().setAuthInfo(authInfo, date);
                                    //添加鉴权结果回调接口(不是必须)
                                    AuthInfoManager.getInstance().addAuthResultListener(mCheckAuthResultListener);
                                    //开始向KSServer申请鉴权
                                    AuthInfoManager.getInstance().checkAuth();
                                    authResult = true;
                                } else {
                                    Log.e(TAG, "get auth failed from app server RetCode:" + result);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "get auth failed from app server json parse failed");
                            }
                        } else {
                            Log.e(TAG, "get auth failed from app server responseCode:" + responseCode);
                        }

                        final boolean finalAuthResult = authResult;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!finalAuthResult) {
                                    Toast.makeText(JokeActivity.this, "获取鉴权失败", Toast.LENGTH_SHORT)
                                            .show();
                                    //鉴权失败，尝试3次
                                    if (mRetryCount < MAX_RETRY_COUNT) {
                                        mRetryCount++;
                                        checkAuth();
                                    }
                                }
                            }
                        });
                    }
                };
            }

            if (mAuthTask != null && mAuthTask.getStatus() != AsyncTask.Status.FINISHED) {
                mAuthTask.cancel(true);
                mAuthTask = null;
            }
            //开启异步任务，向AppServer请求鉴权信息
            mAuthTask = new HttpRequestTask(mAuthResponse);
            String url = AUTH_SERVER_URI + "?Pkg=" + getApplicationContext().getPackageName();
            //    String url = AUTH_SERVER_URI + "?Pkg=" + "com.ksyun.media.shortvideo.demo";// com.qcwl.debo
            Log.d(TAG, "request auth:" + url);
            mAuthTask.execute(url);
        }
    }

    private AuthInfoManager.CheckAuthResultListener mCheckAuthResultListener = new AuthInfoManager
            .CheckAuthResultListener() {
        @Override
        public void onAuthResult(int result) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    AuthInfoManager.getInstance().removeAuthResultListener(mCheckAuthResultListener);
                    if (AuthInfoManager.getInstance().getAuthState()) {
                        Intent intent = new Intent(JokeActivity.this, ConfigActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(JokeActivity.this, "鉴权失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    };
    private void initCameraConfig(int UIType) {
        getSdkService().initConfiguration(
                new CameraConfiguration.Builder()
                        // 可设置最小录制时长,0代表不限制
                        .setVideoMinTime(configData.cameraMinTime)
                        // 可设置最大录制时长,0代表不限制
                        .setVideoMaxTime(configData.cameraMaxTime)
                        // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                        .useMultiShoot(configData.useMultiShoot)
                        /**
                         * 设置录制时默认界面:<br>
                         * 默认16：9录制:<br>
                         * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                         * 默认1：1:<br>
                         * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                         * 仅16：9录制:<br>
                         * CameraConfiguration.ONLY_SCREEN_SCREEN 仅1：1录制:<br>
                         * CameraConfiguration.ONLY_SQUARE_SCREEN
                         */
                        .setCameraUIType(UIType)
                        // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                        .setSingleCameraSaveToAlbum(configData.isSaveToAlbum)
                        // 设置录制时是否静音，true代表录制后无声音
                        .setAudioMute(false)
                        // 设置是否启用人脸贴纸功能
                        .enableFaceu(configData.isDefaultFace)
                        // 设置人脸贴纸鉴权证书
                        .setPack(authpack.A())
                        // 设置是否默认为后置摄像头
                        .setDefaultRearCamera(configData.isDefaultRearCamera)
                        // 是否显示相册按钮
                        .enableAlbum(configData.enableAlbum)
                        // 是否使用自定义相册
                        .useCustomAlbum(configData.useCustomAlbum)
                        // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                        .hideMV(configData.hideMV)
                        .hidePhoto(configData.hidePhoto)
                        .hideRec(configData.hideRec)
                        // 设置mv最小时长
                        .setCameraMVMinTime(configData.cameraMVMinTime)
                        // 设置mv最大时长
                        .setCameraMVMaxTime(configData.cameraMVMaxTime)
                        // 开启相机水印时需注册水印
                        // SdkEntry.registerOSDBuilder(CameraWatermarkBuilder.class);
                        // 相机录制水印
                        .enableWatermark(configData.enableCameraWatermark)
                        // 相机水印片头
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.header, 2f)
                        // 相机录制结束时片尾水印时长(0-1.0 单位：秒)
                        .setCameraTrailerTime(VEOSDBuilder.OSDState.end,
                                configData.cameraWatermarkEnd)
                        // 是否启用防篡改录制
                        .enableAntiChange(configData.enableAntiChange)
                        // 启用前置输出时镜像
                        .enableFrontMirror(configData.enableFrontMirror)
                        // 固定录制界面的方向
                        .setOrientation(configData.mRecordOrientation)
                        // 是否支持录制时播放音乐
                        .enablePlayMusic(configData.enablePlayMusic)
                        // 是否美颜
                        .enableBeauty(configData.enableBeauty)
                        //录制的云音乐
                        .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                        //滤镜(lookup)
                        .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                        .get());
    }
    /**
     * 还原持续久化保存的配置
     */
    private void restoreConfigInstanceState() {
        SharedPreferences sharedPreferences = getSharedPreferences("demo",
                Context.MODE_PRIVATE);
        configData = SdkService.restoreObject(sharedPreferences,
                "CONFIG_DATA", initAndGetConfigData());
    }
    /**
     * 初始化并返回配置
     */
    private ConfigData initAndGetConfigData() {
        if (configData == null) {
            configData = new ConfigData();
        }
        return configData;
    }

    /**
     * TODO:registerAllResultHandlers
     */
    private void registerAllResultHandlers() {
        registerActivityResultHandler(CAMERA_REQUEST_CODE,
                cameraResultHandler);
        registerActivityResultHandler(CAMERA_ANTI_CHANGE_REQUEST_CODE,
                cameraAntiChangeResultHandler);

        registerActivityResultHandler(ALBUM_REQUEST_CODE,
                albumResultHandler);
        registerActivityResultHandler(ALBUM_COMPRESS_REQUEST_CODE,
                albumCompressResultHandler);
        //照片电影
        registerActivityResultHandler(ALBUM_ANIMATION_REQUEST_CODE,
                ablumAnimationResultHandler);
        //异形显示
        registerActivityResultHandler(ALBUM_POINTF_REQUEST_CODE,
                ablumPointFResultHandler);

        registerActivityResultHandler(ALBUM_SOUND_EFFECT_REQUEST_CODE,
                albumSoundEffectResultHandler);
        registerActivityResultHandler(ALBUM_SILHOUETT_REQUEST_CODE,
                albumSilhouetteResultHandler);
        registerActivityResultHandler(EDIT_REQUEST_CODE, editResultHandler);
        registerActivityResultHandler(ALBUM_PLAYER_REQUEST_CODE,
                albumPlayerResultHandler);
        registerActivityResultHandler(TRIM_REQUEST_CODE, trimResultHandler);
        registerActivityResultHandler(TRIM_ALBUM_REQUEST_CODE, trimAlbumResultHandler);


        registerActivityResultHandler(SHORTVIDEO_CAMERA_REQUEST_CODE,
                shortvideoCameraResultHandler);
        registerActivityResultHandler(SHORTVIDEO_ALBUM_REQUEST_CODE,
                shortvideoAlbumResultHandler);
        registerActivityResultHandler(SHORTVIDEO_TRIM_REQUEST_CODE,
                shortvideoTrimResultHandler);
        registerActivityResultHandler(ALBUM_REQUEST_EXPORT_CODE, albumExportResultHandler);
        registerActivityResultHandler(ANIMATION_RESULT_CODE, mAnimResultHandler);

    }

    private ActivityResultHandler mAnimResultHandler = new ActivityResultHandler() {
        @Override
        public void onActivityResult(Context context, int resultCode, Intent data) {
            Log.i(TAG,"..............mAnimResultHandler");
            if (resultCode == RESULT_OK) {
                SdkEntry.playVideo(JokeActivity.this, data.getStringExtra(SdkEntry.EDIT_RESULT));
            }

        }
    };

    private ActivityResultHandler albumSoundEffectResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............albumSoundEffectResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.musicFilter(context, arrMediaListPath, 0);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler albumSilhouetteResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............albumSilhouetteResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        try {
                            SdkEntry.silhouette(context, arrMediaListPath.get(0), 0);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler cameraResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............cameraResultHandler");
            if (resultCode == SdkEntry.RESULT_CAMERA_TO_ALBUM) {
                // 点击拍摄的相册按钮，将返回在此，并在这里做进入相册界面操作
                Log.d(TAG, "............SdkEntry.RESULT_CAMERA_TO_ALBUM");
                /*SdkEntry.openAlbum(context,
                        UIConfiguration.ALBUM_SUPPORT_DEFAULT,
                        ALBUM_REQUEST_CODE);*/
                SdkEntry.selectMedia(JokeActivity.this, EDIT_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // 美颜参数回调
                FaceuInfo info = data
                        .getParcelableExtra(SdkEntry.INTENT_KEY_FACEU);
                if (null != info) {
                    Log.e("faceu美颜参数", info.toString());
                }
                // 拍摄后返回的媒体路径
                ArrayList<String> arrMediaListPath = new ArrayList<String>();
                String videoPath = data
                        .getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH);
                String picPath = data
                        .getStringExtra(SdkEntry.INTENT_KEY_PICTURE_PATH);
                arrMediaListPath.add(videoPath);
                arrMediaListPath.add(picPath);
                String logInfo = String.format("Video path：%s,Picture path：%s",
                        videoPath, picPath);
                Log.e(TAG, logInfo);


                if (configData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY) {

                    if (videoPath != null) {
                        Log.i(TAG,"..............ALBUM_SUPPORT_IMAGE_ONLY");
                        SdkEntry.selectMedia(context);
                        return;
                    }
                } else if (configData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY) {
                    if (picPath != null) {
                        Log.i(TAG,"..............ALBUM_SUPPORT_VIDEO_ONLY");
                        SdkEntry.selectMedia(context);
                        return;
                    }
                }

                if (data.getBooleanExtra(SdkEntry.INTENT_KEY_USE_MV_EDIT, false)) {
                    Log.i(TAG, "onActivityResult: mv");
                    initCameraShortVideoConfig();
                } else {
                    Log.i(TAG, "onActivityResult: false");
                    initEditorUIAndExportConfig();
                }
                try {
                    Log.i(TAG,"..............editMedia");
                    //editMedia(context, arrMediaListPath);
                    SdkEntry.editMedia(context,arrMediaListPath,3);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private ActivityResultHandler cameraAntiChangeResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............cameraAntiChangeResultHandler");
            if (resultCode == RESULT_OK) {
                String logInfo = String.format("Video path：%s,Picture path：%s",
                        data.getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH),
                        data.getStringExtra(SdkEntry.INTENT_KEY_PICTURE_PATH));
                Log.d(TAG, logInfo);
                SDKUtils.onPlayVideo(context, data.getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH));
            }
        }
    };
    private ActivityResultHandler albumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............albumResultHandler");
            if (resultCode == SdkEntry.RESULT_ALBUM_TO_CAMERA) {
                // 点击相册的拍摄按钮，将返回在此，并在这里做进入拍摄界面操作
                SdkEntry.record(context, CAMERA_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                String logInfo = "";
                for (String path : arrMediaListPath) {
                    Log.d(TAG, path);
                    logInfo += path + "\n";
                }
                //Toast.makeText(context, logInfo, Toast.LENGTH_LONG).show();

            }
        }
    };
    /**
     * 要导出的视频
     */
    private ActivityResultHandler albumExportResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG, "..................albumExportResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);

                for (String path : arrMediaListPath) {
                    VideoConfig videoConfig = new VideoConfig();
                    float durationS = VirtualVideo.getMediaInfo(path, videoConfig, true);
                    Log.d(TAG, path + "," + durationS + "," + videoConfig);
                }

                VideoConfig videoConfig = new VideoConfig();
                videoConfig.setVideoEncodingBitRate(4000 * 1000);

                int numChannel = AudioConfigDialog.audioNumChannel;
                if (numChannel == 0) {
                    numChannel = videoConfig.getAudioNumChannels();
                }
                int sampleRate = AudioConfigDialog.audioSampleRate;
                if (sampleRate == 0) {
                    sampleRate = videoConfig.getAudioSampleRate();
                }
                int audioBitRate = AudioConfigDialog.audioBitRate;
                if (audioBitRate == 0) {
                    audioBitRate = videoConfig.getAudioBitrate();
                }
                videoConfig.setAudioEncodingParameters(numChannel, sampleRate, audioBitRate);

                //.水印,左下角
                Watermark watermark = new Watermark(EDIT_WATERMARK_PATH);
                watermark.setShowRect(new RectF(0, 1f, 1f, 1.0f));
                //片尾
                Trailer trailer = new Trailer(SDKUtils.createVideoTrailerImage(JokeActivity.this, "秀友", 480, 50, 50), 2f, 0.5f);

                String exportPath = Environment.getExternalStorageDirectory() + "/export.mp4";
                ExportVideoLisenter mExportListener = new ExportVideoLisenter(exportPath);
                try {
                    SdkEntry.exportVideo(JokeActivity.this, videoConfig, arrMediaListPath, exportPath, watermark, trailer, mExportListener);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * 导出视频的回调演示
     */
    private class ExportVideoLisenter implements ExportListener {
        private String mPath;
        private AlertDialog mDialog;
        private ProgressBar mProgressBar;
        private Button mBtnCancel;
        private TextView mTvTitle;

        public ExportVideoLisenter(String videoPath) {
            mPath = videoPath;
        }

        @Override
        public void onExportStart() {
            View v = LayoutInflater.from(JokeActivity.this).inflate(
                    R.layout.progress_view, null);
            mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
            mTvTitle.setText("正在导出...");
            mProgressBar = (ProgressBar) v.findViewById(R.id.pbCompress);
            mBtnCancel = (Button) v.findViewById(R.id.btnCancelCompress);
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SdkEntry.cancelExport();
                }
            });
            mDialog = new AlertDialog.Builder(JokeActivity.this).setView(v)
                    .show();
            mDialog.setCanceledOnTouchOutside(false);
        }

        /**
         * 导出进度回调
         *
         * @param progress 当前进度
         * @param max      最大进度
         * @return 返回是否继续执行，false为终止导出
         */
        @Override
        public boolean onExporting(int progress, int max) {
            if (mProgressBar != null) {
                mProgressBar.setMax(max);
                mProgressBar.setProgress(progress);
            }
            return true;
        }

        @Override
        public void onExportEnd(int result) {
            mDialog.dismiss();
            if (result >= VirtualVideo.RESULT_SUCCESS) {
                SDKUtils.onPlayVideo(JokeActivity.this, mPath);
            } else if (result != VirtualVideo.RESULT_SAVE_CANCEL) {
                Log.e(TAG, "onExportEnd: " + result);
                Toast.makeText(JokeActivity.this, "导出失败" + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private ActivityResultHandler editResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............editResultHandler="+resultCode);
            if (resultCode == RESULT_OK && null != data) {
                String mediaPath = data.getStringExtra(SdkEntry.EDIT_RESULT);
                if (mediaPath != null) {
                    Log.d(TAG, mediaPath);
                    /*Toast.makeText(context, mediaPath, Toast.LENGTH_LONG)
                            .show();*/
                }
            }else if(resultCode == 0){
                //从相册进入编辑页面 当点击编辑页面的返回   回到录制界面
                SdkEntry.record(JokeActivity.this, CAMERA_REQUEST_CODE);
            }
        }
    };

    private ActivityResultHandler trimResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............trimResultHandler");
            if (resultCode == RESULT_OK) {
                float startTime = data.getFloatExtra(SdkEntry.TRIM_START_TIME, 0f);
                float endTime = data.getFloatExtra(SdkEntry.TRIM_END_TIME, 0);
                String path = data.getStringExtra(SdkEntry.TRIM_MEDIA_PATH);
                Rect rect = data.getParcelableExtra(SdkEntry.TRIM_CROP_RECT);
                String logInfo = "截取开始时间:" + startTime + "s" + ",结束时间:"
                        + endTime + "s\n裁剪区域：" + rect + "...视频:" + path;
                Log.d(TAG, logInfo);
                Toast.makeText(context, logInfo, Toast.LENGTH_LONG).show();
            }
        }
    };

    private ActivityResultHandler trimAlbumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............trimAlbumResultHandler");
            if (resultCode == RESULT_OK) {
                ArrayList<String> arrCameraMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrCameraMediaListPath != null
                        && arrCameraMediaListPath.get(0) != null) {
                    String path = arrCameraMediaListPath.get(0);
                    initTrimConfig();
                    try {
                        trimVideo(context, path, TRIM_REQUEST_CODE);
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    private ActivityResultHandler shortvideoCameraResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............shortvideoCameraResultHandler");
            if (resultCode == SdkEntry.RESULT_CAMERA_TO_ALBUM) {
                // TODO: 按下拍摄的相册按钮，在此进入相册
                SdkEntry.openAlbum(context,
                        UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY,
                        SHORTVIDEO_ALBUM_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // TODO: 拍摄完成，在此进入编辑界面
                FaceuInfo info = data
                        .getParcelableExtra(SdkEntry.INTENT_KEY_FACEU);
                if (null != info) {
                    Log.e("faceu美颜参数", info.toString());
                }

                ArrayList<String> arrMediaListPath = new ArrayList<String>();
                arrMediaListPath.add(data
                        .getStringExtra(SdkEntry.INTENT_KEY_VIDEO_PATH));
                try {
                    editMedia(context, arrMediaListPath);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ActivityResultHandler shortvideoAlbumResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............shortvideoAlbumResultHandler");
            if (resultCode == SdkEntry.RESULT_ALBUM_TO_CAMERA) {
                // TODO: 按下相册的拍摄按钮，在此进入拍摄界面
                SdkEntry.record(context, SHORTVIDEO_CAMERA_REQUEST_CODE);
            } else if (resultCode == RESULT_OK) {
                // TODO: 选择媒体结束后，在此进入截取界面
                ArrayList<String> arrCameraMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrCameraMediaListPath != null) {
                    if (arrCameraMediaListPath.get(0) != null) {
                        String path = arrCameraMediaListPath.get(0);
                        // 获取视频信息
                        VideoMetadataRetriever vmr = new VideoMetadataRetriever();
                        vmr.setDataSource(path);
                        float duration = Float
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_DURATION));
                        int videoHeight = Integer
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                        int videoWidth = Integer
                                .valueOf(vmr
                                        .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_WIDHT));

                        if (duration < 15 && videoHeight == videoWidth) {
                            // TODO: 视频小于15秒且为正方形视频，将跳过截取，进入编辑界面
                            try {
                                editMedia(JokeActivity.this,
                                        arrCameraMediaListPath,
                                        EDIT_REQUEST_CODE);
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        try {
                            trimVideo(JokeActivity.this,
                                    arrCameraMediaListPath.get(0),
                                    SHORTVIDEO_TRIM_REQUEST_CODE);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private ActivityResultHandler shortvideoTrimResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............shortvideoTrimResultHandler");
            if (resultCode == RESULT_OK) {
                // TODO: 截取完成，在此进入编辑界面
                EditObject eo = new EditObject(
                        data.getStringExtra(SdkEntry.TRIM_MEDIA_PATH));
                Rect clip = data.getParcelableExtra(SdkEntry.TRIM_CROP_RECT);
                if (null != clip) {
                    eo.setCropRect(new RectF(clip));
                }
                eo.setStartTime(data.getFloatExtra(SdkEntry.TRIM_START_TIME, 0f));
                eo.setEndTime(data.getFloatExtra(SdkEntry.TRIM_END_TIME, 0f));
                try {
                    editMedia(context, eo, EDIT_REQUEST_CODE);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ActivityResultHandler albumPlayerResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............albumPlayerResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        SDKUtils.onPlayVideo(context, arrMediaListPath.get(0));
                    }
                }

            }
        }
    };

    /**
     * 选择要压缩的视频资源
     */
    private ActivityResultHandler albumCompressResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............albumCompressResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    if (!TextUtils.isEmpty(arrMediaListPath.get(0))) {
                        SdkEntry.onCompressVideo(context,
                                arrMediaListPath.get(0), iCompressVideoCallback);
                    }
                }
            }
        }
    };
    /**
     * 照片电影模式
     */
    private ActivityResultHandler ablumAnimationResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG,"..............ablumAnimationResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    SdkEntry.onAnimation(JokeActivity.this, arrMediaListPath, true, ANIMATION_RESULT_CODE);
                }
            }
        }
    };

    /**
     * 异形显示
     */
    private ActivityResultHandler ablumPointFResultHandler = new ActivityResultHandler() {

        @Override
        public void onActivityResult(Context context, int resultCode,
                                     Intent data) {
            Log.i(TAG, "..................ablumPointFResultHandler");
            if (resultCode == RESULT_OK) {
                // 返回选择的图片视频地址list
                ArrayList<String> arrMediaListPath = data
                        .getStringArrayListExtra(SdkEntry.ALBUM_RESULT);
                if (arrMediaListPath.size() > 0) {
                    SdkEntry.onAnimation(JokeActivity.this, arrMediaListPath, false, ANIMATION_RESULT_CODE);
                }
            }
        }
    };

    /**
     * 短视频录制推荐参数
     */
    private void initCameraShortVideoConfig() {

        CameraConfiguration cameraConfig = new CameraConfiguration.Builder()
                // 为true代表多次拍摄，拍摄完成一段之后，将保存至相册并开始下一段拍摄，默认为false单次拍摄，拍摄完成后返回资源地址
                .useMultiShoot(false)
                /**
                 * 设置录制时默认界面:<br>
                 * 默认16：9录制:<br>
                 * CameraConfiguration. WIDE_SCREEN_CAN_CHANGE<br>
                 * 默认1：1:<br>
                 * CameraConfiguration. SQUARE_SCREEN_CAN_CHANGE<br>
                 * 仅1：1录制:<br>
                 * CameraConfiguration.ONLY_SQUARE_SCREEN
                 */
                .setCameraUIType(CameraConfiguration.ONLY_SQUARE_SCREEN)
                // 设置拍摄完成后，是否保存至相册（仅单次拍摄方式有效），同时通过onActivityResult及SIMPLE_CAMERA_REQUEST_CODE返回
                .setSingleCameraSaveToAlbum(true)
                // 设置录制时是否静音，true代表录制后无声音
                .setAudioMute(false)
                // 设置是否启用人脸贴纸
                .enableFaceu(false)
                // 设置启用人脸贴纸鉴权证书
                .setPack(authpack.A())
                // 设置是否默认为后置摄像头
                .setDefaultRearCamera(false)
                // 是否显示相册按钮
                .enableAlbum(true)
                // 是否使用自定义相册
                .useCustomAlbum(false)
                // 设置隐藏拍摄功能（全部隐藏将强制开启视频拍摄）
                .hideMV(false).hidePhoto(true).hideRec(true)
                // 设置mv最小时长
                .setCameraMVMinTime(3)
                // 设置mv最大时长
                .setCameraMVMaxTime(15)
                //录制的云音乐
                .setCloudMusicUrl(configData.enableNewApi ? configData.customApi : "")
                //滤镜(lookup)
                .setFilterUrl(configData.enableNewApi ? configData.customApi : "")
                // 强制美颜
                .enableBeauty(true).get();
        // 视频编辑UI配置

        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(false);
        if (true) {
            //是否启用新的网络接口方式(资源放到自己服务器或锐动服务器)
            ConfigData configData = new ConfigData();
            configData.enableMV = true;
            configData.customApi = JokeActivity.this.configData.customApi;

            initThridServer(builder, configData);
        } else {
            //网络音乐
            builder.setMusicUrl(ConfigData.MUSIC_URL)
                    // 设置MV和mv网络地
                    .enableMV(true, ConfigData.WEB_MV_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }


        // 设置画面比例为1:1
        builder.setVideoProportion(UIConfiguration.PROPORTION_SQUARE)
                // 相册仅支持视频
                .setAlbumSupportFormat(UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY)
                // 设置视频选择最大数量
                .setMediaCountLimit(1)
                // 设置隐藏相册中的拍摄按钮
                .enableAlbumCamera(false)
                //显示配乐
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.SOUNDTRACK, true)
                //关闭配音
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING, false)
                // 隐藏字幕
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        false)
                // 隐藏片段编辑
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING, false)
                // 隐藏特效
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS, false)
                // 启用自动重播
                .enableAutoRepeat(true);


        UIConfiguration uiConfig = builder.get();

        TrimConfiguration trimConfig = new TrimConfiguration.Builder()
                //设置实际截取时视频导出最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                // 设置默认裁剪区域为1:1
                .setDefault1x1CropMode(true)
                // 设置截取返回类型
                .setTrimReturnMode(TrimConfiguration.TRIM_RETURN_TIME)
                // 设置截取类型
                .setTrimType(TrimConfiguration.TRIM_TYPE_SINGLE_FIXED)
                // 设置是否显示1：1按钮
                .enable1x1(false)
                // 设置定长截取时间
                .setTrimDuration(15).get();
        getSdkService().initConfiguration(null, uiConfig,
                cameraConfig);
        getSdkService().initTrimConfiguration(trimConfig);
    }
    /**
     * 新的网络接口方式(资源放到自己服务器或锐动服务器)
     *
     * @param builder
     * @param configData
     */
    private void initThridServer(UIConfiguration.Builder builder, ConfigData configData) {

        String url = configData.customApi;
        // 设置MV和mv网络地址
        builder.enableNewMV(configData.enableMV, url)
                //设置字幕URL
                .setTitlingUrl(url)
                //设置字体URL
                .setFontUrl(url)
                //特效URL
                .setSpecialEffectsUrl(url)
                //滤镜URL(必须是lookup滤镜)
                .setFilterUrl(url)
                //转场URL
                .setTransitionUrl(url)
                // 设置自定义的网络音乐
                .setNewMusicUrl(url)
                //云音乐
                .setNewCloudMusicUrl(url);


    }
    /**
     * 初始标准编辑及导出配置
     */
    private void initEditorUIAndExportConfig() {

        initAndGetConfigData();
        // 视频编辑UI配置


        UIConfiguration.Builder builder = new UIConfiguration.Builder()
                // 设置是否使用自定义相册
                .useCustomAlbum(configData.useCustomAlbum)
                // 设置向导化
                .enableWizard(configData.enableWizard)
                // 设置自动播放
                .enableAutoRepeat(configData.enableAutoRepeat)
                // 配音模式
                .setVoiceLayoutType(configData.voiceLayoutType)
                // 设置秀拍客相册支持格式
                .setAlbumSupportFormat(configData.albumSupportFormatType)
                // 设置默认进入界面画面比例
                .setVideoProportion(configData.videoProportionType)
                // 设置滤镜界面风格
                .setFilterType(configData.filterLayoutType)
                // 设置相册媒体选择数量上限(目前只对相册接口生效)
                .setMediaCountLimit(configData.albumMediaCountLimit)
                // 设置相册是否显示跳转拍摄按钮(目前只对相册接口生效)
                .enableAlbumCamera(configData.enableAlbumCamera)
                // 编辑与导出模块显示与隐藏（默认不设置为显示）
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SOUNDTRACK,
                        configData.enableSoundTrack)

                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.DUBBING,
                        configData.enableDubbing)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.FILTER,
                        configData.enableFilter)
                .setEditAndExportModuleVisibility(UIConfiguration.EditAndExportModules.TITLING,
                        configData.enableTitling)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.SPECIAL_EFFECTS,
                        configData.enableSpecialEffects)
                .setEditAndExportModuleVisibility(
                        UIConfiguration.EditAndExportModules.CLIP_EDITING,
                        configData.enableClipEditing)
                // 片段编辑模块显示与隐藏（默认不设置为显示）
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.IMAGE_DURATION_CONTROL,
                        configData.enableImageDuration)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.EDIT,
                        configData.enableEdit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRIM,
                        configData.enableTrim)
                .setClipEditingModuleVisibility(
                        UIConfiguration.ClipEditingModules.VIDEO_SPEED_CONTROL,
                        configData.enableVideoSpeed)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SPLIT,
                        configData.enableSplit)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.COPY,
                        configData.enableCopy)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.PROPORTION,
                        configData.enableProportion)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.SORT,
                        configData.enableSort)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TEXT,
                        configData.enableText)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.REVERSE,
                        configData.enableReverse)
                .setClipEditingModuleVisibility(UIConfiguration.ClipEditingModules.TRANSITION, true);

        if (configData.enableNewApi) {
            initThridServer(builder, configData);
        } else {
            // 设置MV和mv网络地址
            builder.enableMV(configData.enableMV, ConfigData.WEB_MV_URL)
                    // 设置自定义的网络音乐
                    .setMusicUrl(ConfigData.MUSIC_URL)
                    //云音乐
                    .setCloudMusicUrl(ConfigData.CLOUDMUSIC_URL);
        }
        UIConfiguration uiConfig = builder
                //是否显示本地音乐
                .enableLocalMusic(configData.enableLocalMusic)
                // 字幕、特效在mv的上面
                .enableTitlingAndSpecialEffectOuter(configData.enableTitlingAndSpecialEffectOuter)
                .get();

        // 导出视频参数配置
        ExportConfiguration exportConfig = new ExportConfiguration.Builder()
                // 设置保存路径，传null或不设置
                // 将保存至默认路径(即调用SdkEntry.initialize初始时自定义路径）
                .setSavePath(null)
                //设置导出时最大边,不设置时，默认为640
                .setVideoMaxWH(640)
                //设置视频导出码率，Mbps为单位,不设置时，默认为4
                .setVideoBitRate(4)
                //设置视频导出帧率，,不设置时，默认为30
                .setVideoFrameRate(30)
                // 设置片尾图片路径，传null或者不设置 将没有片尾
                .setTrailerPath(configData.videoTrailerPath)
                // 设置片尾时长 单位s 默认2s
                .setTrailerDuration(2)
                // 设置导出视频时长 单位s 传0或者不设置 将导出完整视频
                .setVideoDuration(configData.exportVideoDuration)
                // 设置添加媒体时长限制 单位s 传0或者不设置 将没有限制
                .setImportVideoDuration(0)
                // 设置图片水印路径
                .setWatermarkPath(configData.enableWatermark ? EDIT_WATERMARK_PATH : null)
                // 设置水印显示模式
                .setWatermarkShowMode(Watermark.MODE_DEFAULT)
                // 设置是否使用文字水印（使用文字水印，将不再显示图片水印）
                .enableTextWatermark(configData.enableTextWatermark)
                // 设置文字水印内容（开启文字水印才生效）
                .setTextWatermarkContent("watarmark")
                // 设置文字水印大小（开启文字水印才生效）
                .setTextWatermarkSize(10)
                // 设置文字水印颜色（开启文字水印才生效）
                .setTextWatermarkColor(Color.WHITE)
                // 设置文字水印阴影颜色（开启文字水印才生效）
                .setTextWatermarkShadowColor(Color.BLACK)
                // 设置水印位置 (文字或图片水印开启才生效)
                .setWatermarkPosition(configData.watermarkShowRectF).get();

        // 获取秀拍客配置服务器
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            // 初始化所有配置
            sdkService.initConfiguration(exportConfig, uiConfig);
        }
    }
    /**
     * 初始视频截取配置
     */
    private void initTrimConfig() {
        SdkService sdkService = getSdkService();
        if (null != sdkService) {
            sdkService
                    .initTrimConfiguration(new TrimConfiguration.Builder()
                            //设置实际截取时视频导出最大边,不设置时，默认为640
                            .setVideoMaxWH(640)
                            //设置实际截取时视频导出码率，Mbps为单位,不设置时，默认为4
                            .setVideoBitRate(4)
                            // 设置默认裁剪区域为1:1
                            .setDefault1x1CropMode(
                                    configData.default1x1CropMode)
                            // 设置是否显示1:1裁剪按钮
                            .enable1x1(configData.enable1x1)
                            // 设置截取返回类型
                            .setTrimReturnMode(configData.mTrimReturnMode)
                            // 设置截取类型
                            .setTrimType(configData.mTrimType)
                            // 设置两定长截取时间
                            .setTrimDuration(configData.trimTime1, configData.trimTime2)
                            // 设置单个定长截取时间
                            .setTrimDuration(configData.trimSingleFixedDuration)
                            .get());
        }
    }
    /**
     * 压缩视频回调函数
     */
    private ICompressVideoCallback iCompressVideoCallback = new ICompressVideoCallback() {
        private AlertDialog dialog;
        private ProgressBar progressBar;
        private Button btnCancel;
        private Date startDate;

        @Override
        public void onCompressStart() {
            startDate = new Date(System.currentTimeMillis());
            View v = LayoutInflater.from(JokeActivity.this).inflate(
                    R.layout.progress_view, null);
            progressBar = (ProgressBar) v.findViewById(R.id.pbCompress);
            btnCancel = (Button) v.findViewById(R.id.btnCancelCompress);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SdkEntry.cancelCompressVideo();
                }
            });
            dialog = new AlertDialog.Builder(JokeActivity.this).setView(v)
                    .show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        public void onProgress(int progress, int max) {
            if (progressBar != null) {
                progressBar.setMax(max);
                progressBar.setProgress(progress);
            }
        }

        @Override
        public void onCompressError(String errorLog) {
            Log.e(TAG, errorLog);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

        @Override
        public void onCompressComplete(String path) {
            Date endDate = new Date(System.currentTimeMillis());
            long diff = endDate.getTime() - startDate.getTime();
            VideoMetadataRetriever vmr = new VideoMetadataRetriever();
            vmr.setDataSource(path);
            float duration = Float
                    .valueOf(vmr
                            .extractMetadata(VideoMetadataRetriever.METADATA_KEY_VIDEO_DURATION));
            Toast.makeText(JokeActivity.this, "压缩倍速: " + String.format("%.2f", (duration * 1000) / diff) + "x", Toast.LENGTH_LONG).show();
            SDKUtils.onPlayVideo(JokeActivity.this, path);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    };
    /**
     * 注册响应activity result
     *
     * @param requestCode
     * @param handler
     */
    private void registerActivityResultHandler(int requestCode,
                                               ActivityResultHandler handler) {
        if (null == registeredActivityResultHandlers) {
            registeredActivityResultHandlers = new SparseArray<JokeActivity.ActivityResultHandler>();
        }
        registeredActivityResultHandlers.put(requestCode, handler);
    }
    private interface ActivityResultHandler {
        /**
         * 响应
         *
         * @param context
         * @param resultCode The integer result code returned by the child activity
         *                   through its setResult().
         * @param data       An Intent, which can return result data to the caller
         *                   (various data can be attached to Intent "extras").
         */
        void onActivityResult(Context context, int resultCode, Intent data);
    }
    private SparseArray<ActivityResultHandler> registeredActivityResultHandlers;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"........................onActivityResult="+resultCode+"        "+requestCode);
        if (null != registeredActivityResultHandlers) {
            ActivityResultHandler handler = registeredActivityResultHandlers
                    .get(requestCode);
            if (null != handler) {
                handler.onActivityResult(this, resultCode, data);
            }
        }
        if (resultCode == 0&&requestCode == 3){
            SdkEntry.record(JokeActivity.this, CAMERA_REQUEST_CODE);
        }
    }
}
