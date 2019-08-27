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

public class HelpAndFeedbackActivity extends BaseActivity implements MyInfoPresenterInf {
    private EditText et_fankui;
    private Button commit;
    private MyInfoPresenter myInfoPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.help_and_feedback);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("帮助与反馈").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        myInfoPresenter = new MyInfoPresenter(this);
        et_fankui = (EditText) findViewById(R.id.et_fankui);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(et_fankui.getText().toString())) {
                    ToastUtils.showShort(HelpAndFeedbackActivity.this, "请输入您要反馈的信息");
                } else {
                    myInfoPresenter.commitFeedback(HelpAndFeedbackActivity.this, sp.getString("uid"), et_fankui.getText().toString().trim());
                }
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
