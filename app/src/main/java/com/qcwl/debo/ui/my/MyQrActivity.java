package com.qcwl.debo.ui.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.presenter.MyInfoPresenter;
import com.qcwl.debo.presenterInf.MyInfoPresenterInf;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.TitleBarBuilder;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MyQrActivity extends BaseActivity implements MyInfoPresenterInf {

    private MyInfoPresenter myInfoPresenter;
    private ImageView pic, image;
    private TextView name, address;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.my_qr);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("二维码名片")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
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
        StatService.onPageStart(this, "启动二维码页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, "结束二维码页面");
    }

    private void initView() {
        myInfoPresenter = new MyInfoPresenter(this);
        pic = (ImageView) findViewById(R.id.pic);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        image = (ImageView) findViewById(R.id.image);
        if (TextUtils.isEmpty(sp.getString("headsmall"))) {
            if (Util.isOnMainThread()) {
                ImgUtil.setGlideHead(this, R.mipmap.head, pic);
                //Glide.with(this).load(R.mipmap.head).into(pic);
            }
        } else {
            if (Util.isOnMainThread()) {
                ImgUtil.setGlideHead(this, sp.getString("headsmall"), pic);
                //Glide.with(this).load(sp.getString("headsmall")).into(pic);
            }
        }

        name.setText(sp.getString("name"));
        address.setText(sp.getString("address"));
        Log.i("qrcode","............QR_CODE="+sp.getString("qr_code"));
        if (Util.isOnMainThread()) {
            //ImgUtil.setGlideHead(this, sp.getString("qr_code"), image);
           Glide.with(this).load(sp.getString("qr_code")).into(image);
        }
    }


    @Override
    public void getResult(int code, String message, Object o) {

    }
}
