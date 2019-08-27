package com.qcwl.debo.ui.found.jinshanyun;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.shortvideo.utils.FileUtils;
import com.ksyun.media.shortvideo.utils.KS3ClientWrap;
import com.ksyun.media.shortvideo.utils.ProbeMediaInfoTools;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.jinshanyun.util.HttpRequestTask;
import com.qcwl.debo.ui.found.jinshanyun.util.KS3TokenTask;
import com.qcwl.debo.utils.FileUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class PublishActivity extends BaseActivity {
    //获取ks3播放地址，仅供demo使用，不提供上线服务
    private static String FILE_URL_SERVER = "http://ksvs-demo.ks-live.com:8720/api/upload/ks3/signurl";
    public static final String TAG = "PublishActivity";
    public static final String COMPOSE_PATH = "compose_path";
    public static final String MIME_TYPE = "mime_type";
    public static final String PREVIEW_LEN = "preview_length";

    //标记上传状态
    private static final int UPLOAD_STATE_NONE = 0;
    private static final int UPLOAD_STATE_STARTING = 1;
    private static final int UPLOAD_STATE_STARTED = 2;

    private View mPreviewLayout; //上传后的预览布局
    private View mCoverSeekLayout; //封面选择布局
    private ImageView mCoverBack;
    private TextView mCoverComplete;
    private ImageView mCoverImage;
    private AppCompatSeekBar mCoverSeekBar;
    private ImageView mPreviewBack;
    private ImageView mSaveToDCIM;
    private TextView mSaveToast;
    private WebView mGifView;  //用于预览gif
    private SurfaceView mVideoSurfaceView;  //用于预览视频
    private SurfaceHolder mSurfaceHolder;
    private  File PHOTO_DIR;
    private String mLocalPath;  //合成视频的本地存储地址
    private String mFilePath;  //视频实际预览地址
    private String mFileMineType;
    private volatile Bitmap mBitmap;  //视频封面
    private long mSeekTime;
    private Timer mSeekTimer;
    private long mPreviewLength;  //视频预览时长
    private ButtonObserver mButtonObserver;
    private ProbeMediaInfoTools mImageSeekTools; //根据时间获取视频帧的工具类
    private Handler mMainHandler;

    private HttpRequestTask mPlayUrlGetTask;  //获取网络播放地址的http请求任务
    private KSYMediaPlayer mMediaPlayer;

    private HandlerThread mSeekThumbnailThread;
    private Handler mSeekThumbnailHandler;
    private Runnable mSeekThumbnailRunable;
    private volatile boolean mStopSeekThumbnail = true;
    private KS3ClientWrap mKS3Wrap;
    /*****合成窗口View*****/
    private TextView mStateTextView;
    private TextView mProgressText;   //显示上传进度
    private PopupWindow mUploadWindow;  //上传状态的显示窗口
    private View mParentView;
    private VODUploadClient uploader;
    private AtomicInteger mUploadState;  //上传ks3状态

    public LocationClient locationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private String latitude, longitude;
    private  String city="";

    private String upload_address="";
    private String upload_auth="";
    private String video_id="";
    private TextView name,describe;
    private int index = 0;
    private  File avaterFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish);

        mParentView = LayoutInflater.from(this).inflate(R.layout.activity_publish, null);
        //must set
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationClient = new LocationClient(getApplicationContext());
        initLocation();
        locationClient.registerLocationListener(myListener);

        mLocalPath = getIntent().getExtras().getString(COMPOSE_PATH);
        mFilePath = mLocalPath;
        mMainHandler = new Handler();
        mFileMineType = getIntent().getExtras().getString(MIME_TYPE);
        mPreviewLength = getIntent().getExtras().getLong(PREVIEW_LEN);
        mPreviewLayout = findViewById(R.id.compose_preview_layout);
        mCoverSeekLayout = findViewById(R.id.cover_layout);
        mButtonObserver = new ButtonObserver();
        name = (TextView) findViewById(R.id.name);
        describe = (TextView) findViewById(R.id.describe);
        mPreviewBack = (ImageView) findViewById(R.id.preview_back);
        mPreviewBack.setOnClickListener(mButtonObserver);
        mSaveToDCIM = (ImageView) findViewById(R.id.save_to_album);
        mSaveToDCIM.setOnClickListener(mButtonObserver);
        mSaveToast = (TextView) findViewById(R.id.save_toast);
        getMediaPlayer();
        mVideoSurfaceView = (SurfaceView) findViewById(R.id.compose_preview);
        mGifView = (WebView) findViewById(R.id.gif_view);
        WebSettings webSettings = mGifView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mUploadState = new AtomicInteger(UPLOAD_STATE_NONE);
        if (!mFileMineType.equals(FileUtils.MIME_TYPE_GIF)) {
            startCoverSeek();
        } else {
            mCoverSeekLayout.setVisibility(View.GONE);
            startUpload();  //若为gif则不选择封面直接上传
        }

        MPermissions.requestPermissions(this, REQUEST_CODE, permissions);

        uploader = new VODUploadClientImpl(getApplicationContext());
        VODUploadCallback callback = new VODUploadCallback() {
            @Override
            public void onUploadSucceed(UploadFileInfo info) {
                //上传成功
                OSSLog.logDebug("onsucceed ------------------" + info.getFilePath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(latitude)||TextUtils.isEmpty(longitude)||TextUtils.isEmpty(city)){
                            ToastUtils.showShort(PublishActivity.this,"请打开定位权限，方便同城展示哟！");
                        }else {
                            video_success_notyfy(latitude,longitude,city,name.getText().toString(),describe.getText().toString(),avaterFile);
                        }
                    }
                });
            }

            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                //上传失败
                OSSLog.logError("onfailed ------------------ " + info.getFilePath() + " " + code + " " + message);
                hideProgressDialog();
                ToastUtils.showShort(PublishActivity.this,"上传失败"+"message");
            }

            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                //上传进度
                OSSLog.logDebug("onProgress ------------------ " + info.getFilePath() + " " + uploadedSize + " " + totalSize);
            }

            @Override
            public void onUploadTokenExpired() {
                // getShotVideoToken();//tst方式
                getNew_ShotVideoAdress();
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传过程中，状态由正常切换为异常时触发
                OSSLog.logError("onUploadRetry ------------- ");
            }

            @Override
            public void onUploadRetryResume() {
                //上传过程中，从异常中恢复时触发
                OSSLog.logError("onUploadRetryResume ------------- ");
            }

            @Override
            public void onUploadStarted(UploadFileInfo uploadFileInfo) {
                uploader.setUploadAuthAndAddress(uploadFileInfo, upload_auth, upload_address);
                OSSLog.logError("onUploadStarted ------------- ");
                OSSLog.logError("file path:" + uploadFileInfo.getFilePath() +
                        ", endpoint: " + uploadFileInfo.getEndpoint() +
                        ", bucket:" + uploadFileInfo.getBucket() +
                        ", object:" + uploadFileInfo.getObject() +
                        ", status:" + uploadFileInfo.getStatus());
            }
        };
        uploader.init(callback);

    }

    private VodInfo getVodInfo() {
        index++;
        VodInfo vodInfo = new VodInfo();

        vodInfo.setTitle(name.getText().toString());//标题
        vodInfo.setDesc(describe.getText().toString());//描述
        vodInfo.setCateId(index);
        vodInfo.setIsProcess(true);
        vodInfo.setCoverUrl("");//http://files.jb51.net/file_images/article/201610/20161027113728286.png?2016927113755
        List<String> tags = new ArrayList<>();
        tags.add("标签" + index);
        vodInfo.setTags(tags);
        vodInfo.setIsShowWaterMark(false);
        vodInfo.setPriority(7);
        return vodInfo;
    }




    private void startUpload() {
        uploader.clearFiles();
        uploader.addFile(mFilePath,
                getVodInfo());

        //上传
        if (name.getText().toString().isEmpty()){
            ToastUtils.showShort(PublishActivity.this,"请填写视频名称");
        }else if (describe.getText().toString().isEmpty()){
            ToastUtils.showShort(PublishActivity.this,"请填写视频描述");
        }else {

            getShotVideoAdress(name.getText().toString(),mFilePath,PHOTO_DIR+"avater.jpg",describe.getText().toString());//封面，没传
        }
    }

    private void initSeekThread() {
        if (mSeekThumbnailThread == null) {
            mSeekThumbnailThread = new HandlerThread("screen_setup_thread", Thread.NORM_PRIORITY);
            mSeekThumbnailThread.start();
            mSeekThumbnailHandler = new Handler(mSeekThumbnailThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    return;
                }
            };

            mSeekThumbnailRunable = new Runnable() {
                @Override
                public void run() {
                    if (mSeekTime < 0) {
                        mSeekTime = 0;
                    }

                    if (mSeekTime > mPreviewLength) {
                        mSeekTime = mPreviewLength;
                    }
                    mBitmap = mImageSeekTools.getVideoThumbnailAtTime(mLocalPath, mSeekTime,
                            0, 0, true);
                    PHOTO_DIR = new File(Environment.getExternalStorageDirectory()+"image");//设置保存路径
                    avaterFile = new File(PHOTO_DIR, "avater.jpg");//设置文件名称
                    if(avaterFile.exists()){
                        avaterFile.delete();
                    }
                    try{
                        avaterFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(avaterFile);
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch(IOException e){
                        e.printStackTrace();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mBitmap != null) {
                                mCoverImage.setImageBitmap(mBitmap);
                            }
                        }
                    });
                    if (!mStopSeekThumbnail) {
                        mSeekThumbnailHandler.postDelayed(mSeekThumbnailRunable, 100);
                    }
                }
            };
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                uploader.clearFiles();
                hideProgressDialog();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {

        releasePlay();

        if (mPlayUrlGetTask != null) {
            mPlayUrlGetTask.cancel(true);
            mPlayUrlGetTask.release();
            mPlayUrlGetTask = null;
        }

        mStopSeekThumbnail = true;
        if (mSeekThumbnailHandler != null) {
            mSeekThumbnailHandler.removeCallbacksAndMessages(null);
            mSeekThumbnailHandler = null;
        }

        if (mSeekThumbnailThread != null) {
            mSeekThumbnailThread.getLooper().quit();
            try {
                mSeekThumbnailThread.join();
            } catch (InterruptedException e) {
                Log.d(TAG, "ScreenSetUpThread Interrupted!");
            } finally {
                mSeekThumbnailThread = null;
            }
        }

        super.onDestroy();
    }

    private void startCoverSeek() {
        mCoverBack = (ImageView) findViewById(R.id.cover_back);
        mCoverBack.setOnClickListener(mButtonObserver);
        mCoverComplete = (TextView) findViewById(R.id.cover_complete);
        mCoverComplete.setOnClickListener(mButtonObserver);
        mCoverImage = (ImageView) findViewById(R.id.cover_image);
        mCoverSeekBar = (AppCompatSeekBar) findViewById(R.id.cover_seekBar);
        mImageSeekTools = new ProbeMediaInfoTools();
        mBitmap = mImageSeekTools.getVideoThumbnailAtTime(mLocalPath, mSeekTime, 0, 0, true);

        mImageSeekTools.probeMediaInfo(mLocalPath, new ProbeMediaInfoTools.ProbeMediaInfoListener() {
            @Override
            public void probeMediaInfoFinished(ProbeMediaInfoTools.MediaInfo info) {
                //使用合成视频时长更新视频的时长
                mPreviewLength = info.duration;
            }
        });

        mCoverImage.setImageBitmap(mBitmap);
        mCoverSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rate = progress / 100.f;
                mSeekTime = (long) (mPreviewLength * rate);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                initSeekThread();
                mStopSeekThumbnail = false;
                mSeekThumbnailHandler.postDelayed(mSeekThumbnailRunable, 100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mStopSeekThumbnail = true;
                if (mSeekThumbnailHandler != null) {
                    mSeekThumbnailHandler.removeCallbacksAndMessages(null);
                }
                mSeekThumbnailHandler.post(mSeekThumbnailRunable);
            }
        });
    }

    private class ButtonObserver implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cover_complete:
                    onPublishClick();
                    break;
                case R.id.cover_back:
                case R.id.preview_back:
                    onBackClick();
                    break;
                case R.id.save_to_album:
                    saveFileToDCIM();
                    break;
                default:
                    break;
            }
        }
    }

    private void onBackClick() {
        if (mUploadState.get() >= UPLOAD_STATE_STARTING && mUploadState.get() <=
                UPLOAD_STATE_STARTED) {
            //取消上传，直接预览播放本地视频
     /*       mKS3Wrap.cancel();
            onUploadFinished(false);*/
            uploader.stop();
            hideProgressDialog();
            return;
        }

        Intent intent = new Intent(PublishActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    private void onPublishClick() {
        startUpload();
    }

    private void onUploadFinished(final boolean success) {
        mUploadState.set(UPLOAD_STATE_NONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCoverSeekLayout.setVisibility(View.GONE);
                if (success) {
                    mProgressText.setVisibility(View.INVISIBLE);
                    mStateTextView.setVisibility(View.VISIBLE);
                    mStateTextView.setText(R.string.upload_file_success);
                    mCoverComplete.setVisibility(View.INVISIBLE);
                    if (mPlayUrlGetTask != null) {
                        mPlayUrlGetTask.cancel(true);
                        mPlayUrlGetTask.release();
                        mPlayUrlGetTask = null;
                    }

                    mPlayUrlGetTask = new HttpRequestTask(new HttpRequestTask.HttpResponseListener() {
                        @Override
                        public void onHttpResponse(int responseCode, String response) {
                            if (responseCode == 200) {
                                if (!TextUtils.isEmpty(response)) {
                                    try {
                                        JSONObject data = new JSONObject(response);

                                        if (data.getInt("errno") == 0) {
                                            String url = data.getString("presigned_url");
                                            if (!url.contains("http")) {
                                                url = "http://" + url;
                                            }
                                            mFilePath = url;
                                            Log.d(TAG, "play url:" + mFilePath);
                                            startPreview();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //播放合成后的视频
                                        startPreview();
                                    }

                                }
                            }
                        }
                    });
                    mStateTextView.setText(R.string.get_file_url);
               //     mPlayUrlGetTask.execute(FILE_URL_SERVER + "?objkey=" + mCurObjectKey);
                } else {
                    if (mStateTextView != null) {
                        mStateTextView.setVisibility(View.VISIBLE);
                        mStateTextView.setText(R.string.upload_file_fail);
                    }
                    startPreview();
                }
            }
        });
    }

    public KSYMediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new KSYMediaPlayer.Builder(getApplicationContext()).build();
        }
        return mMediaPlayer;
    }

    /**
     * 保存视频到相册并发广播进行通知
     */
    private void saveFileToDCIM() {
        String srcPath = mLocalPath;
        String desDir = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/" : Environment.getExternalStorageDirectory().getAbsolutePath() + "/KSYShortVideo";
        String name = srcPath.substring(srcPath.lastIndexOf('/'));
        String desPath = desDir + name;
        File desFile = new File(desPath);
        try {
            File srcFile = new File(srcPath);
            if (srcFile.exists() && !desFile.exists()) {
                InputStream is = new FileInputStream(srcPath);
                FileOutputStream fos = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                mSaveToast.setText("文件保存相册成功");
                mSaveToast.setVisibility(View.VISIBLE);
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSaveToast.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 发送系统广播通知有图片更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(desFile);
        intent.setData(uri);
        sendBroadcast(intent);
    }

    private void startPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "start compose file Preview:");
                if (mUploadWindow != null && mUploadWindow.isShowing()) {
                    mUploadWindow.dismiss();
                }

                if (mFileMineType.equals(FileUtils.MIME_TYPE_GIF)) {
                    mPreviewLayout.setVisibility(View.VISIBLE);
                    mGifView.setVisibility(View.VISIBLE);

                    mGifView.loadUrl("file://" + mLocalPath);
//                       演示播放gif的网络资源，可根据需求使用
//                        if (mFilePath.startsWith("http")) {
//                            mGifView.loadUrl(mFilePath);
//                        } else {
//                            mGifView.loadUrl("file://" + mFilePath);
//                        }
                } else {
                    mPreviewLayout.setVisibility(View.VISIBLE);
                    mVideoSurfaceView.setVisibility(View.VISIBLE);
                    startPlay(mFilePath);
                }
            }
        });
    }

    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "mediaplayer surfaceChanged");

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "mediaplayer surfaceCreated");
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.setScreenOnWhilePlaying(true);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "mediaplayer surfaceDestroyed");
            // 此处非常重要，必须调用!!!
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            Log.d(TAG, "mediaplayer onCompletion");
        }
    };

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (mMediaPlayer != null) {
                Log.d(TAG, "mediaplayer onPrepared");
                // 设置视频伸缩模式，此模式为填充模式
                mMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                // 开始播放视频
                mMediaPlayer.start();
            }
        }
    };

    private IMediaPlayer.OnErrorListener mOnMediaErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "mediaplayer error:" + i);
            return false;
        }
    };

    private IMediaPlayer.OnInfoListener mOnMediaInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            switch (what) {
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mUploadWindow != null && mUploadWindow.isShowing()) {
                                mUploadWindow.dismiss();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    };


    private void startPlay(String path) {
        mMediaPlayer.setLooping(true);
        mMediaPlayer.shouldAutoPlay(false);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnInfoListener(mOnMediaInfoListener);
        mMediaPlayer.setOnErrorListener(mOnMediaErrorListener);
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //请求过期 更新视频地址和凭证
    private  void getNew_ShotVideoAdress() {
        showProgressDialog("正在请求");
        Api.getNew_ShotVideoAdress(video_id,new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(apiResponse.getData());
                    upload_auth = jsonObject.getString("upload_auth");
                    Log.i(" 更新视频地址",jsonObject.toString());
                    uploader.resumeWithAuth(PublishActivity.this.upload_auth);
                } else {
                    ToastUtils.showShort(PublishActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    //视频地址和凭证
    private  void getShotVideoAdress(String title,String filenamenew,String url,String describe) {
        showProgressDialog("正在请求");
        if (TextUtils.isEmpty(city)){
            city="曲阳";
            return;
        }
        Api.getShotVideoAdress(title,filenamenew,url,describe,""+latitude,""+longitude,city,sp.getString("uid"),new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(apiResponse.getData());
                    upload_auth = jsonObject.getString("upload_auth");
                    upload_address = jsonObject.getString("upload_address");
                    video_id = jsonObject.getString("video_id");
                    uploader.start();
                } else {
                    hideProgressDialog();
                    ToastUtils.showShort(PublishActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    //上传成功之后 调取
    private  void video_success_notyfy(String lat,String lng,String city, String Title,String Description,File file) {
        showProgressDialog("正在上传");
        Log.i("上传成功之后参数",video_id+"   "+lat+"    "+lng+"    "+city+"    "+sp.getString("uid"));
        Api.video_success_notyfy(video_id,lat,lng,city,sp.getString("uid"), Title, Description,file,new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    PublishActivity.this.finish();
                    Intent intent = new Intent(PublishActivity.this, ConfigActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(PublishActivity.this, apiResponse.getMessage());
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                ToastUtils.showShort(PublishActivity.this, errMessage.toString());
                hideProgressDialog();
            }
        });
    }

    private void releasePlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static final int REQUEST_CODE = 200;

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                latitude = ""+location.getLatitude();    //获取纬度信息
                longitude = ""+location.getLongitude();    //获取经度信息
                city = location.getCity();
                locationClient.stop();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        //option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要


        locationClient.setLocOption(option);
    }

    @PermissionGrant(REQUEST_CODE)
    public void requestPermissionSuccess() {
        locationClient.start();
        //注册监听函数
    }

    @PermissionDenied(REQUEST_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您开启！", Toast.LENGTH_SHORT).show();
    }

}
