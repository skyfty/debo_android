package com.qcwl.debo.ui.my.wallet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PointExchangeActivity extends BaseActivity {

    @Bind(R.id.text_rate)
    TextView textRate;
    @Bind(R.id.edit_point)
    EditText editPoint;
    @Bind(R.id.text_result)
    TextView textResult;

    private String uid = "";
    private String total_points = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_exchange);
        ButterKnife.bind(this);
        initTitleBar();
        uid = sp.getString("uid");
        if (getIntent() != null) {
            total_points = getIntent().getStringExtra("total_points");
            editPoint.setText("" + total_points);
            editPoint.setSelection(editPoint.length());
        }
        getRate();
        editPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    double input = Double.parseDouble(s.toString());
                    if (input <= Double.parseDouble(total_points)) {
                        textResult.setText(String.valueOf(round(input * rate)));
                    } else {
                        ToastUtils.showShort(PointExchangeActivity.this, "超过最大可兑换减分");
                    }
                } else {
                    textResult.setText("0");
                }
            }
        });
    }

    //四舍五入
    public static double round(double value) {
        BigDecimal big1 = new BigDecimal(Double.toString(value));
        BigDecimal big2 = new BigDecimal("1");
        return big1.divide(big2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("兑换")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        String inputText = editPoint.getText().toString().trim();
        if (!TextUtils.isEmpty(inputText)) {
            if (Double.parseDouble(inputText) > Double.parseDouble(total_points)) {
                ToastUtils.showShort(PointExchangeActivity.this, "超过最大可兑换减分");
            }
        } else {
            ToastUtils.showShort(PointExchangeActivity.this, "请输入需要兑换的积分");
        }
        exchange(inputText);
    }

    double rate = 0;

    private void getRate() {
        Api.getRate(uid, 2, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        rate = Double.parseDouble(object.optString("rate"));
                        textRate.setText("" + object.optString("content"));
                        textResult.setText(String.valueOf(round(Double.parseDouble(total_points) * rate)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(PointExchangeActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void exchange(String points) {
        Api.pointsExchange(uid, points, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(PointExchangeActivity.this, "兑换成功");
                    finish();
                } else {
                    ToastUtils.showShort(PointExchangeActivity.this, apiResponse.getMessage());
                }
            }
        });
    }
}
