package com.qcwl.debo.ui.pay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.RedPacket_pushActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.my.WishingStarActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/22.
 * 设置支付密码警示框
 */

public class SetPayPwdDialog2 {
    private static Dialog dialog = null;
    private static Context mContext;
    private static String mOrder_sn;
    private static String mTitle;
    private static String mTotal;
    private static String content;
    private static String TAG = "SetPayPwdDialog2";
    //正常零钱支付
    public static Dialog createDialog(Context context, String order_sn, String title, String total, String tt_liuyan) {
        mContext = context;
        mOrder_sn = order_sn;
        mTitle = title;
        mTotal = total;
        content = tt_liuyan;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_pay_pwd, null);
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
        holder.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = holder.editPwd.getText().toString().trim();
                String pwd2 = holder.editPwd2.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
                    ToastUtils.showShort(mContext, "密码和确认密码不能为空");
                    return;
                }
                if (!TextUtils.equals(pwd, pwd2)) {
                    ToastUtils.showShort(mContext, "两次密码不相等");
                    return;
                }
                setPayPwd(pwd, pwd2);
            }
        });
    }

    private static void setPayPwd(String pwd, String re_pwd) {
        Api.setPayPwd(SPUtil.getInstance(mContext).getString("uid"), pwd, re_pwd, new ApiResponseHandler(mContext) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    SPUtil.getInstance(mContext).setInt("is_set_pay_pwd", 1);
                    CoinsPayDialog_redBacket.createDialog(mContext, mOrder_sn, mTitle, mTotal, content).show();
                    dialog.dismiss();
                } else {
                    ToastUtils.showShort(mContext, apiResponse.getMessage());
                }
            }
        });
    }


    static class ViewHolder {
        @Bind(R.id.edit_pwd)
        EditText editPwd;
        @Bind(R.id.edit_pwd2)
        EditText editPwd2;
        @Bind(R.id.btn_finish)
        Button btnFinish;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
