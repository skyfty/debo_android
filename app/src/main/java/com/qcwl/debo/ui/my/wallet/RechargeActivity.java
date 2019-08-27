package com.qcwl.debo.ui.my.wallet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.event.MessageEvent;
import com.qcwl.debo.event.RechargeEvent;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RechargeActivity extends BaseActivity {

    //    @Bind(R.id.root)
//    View root;
    @Bind(R.id.edit_input)
    EditText editInput;
    @Bind(R.id.btn_next)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        initTitleBar();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editInput.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(RechargeActivity.this, "请输入充值金额");
                    return;
                }
                if (Double.parseDouble(input) == 0) {
                    ToastUtils.showShort(RechargeActivity.this, "充值金额不能为零");
                    return;
                }
                if (Double.parseDouble(input) < 0.01) {
                    ToastUtils.showShort(RechargeActivity.this, "充值金额不能小于1分");
                    return;
                }
                PayDialog.createDialog(RechargeActivity.this, "充值", 1, "", input,"").show();
            }
        });
        editInput.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (".".equals(s.toString())) {
                        editInput.setText("0.");
                    }
                    selectionStart = editInput.getSelectionStart();
                    Log.e("====", "start=" + selectionStart);
                    Log.e("====", "end=" + selectionEnd);
                    selectionEnd = editInput.getSelectionEnd();
                    if (!isOnlyPointNumber(editInput.getText().toString())) {
                        //ToastUtils.showShort(WithdrawalActivity.this, "您输入的数字保留在小数点后两位");
                        //删除多余输入的字（不会显示出来）
                        s.delete(selectionStart - 1, selectionEnd);
                        editInput.setText(s);
                    }

                    editInput.setSelection(editInput.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }


    private void initTitleBar() {
//        root.getBackground().setAlpha(255);
        new TitleBarBuilder(this).setTitle("充值").setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRechargeEvent(RechargeEvent messageEvent) {
        if (messageEvent.getMessage().equals("支付成功")){
            ToastUtils.showCustom(this,"支付成功",Toast.LENGTH_SHORT);
            this.finish();
        }
    }
}
