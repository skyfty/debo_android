package com.qcwl.debo.ui.my.wallet.trading;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.TitleBarBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComplainActivity extends BaseActivity {

    @Bind(R.id.edit_text)
    EditText editText;

    private String order_sn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        ButterKnife.bind(this);
        initTitleBar();

        if (getIntent() != null) {
            order_sn = getIntent().getStringExtra("order_sn");
        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("投诉")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入您要投诉的内容", Toast.LENGTH_SHORT).show();
            return;
        }
        Api.complaint(sp.getString("uid"), order_sn, content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Toast.makeText(ComplainActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ComplainActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
