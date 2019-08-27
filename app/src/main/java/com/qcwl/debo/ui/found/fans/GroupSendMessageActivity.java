package com.qcwl.debo.ui.found.fans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.found.fans.contract.FansContract;
import com.qcwl.debo.ui.found.fans.presenter.FansPresenter;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupSendMessageActivity extends BaseActivity implements FansContract.View {

    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.btn_send)
    Button btnSend;
    private FansPresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_send_message);
        ButterKnife.bind(this);

        initTitleBar();
        ininView();
        presenter = new FansPresenter(this);
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("群发消息")
                .setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ininView() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort(GroupSendMessageActivity.this, "不能发送空消息");
                    return;
                }
                presenter.sendMessage(GroupSendMessageActivity.this,sp.getString("uid"), content);
            }
        });
    }

    @Override
    public void doSuccess(int type, Object object) {
        finish();
    }

    @Override
    public void doFailure(int code) {

    }
}
