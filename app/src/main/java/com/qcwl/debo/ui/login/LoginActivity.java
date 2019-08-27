package com.qcwl.debo.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.LoginPresenter;
import com.qcwl.debo.presenterInf.LoginPresenterInf;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;
import com.qcwl.debo.widget.DemoHelper;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginPresenterInf {

    private TextView register, forget_pwd;
    private RoundedImageView image;
    private EditText user, pwd;
    private Button login;
    private LoginPresenter loginPresenter;
    private CustomProgressDialog mProgressDialog;

    private ImageView imageEye;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestLocationPermissions();
        if (DemoHelper.getInstance().isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.login);
        initView();
        monitor();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }


    private void initView() {
        loginPresenter = new LoginPresenter(this);
        register = (TextView) findViewById(R.id.register);
        image = (RoundedImageView) findViewById(R.id.image);
        user = (EditText) findViewById(R.id.user);
        pwd = (EditText) findViewById(R.id.pwd);
        login = (Button) findViewById(R.id.login);
        forget_pwd = (TextView) findViewById(R.id.forget_pwd);
        imageEye = (ImageView) findViewById(R.id.image_eye);

        user.setText(SPUtil.getInstance(this).getString("user"));
        user.setSelection(user.getText().toString().trim().length());
    }

    private void monitor() {
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        imageEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    flag = 1;
                    pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imageEye.setImageResource(R.mipmap.ic_eye_open);
                    pwd.setSelection(pwd.length());
                } else if (flag == 1) {
                    flag = 0;
                    pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageEye.setImageResource(R.mipmap.ic_eye_close);
                    pwd.setSelection(pwd.length());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
                break;
            case R.id.login:
                if (TextUtils.isEmpty(user.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入帐号");
                    return;
                }
                if (TextUtils.isEmpty(pwd.getText().toString().trim())) {
                    ToastUtils.showShort(this, "请输入密码");
                    return;
                }
                showProgressDialog("正在登录");
                //loginPresenter.login(this, user.getText().toString().trim(), pwd.getText().toString().trim());
                Map<String, String> params = new HashMap<>();
                params.put("mobile", user.getText().toString().trim());
                params.put("password", pwd.getText().toString().trim());
                if(province!=null&&!province.equals("")){
                    params.put("province", province);
                }
                if(city!=null&&!city.equals("")){
                    params.put("city", city);
                }
                if(area!=null&&!area.equals("")){
                    params.put("area", area);
                }

                loginPresenter.login(this, params);
                break;
            case R.id.forget_pwd:
                startActivityForResult(new Intent(this, ForgetPwdActivity.class), 1);
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        Log.i("LoginActivity",".....code="+code+"     "+message);
        if (code == 0) {
            Log.i("LoginActivity",".....getResult");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        ToastUtils.showShort(this, message);
        hideProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null)
                user.setText(data.getStringExtra("phone"));
        }
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

    private String province = "", city = "", area = "";

    @Override
    public void locationSuccessResult(BDLocation location) {
        super.locationSuccessResult(location);
        if (location != null) {
            province = location.getProvince();
            city = location.getCity();
            area = location.getDistrict();
        }
        locationClient.stop();
    }
}
