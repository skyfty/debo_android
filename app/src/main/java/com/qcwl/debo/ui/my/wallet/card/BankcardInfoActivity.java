package com.qcwl.debo.ui.my.wallet.card;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankcardInfoActivity extends BaseActivity {

    @Bind(R.id.text_card_type)
    TextView textCardType;
    @Bind(R.id.edit_mobile)
    EditText editMobile;
    @Bind(R.id.checkbox)
    CheckBox checkbox;
    @Bind(R.id.btn_next)
    Button btnNext;

    private String bank_name = "";
    private String bank_card_type = "";
    private String real_name = "";
    private String bank_account = "";

    public static BankcardInfoActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_info);
        instance = this;
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("银行卡信息")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        bank_name = getIntent().getStringExtra("bank_name");
        bank_card_type = getIntent().getStringExtra("bank_card_type");
        real_name = getIntent().getStringExtra("real_name");
        bank_account = getIntent().getStringExtra("bank_account");
        textCardType.setText(bank_name + "  " + bank_card_type);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnNext.setBackgroundResource(R.drawable.rect_green2);
                    btnNext.setClickable(true);
                } else {
                    btnNext.setBackgroundResource(R.color.fans_gray);
                    btnNext.setClickable(false);
                }
            }
        });
        checkbox.setChecked(false);
        btnNext.setBackgroundResource(R.color.fans_gray);
        btnNext.setClickable(false);
    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        String mobile = editMobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort(this, "请输入银行卡绑定的手机号");
            return;
        }
        Intent intent = new Intent(this, VerifyMobileActivity.class);
        intent.putExtra("bank_name", bank_name);
        intent.putExtra("bank_card_type", bank_card_type);
        intent.putExtra("real_name", real_name);
        intent.putExtra("bank_account", bank_account);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }
}
