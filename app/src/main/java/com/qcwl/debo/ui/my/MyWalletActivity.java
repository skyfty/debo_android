package com.qcwl.debo.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.wallet.TradingRecordActivity;
import com.qcwl.debo.ui.my.wallet.card.BankcardActivity;
import com.qcwl.debo.ui.my.wallet.MyPointActivity;
import com.qcwl.debo.ui.my.wallet.RechargeActivity;
import com.qcwl.debo.ui.my.wallet.WithdrawalActivity;
import com.qcwl.debo.ui.my.wallet.storage.MyStorageActivity;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/18.
 * 我的钱包
 */

public class MyWalletActivity extends BaseActivity {

    @Bind(R.id.text_total)
    TextView textTotal;

    private String total = "0";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.my_wallet);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MyWalletActivity","................onResume");
        loadData();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动我的钱包页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束我得钱包页面");
    }

    private void loadData() {
        Api.getMyCoins(sp.getString("uid"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("MyWalletActivity","................="+apiResponse.getData());
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        total = object.optString("coins");
                        textTotal.setText("¥ " + total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(MyWalletActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    @OnClick({R.id.left_image, /*R.id.text_right,*/ R.id.layout_point, R.id.layout_storage,R.id.layout_card, R.id.layout_recharge, R.id.balance_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image://返回键
                finish();
                break;
           /* case R.id.text_right://交易记录
                startActivity(new Intent(this, TradingRecordActivity.class));
                break;*/
            case R.id.layout_point://积分
                startActivity(new Intent(this, MyPointActivity.class));
                break;
            case R.id.layout_storage://存储罐
                startActivity(new Intent(this, MyStorageActivity.class).putExtra("money",total));
                break;
            case R.id.layout_card://银行卡
                startActivity(new Intent(this, BankcardActivity.class).putExtra("type", 0));
                break;
            case R.id.layout_recharge://充值
                startActivity(new Intent(this, RechargeActivity.class));
                break;
            case R.id.balance_withdraw://提现
                startActivity(new Intent(this, WithdrawalActivity.class).putExtra("total", total));
                break;
        }
    }
}
