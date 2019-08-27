package com.qcwl.debo.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.pay.CoinsPayDialog_redBacket;
import com.qcwl.debo.ui.pay.CoinsPayDialog_transfer;
import com.qcwl.debo.ui.pay.SetPayPwdDialog;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by qcwl on 2017/10/25.
 */

public class Transfer_pushActivity extends BaseActivity {
    private EditText tt_money;
    private TextView tv_but_push;
    private ImageView iv_head;
    protected String tophone = "";
    private String chatType ="1";
    private TextView tv_name;
     TextView tv_push_explain;
    private int type = 1;
    private String inputFile ="";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.transferpush_layout);
        tophone = getIntent().getStringExtra("tophone");
        initTitleBar();
        initData();
    }
    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setTitle("给朋友转账")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        tt_money = (EditText) findViewById(R.id.tt_money);
        iv_head = (RoundedImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);


        tt_money.requestFocus();
        EaseUserUtils.setUserNick(this, tophone,tv_name, iv_head);

        tv_push_explain = (TextView) findViewById(R.id.tv_push_explain);
        tv_push_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferDialog.OnSureClickListener listener1 = new TransferDialog.OnSureClickListener() {
                    public void getText(String string) {
                        inputFile = string;
                    }
                };
                TransferDialog dialog = new TransferDialog(Transfer_pushActivity.this,listener1);
                dialog.show();
            }
        });
        tv_but_push = (TextView) findViewById(R.id.tv_but_push);

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
                            s.delete(selectionStart - 1, selectionEnd);
                            tt_money.setText(s);
                        }
                        tt_money.setSelection(tt_money.length());
                        String str = s.toString();
                        Float inputTotal = Float.parseFloat(str);
                        Log.i("输入金额",inputTotal+"");
                            tt_money.setTextColor(getResources().getColor(R.color.black));
                            tv_but_push.setBackgroundResource(R.drawable.over_redpacket_bg);
                            tv_but_push.setClickable(true);
                    }
                } catch (Exception e) {
                    Log.i("",e.toString());
                }
            }
        });

        tv_but_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调接口    生成订单。。。支付。。。然后发消息
                pay(tt_money.getText().toString(),chatType,"1",tophone,tv_push_explain.getText().toString());
            }
        });
    }


    private static String order_sn = "";

    //生成订单  indent    p_type 1、单发；2、群发    num 红包个数  content:祝福语
    private  void pay(final String price, final String p_type, String num, String phone, final String content ) {
        Api.generateOrder_transfer(sp.getString("uid"), "", price, 3, 13, "",p_type,num,phone,content, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        //需要在支付完成后根据indent处理页面跳转问题
                        switch (3) {
                            case 3:
                                JSONObject object2 = new JSONObject(apiResponse.getData());
                                order_sn = object2.optString("order_sn");
                                if (SPUtil.getInstance(Transfer_pushActivity.this).getInt("is_set_pay_pwd") == 1) {
                                    CoinsPayDialog_transfer.createDialog(Transfer_pushActivity.this, order_sn, "", price,content,price).show();
                                } else {
                                    SetPayPwdDialog.createDialog(Transfer_pushActivity.this, order_sn, "", price).show();
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(Transfer_pushActivity.this, apiResponse.getMessage());
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
