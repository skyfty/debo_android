package com.qcwl.debo.ui.found.fans;

import android.os.Bundle;
import android.view.View;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;

public class FansMessageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_message);
        initTitleBar();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("我的粉丝").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
