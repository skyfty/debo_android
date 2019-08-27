package com.qcwl.debo.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.pay.CoinsPayDialog_redBacket;
import com.qcwl.debo.ui.pay.SetPayPwdDialog2;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by qcwl on 2017/10/25.
 */


public class RedPacket_pushActivity extends BaseActivity {
    private EditText tt_liuyan, tt_money;
    private TextView tv_but_push, tv_big_view, tv_tishi;
    protected String tophone = "";
    private String chatType = "";
    private int type = 1;
    private RelativeLayout RL_Num;
    private EditText tt_HowNUM;
    private String HowNUM;
    private TextView tv_jine;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.redpacketpush_layout);
        tophone = getIntent().getStringExtra("tophone");
        chatType = String.valueOf(getIntent().getIntExtra("chatType", 0));
        type = getIntent().getIntExtra("type", 0);

        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("发红包")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        tt_liuyan = (EditText) findViewById(R.id.tt_liuyan);
        tt_money = (EditText) findViewById(R.id.tt_money);
        tv_but_push = (TextView) findViewById(R.id.tv_but_push);
        tv_big_view = (TextView) findViewById(R.id.tv_big_view);
        tv_tishi = (TextView) findViewById(R.id.tv_tishi);
        RL_Num = (RelativeLayout) findViewById(R.id.RL_Num);
        tt_HowNUM = (EditText) findViewById(R.id.tt_HowNUM);
        tv_jine = (TextView) findViewById(R.id.tv_jine);

        if (chatType.equals("2")) {
            RL_Num.setVisibility(View.VISIBLE);
        } else {
            chatType = "1";
            RL_Num.setVisibility(View.GONE);
        }

        tt_money.setSelection(tt_money.length());

        tt_money.addTextChangedListener(new TextWatcher() {
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
                    if (s.length() > 0) {
                        if (".".equals(s.toString())) {
                            tt_money.setText("0.");
                        }
                        selectionStart = tt_money.getSelectionStart();
                        Log.e("====", "start=" + selectionStart);
                        Log.e("====", "end=" + selectionEnd);
                        selectionEnd = tt_money.getSelectionEnd();

                        if (!isOnlyPointNumber(tt_money.getText().toString())) {
                            //ToastUtils.showShort(WithdrawalActivity.this, "您输入的数字保留在小数点后两位");
                            //删除多余输入的字（不会显示出来）
                            s.delete(selectionStart - 1, selectionEnd);
                            tt_money.setText(s);
                        }
                        tt_money.setSelection(tt_money.length());
                        String str = s.toString();
                        Float inputTotal = Float.parseFloat(str);
                        Log.i("输入金额", inputTotal + "");
                        if (inputTotal > 200.00) {
                            tv_tishi.setVisibility(View.VISIBLE);
                            tt_money.setTextColor(getResources().getColor(R.color.bump_red));
                            tv_but_push.setBackgroundResource(R.drawable.noover_redpacket_bg);
                            tv_but_push.setClickable(false);
                        } else {
                            tv_tishi.setVisibility(View.GONE);
                            tt_money.setTextColor(getResources().getColor(R.color.black));
                            tv_but_push.setBackgroundResource(R.drawable.over_redpacket_bg);
                            tv_but_push.setClickable(true);
                        }
                        tv_big_view.setText(String.format("%.2f", inputTotal));
                    } else {
                        tv_tishi.setVisibility(View.GONE);
                        tv_big_view.setText("0.00");
                    }
                } catch (Exception e) {
                    Log.e("", e.toString());
                }
            }
        });

        tv_but_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调接口    生成订单。。。支付。。。然后发消息
                HowNUM = tt_HowNUM.getText().toString();
                if ("1".equals(chatType)) {//单发
                    HowNUM = "1";
                    pay(tt_money.getText().toString(), chatType, HowNUM, tophone, tt_liuyan.getText().toString());
                } else if ("2".equals(chatType)) {//群发
                    if (tt_money.getText().toString().isEmpty()) {
                        ToastUtils.showShort(RedPacket_pushActivity.this, "请填写红包金额");
                        return;
                    }
                    if (HowNUM.isEmpty()) {
                        ToastUtils.showShort(RedPacket_pushActivity.this, "请填写红包个数");
                        return;
                    }
                    pay(tt_money.getText().toString(), chatType, HowNUM, tophone, tt_liuyan.getText().toString());
                }
            }
        });
    }

    private static String order_sn = "";

    //生成订单  indent    p_type 1、单发；2、群发    num 红包个数  content:祝福语
    private void pay(final String price, final String p_type, String num, String phone, final String content) {
        Api.generateOrder_redPacket(sp.getString("uid"), "", price, 3, 3, "", p_type, num, phone, content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        //需要在支付完成后根据indent处理页面跳转问题
                        switch (3) {
                            case 3:
                                JSONObject object2 = new JSONObject(apiResponse.getData());
                                order_sn = object2.optString("order_sn");
                                if (SPUtil.getInstance(RedPacket_pushActivity.this).getInt("is_set_pay_pwd") == 1) {
                                    CoinsPayDialog_redBacket.createDialog(RedPacket_pushActivity.this, order_sn, "", price, content).show();
                                } else {
                                    SetPayPwdDialog2.createDialog(RedPacket_pushActivity.this, order_sn, "", price, content).show();
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(RedPacket_pushActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    private void initData() {
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
}
