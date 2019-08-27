package com.qcwl.debo.ui.my.sign;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.utils.ToastUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignIn2Activity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<SignBean> items = null;
    private SignIn2Adapter adapter = null;
    private int year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);
        ButterKnife.bind(this);
        textTitle.setText("签到");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Calendar calendar = Calendar.getInstance();
        Locale.setDefault(Locale.CHINA);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        signIn(year+"", "0"+month);
    }

    @OnClick(R.id.left_image)
    public void onViewClicked() {
        finish();
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
                    signIn(year+"",month+"");
                }
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void signIn(final String year, final String month) {
        Log.e("year-month",year+"");
        Log.e("year-month",month+"");

        Api.signIn(sp.getString("uid"), year, month, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0 || apiResponse.getCode() == 1) {
                    SignResult result = JSON.parseObject(apiResponse.getData(), SignResult.class);
                    if (result == null) {
                        return;
                    }
                    if (apiResponse.getCode() == 1) {
                        if (dialog == null) {
                            initDialog();
                        }
                        if (result.getBase_info() != null) {
                            textPoint.setText("" + result.getBase_info().getPoints());
                            Log.e("num_all",result.getBase_info().getTotal_sign_num());
                            Log.e("num_month",result.getBase_info().getCurr_month_sign_num());
                        } else {
                            textPoint.setText("0");
                        }
                        dialog.show();
                    }
                    if (result.getDaily_sign_record() != null) {
                        items = result.getDaily_sign_record();
                        Log.e("qiandao","\n"+result.getDaily_sign_record());
                        handleListDate();
                        result.setYear(year);
                        result.setMonth(month);
                        adapter = new SignIn2Adapter(SignIn2Activity.this, result);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    ToastUtils.showShort(SignIn2Activity.this, apiResponse.getMessage() + "");
                    finish();
                }
            }
        });
    }

    private void handleListDate() {
        if (items == null || items.size() == 0) {
            return;
        }
        Locale.setDefault(Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        SignBean bean = null;
        for (int i = 0; i < day_of_week - 1; i++) {
            bean = new SignBean();
            bean.setEmpty(true);
            items.add(i, bean);
        }
    }

}
