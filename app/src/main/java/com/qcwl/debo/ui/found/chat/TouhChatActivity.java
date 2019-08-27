package com.qcwl.debo.ui.found.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.hyphenate.chat.EMClient;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.chat.VideoCallActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.found.chat.bean.Rand_User_Data;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.utils.CallBack;
import com.qcwl.debo.utils.CallBackUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.RadarView;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class TouhChatActivity extends BaseActivity implements CallBack{

    private String TAG = "TouhChatActivity";

    /* @Bind(R.id.status_bar)
     View mStatusBar;
     @Bind(R.id.image_back)
     ImageView mImageBack;
     @Bind(R.id.text_title)
     TextView mTextTitle;
     @Bind(R.id.text_nearby)
     TextView mTextNearby;
     @Bind(R.id.layout_top)
     LinearLayout mLayoutTop;
     @Bind(R.id.text_chat)
     Button mTextChat;
     @Bind(R.id.video_chat)
     Button mVideoChat;
     @Bind(R.id.img_man)
     CheckBox mImgMan;
     @Bind(R.id.img_woman)
     CheckBox mImgWoman;*/
    private int sex;
    private final int PESSION_CODE = 1;
    private RadarView radarView;
    private Path path;
    private ImageView image;
    private Path path1;
    private ObjectAnimator mAnimator1;
    private ObjectAnimator mAnimator;
    private TextView tv_time;
    private int time = 0;
    private Rand_User_Data mBean;
    @SuppressLint("HandlerLeak")
    final Handler startTimehandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.i(TAG, "..........time=" + time);
            if (time == 4) {
                Log.i(TAG, "..........uid=" + SPUtil.getInstance(TouhChatActivity.this).getString("uid"));
                Api.getTouchChat2(SPUtil.getInstance(TouhChatActivity.this).getString("uid"), new ApiResponseHandler(TouhChatActivity.this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        Log.i(TAG, "..........=" + apiResponse);
                        if (apiResponse.getCode() == 0) {
                            mBean = JSON.parseObject(apiResponse.getData(), Rand_User_Data.class);
                            stopAnimation();
                          /*  mBean = JSON.parseObject(apiResponse.getData(), TouchChatBean.class);
                            Log.e("this_sex1",""+sex+"/"+ mBean);
                            ImgUtil.setGlideHead(TouhChatActivity.this,mBean.getAvatar(),mImgAvatar);
                            mUser_nickname = mBean.getUser_nickname();
                            mNameAge.setText(mUser_nickname);*/
                        }
                    }

                });
            }
            if (null != tv_time) {
                time += 1;
                tv_time.setText(time + "s");
            }
        }
    };
    private CircleImageView imageView;
    private int width;
    private Path path2;
    private Path path3;
    private ObjectAnimator mAnimator2;
    private ObjectAnimator mAnimator3;
    private ImageView image_left, image_right;
    private FrameLayout frameLayout_right;
    private FrameLayout frameLayout_left;
    private TextView tv_video;
    private TextView tv_text;
    private View mStatusBar;
    private LinearLayout mLayoutTop;
    private Timer timer;
    private int mFlag;
    private ImageView image_left1,image_right1;
    private TextView tv_name,tv_gender,tv_name1,tv_gender1;
    private TimerTask timerTask;
    private ImageView image_back;
    private ImageView image_back1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_chat1);
        //ButterKnife.bind(this);
        width = getWindowManager().getDefaultDisplay().getWidth();
        CallBackUtils.setCallBack(this);
        initView();
        initAnimator();
        initTimeTask();
        int statusHeight = ScreenUtils.getStatusHeight(this);
      /*  mStatusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        mLayoutTop.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        mLayoutTop.getBackground().setAlpha(255);*/
    }

    private void initTimeTask() {
        time = 0;
        timer = new Timer("开机计时器");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = 1;
                startTimehandler.sendMessage(msg);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAnimator.pause();
        mAnimator1.pause();
        mAnimator = null;
        mAnimator1 = null;
        timer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 10://继续碰聊
                Log.i(TAG,".......startActivityForResult ===== 10");
                setContentView(R.layout.activity_touch_chat1);
               /* //开始水波纹动画
                radarView.startRippleAnimation();
                //开始曲线动画
                mAnimator.start();*/
               initView();
               initAnimator();
                initTimeTask();
                break;
            case 9://返回首页
                Log.i(TAG,".......startActivityForResult ===== 9");
                TouhChatActivity.this.finish();
                break;
        }
        Log.i(TAG,".......startActivityForResult="+requestCode);
    }
    /*@OnClick({R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }*/

    /*private void choicesex(int sex ,int type) {
        Intent intent = new Intent(this, ChatDetailsActivity.class);
        if(mImgMan.isChecked() || mImgWoman.isChecked()){
            if (sex == 1) {
                intent.putExtra("sex" , 1);
            } else if (sex == 2) {
                intent.putExtra("sex" , 2);
            }
        } else {
//            ToastUtils.showShort(this,"请选择性别");
//            startActivity(new Intent(this, ChatDetailsActivity.class).putExtra("sex" , 0));
            if(SPUtil.getInstance(this).getString("sex").equals("1")){
                intent.putExtra("sex" , 2);
            }else if(SPUtil.getInstance(this).getString("sex").equals("2")){
                intent.putExtra("sex" , 1);
            }else
                ToastUtils.showShort(this,"您的性别保密,找不到匹配的人,请选择性别");
        }
        intent.putExtra("type" , type);
        intent.putExtra("key",0);
        startActivity(intent);
    }*/

    private void initAnimator() {
        //正在匹配中的曲线
        path = new Path();
        path.moveTo(0, 300);
        path.quadTo(width / 2, 0, width - 20, 300);
       /* path.moveTo(width-20, 300);
        path.quadTo(width/2, 100, 0, 300);*/
        Log.i("MainActivity", ".........width=" + width);
        path1 = new Path();
        path1.moveTo(width - 20, 300);
        path1.quadTo(width / 2, 0, 0, 300);


        //匹配成功后的曲线
        path2 = new Path();
        path2.moveTo(0, 130);
        path2.lineTo(width / 2 - 140, 130);
        //path2.quadTo(width / 2, 0, 0, 300);

        path3 = new Path();
        path3.moveTo(width - 20, 140);
        path3.lineTo(width / 2, 140);
        //path3.quadTo(width / 2, 0, 0, 300);


        //正在匹配中的动画
        mAnimator = ObjectAnimator.ofFloat(image, View.X, View.Y, path);
        mAnimator.setDuration(4000);
        //mAnimator.setRepeatCount(ValueAnimator.INFINITE);//循环
        mAnimator.start();
        mAnimator1 = ObjectAnimator.ofFloat(image, View.X, View.Y, path1);
        mAnimator1.setDuration(4000);
        mAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i("MainActivity", ".........onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("MainActivity", ".........onAnimationEnd");
                image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.feiji_left));
                mAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i("MainActivity", ".........onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.i("MainActivity", ".........onAnimationRepeat");
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i("MainActivity", ".........onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("MainActivity", ".........onAnimationEnd");
                image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.feiji_right));
                mAnimator1.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i("MainActivity", ".........onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.i("MainActivity", ".........onAnimationRepeat");
            }
        });
    }

    private void initView() {
       /* mStatusBar = (View)findViewById(R.id.status_bar);
        mLayoutTop = (LinearLayout)findViewById(R.id.layout_top);*/
        imageView = (CircleImageView) findViewById(R.id.centerImage);
        radarView = (RadarView) findViewById(R.id.content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        image = (ImageView) findViewById(R.id.image);
        image_back = (ImageView) findViewById(R.id.image_back);

        if (TextUtils.isEmpty(sp.getString("headsmall"))) {
            ImgUtil.setGlideHead(this, R.mipmap.head, imageView);
        } else {
            ImgUtil.setGlideHead(this, sp.getString("headsmall"), imageView);
        }
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //开始动画
        radarView.startRippleAnimation();
       /* imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
               stopAnimation();
            }
        });*/
    }

    private void stopAnimation() {
        //停止水波纹动画
        radarView.stopRippleAnimation();
        //停止曲线动画
        mAnimator1.pause();
        mAnimator.pause();
        timer.cancel();
        image.setVisibility(View.GONE);

        setContentView(R.layout.dialog_pengliao);

        image_left = (ImageView) findViewById(R.id.image_feiji_left);
        image_back1 = (ImageView) findViewById(R.id.image_back);
        image_right = (ImageView) findViewById(R.id.image_feiji_right);
        frameLayout_right = (FrameLayout) findViewById(R.id.frameLayout_right);
        frameLayout_left = (FrameLayout) findViewById(R.id.frameLayout_left);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_text = (TextView) findViewById(R.id.tv_text);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_name1 = (TextView) findViewById(R.id.tv_name1);
        tv_gender1 = (TextView) findViewById(R.id.tv_gender1);

        image_left1 = (ImageView) findViewById(R.id.image_left1);
        image_right1 = (ImageView) findViewById(R.id.image_right1);
        tv_name.setText(mBean.getUser().getUser_nickname());
        tv_name1.setText(mBean.getRand_user().getUser_nickname());
        tv_gender.setText(mBean.getUser().getSex().equals("1")==true?"男":"女");
        tv_gender1.setText(mBean.getRand_user().getSex().equals("1")==true?"男":"女");
        ImgUtil.load(TouhChatActivity.this,mBean.getUser().getAvatar(),image_left1);
        ImgUtil.load(TouhChatActivity.this,mBean.getRand_user().getAvatar(),image_right1);
        //匹配成功后的动画
        mAnimator2 = ObjectAnimator.ofFloat(image_left, View.X, View.Y, path2);
        mAnimator2.setDuration(1200);

        mAnimator3 = ObjectAnimator.ofFloat(image_right, View.X, View.Y, path3);
        mAnimator3.setDuration(1200);

        mAnimator2.start();
        mAnimator3.start();

        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setCurrentValue(1.0f);
        spring.setSpringConfig(new SpringConfig(90, 3));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                float currentValue = (float) spring.getCurrentValue();
                frameLayout_right.setScaleX(currentValue);
                frameLayout_right.setScaleY(currentValue);

                frameLayout_left.setScaleX(currentValue);
                frameLayout_left.setScaleY(currentValue);

                tv_video.setScaleX(currentValue);
                tv_video.setScaleY(currentValue);

                tv_text.setScaleX(currentValue);
                tv_text.setScaleY(currentValue);
            }
        });
        spring.setEndValue(1.05);

        tv_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpdetails(1);
            }
        });
        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpdetails(2);
            }
        });
        image_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void jumpdetails(int type) {
        //1位文字聊天 2为视频聊天
        switch (type){
            case 1:
                if (sp.getString("phone").equals("" + mBean.getRand_user().getMobile())) {
                    ToastUtils.showShort(this, "不能和自己聊天");
                    return;
                }
                startActivityForResult(new Intent(TouhChatActivity.this, ChatActivity.class).putExtra("userId", "" + mBean.getRand_user().getMobile()).putExtra("nickname", mBean.getRand_user().getUser_nickname()).putExtra("type", type).putExtra("module","touh"),100);
                break;
            case 2:
                mFlag = 1;
                MPermissions.requestPermissions(TouhChatActivity.this, PESSION_CODE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO);
                break;
        }
    }
    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {

        switch (mFlag) {
            case 1:
                startVideoCall();
                break;
        }
    }
    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(TouhChatActivity.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivityForResult(new Intent(TouhChatActivity.this, VideoCallActivity.class).putExtra("username", mBean.getRand_user().getMobile())
                    .putExtra("isComingCall", false).putExtra("nickname", mBean.getRand_user().getUser_nickname()),100);
            // videoCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
        }
    }


    @Override
    public Rand_User_Data doSomeThing() {
        return mBean;
    }
}
