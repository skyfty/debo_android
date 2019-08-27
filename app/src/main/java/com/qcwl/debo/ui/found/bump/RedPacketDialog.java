package com.qcwl.debo.ui.found.bump;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.circle.ScreenUtils;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.ToastUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/10/10.
 * 红包确认弹窗
 */

public class RedPacketDialog extends Dialog {

    private View view = null;
    private CircleImageView imageView = null;
    private TextView textName = null;
    private ImageView imageOpen = null;

    private String t_id = "";

    public RedPacketDialog(@NonNull Context context, String t_id, String avatar, String name) {
        super(context, R.style.red_packet_dialog);
        initView(context);
        this.t_id = t_id;
        ImgUtil.loadHead(context, avatar, imageView);
        textName.setText(name);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_red_packet, null);
        //view.setLayoutParams(new LinearLayout.LayoutParams(getRealDpVal(260),-2));
        imageView = (CircleImageView) view.findViewById(R.id.image_view);
        textName = (TextView) view.findViewById(R.id.text_name);
        imageOpen = (ImageView) view.findViewById(R.id.image_open);
//        imageOpen.setBackgroundResource(R.drawable.open_red_packet);
//        animationDrawable = (AnimationDrawable) imageOpen.getBackground();
//
//        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
//            mDuration += animationDrawable.getDuration(i);
//        }
        imageOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRedPacket(t_id);
                dismiss();
//                imageOpen.setClickable(false);
//                animationDrawable.start();//启动动画
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        confirmRedPacket(bean.getT_id());
//                        imageOpen.setBackgroundResource(0);
//                        //重设imageOpen背景
//                        animationDrawable.stop();
//                        animationDrawable = null;
//                        imageOpen.setBackgroundResource(R.drawable.open_red_packet);
//                        animationDrawable = (AnimationDrawable) imageOpen.getBackground();
//                        imageOpen.setClickable(true);
//                        dialog.dismiss();
//                    }
//                }, mDuration * 2);
            }
        });
        getWindow().setContentView(view);//, new LinearLayout.LayoutParams(dp2px(295), dp2px(410))
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = dp2px(297);
        lp.height = dp2px(415);
        getWindow().setAttributes(lp);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private int mCode = 0;

    private void confirmRedPacket(String t_id) {
        Api.confirmRedPacket(SPUtil.getInstance(getContext()).getString("uid"),
                t_id, "", new ApiResponseHandler(getContext()) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        mCode = apiResponse.getCode();
                        if (mCode == 0) {
                            RedPacketBean bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                            if (bean == null) {
                                return;
                            }
                            getContext().startActivity(new Intent(getContext()
                                    , RedPacketDetailActivity.class)
                                    .putExtra("bean", bean));
                        } else {
                            ToastUtils.showShort(getContext(), apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        super.onFailure(errMessage);
                    }
                });
    }

    private int dp2px(int dpVal) {
        return ScreenUtils.dp2px(getContext(), dpVal);
    }

}
