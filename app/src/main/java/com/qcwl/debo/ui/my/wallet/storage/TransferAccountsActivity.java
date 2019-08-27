package com.qcwl.debo.ui.my.wallet.storage;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.MD5;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferAccountsActivity extends BaseActivity {

    @Bind(R.id.edit_input)
    EditText editInput;
    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;
    @Bind(R.id.text_key)
    TextView textKey;
    @Bind(R.id.text_value)
    TextView textValue;

    private int type = 0;
    private String total_money;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_accounts);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
            total_money = getIntent().getStringExtra("total_money");
            money = getIntent().getStringExtra("money");
        }
        if (type == 0) {
            textTitle.setText("转出");
            textKey.setText("转出到");
            textValue.setText("我的余额");
            editInput.setHint("可转出" + total_money + "元到余额");
            btnConfirm.setText("确认转出");
        } else if (type == 1) {
            textTitle.setText("转入");
            textKey.setText("账户余额");
            textValue.setText(money + "元");
            editInput.setHint("建议转入100元以上金额");
            btnConfirm.setText("确认转入");
        }
    }

    @Override
    public void statusBarSetting() {
        //
    }



    private void loadData(String money) {
        StringBuilder sb = new StringBuilder();
        sb.append("money").append("=").append(money)
                .append("&")
                .append("uid").append("=").append(sp.getString("uid"))
                .append("&")
                .append("mobile").append("=").append(sp.getString("phone"))
                .append("&")
                .append("key").append("=").append("g6R1dfMjI7K8/Y");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Log.i("Md5123456",appSign.toString());
        Log.i("Md5123456",sp.getString("uid")+"  ,  "+sp.getString("phone")+"  ,  "+  money  +"   ,  "+appSign );
        Api.transferAccounts(sp.getString("uid"), sp.getString("phone"), money,appSign
                , new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        Log.i("apiResponse",apiResponse.toString());
                        if (apiResponse.getCode() == 0) {
                            Toast.makeText(TransferAccountsActivity.this, "转出成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TransferAccountsActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick({R.id.left_image, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_confirm:
                String input = editInput.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type == 0) {
                    loadData(input);
                } else if (type == 1) {
                    PayDialog.createDialog(this, "存储罐", 15, "", input,"").show();
                }
                break;
        }
    }
}
