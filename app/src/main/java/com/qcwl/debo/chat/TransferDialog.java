package com.qcwl.debo.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.KeyBoardUtils;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.ui.found.bump.RedPacketDetailActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/10/10.
 * 转账说明
 */

public class TransferDialog extends Dialog {

    private View view;
    private  EditText et_liuiyan;
    private TextView tv_cancel,tv_sure;
    public OnSureClickListener mListener;
    private Context mContext;
    public TransferDialog(Context context,OnSureClickListener listener) {
        super(context, R.style.transfer_dialog);
        mContext =context;
        mListener = listener;
        initView(context);
    }

    private void initView(final Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.transfer_dialog_layout, null);
        et_liuiyan = (EditText) view.findViewById(R.id.et_liuiyan);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_sure = (TextView) view.findViewById(R.id.tv_sure);

        KeyBoardUtils.openKeybord(et_liuiyan,context);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtils.closeKeybord(et_liuiyan,context);
                dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Transfer_pushActivity)mContext).tv_push_explain.setText("转账说明："+et_liuiyan.getText().toString());
                ((Transfer_pushActivity)mContext).tv_push_explain.setTextColor(Color.parseColor("#525B6A"));
                ((Transfer_pushActivity)mContext).hideSoftKeyboard();
                KeyBoardUtils.closeKeybord(et_liuiyan,context);
                dismiss();
            }
        });

        getWindow().setContentView(view);//, new LinearLayout.LayoutParams(dp2px(295), dp2px(410))
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = dp2px(297);
        getWindow().setAttributes(lp);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public interface OnSureClickListener {
        void getText(String string); // 声明获取EditText中数据的接口  
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(getContext(), dpVal);
    }

}
