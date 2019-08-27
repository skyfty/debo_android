package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;
import com.ta.utdid2.android.utils.StringUtils;

/**
 * Created by qcwl02 on 2017/9/13.
 */

public class AccountAndSecurity extends BaseActivity implements MyInfoPresenterInf {

    private EditText old_pwd, new_pwd, confirm_new_pwd;
    private Button modify;
    private MyInfoPresenter myInfoPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.account_and_security);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setAlpha(1).setTitle("账号与安全").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        myInfoPresenter = new MyInfoPresenter(this);
        old_pwd = (EditText) findViewById(R.id.old_pwd);
        new_pwd = (EditText) findViewById(R.id.new_pwd);
        confirm_new_pwd = (EditText) findViewById(R.id.confirm_new_pwd);
        modify = (Button) findViewById(R.id.modify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(old_pwd.getText().toString())) {
                    ToastUtils.showShort(AccountAndSecurity.this, "请输入旧密码");
                    return;
                }
                if (StringUtils.isEmpty(new_pwd.getText().toString())) {
                    ToastUtils.showShort(AccountAndSecurity.this, "请输入新密码");
                    return;
                }
                if (StringUtils.isEmpty(confirm_new_pwd.getText().toString())) {
                    ToastUtils.showShort(AccountAndSecurity.this, "请输入确认密码");
                    return;
                }
                myInfoPresenter.modifyPwd(AccountAndSecurity.this, sp.getString("uid"), old_pwd.getText().toString().trim(), new_pwd.getText().toString().trim(), confirm_new_pwd.getText().toString().trim());

            }
        });
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            finish();
        }
        ToastUtils.showShort(this, message);
    }
}
