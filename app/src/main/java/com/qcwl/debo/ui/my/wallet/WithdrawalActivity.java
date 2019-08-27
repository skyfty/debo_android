package com.qcwl.debo.ui.my.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.wallet.card.BankcardActivity;
import com.qcwl.debo.ui.my.wallet.card.BankcardBean;
import com.qcwl.debo.ui.my.wallet.trading.TradingBean;
import com.qcwl.debo.ui.my.wallet.trading.WithDrawAdapter;
import com.qcwl.debo.ui.pay.CoinsPayDialog;
import com.qcwl.debo.ui.pay.CoinsWithdrawalDialog;
import com.qcwl.debo.ui.pay.SetPayPwdDialog;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawalActivity extends BaseActivity {

    @Bind(R.id.text_bank_card)
    TextView textBankCard;
    @Bind(R.id.text_rate)
    TextView textRate;
    @Bind(R.id.edit_input)
    EditText editInput;
    @Bind(R.id.text_total)
    TextView textTotal;
    @Bind(R.id.layout_total)
    LinearLayout layoutTotal;
    @Bind(R.id.layout_hint)
    LinearLayout layoutHint;
    @Bind(R.id.text_hint)
    TextView textHint;
    @Bind(R.id.text_hint_left)
    TextView textHintLeft;
    @Bind(R.id.swipe_target)
    ListView swipe_target;
    private String total = "";
    private String uid = "";
    double totalValue = 0;
    private List<TradingBean> items = items = new ArrayList<>();;
    private WithDrawAdapter adapter = null;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        ButterKnife.bind(this);
        initTitleBar();
        uid = sp.getString("uid");
        mobile = sp.getString("phone");
        try {
            if (getIntent() != null) {
                total = getIntent().getStringExtra("total");
                totalValue = Double.parseDouble(total);
                textTotal.setText("" + total);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        editInput.setSelection(editInput.length());
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
                    if (s.length() > 0) {
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

                        String str = s.toString();

                        layoutHint.setVisibility(View.VISIBLE);
                        textHintLeft.setVisibility(View.VISIBLE);
                        layoutTotal.setVisibility(View.GONE);
                        double inputTotal = Double.parseDouble(str);
                        double payRateCoins = round(inputTotal * rate);
                        if (inputTotal <= totalValue) {
                            if (inputTotal + payRateCoins > totalValue) {
                                double maxValue = round(totalValue * (1 - rate));
                                hintDialog(payRateCoins, maxValue);
                            }
                            textHintLeft.setVisibility(View.VISIBLE);
                            textHint.setText(df.format(payRateCoins));
                            textHint.setTextColor(getResources().getColor(R.color.fans_gray));
                        } else {
                            textHintLeft.setVisibility(View.GONE);
                            textHint.setText("输入金额超过零钱余额");
                            textHint.setTextColor(getResources().getColor(R.color.ad_red));
                        }
                    } else {
                        layoutHint.setVisibility(View.GONE);
                        layoutTotal.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        items = new ArrayList<>();
        adapter = new WithDrawAdapter(this, items);
        swipe_target.setAdapter(adapter);
        swipe_target.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).getType() == 1) {
                    startActivity(new Intent(WithdrawalActivity.this,
                            TradingRecordDetailActivity.class)
                            .putExtra("order_sn", items.get(position).getOrder_sn())
                            .putExtra("img_res_id", 2131558633));
                }
            }
        });
        getRate();
        getTradingRecord();
        getBankcardList();
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    //四舍五入
    public static double round(double value) {
        BigDecimal big1 = new BigDecimal(Double.toString(value));
        BigDecimal big2 = new BigDecimal("1");
        return big1.divide(big2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void hintDialog(final double payRateCoins, final double maxValue) {
        String message = "您的剩余零钱不足以支付提现手续费￥"
                + df.format(payRateCoins)
                + "，当前最大可提现金额为￥"
                + df.format(maxValue)
                + "，是否要全部提现？";

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("全部提现", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bean == null) {
                            ToastUtils.showShort(WithdrawalActivity.this, "请选择银行卡");
                            return;
                        }
                        CoinsWithdrawalDialog.createDialog(WithdrawalActivity.this,
                                df.format(maxValue),
                                df.format(payRateCoins),
                                bean.getBank_account(),
                                bean.getBank_name()).show();
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void initTitleBar() {
        new TitleBarBuilder(this).setTitle("提现")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @OnClick({R.id.text_bank_card, R.id.text_all, R.id.btn_withdrawal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_bank_card:
                startActivityForResult(new Intent(this, BankcardActivity.class).putExtra("type", 1), 100);
                break;
            case R.id.text_all:
                double payRateCoins = round(totalValue * rate);
                double maxValue = round(totalValue - payRateCoins);
                editInput.setText(df.format(maxValue));
                editInput.setSelection(editInput.length());
                textHint.setVisibility(View.VISIBLE);
                layoutTotal.setVisibility(View.GONE);
                textHint.setTextColor(getResources().getColor(R.color.fans_gray));
                textHint.setText(df.format(payRateCoins));
                break;
            case R.id.btn_withdrawal:
                if (totalValue == 0) {
                    ToastUtils.showShort(this, "余额为零，不能提现");
                    return;
                }
                if (bean == null) {
                    ToastUtils.showShort(this, "请选择银行卡");
                    return;
                }
                String inputValue = editInput.getText().toString().trim();
                String rateValue = textHint.getText().toString().trim();
                if (!TextUtils.isEmpty(inputValue)) {
                    double inputTotal = Double.parseDouble(inputValue.toString());
                    double rateTotal = Double.parseDouble(rateValue.toString());

                    //int _num = Integer.parseInt(inputValue);
                    Log.i("WithDrawCoins","..........._num="+inputTotal+"    "+inputTotal/100);
                    if (inputTotal/100<1){
                        Toast.makeText(this, "提现金额最少100元", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (inputTotal <= totalValue) {
                        if (inputTotal + rateTotal > totalValue) {
                            ToastUtils.showShort(this, "提现金额加手续费超过了零钱余额");
                        } else {
                            if (SPUtil.getInstance(this).getInt("is_set_pay_pwd") == 1) {
                                CoinsWithdrawalDialog.createDialog(this,
                                        inputValue,
                                        rateValue,
                                        bean.getBank_account(),
                                        bean.getBank_name()).show();
                            } else {
                                SetPayPwdDialog.createDialog(this,
                                        1,
                                        inputValue  ,
                                        rateValue,
                                        bean.getBank_account(),
                                        bean.getBank_name()).show();
                            }
                        }
                    } else {
                        ToastUtils.showShort(this, "输入金额超过零钱余额");
                    }
                } else {
                    ToastUtils.showShort(this, "请输入提现金额");
                }
                break;
        }
    }

    double rate = 0;
    private DecimalFormat df = new DecimalFormat("0.00");

    private void getRate() {
        Api.getRate(uid, 1, new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        JSONObject object = new JSONObject(apiResponse.getData());
                        rate = Double.parseDouble(object.optString("rate"));
                        textRate.setText("提现到银行，手续费率" + df.format(rate * 100) + "%");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(WithdrawalActivity.this, apiResponse.getMessage());
                }
            }
        });
    }
    private void getTradingRecord() {
        Api.getTradingRecord(uid, mobile,
                0, 9, "", "",
                new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            List<TradingBean> list = JSON.parseArray(apiResponse.getData(), TradingBean.class);
                            if (list != null && list.size() > 0) {
                                items.clear();
                                list.remove(0);
                                items.addAll(list);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showShort(WithdrawalActivity.this, apiResponse.getMessage());
                        }
                    }
                });
    }

    private void getBankcardList() {
        Api.getBankcardList(sp.getString("uid"), sp.getString("phone"), new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        List<BankcardBean> list = JSON.parseArray(apiResponse.getData(), BankcardBean.class);
                        if (list != null && list.size() > 0) {
                            bean = list.get(0);
                            String bank_name = bean.getBank_name();
                            String card="";
                            if (bean.getBank_account().length()>4){
                                card = bean.getBank_account().substring(bean.getBank_account().length() - 4);
                            }
                            textBankCard.setText(bank_name + "(" + card + ")");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private BankcardBean bean = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            bean = (BankcardBean) data.getSerializableExtra("bean");
            if (bean != null) {
                String bank_name = bean.getBank_name();
                String card="";
                if (bean.getBank_account().length()>4){
                    card = bean.getBank_account().substring(bean.getBank_account().length() - 4);
                }
                textBankCard.setText(bank_name + "(" + card + ")");
            }
        }
    }
}
