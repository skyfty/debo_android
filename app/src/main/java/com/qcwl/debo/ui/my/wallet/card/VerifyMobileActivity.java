package com.qcwl.debo.ui.my.wallet.card;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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

public class VerifyMobileActivity extends BaseActivity {


    @Bind(R.id.text_hint)
    TextView textHint;
    @Bind(R.id.edit_code)
    EditText editCode;
    @Bind(R.id.text_send)
    TextView textSend;
    private String bank_name = "";
    private String bank_card_type = "";
    private String bank_account = "";
    private String real_name = "";
    private String mobile = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        bank_name = getIntent().getStringExtra("bank_name");
        bank_card_type = getIntent().getStringExtra("bank_card_type");
        real_name = getIntent().getStringExtra("real_name");
        bank_account = getIntent().getStringExtra("bank_account");
        mobile = getIntent().getStringExtra("mobile");
        textHint.setText("绑定银行卡需短信确认，验证码已发送至手机：" + mobile + "，请按提示操作");
        sendVerifyCode(mobile);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("验证手机号")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @OnClick({R.id.text_send, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_send:
                //调发送验证码接口
                sendVerifyCode(mobile);
                break;
            case R.id.btn_finish:
                submit();
                break;
        }
    }

    private void submit() {
        String code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showShort(this, "请输入验证码");
            return;
        }
        Api.submitBankcardInfo(sp.getString("uid"), code, real_name, bank_account, mobile,
                bank_name, bank_card_type, new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            BankcardBean bean = JSON.parseObject(apiResponse.getData(), BankcardBean.class);
                            if (bean != null) {
                                Intent intent = new Intent();
                                intent.putExtra("bean", bean);
                                setResult(RESULT_OK, intent);
                                finish();
                                BankcardInfoActivity.instance.finish();
                                AddBankcardActivity.instance.finish();
                            } else {
                                ToastUtils.showShort(VerifyMobileActivity.this, "获取数据出错");
                            }
                        } else {
                            ToastUtils.showShort(VerifyMobileActivity.this, apiResponse.getMessage());
                        }
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
                    ToastUtils.showShort(VerifyMobileActivity.this, apiResponse.getMessage());
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
}
