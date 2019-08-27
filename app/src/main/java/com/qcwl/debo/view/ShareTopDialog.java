package com.qcwl.debo.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcwl.debo.R;


public class ShareTopDialog extends TopBaseDialog<ShareTopDialog> implements View.OnClickListener {

    private Context context;
    private LinearLayout add_friend, create_group, eye, pay, help,close;
    private MyOnclickListener myOnclickListener;
    private int type;
    public ShareTopDialog(Context context, View animateView) {
        super(context, animateView);
        this.context = context;
    }

    public ShareTopDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void setMyOnclickListener(MyOnclickListener myOnclickListener){
        this.myOnclickListener = myOnclickListener;
    }

    public void setType(int type){
        this.type = type;
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_more, null);
        add_friend = (LinearLayout) inflate.findViewById(R.id.add_friend);
        create_group = (LinearLayout) inflate.findViewById(R.id.create_group);
        eye = (LinearLayout) inflate.findViewById(R.id.eye);
        pay = (LinearLayout) inflate.findViewById(R.id.pay);
        help = (LinearLayout) inflate.findViewById(R.id.help);
        close = (LinearLayout) inflate.findViewById(R.id.close);

        ImageView iv_invite = (ImageView) inflate.findViewById(R.id.iv_invite);
        TextView tv_invite = (TextView) inflate.findViewById(R.id.tv_invite);
        ImageView iv_friend_invite = (ImageView) inflate.findViewById(R.id.iv_friend_invite);
        TextView tv_friend_invite = (TextView) inflate.findViewById(R.id.tv_friend_invite);
        if(type==2){
            eye.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            help.setVisibility(View.GONE);
        }else if(type == 3){
            Log.i("ShareTopDialog","...........=====3");
            eye.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            help.setVisibility(View.GONE);
            iv_invite.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.invite));
            //iv_invite.setBackground(context.getResources().getDrawable(R.mipmap.invite));
            tv_invite.setText("邀请好友得红包");
            iv_friend_invite.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.invite_friend));
            tv_friend_invite.setText("添加好友");
        }
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        add_friend.setOnClickListener(this);
        create_group.setOnClickListener(this);
        eye.setOnClickListener(this);
        pay.setOnClickListener(this);
        help.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_friend:
                myOnclickListener.onClick(1);
                break;
            case R.id.create_group:
                myOnclickListener.onClick(2);
                break;
            case R.id.eye:
                myOnclickListener.onClick(3);
                break;
            case R.id.pay:
                myOnclickListener.onClick(4);
                break;
            case R.id.help:
                myOnclickListener.onClick(5);
                break;
            case R.id.close:
                break;
        }
        dismiss();
    }

    public interface MyOnclickListener{
        void onClick(int i);
    }

}
