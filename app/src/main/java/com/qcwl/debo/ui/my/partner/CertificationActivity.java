package com.qcwl.debo.ui.my.partner;

import android.os.Bundle;
import android.text.TextUtils;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CertificationActivity extends BaseActivity {

    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.edit_name)
    TextView editName;
    @Bind(R.id.edit_number)
    EditText editNumber;
    @Bind(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        ButterKnife.bind(this);
        textTitle.setText("实名认证");
    }

    @Override
    public void statusBarSetting() {
        //
    }

    @OnClick({R.id.left_image, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private void submit() {
        String name = editName.getText().toString().trim();
        String number = editNumber.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number)) {
            Toast.makeText(this, "姓名和身份证信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Api.certification(sp.getString("uid"), name, number, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() != 0) {
                    Toast.makeText(CertificationActivity.this, "" + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                btnSubmit.setText("审核中");
                btnSubmit.setTextColor(getResources().getColor(R.color.circle_time));
                btnSubmit.setClickable(false);
            }
        });
    }
}
