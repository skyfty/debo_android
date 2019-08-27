package com.qcwl.debo.ui.found.bump;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;

/**
 * Created by qcwl on 2017/9/21.
 */

public class QueryActivity extends BaseActivity {
    private TextView tv_detail;
    private int type;
    private String title;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.query_layout);
        if (getIntent() == null) {
            return;
        }
        type = getIntent().getIntExtra("type", 1);
        initView();
        initTitleBar();

    }

    private void initTitleBar() {
        if (type == 1) {
            title = "碰一碰";
            tv_detail.setText(getResources().getString(R.string.pengyipeng_query));
        } else if (type == 2) {
            title = "撞一撞";
            tv_detail.setText(getResources().getString(R.string.zhuangyizhuang_query));
        }
        new TitleBarBuilder(this)
                .setTitle(title)
                .setAlpha(1f)
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initView() {
        tv_detail = (TextView) findViewById(R.id.tv_detail);

    }
}
