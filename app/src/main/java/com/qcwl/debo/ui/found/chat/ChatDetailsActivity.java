package com.qcwl.debo.ui.found.chat;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.chat.ChatActivity;
import com.qcwl.debo.chat.VideoCallActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.chat.bean.TouchChatBean;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatDetailsActivity extends BaseActivity {

    private final String TAG = "ChatDetailsActivity";
    private final int PESSION_CODE = 1;

    @Bind(R.id.status_bar)
    View mStatusBar;
    @Bind(R.id.image_back)
    ImageView mImageBack;
    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.layout_top)
    LinearLayout mLayoutTop;
    @Bind(R.id.text_chat)
    Button mTextChat;
    @Bind(R.id.name_age)
    TextView mNameAge;
    @Bind(R.id.btn_like)
    ImageButton mBtnLike;
    @Bind(R.id.btn_dislike)
    ImageButton mBtnDislike;
    @Bind(R.id.img_avatar)
    ImageView mImgAvatar;
    private int mSex;
    private int mType;
    private TouchChatBean mBean;
    private String mUser_nickname;
    private int mFlag;
    private int mKey;

    private LocationManager locationManager;
    private String locationProvider;
    private String latitude = "" ;//维度
    private String longitude = "" ;//精度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.bind(this);

        int statusHeight = ScreenUtils.getStatusHeight(this);
        mStatusBar.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight));
        mLayoutTop.setLayoutParams(new LinearLayout.LayoutParams(-1, statusHeight + ScreenUtils.dp2px(this, 50)));
        mLayoutTop.getBackground().setAlpha(255);

        initView();
//        Log.e("this_sex",mSex+"");
//        getLocation();

    }

    private void initView() {
        if(getIntent()!=null){
            mSex = getIntent().getIntExtra("sex", 2);
            mType = getIntent().getIntExtra("type", 1);
            mKey = getIntent().getIntExtra("key",0);
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
            if(mType == 1){
                mTextChat.setText("发送消息");
            }else if(mType == 2){
                mTextChat.setText("发送视频");
            }
            if(mKey == 0){
                getTouchChat(mSex);
            }else{
                getTouchChatNearby(mSex);
            }
        }
    }

    @OnClick({R.id.image_back, R.id.text_chat, R.id.btn_like, R.id.btn_dislike})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.text_chat:
                jumpdetails(mType);
                break;
            case R.id.btn_like:
//                ToastUtils.showShort(this, "喜欢");
                break;
            case R.id.btn_dislike:
//                ToastUtils.showShort(this, "不喜欢");
                break;
        }
    }

    private void jumpdetails(int type) {
        switch (type){
            case 1:
                if (sp.getString("phone").equals("" + mBean.getMobile())) {
                    ToastUtils.showShort(this, "不能和自己聊天");
                    return;
                }
                startActivity(new Intent(ChatDetailsActivity.this, ChatActivity.class).putExtra("userId", "" + mBean.getMobile()).putExtra("nickname", mBean.getUser_nickname()).putExtra("type", type));
                break;
            case 2:
                mFlag = 1;
                MPermissions.requestPermissions(ChatDetailsActivity.this, PESSION_CODE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(ChatDetailsActivity.this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private void getTouchChat(final int sex) {

        Api.getTouchChat(SPUtil.getInstance(this).getString("uid"), sex, new ApiResponseHandler(ChatDetailsActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    mBean = JSON.parseObject(apiResponse.getData(), TouchChatBean.class);
                    Log.e("this_sex1",""+sex+"/"+ mBean);
                    ImgUtil.setGlideHead(ChatDetailsActivity.this,mBean.getAvatar(),mImgAvatar);
                    /*Glide.with(ChatDetailsActivity.this)
                            .load(mBean.getAvatar())
                            .placeholder(R.mipmap.head)
                            .error(R.mipmap.head)
                            .into(mImgAvatar);*/
                    mUser_nickname = mBean.getUser_nickname();
                    mNameAge.setText(mUser_nickname);
                }
            }
        });

    }

    private void getTouchChatNearby(final int sex) {
        Log.i("lat_lng_url",SPUtil.getInstance(this).getString("uid")+"*"+latitude + "*" + longitude+"*"+sex);
        Api.getTouchChatNearby(SPUtil.getInstance(this).getString("uid"),latitude,longitude, sex, new ApiResponseHandler(ChatDetailsActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Log.e("mbean_nearby","+++++++"+apiResponse.getMessage()+"+++++++");
                    mBean = JSON.parseObject(apiResponse.getData(), TouchChatBean.class);
                    if(mBean!= null){
                        Glide.with(ChatDetailsActivity.this)
                                .load(mBean.getAvatar())
                                .placeholder(R.mipmap.head)
                                .error(R.mipmap.head)
                                .into(mImgAvatar);

                        mUser_nickname = mBean.getUser_nickname();
                        mNameAge.setText(mUser_nickname);
                    }else{
                        ToastUtils.showShort(ChatDetailsActivity.this,"找不到附近的人");
                        finish();
                    }
                    Log.i("lat_lng_sucess",apiResponse.getMessage()+"/"+apiResponse.getCode());
                } else{
                    Log.i("lat_lng_fail",apiResponse.getMessage()+"/"+apiResponse.getCode());
                    ToastUtils.showShort(ChatDetailsActivity.this,"找不到附近的人");
                }
            }
        });
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
            Toast.makeText(ChatDetailsActivity.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(ChatDetailsActivity.this, VideoCallActivity.class).putExtra("username", mBean.getMobile())
                    .putExtra("isComingCall", false).putExtra("nickname", mBean.getUser_nickname()));
            // videoCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
        }
    }

}
