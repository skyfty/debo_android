package com.qcwl.debo.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.ModifyRemarkPresenter;
import com.qcwl.debo.presenterInf.ModifyRemarkPresenterInf;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ModifyRemarkActivity extends BaseActivity implements ModifyRemarkPresenterInf {

    private EditText remark;
    private ModifyRemarkPresenter modifyRemarkPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.edit_remark);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("备注").setImageLeftRes(R.mipmap.back).setTextRight("保存").setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(getIntent().getStringExtra("type"))){
                    modifyRemarkPresenter.modifyRemark(ModifyRemarkActivity.this, sp.getString("uid"), getIntent().getStringExtra("id"),getIntent().getStringExtra("mobile"), remark.getText().toString());
                }else {
                    modifyRemarkPresenter.modifyRenMaiRemark(ModifyRemarkActivity.this, sp.getString("uid"), getIntent().getStringExtra("id"),getIntent().getStringExtra("mobile"), remark.getText().toString());
                }
            }
        });
    }

    private void initView() {
        modifyRemarkPresenter = new ModifyRemarkPresenter(this);
        remark = (EditText) findViewById(R.id.remark);
        remark.setText(getIntent().getStringExtra("remark"));
        remark.setSelection(remark.getText().toString().length());
    }

    @Override
    public void getResult(int code, String message, Object o) {
        if (code == 0) {
            Intent intent = new Intent(ModifyRemarkActivity.this, ContactsContentActivity.class);
            intent.putExtra("remark", remark.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }else {
            ToastUtils.showShort(this,message);
        }
    }
}
