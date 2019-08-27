package com.qcwl.debo.ui.my.partner.sale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishSaleActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.edit_num)
    EditText editNum;
    @Bind(R.id.edit_price)
    EditText editPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_sale);
        ButterKnife.bind(this);
        textTitle.setText("发布");
    }

    @Override
    public void statusBarSetting() {
        //
    }

    @OnClick({R.id.left_image, R.id.btn_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_publish:
                publish();
                break;
        }
    }

    private void publish() {
        String num = editNum.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        if (TextUtils.isEmpty(num) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "请输入嘚啵币数额与售价", Toast.LENGTH_SHORT).show();
            return;
        }
        Api.sellCoins(sp.getString("uid"), sp.getString("phone"), num, price, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Toast.makeText(PublishSaleActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PublishSaleActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
