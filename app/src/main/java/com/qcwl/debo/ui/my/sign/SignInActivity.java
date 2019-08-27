package com.qcwl.debo.ui.my.sign;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity {

    @Bind(R.id.textOne)
    TextView textOne;
    @Bind(R.id.textTwo)
    TextView textTwo;
    @Bind(R.id.textThree)
    TextView textThree;
    @Bind(R.id.textFour)
    TextView textFour;
    @Bind(R.id.textSignDays)
    TextView textSignDays;
    @Bind(R.id.gridView)
    GridView gridView;

    private List<SignBean> items = null;
    private SignAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initTitleBar();
        items = new ArrayList<>();
        adapter = new SignAdapter(this, items);
        gridView.setAdapter(adapter);
        gridView.setEnabled(false);
        signIn();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("每日签到")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private Dialog dialog = null;
    private TextView textPoint = null;

    private void initDialog() {
        dialog = new Dialog(this, R.style.red_packet_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sign_success, null);
        textPoint = (TextView) view.findViewById(R.id.textPoint);
        view.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void signIn() {
        Api.signIn(sp.getString("uid"), "","",new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0 || apiResponse.getCode() == 1) {
                    SignResult result = JSON.parseObject("" + apiResponse.getData(), SignResult.class);
                    if (result == null) {
                        return;
                    }
                    if (apiResponse.getCode() == 1) {
                        if (dialog == null) {
                            initDialog();
                        }
                        if (result.getBase_info() != null) {
                            textPoint.setText("" + result.getBase_info().getPoints());
                        } else {
                            textPoint.setText("0");
                        }
                        dialog.show();
                    }
//                    if (result.getTotal_num_format() != null && result.getTotal_num_format().length == 4) {
//                        textOne.setText(result.getTotal_num_format()[0]);
//                        textTwo.setText(result.getTotal_num_format()[1]);
//                        textThree.setText(result.getTotal_num_format()[2]);
//                        textFour.setText(result.getTotal_num_format()[3]);
//                    } else {
//                        textOne.setText("0");
//                        textTwo.setText("0");
//                        textThree.setText("0");
//                        textFour.setText("0");
//                    }
                    if (result.getBase_info() != null) {
                        textSignDays.setText("本月已累计签到" + result.getBase_info().getCurr_month_sign_num() + "天");
                    } else {
                        textSignDays.setText("本月已累计签到0天");
                    }
                    if (result.getDaily_sign_record() != null) {
                        items.addAll(result.getDaily_sign_record());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(SignInActivity.this, apiResponse.getMessage() + "");
                    finish();
                }
            }
        });
    }
}
