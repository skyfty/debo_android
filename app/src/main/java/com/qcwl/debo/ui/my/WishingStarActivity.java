package com.qcwl.debo.ui.my;

import android.animation.Animator;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.ui.found.bump.RedPacketDetailActivity;
import com.qcwl.debo.ui.found.bump.RedPacketDialog;
import com.qcwl.debo.ui.found.star.ShipActivity;
import com.qcwl.debo.ui.found.star.WishingRecordActivity;
import com.qcwl.debo.ui.pay.PayDialog;
import com.qcwl.debo.utils.ToastUtils;


/**
 * Created by Administrator on 2017/7/17.
 */

public class WishingStarActivity extends BaseActivity implements View.OnClickListener {

    private static final int TYPE_01 = 100;
    private static final int TYPE_02 = 101;
    private static final int TYPE_03 = 102;

    public static final String JSON_01 = "wishing_01.json";
    public static final String JSON_02 = "wishing_02.json";
    public static final String JSON_03 = "wishing_03.json";

    private LinearLayout left_layout, star1, star2, star3;
    private TextView textRight;

    private LottieAnimationView animationView;

    private ImageView imageBg = null;

    private int type = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.wish_star);
        initView();
        monitor();
    }

    private void initView() {
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        left_layout = (LinearLayout) findViewById(R.id.left_layout);
        star1 = (LinearLayout) findViewById(R.id.star1);
        star2 = (LinearLayout) findViewById(R.id.star2);
        star3 = (LinearLayout) findViewById(R.id.star3);
        textRight = (TextView) findViewById(R.id.text_right);

        imageBg = (ImageView) findViewById(R.id.image_bg);
        ImgUtil.load(this, R.drawable.bg_wishing, imageBg);
        animationView.setImageAssetsFolder("images");
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                switch (type) {
                    case TYPE_01:
                        star1.setVisibility(View.INVISIBLE);
                        break;
                    case TYPE_02:
                        star2.setVisibility(View.INVISIBLE);
                        break;
                    case TYPE_03:
                        star3.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(View.GONE);
                switch (type) {
                    case TYPE_01:
                        star1.setVisibility(View.VISIBLE);
                        star1.setAnimation(AnimationUtils.loadAnimation(WishingStarActivity.this, R.anim.wishing_alpha));
                        break;
                    case TYPE_02:
                        star2.setVisibility(View.VISIBLE);
                        star2.setAnimation(AnimationUtils.loadAnimation(WishingStarActivity.this, R.anim.wishing_alpha));
                        if (isStartPay) {
                            PayDialog.createDialog(WishingStarActivity.this, ad_title, 3, ad_id, ad_price,"").show();
                        }
                        break;
                    case TYPE_03:
                        star3.setVisibility(View.VISIBLE);
                        star3.setAnimation(AnimationUtils.loadAnimation(WishingStarActivity.this, R.anim.wishing_alpha));
                        getAdRedPacket();
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void monitor() {
        left_layout.setOnClickListener(this);
        star1.setOnClickListener(this);
        star2.setOnClickListener(this);
        star3.setOnClickListener(this);
        textRight.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatService.onPageStart(this,"启动许愿星页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this,"结束许愿星页面");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_layout:
                new Thread () {
                    public void run () {
                        try {
                            Instrumentation inst= new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.star1:
                if (star2.getVisibility() != View.VISIBLE) {
                    star2.setVisibility(View.VISIBLE);
                }
                if (star3.getVisibility() != View.VISIBLE) {
                    star3.setVisibility(View.VISIBLE);
                }
                type = TYPE_01;
                startActivityForResult(new Intent(this, PublishWishingStarActivity.class), TYPE_01);
                break;
            case R.id.star2:
                if (star1.getVisibility() != View.VISIBLE) {
                    star1.setVisibility(View.VISIBLE);
                }
                if (star3.getVisibility() != View.VISIBLE) {
                    star3.setVisibility(View.VISIBLE);
                }
                type = TYPE_02;
                startActivityForResult(new Intent(this, BumpAndTouchActivity.class).putExtra("type", 3), TYPE_02);//PublishRedEnvelopeStarActivity
                break;
            case R.id.star3:
                if (star1.getVisibility() != View.VISIBLE) {
                    star1.setVisibility(View.VISIBLE);
                }
                if (star2.getVisibility() != View.VISIBLE) {
                    star2.setVisibility(View.VISIBLE);
                }
                type = TYPE_03;
                animationView.setAnimation(JSON_03);
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                break;
            case R.id.text_right:
                startActivity(new Intent(this, WishingRecordActivity.class));
                break;
        }
    }

    private String ad_id, ad_title, ad_price;
    private boolean isStartPay = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TYPE_01) {
                animationView.setAnimation(JSON_01);
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                isStartPay = false;
            } else if (requestCode == TYPE_02) {
                ad_id = data.getStringExtra("ad_id");
                ad_title = data.getStringExtra("ad_title");
                ad_price = data.getStringExtra("ad_price");
                animationView.setAnimation(JSON_02);
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                isStartPay = true;
            }
        }
    }

    private RedPacketBean bean;

    private void getAdRedPacket() {
        Api.getAdRedPacket(sp.getString("uid"), 3, "", new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    try {
                        bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                        if (bean == null) {
                            return;
                        }
                        int star_type = bean.getStar_type();//1 文字型许愿星；2 红包型许愿星
                        if (star_type == 1) {
                            startActivity(new Intent(WishingStarActivity.this, ShipActivity.class).putExtra("red_packet", bean));
                        } else if (star_type == 2) {
                            new RedPacketDialog(WishingStarActivity.this
                                    , bean.getT_id()
                                    , bean.getAvatar()
                                    , bean.getUser_nickname())
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    resultDialog();
                }
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
                resultDialog();
            }
        });
    }

    private void confirmRedPacket(String t_id) {
        if (bean == null) {
            ToastUtils.showShort(this, "不能打开红包");
            return;
        }
        Api.confirmRedPacket(sp.getString("uid"), t_id, "", new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                    if (bean == null) {
                        return;
                    }
                    startActivity(new Intent(WishingStarActivity.this, RedPacketDetailActivity.class).putExtra("bean", bean));
                } else {
                    ToastUtils.showShort(WishingStarActivity.this, apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
            }
        });
    }

    private void resultDialog() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_wishing);
        new AlertDialog.Builder(this)
                .setTitle("提示")
//                .setMessage("没有获得红包呦，继续加油吧！")
                .setView(imageView)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
