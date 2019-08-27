package com.qcwl.debo.ui.found.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.contact.ContactsContentActivity;
import com.qcwl.debo.ui.found.chat.bean.Rand_User_Data;
import com.qcwl.debo.utils.CallBack;
import com.qcwl.debo.utils.CallBackUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatOverActivity extends BaseActivity {
    private TextView tv_add;
    private String TAG = "ChatOverActivity";
    private TextView tv_continue;
    private TextView tv_return;
    private FrameLayout framelayout;
    private String mobile;
    private CircleImageView iv_user, iv_pruser;
    private TextView tv_user_name;
    private TextView tv_name;
    private Rand_User_Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_over);
        mobile = getIntent().getStringExtra("mobile");
        data = CallBackUtils.doCallBackMethod();
        initView();
        Log.i("ChatOverActivity", "........=" + data);
    }

    private void initView() {

        iv_user = (CircleImageView) findViewById(R.id.iv_user);
        iv_pruser = (CircleImageView) findViewById(R.id.iv_pruser);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_name = (TextView) findViewById(R.id.tv_name);

        framelayout = (FrameLayout) findViewById(R.id.framelayout);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_continue = (TextView) findViewById(R.id.tv_continue);
        tv_return = (TextView) findViewById(R.id.tv_return);

        ImgUtil.load(this, data.getUser().getAvatar(), iv_user);
        ImgUtil.load(this, data.getRand_user().getAvatar(), iv_pruser);
        tv_user_name.setText(data.getUser().getUser_nickname());
        tv_name.setText(data.getRand_user().getUser_nickname());

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatOverActivity.this, ContactsContentActivity.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("type", "3");
                startActivity(intent);
            }
        });
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(10);
                finish();
                Log.i(TAG, ".........tv_continue");
            }
        });
        tv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(9);
                finish();
                Log.i(TAG, ".........tv_return");
            }
        });

        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.setCurrentValue(1.0f);
        spring.setSpringConfig(new SpringConfig(90, 3));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                float currentValue = (float) spring.getCurrentValue();
                framelayout.setScaleX(currentValue);
                framelayout.setScaleY(currentValue);

            }
        });
        spring.setEndValue(1.05);

    }

}
