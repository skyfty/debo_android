package com.qcwl.debo.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.guide.Guide;
import com.qcwl.debo.presenter.RegisterPresenter;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

/**
 * Created by Administrator on 2017/7/14.
 */

public class Register1Activity extends BaseActivity implements View.OnClickListener{
    private EditText  pwd, confirm_pwd;

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
    private Guide guide;
    private View view3,view4;
    private int index=0;
    private Button register;
    private TextView tv_login;
    private String nickname,phone,code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);
        requestLocationPermissions();
        nickname = getIntent().getStringExtra("nickname");
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
        initView();
        monitor();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }

    private void initView() {
        view3 = (View) findViewById(R.id.view3);
        view4 = (View) findViewById(R.id.view4);
        pwd = (EditText) findViewById(R.id.pwd);
        confirm_pwd = (EditText) findViewById(R.id.confirm_pwd);
        register = (Button) findViewById(R.id.register);
        goback = (ImageView) findViewById(R.id.go_back);
        tv_login = (TextView) findViewById(R.id.tv_login);
        if (!TextUtils.isEmpty(SPUtil.getInstance(this).getString("register_guide"))) {
            return;
        }

    }


    private void monitor() {
        register.setOnClickListener(this);
        goback.setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                setResult(3);
                finish();
                break;
            case R.id.go_back:
                finish();
                break;
            case R.id.register:
                    if (TextUtils.isEmpty(pwd.getText().toString().trim())) {
                        ToastUtils.showShort(this, "请输入密码");
                        return;
                    }
                    if (TextUtils.isEmpty(confirm_pwd.getText().toString().trim())) {
                        ToastUtils.showShort(this, "请输入确认密码");
                        return;
                    }
                    if (!pwd.getText().toString().equals(confirm_pwd.getText().toString())){
                        ToastUtils.showShort(this, "两次输入的密码不相同");
                        return;
                    }
                Intent intent = new Intent(Register1Activity.this, Register2Activity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("phone", phone);
                intent.putExtra("code", code);
                intent.putExtra("pwd", pwd.getText().toString());
                intent.putExtra("confirm_pwd", confirm_pwd.getText().toString());
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
        Log.i("RegisterActivity1","......requestCode"+requestCode+"    "+resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null){
                qr_user = data.getStringExtra("result");
            }
        }if (requestCode == 100 && resultCode == 3) {
            setResult(3);
            this.finish();
        }if (requestCode == 100 && resultCode == 4) {
            Intent intent = new Intent();
            intent.putExtra("phone", phone);
            setResult(4, intent);
            finish();
        }
    }

    private String province = "", city = "", area = "";

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        if (location != null) {
            province = location.getProvince();
            SPUtil.getInstance(this).setString("city",province);
            city = location.getCity();
            area = location.getDistrict();
        }
        locationClient.stop();
    }
}
