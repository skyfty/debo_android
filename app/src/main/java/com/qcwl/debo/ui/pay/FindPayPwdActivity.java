package com.qcwl.debo.ui.pay;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPayPwdActivity extends BaseActivity {

    @Bind(R.id.edit_pwd)
    EditText editPwd;
    @Bind(R.id.edit_mobile)
    EditText editMobile;
    @Bind(R.id.edit_code)
    EditText editCode;
    @Bind(R.id.text_send)
    TextView textSend;

    private String mobile, code, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pay_pwd);
        ButterKnife.bind(this);
        initTitleBar();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("找回密码")
                .setAlpha(1)
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }


    private void sendVerifyCode(String momile) {
        Api.getCode(momile, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    count = 60;
                    handler.postDelayed(runnable, 1000);
                } else {
                    ToastUtils.showShort(FindPayPwdActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    int count = 60;
    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count > 0) {
                textSend.setText("重新获取(" + count + "s)");
                handler.postDelayed(runnable, 1000);
                textSend.setClickable(false);
            } else {
                textSend.setText("获取验证码");
                textSend.setClickable(true);
            }
        }
    };

    private void noCodeHint() {
        code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showShort(this, "请输入验证码");
            return;
        }
    }

    private void noMobileHint() {
        mobile = editMobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort(this, "手机号不能为空");
            return;
        }
    }

    private void pwdHint() {
        pwd = editPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
            ToastUtils.showShort(this, "支付密码为6位数字");
            return;
        }
    }

    @OnClick({R.id.text_send, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_send:
                //调发送验证码接口
                noMobileHint();
                sendVerifyCode(mobile);
                break;
            case R.id.btn_finish:
                submit();
                break;
        }
    }

    private void submit() {
        noMobileHint();
        noCodeHint();
        pwdHint();
        Api.resetPayPwd(sp.getString("uid"), mobile, code, pwd, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(FindPayPwdActivity.this, "设置成功");
                    finish();
                } else {
                    ToastUtils.showShort(FindPayPwdActivity.this, apiResponse.getMessage());
                }
            }
        });
    }
}
