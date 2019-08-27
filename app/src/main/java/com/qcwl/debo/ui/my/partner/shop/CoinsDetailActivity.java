package com.qcwl.debo.ui.my.partner.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.pay.PayDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoinsDetailActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.text_number)
    TextView textNumber;
    @Bind(R.id.text_price)
    TextView textPrice;
    @Bind(R.id.text_number2)
    TextView textNumber2;
    @Bind(R.id.btn_buy)
    Button btnBuy;
    private CoinsShopBean shopBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins_detail);
        ButterKnife.bind(this);
        textTitle.setText("详情");
        if (getIntent() != null) {
            shopBean = (CoinsShopBean) getIntent().getSerializableExtra("bean");
            if (shopBean != null) {
                textNumber.setText("" + shopBean.getDebo_coins());
                textNumber2.setText("数额：" + shopBean.getDebo_coins() + "个");
                textPrice.setText(shopBean.getMoney() + "元");
                btnBuy.setClickable(true);
            } else {
                btnBuy.setClickable(false);
            }
        }
    }

    @Override
    public void statusBarSetting() {
        //
    }

    @OnClick({R.id.left_image, R.id.btn_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_buy:
                PayDialog.createDialog(this, "购买嘚啵币", 18
                        , shopBean.getUid() + "," + shopBean.getId()
                        , shopBean.getMoney(),"").show();
                break;
        }
    }
}
