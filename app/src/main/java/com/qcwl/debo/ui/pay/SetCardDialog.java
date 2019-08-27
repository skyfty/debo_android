package com.qcwl.debo.ui.pay;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.qcwl.debo.R;
import com.qcwl.debo.ui.circle.ImgUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/22.
 * 设置名片警示框
 */

public class SetCardDialog extends AlertDialog{
    private AlertDialog dialog = null;
    private Context mContext;
    private String mOrder_sn;
    private String mTitle;
    private String mTotal;
    private String content;
    private SetCardDialogListener mListener;
    //正常名片设置
    public SetCardDialog(Context context, String order_sn, String title, String total, String tt_liuyan) {
        super(context);
        this.mContext = context;
        this.mOrder_sn = order_sn;//接收人头像
        this.mTitle = title;//接收人姓名
        this.mTotal = total;//某人名片
        this.content = tt_liuyan;//给接收人留言
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_card, null);
        initView(view);
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void initView(View view) {

        holder = new ViewHolder(view);
        clickListener clickListener = new clickListener();
        holder.btnFinish.setOnClickListener(clickListener);
        holder.btnCancel.setOnClickListener(clickListener);
        //holder.ivPersonalIcon.setImageBitmap();
        holder.tvPersonalName.setText(mTitle);
        //EaseUserUtils.setUserNick(mContext,mTitle, holder.tvPersonalName,holder.ivPersonalIcon);

        if (TextUtils.isEmpty(mOrder_sn)) {
            if (Util.isOnMainThread()) {
                ImgUtil.setGlideHead(mContext, com.hyphenate.easeui.R.drawable.head, holder.ivPersonalIcon);
                //Glide.with(mContext).load(com.hyphenate.easeui.R.drawable.head).into(holder.ivPersonalIcon);
            }
            //   Glide.with(context).load(R.drawable.head).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
        } else {
            if (Util.isOnMainThread()) {
                ImgUtil.setGlideHead(mContext, mOrder_sn, holder.ivPersonalIcon);
               // Glide.with(mContext).load(mOrder_sn).into(holder.ivPersonalIcon);
            }
            //  Glide.with(context).load(headsmall).bitmapTransform(new RoundedCornersTransformation(context,5,0)).into(imageView);
        }


        Log.i("333","name: "+mTitle+"tou: "+mOrder_sn);
        holder.tvCardName.setText(mTotal);


    }
    public void setClickListener(SetCardDialog.SetCardDialogListener listener) {
        this.mListener = listener;
    }
    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_finish:
                    if (mListener != null) {
                        mListener.onItem1Listener(holder.editContent.getText().toString().trim());
                    }
                    break;
                case R.id.btnCancel:
                    if (mListener != null) {
                        mListener.onItem2Listener();
                    }
                    break;

            }
            dialog.dismiss();
        }
    }

    public interface SetCardDialogListener {
        void onItem1Listener(String liuYanContent);

        void onItem2Listener();

    }

    static ViewHolder holder = null;

    static class ViewHolder {
        @Bind(R.id.tv_personal_name)
        TextView tvPersonalName;
        @Bind(R.id.tv_card_name)
        TextView tvCardName;
        @Bind(R.id.edit_content)
        EditText editContent;
        @Bind(R.id.iv_personal_icon)
        ImageView ivPersonalIcon;
        @Bind(R.id.btn_finish)
        TextView btnFinish;
        @Bind(R.id.btn_cancel)
        TextView btnCancel;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
