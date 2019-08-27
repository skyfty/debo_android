package com.qcwl.debo.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.util.EasyUtils;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.chat.ChatOverActivity;
import com.qcwl.debo.ui.found.chat.bean.Rand_User_Data;
import com.qcwl.debo.ui.login.WelcomeActivity;
import com.qcwl.debo.utils.AndroidSoftEditUtils;
import com.qcwl.debo.utils.CallBackUtils;
import com.qcwl.debo.widget.ChatOverDialog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 张龙  on 2016/9/22.
 * 聊天页面
 */

@SuppressLint("Override")
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    public String toChatUsername, remark, type, isShowName;

    private ImmersionBar mImmersionBar;
    private String TAG = "ChatActivity";
    private String module;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //View view = LayoutInflater.from(this).inflate(R.layout.em_activity_chat, null);
        //setContentView(view, new FrameLayout.LayoutParams(-1, ScreenUtils.getScreenHeight(this)));
        setContentView(R.layout.em_activity_chat);
        //AndroidSoftEditUtils.init(this);
        activityInstance = this;
        //get user id or group id
        try {
            module = getIntent().getStringExtra("module");
        }catch (Exception e){
            e.printStackTrace();
        }
        toChatUsername = getIntent().getStringExtra("userId");
        remark = getIntent().getStringExtra("remark");
        type = getIntent().getStringExtra("type");
        Log.i(TAG,".....TYPE="+type);
        isShowName = getIntent().getStringExtra("is_show_name");
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    public void statusBarSetting() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false)
                .keyboardEnable(true)
                .init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        Log.i("ChatActivity",".......onBackPressed==="+module);
        if (module!=null&&!module.equals("")){
            //new ChatOverDialog(ChatActivity.this,toChatUsername).show();
            /*Intent intent = new Intent(this, ChatOverActivity.class);
            intent.putExtra("mobile",toChatUsername);
            startActivityForResult(intent,100);*/
            Log.i("ChatActivity",".......onBackPressed");
            new ChatOverDialog(this,toChatUsername, new ChatOverDialog.AlertDialogUser() {
                @Override
                public void onResult(int confirmed, Bundle bundle) {
                    Log.i("ChatActivity",".......confirmed="+confirmed);
                    if (confirmed == 0){
                        setResult(10);
                        finish();
                    }else if(confirmed == 1){
                        setResult(9);
                        finish();
                    }else if(confirmed == 2){
                        Intent intent = new Intent(ChatActivity.this, ContactsContentActivity.class);
                        intent.putExtra("mobile", toChatUsername);
                        intent.putExtra("type", "3");
                        startActivity(intent);
                    }
                }
            }).show();
        }else {
            if (EasyUtils.isSingleActivity(this)) {
                //跳转到MainActivity会有异常  直接跳转到欢迎页
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        startActivity(intent);
                Log.i(TAG,"............isSingleActivity");
                /*Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);*/
            }
            Log.i("ChatActivity",".......onBackPressed_else");
            chatFragment.onBackPressed();
        }
//        if (EasyUtils.isSingleActivity(this)) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 10://继续碰聊
                 setResult(10);
                chatFragment.onBackPressed();
                break;
            case 9://返回首页
                setResult(9);
                chatFragment.onBackPressed();
                break;
        }
        Log.i("ChatActivity",".......startActivityForResult="+resultCode+"    "+requestCode);
    }


}