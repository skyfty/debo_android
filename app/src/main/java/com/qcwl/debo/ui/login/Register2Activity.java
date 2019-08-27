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

public class Register2Activity extends BaseActivity implements View.OnClickListener, RegisterPresenterInf {
    private RegisterPresenter registerPresenter;
    private String[] permissions;
    private final int PESSION_CODE = 1;
    private int flag;
    private String title;
    private String qr_user;

    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private int TIME = 180;//倒计时60s
    private Guide guide;

    private RoundedImageView image;

    private int index = 0;

    private ImageView goback;
    private TextView tv_login;
    private ImageView iv_qrcode;
    private EditText et_code;
    private Button register;
    private String nickname,phone,code,pwd,confirm_pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);
        requestLocationPermissions();
        nickname = getIntent().getStringExtra("nickname");
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
        pwd = getIntent().getStringExtra("pwd");
        confirm_pwd = getIntent().getStringExtra("confirm_pwd");
        initView();
        monitor();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }

    private void initView() {
        registerPresenter = new RegisterPresenter(this);
        image = (RoundedImageView) findViewById(R.id.image);
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        register = (Button) findViewById(R.id.register);
        goback = (ImageView) findViewById(R.id.go_back);
        tv_login = (TextView) findViewById(R.id.tv_login);
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        et_code = (EditText) findViewById(R.id.et_code);
        if (!TextUtils.isEmpty(SPUtil.getInstance(this).getString("register_guide"))) {
            return;
        }
        iv_qrcode.post(new Runnable() {
            @Override
            public void run() {
                SPUtil.getInstance(Register2Activity.this).setString("register_guide", "1");
                showGuideView();
            }
        });
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(iv_qrcode)
                .setAlpha(180)
                .setHighTargetCorner(20)
                .setHighTargetPadding(5)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {

            }
        });

        builder.addComponent(new RegisterGuideComponent());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(Register2Activity.this);
    }

    private void monitor() {
        iv_qrcode.setOnClickListener(this);
        register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        goback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_qrcode:
                flag = 1;
                title = "扫码注册";
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                MPermissions.requestPermissions(Register2Activity.this, PESSION_CODE, permissions);
                break;
            case R.id.tv_login:
                setResult(3);
                finish();
                break;
            case R.id.go_back:
                finish();
                break;
            case R.id.register:
                Map<String, String> params = new HashMap<>();
                params.put("user_nickname", RAS.getPublicKeyStrRAS(nickname.getBytes()));
                params.put("mobile", RAS.getPublicKeyStrRAS(phone.getBytes()));
                params.put("code", RAS.getPublicKeyStrRAS(code.getBytes()));
                params.put("password", RAS.getPublicKeyStrRAS(pwd.getBytes()));
                params.put("confirm_password", RAS.getPublicKeyStrRAS(confirm_pwd.getBytes()));
                if (!et_code.getText().toString().equals("")) {
                    Log.i("RegisterActivity", "..............et_code=" + et_code.getText().toString());
                    params.put("invitation_code", RAS.getPublicKeyStrRAS(et_code.getText().toString().getBytes()));
                }
                registerPresenter.register(this, params);
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
        Log.i("RegisterActivity", "......requestCode" + requestCode + "    " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                qr_user = data.getStringExtra("result");
                et_code.setText(qr_user);
            }
            Log.i("RegisterActivity", "......qr_user" + qr_user);
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            Intent intent = new Intent();
            intent.putExtra("phone", phone);
            setResult(4, intent);
            finish();
        }
        ToastUtils.showShort(this, message);
    }
    private String province = "", city = "", area = "";

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        if (location != null) {
            province = location.getProvince();
            SPUtil.getInstance(this).setString("city", province);
            city = location.getCity();
            area = location.getDistrict();
        }
        locationClient.stop();
    }
}
