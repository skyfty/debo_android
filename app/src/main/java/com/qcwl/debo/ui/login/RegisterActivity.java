package com.qcwl.debo.ui.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.guide.Guide;
import com.qcwl.debo.guide.GuideBuilder;
import com.qcwl.debo.guide.RegisterGuideComponent;
import com.qcwl.debo.presenter.RegisterPresenter;
import com.qcwl.debo.presenterInf.RegisterPresenterInf;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.utils.RAS;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener, RegisterPresenterInf {
    private EditText nickname, phone, code, pwd, confirm_pwd;
    private TextView btn_code, register;
    private CheckBox checkbox;
    private RegisterPresenter registerPresenter;
    private String[] permissions;
    private final int PESSION_CODE = 1;
    private int flag;
    private String title;
    private String qr_user;
    private ImageView goback;
    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private int TIME = 180;//倒计时60s
    private TextView tv_login;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        requestLocationPermissions();
        initView();
        monitor();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }

    private void initView() {
        registerPresenter = new RegisterPresenter(this);

        phone = (EditText) findViewById(R.id.phone);
        nickname = (EditText) findViewById(R.id.nickname);
        code = (EditText) findViewById(R.id.code);
        btn_code = (TextView) findViewById(R.id.btn_code);
        tv_login = (TextView) findViewById(R.id.tv_login);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        register = (Button) findViewById(R.id.register);
        goback = (ImageView) findViewById(R.id.go_back);

        if (!TextUtils.isEmpty(SPUtil.getInstance(this).getString("register_guide"))) {
            return;
        }
    }


    private void monitor() {
        btn_code.setOnClickListener(this);
        register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        goback.setOnClickListener(this);
        findViewById(R.id.text_register_desc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, AboutActivity.class).putExtra("type", 2));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入手机号");
                    return;
                }
                registerPresenter.getCode(this, phone.getText().toString().trim());
                break;
            case R.id.tv_login:
                finish();
                break;
            case R.id.go_back:
                finish();
                break;
            case R.id.register:
                if (TextUtils.isEmpty(nickname.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入昵称");
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(code.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入验证码");
                    return;
                }
                if (!checkbox.isChecked()) {
                    ToastUtils.showShort(this, "请同意用户协议");
                    return;
                }
                Intent intent = new Intent(RegisterActivity.this, Register1Activity.class);
                intent.putExtra("nickname", nickname.getText().toString());
                intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("code", code.getText().toString());
                startActivityForResult(intent, 100);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PESSION_CODE)
    public void requestPermissionSuccess() {
        startActivityForResult(new Intent(this, CaptureActivity.class).putExtra("flag", flag).putExtra("tip", "register").putExtra("title", title), 1);
    }

    @PermissionDenied(PESSION_CODE)
    public void requestPermissionFailed() {
        Toast.makeText(this, "权限被禁止，请您去设置界面开启！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("RegisterActivity0", "......requestCode" + requestCode + "    " + resultCode);
           if (requestCode == 100 && resultCode == 3) {
                this.finish();
            }if (requestCode == 100 && resultCode == 4) {
                Intent intent = new Intent();
                intent.putExtra("phone", phone.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
            }
            Log.i("RegisterActivity0", "......qr_user" + qr_user);
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            Intent intent = new Intent();
            intent.putExtra("phone", phone.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        } else if (code == 100) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 180; i > 0; i--) {
                        handler.sendEmptyMessage(CODE_ING);
                        if (i <= 0) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(CODE_REPEAT);
                }
            }).start();
        }
        ToastUtils.showShort(this, message);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ING://已发送,倒计时
                    btn_code.setText("" + --TIME + "s");
                    btn_code.setClickable(false);
                    break;
                case CODE_REPEAT://重新发送
                    TIME = 180;
                    btn_code.setText("获取验证码");
                    btn_code.setClickable(true);
                    break;
            }
        }
    };
}
