package com.qcwl.debo.ui.found.joke.video_new;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gyf.barlibrary.ImmersionBar;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.Joke_like_list;
import com.qcwl.debo.model.Joke_list;
import com.qcwl.debo.model.VideoDetailsBean;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.joke.CommentBean;
import com.qcwl.debo.ui.found.joke.ParagraphInfoBean;
import com.qcwl.debo.ui.found.joke.PersonalActivity;
import com.qcwl.debo.ui.found.joke.VideoBean;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ShareUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.widget.VideoScrollView;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.UMShareAPI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class PlayVideoActivity extends BaseActivity implements View.OnClickListener, VideoScrollView.OnScrollChangeListener/*, TouchCallBack */ {

    private List<Map<String, String>> list1;
    private List<Joke_list> joke_lists;
    private List<Joke_like_list> joke_like_lists;
    private List<VideoBean> mlist1;
    private JZVideoPlayerStandardLoopVideo videoPlayer;
    //private VideoView video;// 视频播放
    private CircleImageView iconimg;// 视频发布者头像
    private TextView tvuname;// 用户昵称
    private TextView tvspeak;// 用户发布视频时发表的言论
    private TextView tvlovenum;// 收藏该视频的人数
    private TextView tvCommentnum;// 评论该视频的人数
    private TextView shareCount, loveCount, guanzhuCount;// 评论该视频的人数

    private EditText etcontent;// 评论的内容
    private ListView commentlv;// 评论列表
    //private ProgressBar progressBar;// 显示当前缓存进度
    private TextView tvtime;// 视频总时长
    private ImageView imglove;// 收藏
    //    private ImageView frame;
    private FrameLayout frame_layout;
    private ParagraphInfoBean videoInfoBean;
    //	private UserBean ub;// 当前登录用户
    //    private MediaController mController;
    private String follow_uid = "";
    private String fengmian = "";
    private ImageView act_playvideo_back;
    private TextView palyer_num;
    private ImageView mGuanzhu;
    private List<CommentBean> list;
    private CommentlvAdapter adapter;
    private Button btn_reply;
    private EditText editText;
    private String reply_content;
    private String avatar = "";
    private String nickname = "";
    private String upvote_num = "";
    private String pingLunNum = "";
    private String video_url = "";
    private String is_upvote, is_follow;
    private int Pmwidth, Pmheight;
    private VideoScrollView scroll_view;
    private LinearLayout all;
    private ImageView mShare;
    private int page = 1;
    private String mComment_num;
    private String TAG = "PlatyVideoActivity";
    private TextView messageCount;
    private LinearLayout linear;
    private LinearLayout linear_guanzhu;
    private TextView tv_guanzhu;
    private final int COL_3F51B5 = Color.parseColor("#71cca2");
    private final int RED = Color.red(COL_3F51B5);
    private final int GREEN = Color.green(COL_3F51B5);
    private final int BLUE = Color.blue(COL_3F51B5);
    private View view;
    private String video_id, uid, uname, image_photo;
    private VideoDetailsBean bean;
    private String type;//type = 1头像不可以点击，0头像可以点击
    private String upvote;
    private SPUtil spUtil;
    private int share_count;
    private int anInt;
    //private ViewPager viewPager;
    // private ViewPager vp_guide;
    private Serializable mlist;
    private int position;
    private int stop = 0;
    private int mViewPagerIndex;
    private ViewPagerAdapter mAdapter;
    private ListVideoAdapter videoAdapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private RecyclerView rv_page2;
    private ListVideoAdapter1 videoAdapter1;
    private ListVideoAdapter2 videoAdapter2;
    private ListVideoAdapter3 videoAdapter3;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.act_playvideo);
        UMConfigure.setLogEnabled(true);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type != null) {
            position = intent.getIntExtra("position", 0);
        }
        mlist = intent.getSerializableExtra("data");
        Log.i(TAG, ".........DATA=" + mlist);
        spUtil = SPUtil.getInstance(this);
        video_id = intent.getStringExtra("id");
        if (video_id == null || video_id.equals("")) {
            video_id = intent.getStringExtra("vid");
        }
        video_url = intent.getStringExtra("video_url");
        uid = intent.getStringExtra("uid");
        if (uid == null || uid.equals("")) {
            uid = intent.getStringExtra("follow_uid");
        }
        nickname = intent.getStringExtra("nickname");
        avatar = intent.getStringExtra("image_photo");
        if (avatar == null || avatar.equals("")) {
            avatar = intent.getStringExtra("avatar");
        }
        if (type == null) {
            fengmian = intent.getStringExtra("fengmian");
        }
        else {
            fengmian = intent.getStringExtra("img_url");
        }
        Log.e(TAG, "视频id:" + video_id + ",图片地址:" + fengmian + ",视频地址:" + video_url + ",昵称:" + nickname + ",点赞数:" + ",avatar:" + avatar + ",uid:" + uid + ",position:" + position);

        WindowManager wm = this.getWindowManager();
        Pmwidth = wm.getDefaultDisplay().getWidth();
        Pmheight = wm.getDefaultDisplay().getHeight();
        //CallBackUtils.setCallBack(this);
        initViews();
        //initOper(ApiHttpClient.VIDEO_URL + video_url);
        //initDatas();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, ".....onResume");
        try {
            JZVideoPlayerStandard.goOnPlayOnResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatService.onResume(this);
        initDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, ".....onStart");
        StatService.onPageStart(this, "开始" + "段子播放页面");
    }

    private int playPostion = 0;

    @Override
    public void onPause() {
        super.onPause();
        //        playPostion = video.getCurrentPosition();
        //        videoPlayer.onStatePause();
        //        videoPlayer.changeUiToPauseShow();

        //Caused by: java.lang.IllegalStateException
        //JZVideoPlayerStandard.goOnPlayOnPause();
        try {
            JZVideoPlayer.goOnPlayOnPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 页面埋点
        StatService.onPause(this);
    }


    public void initViews() {
        //videoPlayer = (JZVideoPlayerStandardLoopVideo) findViewById(R.id.videoPlayer);
        //        video = (VideoView) findViewById(R.id.act_playvideo_video);
        rv_page2 = (RecyclerView) findViewById(R.id.rv_page2);
        iconimg = (CircleImageView) findViewById(R.id.act_playvideo_usericon);
        tvuname = (TextView) findViewById(R.id.act_playvideo_username);
        tvspeak = (TextView) findViewById(R.id.act_playvideo_speak);
        tvCommentnum = (TextView) findViewById(R.id.act_playvideo_commentnum);
        tvlovenum = (TextView) findViewById(R.id.act_playvideo_lovenum);
        tv_guanzhu = (TextView) findViewById(R.id.tv_guanzhu);
        etcontent = (EditText) findViewById(R.id.edit_text);
        commentlv = (ListView) findViewById(R.id.act_playvideo_lv);
        act_playvideo_back = (ImageView) findViewById(R.id.act_playvideo_back);
        palyer_num = (TextView) findViewById(R.id.act_playvideo_num);
        linear = (LinearLayout) findViewById(R.id.linear);
        linear_guanzhu = (LinearLayout) findViewById(R.id.linear_guanzhu);
        view = (View) findViewById(R.id.view_bg);
        loveCount = (TextView) findViewById(R.id.tv_love_count);
        shareCount = (TextView) findViewById(R.id.tv_share_count);
        messageCount = (TextView) findViewById(R.id.tv_message_count);
        //viewPager = (ViewPager) findViewById(R.id.viewPager);
        /*guanzhuCount = (TextView) findViewById(R.id.tv_guanzhu_count);

        mGuanzhu = (ImageView) findViewById(R.id.act_playvideo_message);*/
        mShare = (ImageView) findViewById(R.id.act_playvideo_share);
        scroll_view = (VideoScrollView) findViewById(R.id.scroll_view);
        all = (LinearLayout) findViewById(R.id.all);
        //	all.setVisibility(View.GONE);
        //	progressBar = (ProgressBar) findViewById(R.id.act_playvideo_progressbar);
        tvtime = (TextView) findViewById(R.id.act_playvideo_time);
        imglove = (ImageView) findViewById(R.id.act_playvideo_love);
        //        frame = (ImageView) findViewById(R.id.act_playvideo_frame);
        frame_layout = (FrameLayout) findViewById(R.id.frame_layout);
        btn_reply = (Button) findViewById(R.id.btn_reply);
        editText = (EditText) findViewById(R.id.edit_text);
        act_playvideo_back.setOnClickListener(this);
        if (type != null && type.equals("0")) {
            iconimg.setOnClickListener(this);
            linear_guanzhu.setOnClickListener(this);
        }
        btn_reply.setOnClickListener(this);
        imglove.setOnClickListener(this);
        mShare.setOnClickListener(this);

        scroll_view.setOnScrollChangeListener(this);
        //shareCount.setText(share_count+"");
        //findViewById(R.id.act_playvideo_guanzhu).setOnClickListener(this);

        //String[] s = new String[]{"/data/upload/joke_video/17977/shipin(1586).mp4", "/data/upload/joke_video/269/5bd955fd13bb33.05939508.mp4", "/data/upload/joke_video/17977/shipin(1521).mp4", "/data/upload/joke_video/17977/shipin(1078).mp4", "/data/upload/joke_video/17977/shipin(1299).mp4", "/data/upload/joke_video/105/5bd94559aeeae5.62690590.mp4"};


        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_page2);

        if (type != null) {
            if (type.equals("0")) {
                mlist1 = (List<VideoBean>) mlist;
            }
            else if (type.equals("1")) {
                joke_lists = (List<Joke_list>) mlist;
            }
            else if (type.equals("2")) {
                joke_like_lists = (List<Joke_like_list>) mlist;
            }
        }
        else {
            list1 = (List<Map<String, String>>) mlist;
        }
        Log.i(TAG, "...........type=" + type);
        layoutManager = new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv_page2.setLayoutManager(layoutManager);
        if (type == null) {
            videoAdapter3 = new ListVideoAdapter3(list1);
            rv_page2.setAdapter(videoAdapter3);
        }
        else {
            if (type.equals("0")) {
                videoAdapter = new ListVideoAdapter(mlist1);
                rv_page2.setAdapter(videoAdapter);
            }
            else if (type.equals("1")) {
                videoAdapter1 = new ListVideoAdapter1(joke_lists);
                rv_page2.setAdapter(videoAdapter1);
            }
            else if (type.equals("2")) {
                videoAdapter2 = new ListVideoAdapter2(joke_like_lists);
                rv_page2.setAdapter(videoAdapter2);
            }
        }

        addListener();
        rv_page2.scrollToPosition(position);

        //vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        //vp_guide.setOffscreenPageLimit(1);
        //initDatas();
        //mAdapter = new ViewPagerAdapter(mlist);
        //vp_guide.setAdapter(mAdapter);
        //vp_guide.setCurrentItem(position);
        /*vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"...........onPageScrolled="+position);
                if(mViewPagerIndex ==position){
                    //Log.d(TAG,"正在向左滑动");
                }else{
                    //Log.d(TAG,"正在向右滑动");
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"...........onPageSelected="+position);
                if (type!= null){
                    if (type.equals("0")) {
                        uid = mlist1.get(position).getUid();
                        video_id = mlist1.get(position).getId();
                        nickname = mlist1.get(position).getUser_nickname();
                        avatar = mlist1.get(position).getAvatar();
                    } else if (type.equals("1")) {
                        uid = joke_lists.get(position).getUid();
                        video_id = joke_lists.get(position).getId();
                    } else if (type.equals("2")) {
                        uid = joke_like_lists.get(position).getUid();
                        video_id = joke_like_lists.get(position).getId();
                    }
                }else {
                    uid = list1.get(position).get("uid");
                    video_id = list1.get(position).get("video_id");
                }
                Log.i(TAG, "............SCROLL_STATE_SETTLING=" + video_url + "　　封面=" + fengmian+"    uid="+uid+"    video_id="+video_id+"    position="+position+"    getId()=");
                initDatas();
                mAdapter.playVide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG,"...........onPageScrollStateChanged="+state+"    position="+position);
                switch (state){
                    case ViewPager.SCROLL_STATE_IDLE:
                        //无动作、初始状态
                        Log.i(TAG,"---->onPageScrollStateChanged无动作");
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //点击、滑屏
                        mViewPagerIndex = vp_guide.getCurrentItem();
                        Log.i(TAG,"---->onPageScrollStateChanged点击、滑屏");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        //释放
                        Log.i(TAG,"---->onPageScrollStateChanged释放");
                        JZVideoPlayer.releaseAllVideos();
                        break;
                }
            }
        });*/
    }

    public void initDatas() {
        getVideoDetails();
        initOper(ApiHttpClient.VIDEO_URL + video_url);
    }

    public void initOper(String playUrl) {
        getVideoComment(video_id, 1);
        list = new ArrayList<>();
        adapter = new CommentlvAdapter(PlayVideoActivity.this, list);
        commentlv.setAdapter(adapter);

        Intent intent = new Intent("playvidew_update");
        sendBroadcast(intent);
        /*if (avatar.isEmpty()) {
            iconimg.setImageResource(R.mipmap.logo);
        } else {
            ImgUtil.setGlideHead(PlayVideoActivity.this, avatar + "", iconimg);
          *//*  Glide.with(PlayVideoActivity.this)
                    .load(avatar + "")
                    .into(iconimg);*//*
        }*/
       /* videoPlayer = new JZVideoPlayerStandardLoopVideo(getApplication(),this);
        videoPlayer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        frame_layout.addView(videoPlayer);
        frame_layout.setLayoutParams(new LinearLayout.LayoutParams(-1, Pmheight));*/

        // 播放视频
        //playVideo(playUrl);
    }

    private void setDate() {
        palyer_num.setText("播放次数：" + bean.getPlay_num());
        tvuname.setText(nickname + "");
        tvlovenum.setText(bean.getUpvote_num() + "人喜欢");
        tvCommentnum.setText(bean.getComment_num() + "人评论");
        shareCount.setText(bean.getShare_video_record_num());
        if (is_upvote.equals("0")) {
            imglove.setImageResource(R.mipmap.heartoff);
        }
        else {
            imglove.setImageResource(R.mipmap.hearton);
        }
        loveCount.setText("" + bean.getUpvote_num());

        if (is_follow.equals("0")) {
            Drawable drawable = getResources().getDrawable(R.mipmap.add);
            // drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_guanzhu.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            tv_guanzhu.setCompoundDrawablePadding(dp2px(5));
            tv_guanzhu.setText("关注");
        }
        else {
            tv_guanzhu.setText("已关注");
        }

        messageCount.setText("" + bean.getComment_num());

        if (avatar.isEmpty()) {
            iconimg.setImageResource(R.mipmap.logo);
        }
        else {
            //ImgUtil.setGlideHead(PlayVideoActivity.this, avatar + "", iconimg);
            if (PlayVideoActivity.this != null && PlayVideoActivity.this.isDestroyed() == false && PlayVideoActivity.this.getApplication() != null) {
                Glide.with(getApplicationContext()).load(avatar + "").into(iconimg);
            }
        }
    }

    /**
     * 判断对话框中是否输入内容
     */
    private boolean isEditEmply() {
        reply_content = editText.getText().toString().trim();
        if (reply_content.equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        editText.setText("");
        return true;
    }

    private void playVideo(String url) {
        Log.i(TAG, "............URL=" + url + "    " + videoPlayer);
        videoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        //        ImgUtil.load(this, fengmian, videoPlayer.thumbImageView);
        //        videoPlayer.startButton.setImageResource(R.mipmap.play_button);
        videoPlayer.startButton.setVisibility(View.GONE);
        videoPlayer.backButton.setVisibility(View.GONE);
        videoPlayer.fullscreenButton.setVisibility(View.GONE);
        videoPlayer.progressBar.setVisibility(View.VISIBLE);
        if (null != this && !this.isFinishing())
            Glide.with(this).load(ApiHttpClient.VIDEO_URL + fengmian).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    videoPlayer.thumbImageView.setImageBitmap(resource);
                    int width = ScreenUtils.getScreenWidth(PlayVideoActivity.this);
                    int height = (int) (1.0f * width * resource.getHeight() / resource.getWidth());
                    frame_layout.setLayoutParams(new LinearLayout.LayoutParams(-1, height));
                }
            });

        //        long time = videoPlayer.getDuration();
        //        String time_str = GetVideoTimeUtils.getvideotime(time);
        //        // 显示时长
        //        tvtime.setText(time_str);
        //        palyer_num.setText("播放次数：" + videoInfoBean.getPlay_num());
        videoPlayer.startVideo();
        //videoPlayer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        //        mController = new MediaController(this);
        //        //加载指定视频
        //        video.setVideoPath(url);
        //        //设置video与mController建立关联
        //        /*video.setMediaController(mController);
        //        //设置mController与video建立关联
        //		mController.setMediaPlayer(video);*/
        //        //让video获得焦点
        //        video.requestFocus();
        //        //设置监听，准备好后显示视频时长
        //        video.setOnPreparedListener(new OnPreparedListener() {
        //
        //            @Override
        //            public void onPrepared(MediaPlayer mp) {
        //
        //                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
        //                    @Override
        //                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
        //                            video.setBackgroundColor(Color.TRANSPARENT);
        //                        return true;
        //                    }
        //                });
        //
        //                frame.setVisibility(View.GONE);
        //                // 获得视频总时长
        //                long time = video.getDuration();
        //                String time_str = GetVideoTimeUtils.getvideotime(time);
        //                // 显示时长
        //                tvtime.setText(time_str);
        //                palyer_num.setText("播放次数：" + videoInfoBean.getPlay_num());
        //                mp.setLooping(true);
        //            }
        //        });
        //
        //        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
        //            @Override
        //            public boolean onError(MediaPlayer mp, int what, int extra) {
        //                video.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
        //                return true;
        //            }
        //        });
        //        //播放视频
        //        video.start();
    }

    private ImmersionBar mImmersionBar;

    @Override
    public void statusBarSetting() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false).keyboardEnable(true).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        Log.i(TAG, ".....onDestroy");
        StatService.onPageEnd(this, "结束" + "段子播放页面");
        try {
            JZVideoPlayer.releaseAllVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //DataCallBackUtils.removeCallBack();
    }

    //评论接口
    private void videoComment(String content) {
        Api.videoComment(SPUtil.getInstance(this).getString("uid"), video_id, content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                ToastUtils.showShort(PlayVideoActivity.this, "评论成功！");
                anInt += 1;
                messageCount.setText("" + anInt);
                tvCommentnum.setText(anInt + "人评论");
                //videoBean.setComment_num("" + anInt);
                getVideoComment(video_id, 1);
            }
        });
    }

    //获取评论列表
    private void getVideoComment(String video_id, int page) {
        Api.videoCommentList(video_id, page, new ApiResponseHandler(PlayVideoActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.e("choise——success", apiResponse.getMessage());
                    List<CommentBean> commentBeen = JSON.parseArray(apiResponse.getData(), CommentBean.class);
                    Log.e("choise——success", commentBeen.get(0).toString());
                    pingLunNum = commentBeen.size() + "";
                    list.clear();
                    list.addAll(commentBeen);
                    //                    tvCommentnum.setText(pingLunNum + "评论");
                }
                else {
                    //ToastUtils.showShort(PlayVideoActivity.this, apiResponse.getMessage().toString());
                    pingLunNum = "0";
                    list.clear();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_playvideo_back:
                finish();
                break;
            case R.id.act_playvideo_usericon://点击头像 查看信息
                startActivity(new Intent(PlayVideoActivity.this, PersonalActivity.class).putExtra("f_udi", uid).putExtra("is_follow", is_follow));
                break;
            case R.id.btn_reply:
                if (isEditEmply()) {        //判断用户是否输入内容
                    videoComment(reply_content);
                    onFocusChange(false);
                }
                break;
            case R.id.act_playvideo_love:   //点赞
                UpVote(video_id);
                break;
           /* case R.id.act_playvideo_guanzhu:  //关注
                videoFollow(video_id);
                break;*/
            case R.id.act_playvideo_share:  //分享

                //判断是否已经赋予权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    ShareUtil.openShare(PlayVideoActivity.this, ApiHttpClient.VIDEO_URL + fengmian, video_id, "笑死人，自从玩了嘚啵段子，我实在是不舍得睡觉。。。", "你想看的，都在里面，又来一波，你懂的！来自小啵的段子短视频~", ApiHttpClient.API_URL + "video/video_play_web" + "?video_id=" + RAS.getPublicKeyStrRAS(video_id.getBytes()));
                }

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
                    ActivityCompat.requestPermissions(this, mPermissionList, 123);
                }
                else {
                    ShareUtil.openShare(PlayVideoActivity.this, ApiHttpClient.VIDEO_URL + fengmian, video_id, "笑死人，自从玩了嘚啵段子，我实在是不舍得睡觉。。。", "你想看的，都在里面，又来一波，你懂的！来自小啵的段子短视频~", ApiHttpClient.API_URL + "video/video_play_web" + "?video_id=" + RAS.getPublicKeyStrRAS(video_id.getBytes()));
                }
                break;
            case R.id.linear_guanzhu:
                videoFollow(video_id);
                break;
        }
    }

    //点赞
    private void UpVote(String video_id) {
        Log.i(TAG, ".........UpVote=" + video_id + "     " + SPUtil.getInstance(this).getString("uid"));
        Api.getUpvote(SPUtil.getInstance(this).getString("uid"), video_id, new ApiResponseHandler(PlayVideoActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {

                if ("取消点赞成功".equals(apiResponse.getMessage().toString().trim())) {
                    ToastUtils.showShort(PlayVideoActivity.this, apiResponse.getMessage());
                    imglove.setImageResource(R.mipmap.heartoff);
                    upvote = "" + (Integer.parseInt(upvote) - 1);
                    //videoBean.setIs_upvote(0 + "");
                }
                else {
                    ToastUtils.showShort(PlayVideoActivity.this, apiResponse.getMessage());
                    upvote = "" + (Integer.parseInt(upvote) + 1);
                    imglove.setImageResource(R.mipmap.hearton);
                    //videoBean.setIs_upvote(1 + "");
                }

                loveCount.setText(upvote + "");
                tvlovenum.setText(upvote + "人喜欢");
                //videoBean.setUpvote("" + upvote);
                //Log.i(TAG, "getUpvote。。=" + upvote + "         " + videoBean.getUpvote());
            }
        });
    }

    //关注
    private void videoFollow(String video_id) {
        Log.i(TAG, ".........videoFollow=" + video_id + "     " + SPUtil.getInstance(this).getString("uid"));
        Api.videoFollow(SPUtil.getInstance(this).getString("uid"), video_id, uid, new ApiResponseHandler(PlayVideoActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.e("getApplicationContext", "apiResponse=======" + apiResponse);
                if ("关注成功".equals(apiResponse.getMessage().toString().trim())) {
                    //mGuanzhu.setImageResource(R.mipmap.icon_invite);
                    tv_guanzhu.setText("已关注");
                    tv_guanzhu.setCompoundDrawables(null, null, null, null);
                    Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT).show();
                    //videoBean.setIs_follow(1 + "");
                    is_follow = 1 + "";
                }
                else {
                    //mGuanzhu.setImageResource(R.mipmap.icon_not_invite);
                    //videoBean.setIs_follow(0 + "");
                    tv_guanzhu.setText("关注");
                    Drawable drawable = getResources().getDrawable(R.mipmap.add);
                    // drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    Log.e("getApplicationContext", "drawable=======" + drawable);
                    tv_guanzhu.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    tv_guanzhu.setCompoundDrawablePadding(dp2px(5));
                    is_follow = 0 + "";
                    Toast.makeText(getApplicationContext(), "取消关注", Toast.LENGTH_SHORT).show();
                }
                Log.e("getApplicationContext", "" + apiResponse.getMessage());
            }
        });
    }

    //获取视频信息
    private void getVideoDetails() {
        Log.i(TAG, ".........getVideoDetails_video_id=" + video_id + "     登录用户uid=" + SPUtil.getInstance(this).getString("uid") + "      uid=" + uid);
        Api.getVideoDetails(SPUtil.getInstance(this).getString("uid"), video_id, new ApiResponseHandler(PlayVideoActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.e(TAG, "   getVideoDetails_onSuccess=" + apiResponse.toString());
                if (apiResponse.getCode() == 0) {
                    bean = JSON.parseObject(apiResponse.getData(), VideoDetailsBean.class);
                    is_follow = bean.getIs_follow() + "";
                    is_upvote = bean.getIs_upvote() + "";
                    upvote = bean.getUpvote_num();

                    if (mComment_num == null) {
                        anInt = Integer.parseInt(bean.getComment_num());
                    }
                    else {
                        anInt = Integer.parseInt(mComment_num);
                    }
                    setDate();
                }
            }

            @Override
            public void onFailure(String errMessage) {
                Log.e("getbean_info2", "   " + errMessage);
                super.onFailure(errMessage);
            }
        });
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }

    @Override
    public void onScrollChanged(VideoScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Log.i(TAG, "..............onScrollChange=" + scrollX + "    " + scrollY + "    " + oldScrollX + "    " + oldScrollY);
        if (scrollY <= 0) {
            linear.setBackgroundColor(Color.argb((int) 0, RED, GREEN, BLUE));//AGB由相关工具获得，或者美工提供
            view.setBackgroundColor(Color.argb((int) 0, RED, GREEN, BLUE));
        }
        else if (scrollY > 0 && scrollY <= dp2px(70)) {
            float scale = (float) scrollY / dp2px(70);
            float alpha = (255 * scale);
            // 只是layout背景透明(仿知乎滑动效果)
            linear.setBackgroundColor(Color.argb((int) alpha, RED, GREEN, BLUE));
            view.setBackgroundColor(Color.argb((int) alpha, RED, GREEN, BLUE));
        }
        else {
            linear.setBackgroundColor(Color.argb((int) 255, RED, GREEN, BLUE));
            view.setBackgroundColor(Color.argb((int) 255, RED, GREEN, BLUE));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, ".............onActivityResult=" + requestCode + "    " + resultCode + "    " + data);
        if (resultCode == -1) {
            share_count += 1;
            shareCount.setText(share_count + "");
            spUtil.setInt(video_id, share_count);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "..........onRequestPermissionsResult=" + requestCode + "    " + grantResults[0]);
        if (requestCode == 123) {
            for (int i = 0; i <= grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    return;
                }
            }
            ShareUtil.openShare(PlayVideoActivity.this, fengmian, video_id, "笑死人，自从玩了嘚啵段子，我实在是不舍得睡觉。。。", "你想看的，都在里面，又来一波，你懂的！来自小啵的段子短视频~", ApiHttpClient.API_URL + "video/video_play_web" + "?video_id=" + RAS.getPublicKeyStrRAS(video_id.getBytes()));
        }
    }

   /* @Override
    public void doSomeThing(int action) {
        Log.i(TAG, "..........doSomeThing=" + action);
        org.json.JSONObject jsonObject = DataCallBackUtils.doCallBackMethod(action);
        if (jsonObject == null) {
            return;
        }
        JZVideoPlayer.releaseAllVideos();
        try {
            initCallbackData(jsonObject);
            playVideo(ApiHttpClient.VIDEO_URL + video_url);
            getVideoComment(video_id, 1);
            getVideoDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCallbackData(org.json.JSONObject jsonObject) throws Exception {
        video_id = jsonObject.getString("id");
        if (video_id == null || video_id.equals("")) {
            video_id = jsonObject.getString("vid");
        }
        video_url = jsonObject.getString("video_url");
        uid = jsonObject.getString("uid");
        if (uid == null || uid.equals("")) {
            uid = jsonObject.getString("follow_uid");
        }
        nickname = jsonObject.getString("nickname");
        avatar = jsonObject.getString("image_photo");
        if (avatar == null || avatar.equals("")) {
            avatar = jsonObject.getString("avatar");
        }

        type = jsonObject.getString("type");
        fengmian = jsonObject.getString("img_url");
    }*/

    /*public class ViewPagerAdapter extends PagerAdapter {
        private JZVideoPlayerStandardLoopVideo videoPlayer;
        public ViewPagerAdapter(Serializable list) {
            Log.i(TAG, "................ViewPagerAdapter_type=" + type);
            if (type != null) {
                if (type.equals("0")) {
                    mlist1 = (List<VideoBean>) list;
                } else if (type.equals("1")) {
                    joke_lists = (List<Joke_list>) list;
                } else if (type.equals("2")) {
                    joke_like_lists = (List<Joke_like_list>) list;
                }
            } else {
                list1 = (List<Map<String, String>>) list;
            }

        }

        @Override
        public int getCount() {
            if (type != null){
                if (type.equals("0")) {
                    return mlist1.size();
                } else if (type.equals("1")) {
                    return joke_lists.size();
                } else if (type.equals("2")) {
                    return joke_like_lists.size();
                }
            }
            return list1.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = View.inflate(PlayVideoActivity.this, R.layout.item_guide_video, null);
            videoPlayer = (JZVideoPlayerStandardLoopVideo) view.findViewById(R.id.videoPlayer);
            if (type != null) {
                if (type.equals("0")) {
                    video_url = mlist1.get(position).getVideo_url();
                    fengmian = mlist1.get(position).getImg_url();
                } else if (type.equals("1")) {
                    video_url = joke_lists.get(position).getVideo_url();
                    fengmian = joke_lists.get(position).getImg_url();
                } else if (type.equals("2")) {
                    video_url = joke_like_lists.get(position).getVideo_url();
                    fengmian = joke_like_lists.get(position).getImg_url();
                }
            }else{
                video_url = list1.get(position).get("video_url");
                fengmian = list1.get(position).get("fengmian");
            }
            try {
                playVideo(ApiHttpClient.VIDEO_URL + video_url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            container.addView(videoPlayer);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        private void playVideo(String url) {
            Log.i(TAG, "............ViewPagerAdapter_playVideo=" + url);
            videoPlayer.setUp(ApiHttpClient.VIDEO_URL + video_url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            videoPlayer.startButton.setVisibility(View.GONE);
            videoPlayer.backButton.setVisibility(View.GONE);
            videoPlayer.fullscreenButton.setVisibility(View.GONE);
            videoPlayer.progressBar.setVisibility(View.VISIBLE);
            Glide.with(PlayVideoActivity.this)
                    .load(ApiHttpClient.VIDEO_URL + fengmian)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            videoPlayer.thumbImageView.setImageBitmap(resource);
                            int width = ScreenUtils.getScreenWidth(PlayVideoActivity.this);
                            int height = (int) (1.0f * width * resource.getHeight() / resource.getWidth());
                            frame_layout.setLayoutParams(new LinearLayout.LayoutParams(-1, height));
                        }
                    });
            if (stop == 0){
                stop=1;
                playVide();
            }
        }
        public void playVide() {
            videoPlayer.startVideo();
        }
    }*/


    class ListVideoAdapter extends BaseRecAdapter<VideoBean, VideoViewHolder> {

        public ListVideoAdapter(List<VideoBean> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, VideoBean bean, int position) {
            Log.i(TAG, "...........onHolder=" + bean + "    " + position);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = Pmheight;
            holder.mp_video.setUp(ApiHttpClient.VIDEO_URL + bean.getVideo_url(), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (PlayVideoActivity.this.position == position) {
                Log.i(TAG, "...........PlayVideoActivity.this.position");
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(ApiHttpClient.VIDEO_URL + bean.getImg_url()).into(holder.mp_video.thumbImageView);
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.fm_video));

        }
    }

    class ListVideoAdapter1 extends BaseRecAdapter<Joke_list, VideoViewHolder> {

        public ListVideoAdapter1(List<Joke_list> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, Joke_list bean, int position) {
            Log.i(TAG, "...........onHolder=" + bean + "    " + position);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = Pmheight;
            holder.mp_video.setUp(ApiHttpClient.VIDEO_URL + bean.getVideo_url(), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (PlayVideoActivity.this.position == position) {
                Log.i(TAG, "...........PlayVideoActivity.this.position");
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(ApiHttpClient.VIDEO_URL + bean.getImg_url()).into(holder.mp_video.thumbImageView);
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.fm_video));

        }
    }

    class ListVideoAdapter2 extends BaseRecAdapter<Joke_like_list, VideoViewHolder> {

        public ListVideoAdapter2(List<Joke_like_list> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, Joke_like_list bean, int position) {
            Log.i(TAG, "...........onHolder=" + bean + "    " + position);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = Pmheight;
            holder.mp_video.setUp(ApiHttpClient.VIDEO_URL + bean.getVideo_url(), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (PlayVideoActivity.this.position == position) {
                Log.i(TAG, "...........PlayVideoActivity.this.position");
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(ApiHttpClient.VIDEO_URL + bean.getImg_url()).into(holder.mp_video.thumbImageView);
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.fm_video));

        }
    }

    class ListVideoAdapter3 extends BaseRecAdapter<Map<String, String>, VideoViewHolder> {

        public ListVideoAdapter3(List<Map<String, String>> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, Map<String, String> map, int position) {
            Log.i(TAG, "...........onHolder=" + bean + "    " + position);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = Pmheight;
            holder.mp_video.setUp(ApiHttpClient.VIDEO_URL + map.get("video_url"), JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (PlayVideoActivity.this.position == position) {
                Log.i(TAG, "...........PlayVideoActivity.this.position");
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(ApiHttpClient.VIDEO_URL + map.get("fengmian")).into(holder.mp_video.thumbImageView);
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.fm_video));
        }
    }

    public class VideoViewHolder extends BaseRecViewHolder {
        public View rootView;
        public JZVideoPlayerStandardLoopVideo mp_video;

        public VideoViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.mp_video = rootView.findViewById(R.id.videoPlayer);
        }

    }

    private void addListener() {
        rv_page2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = snapHelper.findSnapView(layoutManager);
                        JZVideoPlayer.releaseAllVideos();
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        if (viewHolder != null && viewHolder instanceof VideoViewHolder) {
                            ((VideoViewHolder) viewHolder).mp_video.startVideo();
                        }
                        position = viewHolder.getAdapterPosition();
                        if (type != null) {
                            if (type.equals("0")) {
                                uid = mlist1.get(position).getUid();
                                video_id = mlist1.get(position).getId();
                                nickname = mlist1.get(position).getUser_nickname();
                                avatar = mlist1.get(position).getAvatar();
                                fengmian = mlist1.get(position).getImg_url();
                            }
                            else if (type.equals("1")) {
                                uid = joke_lists.get(position).getUid();
                                video_id = joke_lists.get(position).getId();
                                fengmian = joke_lists.get(position).getImg_url();
                            }
                            else if (type.equals("2")) {
                                uid = joke_like_lists.get(position).getUid();
                                video_id = joke_like_lists.get(position).getId();
                                fengmian = joke_like_lists.get(position).getImg_url();
                            }
                        }
                        else {
                            uid = list1.get(position).get("uid");
                            video_id = list1.get(position).get("video_id");
                            fengmian = list1.get(position).get("fengmian");
                        }
                        Log.i(TAG, "............SCROLL_STATE_SETTLING=" + video_url + "　　封面=" + fengmian + "    uid=" + uid + "    video_id=" + video_id + "    position=" + position + "    getId()=");
                        initDatas();
                        Log.i(TAG, ".............onScrollStateChanged=" + position + "     ");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }

            }
        });
    }
}
