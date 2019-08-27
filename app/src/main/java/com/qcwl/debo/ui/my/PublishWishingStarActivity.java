package com.qcwl.debo.ui.my;

import android.app.Instrumentation;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.PublishWishingPresenter;
import com.qcwl.debo.presenterInf.PublishWishingPresenterInf;
import com.qcwl.debo.utils.ToastUtils;

/**
 * Created by Administrator on 2017/7/24.
 */

public class PublishWishingStarActivity extends BaseActivity implements View.OnClickListener, PublishWishingPresenterInf {
    private LinearLayout left_layout;
    private EditText content;
    private Button send;
    private PublishWishingPresenter publishWishingPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.publish_wishing);
        initView();
        monitor();
    }

    private void initView() {
        publishWishingPresenter = new PublishWishingPresenter(this);
        left_layout = (LinearLayout) findViewById(R.id.left_layout);
        content = (EditText) findViewById(R.id.content);
        send = (Button) findViewById(R.id.send);
    }

    private void monitor() {
        left_layout.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_layout:         new Thread () {
                public void run () {
                    try {
                        Instrumentation inst= new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
                finish();
                break;
            case R.id.send:
                publishWishingPresenter.publishWishingStar(this, sp.getString("uid"), content.getText().toString());
                break;
        }
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            setResult(RESULT_OK);
            finish();
        } else {
            ToastUtils.showShort(this, message);
        }
    }
}
