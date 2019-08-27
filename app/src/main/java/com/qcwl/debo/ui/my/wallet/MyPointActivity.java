package com.qcwl.debo.ui.my.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.AboutActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPointActivity extends BaseActivity {

    @Bind(R.id.text_point)
    TextView textPoint;

    private String total_points = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_point);
        ButterKnife.bind(this);
        initTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("我的积分")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTextRight("积分\n说明")
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyPointActivity.this, AboutActivity.class).putExtra("type",1));
                    }
                });
    }

    private void loadData() {
        Api.getMyCoins(sp.getString("uid"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        total_points = object.optString("points");
                        textPoint.setText(total_points + "分");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(MyPointActivity.this, apiResponse.getMessage());
                }
            }
        });
    }


    @OnClick(R.id.btn_exchange)
    public void onViewClicked() {
        startActivity(new Intent(this, PointExchangeActivity.class).putExtra("total_points", total_points));
    }
}
