package com.qcwl.debo.ui.pay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CoinsWithdrawalDialog {

    private static Dialog dialog = null;
    private static Context mContext;
    private static String mTotal;
    private static String mRateValue;
    private static String mBankcard;
    private static String mBankName;

    public static Dialog createDialog(Context context, String total, String rateValue, String bankcard, String bankName) {
        mContext = context;
        mTotal = total;
        mRateValue = rateValue;
        mBankcard = bankcard;
        mBankName = bankName;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_coins_withdrawal, null);
        listener(view);
        dialog = new AlertDialog.Builder(context)
                .setTitle("请输入支付密码")
                .setView(view)
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    static ViewHolder holder = null;

    private static void listener(View view) {
        holder = new ViewHolder(view);
        holder.textTotal.setText("￥" + mTotal);
        holder.textDecuct.setText("额外扣除￥" + mRateValue + "手续费");
        holder.pwdView.requestFocus();
        holder.pwdView.setFocusable(true);
        KeyBoardUtils.openKeybord(holder.pwdView, mContext);
        holder.pwdView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                withdrawal(psw);
            }
        });
    }

    private static void withdrawal(String pwd) {
        double inputTotal = Double.parseDouble(mTotal);
        double rateTotal = Double.parseDouble(mRateValue.toString());
        Api.withdrawalCash(SPUtil.getInstance(mContext).getString("uid"),
                pwd, String.valueOf(inputTotal + rateTotal), mRateValue, mBankcard, mBankName, new ApiResponseHandler(mContext) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        try {
                            if (apiResponse.getCode() == 0) {
                                ToastUtils.showShort(mContext, "提现成功");
                                dialog.dismiss();
                                ((Activity) mContext).finish();
                            } else if (apiResponse.getCode() == -5) {
//                                code -5 密码不正确
                                payPwdErrorDialog();
                            } else {
                                ToastUtils.showShort(mContext, apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static void payPwdErrorDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_error, null);
        final Dialog dlg = new AlertDialog.Builder(mContext)
                .setView(view)
                .create();
        getView(view, R.id.text_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pwdView.clearPassword();
                dlg.dismiss();
            }
        });
        getView(view, R.id.text_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, FindPayPwdActivity.class));
                holder.pwdView.clearPassword();
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    private static View getView(View view, int id) {
        return view.findViewById(id);
    }


    static class ViewHolder {
        @Bind(R.id.text_total)
        TextView textTotal;
        @Bind(R.id.text_decuct)
        TextView textDecuct;
        @Bind(R.id.pwdView)
        GridPasswordView pwdView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
