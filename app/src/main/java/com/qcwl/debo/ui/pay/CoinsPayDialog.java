package com.qcwl.debo.ui.pay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.RedPacket_pushActivity;
import com.qcwl.debo.event.MessageEvent;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.my.WishingStarActivity;
import com.qcwl.debo.ui.my.partner.PartnerActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;


import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CoinsPayDialog {

    private static Dialog dialog = null;
    private static Context mContext;
    private static String mOrder_sn;
    private static String mTitle;
    private static String mTotal;
    private static String TAG = "CoinsPayDialog";
    public static Dialog createDialog(Context context, String order_sn, String title, String total) {
        mContext = context;
        mOrder_sn = order_sn;
        mTitle = title;
        mTotal = total;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_coins_pay, null);
        listener(view);
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    static ViewHolder holder = null;

    private static void listener(View view) {
        holder = new ViewHolder(view);
        holder.textTitle.setText("" + mTitle);
        holder.textTotal.setText("￥" + mTotal);
        holder.pwdView.requestFocus();
        holder.pwdView.setFocusable(true);
        KeyBoardUtils.openKeybord(holder.pwdView, mContext);
        holder.pwdView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                coinsPay(psw);
            }
        });
    }

    private static void coinsPay(String pwd) {
        Log.i("CoinsPayDialog","............="+mOrder_sn);
        Api.coinsPay(SPUtil.getInstance(mContext).getString("uid"), pwd, mOrder_sn, new ApiResponseHandler(mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                closeKeyboard();
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(mContext, "支付成功");
                    Log.i("支付成功返回", apiResponse.getData());
                    dialog.dismiss();
                    if (!(mContext instanceof WishingStarActivity) && !(mContext instanceof PartnerActivity)) {
                        ((Activity) mContext).finish();
                    }
                    if (mContext instanceof PartnerActivity){
                        Log.i(TAG, "EventBus");
                        EventBus.getDefault().post(new MessageEvent("支付成功"));
                    }
                } else if (apiResponse.getCode() == -4) {
//                                code -4 密码不正确
                    payPwdErrorDialog();
                } else {
                    ToastUtils.showShort(mContext, apiResponse.getMessage());
                }
            }
        });
    }

    //聊天发红包
    private static void coinsPay_red(String pwd) {
        Api.coinsPay_red(SPUtil.getInstance(mContext).getString("uid"), pwd, mOrder_sn, "1", new ApiResponseHandler(mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                closeKeyboard();
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(mContext, "支付成功");
                    Log.i("支付成功返回", apiResponse.getData());
                    dialog.dismiss();
                    if (!(mContext instanceof WishingStarActivity)) {
                        if (mContext instanceof RedPacket_pushActivity) {
                            ((RedPacket_pushActivity) mContext).setResult(Activity.RESULT_OK);
                        } else {
                            ((Activity) mContext).finish();
                        }
                    }
                } else if (apiResponse.getCode() == -4) {
//                                code -4 密码不正确
                    payPwdErrorDialog();
                } else {
                    ToastUtils.showShort(mContext, apiResponse.getMessage());
                }
            }
        });
    }

    private static void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(holder.pwdView.getWindowToken(), 0);
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
        @Bind(R.id.text_title)
        TextView textTitle;
        @Bind(R.id.text_total)
        TextView textTotal;
        @Bind(R.id.pwdView)
        GridPasswordView pwdView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
