package com.qcwl.debo.ui.my.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.wallet.trading.ComplainActivity;
import com.qcwl.debo.ui.my.wallet.trading.TradingDetailBean;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TradingRecordDetailActivity extends BaseActivity {

    @Bind(R.id.image_type)
    ImageView imageType;
    @Bind(R.id.text_type)
    TextView textType;
    @Bind(R.id.text_money)
    TextView textMoney;
    @Bind(R.id.text_type2)
    TextView textType2;
    @Bind(R.id.text_status)
    TextView textStatus;
    @Bind(R.id.text_time)
    TextView textTime;
    @Bind(R.id.text_pay_type)
    TextView textPayType;
    @Bind(R.id.text_sn)
    TextView textSn;
    @Bind(R.id.layout_complaint)
    LinearLayout layoutComplaint;
    private String order_sn;
    private int imgResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_record_detail);
        ButterKnife.bind(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("交易详情")
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
        order_sn = getIntent().getStringExtra("order_sn");
        imgResId = getIntent().getIntExtra("img_res_id", 0);
        imageType.setImageResource(imgResId);
        getDetail();
        layoutComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TradingRecordDetailActivity.this, ComplainActivity.class)
                        .putExtra("order_sn", order_sn));
            }
        });
    }

    private void getDetail() {
        Api.tradingRecordDetail(sp.getString("uid"), order_sn, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    TradingDetailBean detailBean = JSON.parseObject(apiResponse.getData(), TradingDetailBean.class);
                    if (detailBean != null) {
                        try {
                            textType.setText(detailBean.getInfo());
                            textType2.setText(detailBean.getInfo());
                            textMoney.setText(detailBean.getOrder_price());
                            textStatus.setText(detailBean.getPay_status());
                            textTime.setText(detailBean.getTime());
                            textPayType.setText(detailBean.getPay_type());
                            textSn.setText(detailBean.getOrder_sn());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ToastUtils.showShort(TradingRecordDetailActivity.this,
                            apiResponse.getMessage());
                }
            }
        });
    }


}
