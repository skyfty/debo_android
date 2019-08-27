package com.qcwl.debo.ui.found.joke;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.vodplayer.media.AliyunPlayAuth;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayer.utils.VcPlayerLog;
import com.bumptech.glide.Glide;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NoSkinActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NoSkinActivity";

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");

    private SurfaceView surfaceView;

    private List<String> logStrs = new ArrayList<>();

    private AliyunVodPlayer aliyunVodPlayer;
    //   private AliVcMediaPlayer mPlayer;
    private AliyunPlayAuth mPlayAuth = null;
    private ImageView iv_playOrpouse;
    private ImageView go_badk, iv_head;
    private LinearLayout rl_dianzan;
    private LinearLayout rl_pinglun;
    private LinearLayout rl_guanzhu;
    private String mVid = "";
    private String follow_uid = "";
    private TextView tv_describe;
    private String describe = "";
    private TextView tv_dianzanNum, tv_commentNum;
    private ImageView iv_dianzan;
    private ImageView iv_guanzhu;
    private TextView tv_palynum;
    private String upvote_num;
    private CustomProgressDialog mProgressDialog;
    private String fengmian = "";
    private ImageView iv_fengmian;
    private VideoInfoBean videoInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        //   AndroidXuNiJian.assistActivity(findViewById(android.R.id.content));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      /*  View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/

        if (!getIntent().getStringExtra("vid").isEmpty()) {
            mVid = getIntent().getStringExtra("vid").toString();
        }
        if (!getIntent().getStringExtra("follow_uid").isEmpty()) {
            follow_uid = getIntent().getStringExtra("follow_uid").toString();
        }
        if (!getIntent().getStringExtra("describe").isEmpty()) {
            describe = getIntent().getStringExtra("describe").toString();
        }
        if (!getIntent().getStringExtra("fengmian").isEmpty()) {
            fengmian = getIntent().getStringExtra("fengmian").toString();
        }
        initView();
        initVodPlayer();
        getVideo_Info(mVid, follow_uid);


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                aliyunVodPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");
                aliyunVodPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
            }
        });
        setPlaySource();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        iv_playOrpouse = (ImageView) findViewById(R.id.iv_playOrpouse);
        rl_dianzan = (LinearLayout) findViewById(R.id.rl_dianzan);
        rl_pinglun = (LinearLayout) findViewById(R.id.rl_pinglun);
        rl_guanzhu = (LinearLayout) findViewById(R.id.rl_guanzhu);
        tv_describe = (TextView) findViewById(R.id.tv_describe);
        go_badk = (ImageView) findViewById(R.id.go_badk);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_dianzanNum = (TextView) findViewById(R.id.tv_dianzanNum);
        tv_commentNum = (TextView) findViewById(R.id.tv_commentNum);
        iv_dianzan = (ImageView) findViewById(R.id.iv_dianzan);
        iv_guanzhu = (ImageView) findViewById(R.id.iv_guanzhu);
        tv_palynum = (TextView) findViewById(R.id.tv_palynum);
        iv_fengmian = (ImageView) findViewById(R.id.iv_fengmian);

        go_badk.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        rl_dianzan.setOnClickListener(this);
        rl_pinglun.setOnClickListener(this);
        rl_guanzhu.setOnClickListener(this);
        iv_playOrpouse.setOnClickListener(this);

    }

    private void initVodPlayer() {
        //  mPlayer = new AliVcMediaPlayer(this, surfaceView);

        aliyunVodPlayer = new AliyunVodPlayer(this);
        aliyunVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        aliyunVodPlayer.setPlaySpeed(1.0f);

        aliyunVodPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                logStrs.add(format.format(new Date()) + "准备成功");
                //准备成功之后可以调用start方法开始播放
                tv_describe.setText(describe);
                aliyunVodPlayer.start();
                //  iv_playOrpouse.setVisibility(View.INVISIBLE);
                iv_playOrpouse.setAlpha(0);
            }
        });
        aliyunVodPlayer.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                Map<String, String> debugInfo = aliyunVodPlayer.getAllDebugInfo();
                long createPts = 0;
                if (debugInfo.get("create_player") != null) {
                    String time = debugInfo.get("create_player");
                    createPts = (long) Double.parseDouble(time);

                    logStrs.add(format.format(new Date(createPts)) + "播放创建成功");
                }
                if (debugInfo.get("open-url") != null) {
                    String time = debugInfo.get("open-url");

                    long openPts = (long) Double.parseDouble(time) + createPts;
                    logStrs.add(format.format(new Date(openPts)) + "url请求成功");
                }
                if (debugInfo.get("find-stream") != null) {
                    String time = debugInfo.get("find-stream");

                    VcPlayerLog.d(TAG + "lfj0914", "find-Stream time =" + time + " , createpts = " + createPts);
                    long findPts = (long) Double.parseDouble(time) + createPts;
                    logStrs.add(format.format(new Date(findPts)) + "请求流成功");
                }
                if (debugInfo.get("open-stream") != null) {

                    iv_fengmian.setVisibility(View.GONE);

                    String time = debugInfo.get("open-stream");
                    VcPlayerLog.d(TAG + "lfj0914", "open-Stream time =" + time + " , createpts = " + createPts);
                    long openPts = (long) Double.parseDouble(time) + createPts;
                    logStrs.add(format.format(new Date(openPts)) + "开始传输码流");
                }
                logStrs.add(format.format(new Date()) + "第一帧播放完成");
            }
        });
        aliyunVodPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int arg0, int arg1, String msg) {

                if (aliyunVodPlayer != null) {
                    aliyunVodPlayer.stop();
                }
                Toast.makeText(NoSkinActivity.this.getApplicationContext(), "失败！！！！原因：" + msg, Toast.LENGTH_SHORT).show();
            }
        });
        aliyunVodPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                Log.d(TAG, "onCompletion--- ");
                setPlaySource();
                aliyunVodPlayer.start();
                aliyunVodPlayer.replay();
            }
        });

        aliyunVodPlayer.setOnStoppedListner(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                Toast.makeText(NoSkinActivity.this.getApplicationContext(), "播放停止", Toast.LENGTH_SHORT).show();
            }
        });
        aliyunVodPlayer.enableNativeLog();

      /*  mPlayer.setPreparedListener(new MediaPlayer.MediaPlayerPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成时触发
                mPlayer.play();
                tv_describe.setText(describe);
                //  iv_playOrpouse.setVisibility(View.INVISIBLE);
                iv_playOrpouse.setAlpha(0);
            }
        });
        mPlayer.setPcmDataListener(new MediaPlayer.MediaPlayerPcmDataListener() {
            @Override
            public void onPcmData(byte[] bytes, int i) {
                //音频数据回调接口，在需要处理音频时使用，如拿到视频音频，然后绘制音柱。
            }
        });
      *//*  mPlayer.setFrameInfoListener(new MediaPlayer.MediaFrameInfoListener() {
            @Override
            public void onFrameInfoListener() {
                //首帧显示时触发
            }
        });*//*
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                //错误发生时触发，错误码见接口文档
                Log.i("播放错误","msg.."+msg);
            }
        });
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {
                //视频正常播放完成时触发
                mPlayer.prepareAndPlay(videoInfoBean.getPlayURL());
                Log.i("播放完成","msg..");
            }
        });
        mPlayer.setSeekCompleteListener(new MediaPlayer.MediaPlayerSeekCompleteListener() {
            @Override
            public void onSeekCompleted() {
                //视频seek完成时触发
                Log.i("播放","视频seek完成时触发..");
            }
        });
        mPlayer.setStopedListener(new MediaPlayer.MediaPlayerStopedListener() {
            @Override
            public void onStopped() {
                //使用stop接口时触发
            }
        });
        mPlayer.setBufferingUpdateListener(new MediaPlayer.MediaPlayerBufferingUpdateListener() {
            @Override
            public void onBufferingUpdateListener(int percent) {
                //视频缓冲时的百分比
                Log.i("播放","视频缓冲时的百分比.."+"percent"+percent);
            }
        });
        mPlayer.setCircleStartListener(new MediaPlayer.MediaPlayerCircleStartListener(){
            @Override
            public void onCircleStart() {
                //循环播放开始
            }
        });*/
    }


    private void setPlaySource() {
        iv_fengmian.setVisibility(View.VISIBLE);
        ImgUtil.load(NoSkinActivity.this, fengmian, iv_fengmian);
        ImgUtil.setGlideHead(NoSkinActivity.this, fengmian, iv_fengmian);
        /*Glide.with(NoSkinActivity.this)
                .load(fengmian)
                .into(iv_fengmian);*/
        iv_playOrpouse.setAlpha(0);

//        getShotVideoPlayer(mVid,follow_uid);
        //auth方式
        //NOTE： 注意过期时间。特别是重播的时候，可能已经过期。所以重播的时候最好重新请求一次服务器。

    }

    @Override
    protected void onResume() {
        super.onResume();
        //保存播放器的状态，供resume恢复使用。
        resumePlayerState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //onStop中记录下来的状态，在这里恢复使用
        savePlayerState();
    }


    //用来记录前后台切换时的状态，以供恢复。
    private IAliyunVodPlayer.PlayerState mPlayerState;

    private void resumePlayerState() {
        if (mPlayerState == IAliyunVodPlayer.PlayerState.Paused) {
            aliyunVodPlayer.pause();
            iv_playOrpouse.setAlpha(255);
        } else if (mPlayerState == IAliyunVodPlayer.PlayerState.Started) {
            aliyunVodPlayer.start();
            //  iv_playOrpouse.setVisibility(View.INVISIBLE);
            iv_playOrpouse.setAlpha(0);
        }
    }

    private void savePlayerState() {
        mPlayerState = aliyunVodPlayer.getPlayerState();
        if (aliyunVodPlayer.isPlaying()) {
            //然后再暂停播放器
            aliyunVodPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        aliyunVodPlayer.stop();
        aliyunVodPlayer.release();
        //  mPlayer.stop();
        //  mPlayer.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_playOrpouse:
                Log.i("点击事件", "暂停");

                mPlayerState = aliyunVodPlayer.getPlayerState();
                if (mPlayerState == IAliyunVodPlayer.PlayerState.Started) {
                    aliyunVodPlayer.pause();
                    iv_playOrpouse.setAlpha(255);
                } else if (mPlayerState == IAliyunVodPlayer.PlayerState.Paused) {
                    aliyunVodPlayer.resume();
                    //   iv_playOrpouse.setVisibility(View.INVISIBLE);
                    iv_playOrpouse.setAlpha(0);
                }
              /*  if ( mPlayer.isPlaying()==true){
                    mPlayer.pause();
                    iv_playOrpouse.setAlpha(255);
                } else if (mPlayer.isPlaying()==false) {
                    mPlayer.resume();
                    iv_playOrpouse.setAlpha(0);
                }*/
                break;
            case R.id.rl_dianzan:
//                UpVote(vid);
                break;
            case R.id.rl_pinglun:
                Log.i("点击事件", "评论");
                Intent intent = new Intent();
                intent.setClass(this, CommentActivity.class);
//                intent.putExtra("video_id",vid);
                startActivity(intent);
                break;
            case R.id.rl_guanzhu:
//                videoFollow(vid);
                break;
            case R.id.go_badk:
                finish();
                break;
            case R.id.iv_head:
                //进入对方quan'b
                break;
        }
    }

    //播放凭证
    private void getShotVideoPlayer(final String video_id, String follow_uid) {
        Api.getShotVideoPlayer(video_id, follow_uid, SPUtil.getInstance(this).getString("uid"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    JSONObject jsonObject = JSON.parseObject(apiResponse.getData());
                    String authInfo = jsonObject.getString("PlayAuth");
                    String is_follow = jsonObject.getString("is_follow");
                    String comment_num = jsonObject.getString("comment_num");
                    upvote_num = jsonObject.getString("upvote_num");
                    String avatar = jsonObject.getString("avatar");
                    String play_num = jsonObject.getString("play_num");
                    String is_my_upvote = jsonObject.getString("is_my_upvote");
                    AliyunPlayAuth.AliyunPlayAuthBuilder aliyunPlayAuthBuilder = new AliyunPlayAuth.AliyunPlayAuthBuilder();
                    aliyunPlayAuthBuilder.setVid(video_id);
                    aliyunPlayAuthBuilder.setPlayAuth(authInfo);
                    aliyunPlayAuthBuilder.setQuality(IAliyunVodPlayer.QualityValue.QUALITY_ORIGINAL);
                    mPlayAuth = aliyunPlayAuthBuilder.build();

                    if (mPlayAuth != null) {
                        aliyunVodPlayer.prepareAsync(mPlayAuth);
                    }

                    if ("1".equals(is_my_upvote)) {
                        iv_dianzan.setImageResource(R.mipmap.yidianzan);
                    } else {
                        iv_dianzan.setImageResource(R.mipmap.dianzan);
                    }
                    if ("0".equals(is_follow)) {
                        iv_guanzhu.setImageResource(R.mipmap.guanzhu1);
                    } else {
                        iv_guanzhu.setImageResource(R.drawable.yiguanzhu2);
                    }

                    if (!TextUtils.isEmpty(play_num)) {
                        tv_palynum.setText("播放次数：" + play_num);
                    }
                    //   ImgUtil.load(NoSkinActivity.this,avatar,iv_head);
                    ImgUtil.setGlideHead(NoSkinActivity.this, avatar, iv_head);
                   /* Glide.with(NoSkinActivity.this)
                            .load(avatar).bitmapTransform(new CropCircleTransformation(NoSkinActivity.this))
                            .placeholder(R.mipmap.head)
                            .error(R.mipmap.head)
                            .into(iv_head);*/
                    tv_dianzanNum.setText(upvote_num);//点赞数
                    tv_commentNum.setText(comment_num);//评论数
                } else {
                    ToastUtils.showShort(NoSkinActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void UpVote(String video_id) {
        Api.getUpvote(SPUtil.getInstance(this).getString("uid"), video_id, new ApiResponseHandler(NoSkinActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if ("取消点赞成功".equals(apiResponse.getMessage().toString().trim())) {
                    iv_dianzan.setImageResource(R.mipmap.dianzan);
                    upvote_num = "" + (Integer.parseInt(upvote_num) - 1);
                    tv_dianzanNum.setText(upvote_num);
                } else {
                    upvote_num = "" + (Integer.parseInt(upvote_num) + 1);
                    iv_dianzan.setImageResource(R.mipmap.yidianzan);
                    tv_dianzanNum.setText(upvote_num);
                }
            }
        });
    }

    private void videoFollow(String video_id) {
        Api.videoFollow(SPUtil.getInstance(this).getString("uid"), video_id, follow_uid, new ApiResponseHandler(NoSkinActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if ("关注成功".equals(apiResponse.getMessage().toString().trim())) {
                    iv_guanzhu.setImageResource(R.drawable.yiguanzhu2);
                } else {
                    iv_guanzhu.setImageResource(R.mipmap.guanzhu1);
                }
            }
        });
    }

    public void showProgressDialog(String msg) {
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    //这个接口不从阿里云获取 直接从自己后台获取播放地址
    //获取视频列表  发现
    private void getVideo_Info(String video_id, String follow_uid) {
        showProgressDialog("正在获取");
        Api.getVideo_Info(video_id, new ApiResponseHandler(NoSkinActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("获取视频信息", "发现apiResponse。。。" + apiResponse);
                if (apiResponse.getCode() == 0) {
                    videoInfoBean = JSON.parseObject(apiResponse.getData(), VideoInfoBean.class);
                } else {
                    ToastUtils.showShort(NoSkinActivity.this, apiResponse.getMessage());
                }
                hideProgressDialog();
            }
        });
    }
}
