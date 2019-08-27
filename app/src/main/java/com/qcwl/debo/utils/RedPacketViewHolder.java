package com.qcwl.debo.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qcwl.debo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ChayChan
 * @description: 红包弹框
 * @date 2017/11/27  15:12
 */

public class RedPacketViewHolder {

    @Bind(R.id.iv_close)
    ImageView mIvClose;

    @Bind(R.id.iv_photo)
    ImageView mIvAvatar;

    @Bind(R.id.tv_name)
    TextView mTvName;

    @Bind(R.id.tv_content2)
    TextView tv_content2;

    @Bind(R.id.tv_content)
    TextView tv_content;

    @Bind(R.id.iv_open)
    ImageView mIvOpen;

    private Context mContext;
    private OnRedPacketDialogClickListener mListener;

    private FrameAnimation mFrameAnimation;

    public RedPacketViewHolder(Context context, View view) {
        mContext = context;
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.iv_close, R.id.iv_open})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                stopAnim();
                if (mListener != null) {
                    mListener.onCloseClick();
                }
                break;

            case R.id.iv_open:
                if (mFrameAnimation != null) {
                    //如果正在转动，则直接返回
                    return;
                }
                startAnim();
                if (mListener != null) {
                    mListener.onOpenClick();
                }
                break;
        }
    }

    public void setData(String user_nickname, String avatar, String content, String content2) {
        Glide.with(mContext)
                .load(avatar)
                .into(mIvAvatar);
        mTvName.setText(user_nickname);
        //tv_content2.setText(content2);
        tv_content.setText(content);
    }

    public void startAnim() {

        RotateYAnimation animation = new RotateYAnimation();
        //旋转的次数
        //animation.setRepeatCount(3);
        //旋转的时间
        animation.setDuration(5000);
        //是否停留在动画的最后一帧
        animation.setFillAfter(true);
        mIvOpen.startAnimation(animation);
    }

    public void stopAnim() {
        if (mFrameAnimation != null) {
            mFrameAnimation.release();
            mFrameAnimation = null;
        }
    }

    public void setOnRedPacketDialogClickListener(OnRedPacketDialogClickListener listener) {
        mListener = listener;
    }
}
