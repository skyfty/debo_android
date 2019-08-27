package com.qcwl.debo.ui.my.wallet.card;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBankcardActivity extends BaseActivity {

    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.edit_card)
    EditText editCard;

    public static AddBankcardActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bankcard);
        instance = this;
        ButterKnife.bind(this);
        initTitleBar();
        listener();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("添加银行卡")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        String name = editName.getText().toString().trim();
        String card = editCard.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort(this, "请输入持卡人姓名");
            return;
        }
        if (TextUtils.isEmpty(card)) {
            ToastUtils.showShort(this, "请输入银行卡号");
            return;
        }
        getBankcardInfo(name, card);
    }

    private void getBankcardInfo(String name, String cardNO) {
        Api.getBankcardInfo(sp.getString("uid"), name, cardNO, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                Log.i("getBankcardInfo","..........apiResponse="+apiResponse.getMessage()+"    "+apiResponse.getCode());
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        Intent intent = new Intent(AddBankcardActivity.this, BankcardInfoActivity.class);
                        intent.putExtra("bank_name", object.optString("bank_name"));
                        intent.putExtra("bank_card_type", object.optString("bank_card_type"));
                        intent.putExtra("real_name", object.optString("real_name"));
                        intent.putExtra("bank_account", object.optString("bank_account"));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(AddBankcardActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void listener() {

    }

}
