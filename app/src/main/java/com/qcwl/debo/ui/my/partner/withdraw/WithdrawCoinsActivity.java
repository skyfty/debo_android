package com.qcwl.debo.ui.my.partner.withdraw;

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

public class WithdrawCoinsActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.edit_input)
    EditText editInput;
    @Bind(R.id.text_total_coins)
    TextView textTotalCoins;

    private String total_coins = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_coins);
        ButterKnife.bind(this);
        textTitle.setText("提现");
        if (getIntent() != null) {
            total_coins = getIntent().getStringExtra("total_coins");
            textTotalCoins.setText("" + total_coins);
        }
    }

    @Override
    public void statusBarSetting() {
        //
    }

    @OnClick({R.id.left_image, R.id.text_all, R.id.btn_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.text_all:
                if (TextUtils.isEmpty(total_coins) || "0".equals(total_coins)) {
                    Toast.makeText(this, "暂无可提现嘚啵币", Toast.LENGTH_SHORT).show();
                    return;
                }
                editInput.setText(total_coins);
                editInput.setSelection(total_coins.length());
                break;
            case R.id.btn_withdraw:
                withdraw();
                break;
        }
    }

    private void withdraw() {
        String num = editInput.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(this, "请输入提现嘚啵币数量", Toast.LENGTH_SHORT).show();
            return;
        }
        Api.withdrawalCoins(sp.getString("uid"), sp.getString("phone"), num, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    Toast.makeText(WithdrawCoinsActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(WithdrawCoinsActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
