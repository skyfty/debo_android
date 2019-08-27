package com.qcwl.debo.ui.found.guarantee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.SelectActivity;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.ui.my.AgreementActivity;
import com.qcwl.debo.ui.my.wallet.MyPointActivity;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 三方客
 */

public class GuaranteeActivity extends BaseActivity {

    @Bind(R.id.image_self)
    CircleImageView imageSelf;
    @Bind(R.id.text_name_self)
    TextView textNameSelf;
    @Bind(R.id.image_other)
    CircleImageView imageOther;
    @Bind(R.id.text_name_other)
    TextView textNameOther;
    @Bind(R.id.edit_money)
    EditText editMoney;

    private SPUtil sp;
    private  CheckBox ck_box;
    private String con_uid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarantee);
        sp = SPUtil.getInstance(this);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        if(TextUtils.isEmpty(sp.getString("headsmall"))){
            if(Util.isOnMainThread()) {
                ImgUtil.setGlideHead(this,R.mipmap.head,imageSelf);
                //Glide.with(this).load(R.mipmap.head).into(imageSelf);
            }
        }else {
            if(Util.isOnMainThread()) {
                ImgUtil.setGlideHead(this,sp.getString("headsmall"),imageSelf);
               // Glide.with(this).load(sp.getString("headsmall")).into(imageSelf);
            }
        }
        if(TextUtils.isEmpty(sp.getString("name"))){
            textNameSelf.setText(sp.getString("phone"));
        }else {
            textNameSelf.setText(sp.getString("name"));
        }
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("三方客")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        })
                .setTextRight("担保\n明细")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GuaranteeActivity.this,GuaranteeDetailActivity.class));
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动三方客页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束三方客页面");
    }

    @OnClick({R.id.layout_select, R.id.text_agreement, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_select:
                startActivityForResult(new Intent(this, SelectActivity.class), 100);
                break;
            case R.id.text_agreement:
                startActivity(new Intent(this, AboutActivity.class).putExtra("type",3));
                break;
            case R.id.btn_confirm:
                submit();
                break;
        }
    }

    private void submit() {
        ck_box = (CheckBox) findViewById(R.id.ck_box);
        if (TextUtils.isEmpty(con_uid)) {
            ToastUtils.showShort(this, "请选择好友");
            return;
        }
        if (!ck_box.isChecked()) {
            ToastUtils.showShort(this, "请同意三方客责任协议");
            return;
        }
        String price = editMoney.getText().toString();
        if (TextUtils.isEmpty(price)) {
            ToastUtils.showShort(this, "请输入担保金额");
            return;
        }
        if (Double.parseDouble(price) == 0) {
            ToastUtils.showShort(this, "担保金额不能为零");
            return;
        }
        if (Double.parseDouble(price) < 0.01) {
            ToastUtils.showShort(this, "担保金额不能小于1分");
            return;
        }
        PayDialog.createDialog(this, "三方客", 7, con_uid, price,"").show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                String avatar = data.getStringExtra("avatar");
                con_uid = data.getStringExtra("uid");
                String nickname = data.getStringExtra("nickname");
                imageOther.setImageResource(0);
                ImgUtil.loadHead(this, avatar, imageOther);
                textNameOther.setText(nickname);
            }
        }
    }
}
