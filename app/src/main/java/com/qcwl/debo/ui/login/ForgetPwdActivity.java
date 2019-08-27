package com.qcwl.debo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.qcwl.debo.MainActivity;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.RegisterPresenter;
import com.qcwl.debo.presenterInf.RegisterPresenterInf;
import com.qcwl.debo.utils.ToastUtils;
import com.qcwl.debo.view.CustomProgressDialog;
import com.qcwl.debo.widget.DemoHelper;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener, RegisterPresenterInf {

    private EditText phone, code,pwd,confirm_pwd;
    private Button btn_code,modify;
    private RegisterPresenter registerPresenter;
    private CustomProgressDialog mProgressDialog;
    private ImageView back;
    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private int TIME = 180;//倒计时60s
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (DemoHelper.getInstance().isLoggedIn()) {
            startActivity(new Intent(ForgetPwdActivity.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.forget_pwd);
        initView();
        monitor();
    }

    @Override
    public void statusBarSetting() {
        setDefaultStatusBar();
    }

    private void initView() {
        registerPresenter = new RegisterPresenter(this);
        phone = (EditText)findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        btn_code = (Button)findViewById(R.id.btn_code);
        pwd = (EditText) findViewById(R.id.pwd);
        confirm_pwd = (EditText) findViewById(R.id.confirm_pwd);
        modify = (Button)findViewById(R.id.modify);
        back = (ImageView)findViewById(R.id.back);
    }

    private void monitor() {
        btn_code.setOnClickListener(this);
        modify.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_code:
                if(TextUtils.isEmpty(phone.getText().toString().trim())){
                    ToastUtils.showShort(this,"请输入手机号");
                    return;
                }
                registerPresenter.getCode(this,phone.getText().toString().trim());
                break;
            case R.id.modify:
                if(TextUtils.isEmpty(phone.getText().toString().trim())){
                    ToastUtils.showShort(this,"请输入手机号");
                    return;
                }
                if(TextUtils.isEmpty(code.getText().toString().trim())){
                    ToastUtils.showShort(this,"请输入验证码");
                    return;
                }
                if(TextUtils.isEmpty(pwd.getText().toString().trim())){
                    ToastUtils.showShort(this,"请输入新密码");
                    return;
                }
                if(TextUtils.isEmpty(confirm_pwd.getText().toString().trim())){
                    ToastUtils.showShort(this,"请输入确认新密码");
                    return;
                }
                showProgressDialog("正在修改");
                registerPresenter.forgetPwd(this,phone.getText().toString().trim(),code.getText().toString().trim(),pwd.getText().toString().trim());
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void getResult(int code, String message,Object o) {
        if(code==0){
            Intent intent = new Intent();
            intent.putExtra("phone",phone.getText().toString().trim());
            setResult(RESULT_OK,intent);
            finish();
        }else if(code==100){
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
        ToastUtils.showShort(this,message);
        hideProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
