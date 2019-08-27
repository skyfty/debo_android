package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/25.
 */

public class EditNickNameActivity extends BaseActivity implements MyInfoPresenterInf {
    private EditText nick;
    private int type;
    private TextView tip;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.edit_nick);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle(getIntent().getStringExtra("title"))
                .setImageLeftRes(R.mipmap.back)
                .setTextRight("保存")
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(nick.getText().toString())) {
                            ToastUtils.showShort(EditNickNameActivity.this, "昵称不能为空");
                            return;
                        }
                        submit(nick.getText().toString());
                    }
                });
    }

    private void submit(String value) {
        String key="";
        if (type == 1) {
           key="user_nickname";
        } else if (type == 2) {
            key="address";
        } else if (type == 3) {
            key="signature";
        }else if(type == 4){
            key = "debo_number";
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", sp.getString("uid"));
        params.put(key, value);
        new MyInfoPresenter(this).editUserInformation(this, params, null);
    }

    private void initView() {
        type = getIntent().getIntExtra("type", 0);
        nick = (EditText) findViewById(R.id.nick);
        tip = (TextView) findViewById(R.id.tip);
        if (type == 1) {
            tip.setVisibility(View.VISIBLE);
            nick.setText(getIntent().getStringExtra("nick"));
            nick.setMaxLines(1);
            nick.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        } else if (type == 2) {
            nick.setText(getIntent().getStringExtra("address"));
            nick.setMaxLines(2);
        } else if (type == 3) {
            nick.setText(getIntent().getStringExtra("sign"));
            nick.setMaxLines(2);
            nick.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        }else if(type == 4){
            nick.setText(getIntent().getStringExtra("debo"));
            nick.setMaxLines(1);
            nick.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        }
        nick.setSelection(nick.getText().toString().length());
    }

    @Override
    public void getResult(int code, String message, Object o) {
        finish();
    }
}
