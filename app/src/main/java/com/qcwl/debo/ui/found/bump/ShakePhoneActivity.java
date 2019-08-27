package com.qcwl.debo.ui.found.bump;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.view.RoundedImageView;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.ui.found.bump.util.VibratorHelper;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.utils.ToastUtils;

public class ShakePhoneActivity extends BaseActivity {

    private SensorManager sensorManager;
    private SensorEventListener shakeListener;
    private boolean isRefresh = false;
    private String TAG = "ShakePhoneActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_phone);

        initTitleBar();
//        initShakeSensor();

    }

    private void initTitleBar() {
        new TitleBarBuilder(this)
                .setAlpha(1)
                .setTitle("撞一撞")
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initShakeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeListener = new ShakeSensorListener();
    }

    private RedPacketBean bean;
    private int mCode = 0;

    private void getAdRedPacket2() {
        Api.getAdRedPacket(sp.getString("uid"), 2, "", new ApiResponseHandler(this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                mCode = apiResponse.getCode();
                if (mCode == 0) {
                    bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                    if (bean == null) {
                        return;
                    }
                    new RedPacketDialog(ShakePhoneActivity.this
                            , bean.getT_id()
                            , bean.getAvatar()
                            , bean.getUser_nickname())
                            .show();
                    isRefresh = false;
                } else {
                    resultDialog();
                }
            }

            @Override
            public void onFailure(String errMessage) {
                super.onFailure(errMessage);
            }
        });
    }

    private void resultDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("没有撞到好友或红包！")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isRefresh = false;
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        initShakeSensor();
        sensorManager.registerListener(shakeListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // acitivity后台时取消监听
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    private void getJumpFriendOrRedPacket() {
        Api.getJumpFriendOrRedPacket(SPUtil.getInstance(this).getString("uid"), new ApiResponseHandler(ShakePhoneActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                JumpFriOrRPBean bean = JSON.parseObject(apiResponse.getData(), JumpFriOrRPBean.class);
                //撞一撞，撞到人
                if (apiResponse.getCode() == 0) {
                    frindfriend(bean);
                } else if (apiResponse.getCode() == 1) {//撞到红包
                    findredpacker(bean);
                } else {
                    ToastUtils.showShort(ShakePhoneActivity.this, "没有撞到好友或红包！");
                    isRefresh = false;
                }
            }
        });
    }

    //撞到好友
    private void frindfriend(final JumpFriOrRPBean bean) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend2, null);
        RoundedImageView image_view = (RoundedImageView) view.findViewById(R.id.image_view);
        TextView text_name = (TextView) view.findViewById(R.id.text_name);
        Button ben_addfriend = (Button) view.findViewById(R.id.ben_addfriend);
        final Dialog dialog = new Dialog(this, R.style.red_packet_dialog);

        view.findViewById(R.id.img_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRefresh = false;
                dialog.dismiss();
            }
        });
        ImgUtil.setGlideHead(this, bean.getAvatar(), image_view);
       /* Glide.with(this)
                .load(bean.getAvatar())
                .into(image_view);*/
        text_name.setText(bean.getUser_nickname());
        ben_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRefresh = false;
                addfriend(bean.getId());
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    //撞到红包
    private void findredpacker(final JumpFriOrRPBean bean) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_red_packet2, null);
        ImageView img_dismiss = (ImageView) view.findViewById(R.id.img_dismiss);
        ImageView image_view = (ImageView) view.findViewById(R.id.image_view);
        TextView text_name = (TextView) view.findViewById(R.id.text_name);
        ImageView image_open = (ImageView) view.findViewById(R.id.image_open);
        final Dialog dialog = new Dialog(this, R.style.red_packet_dialog);

        img_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRefresh = false;
                dialog.dismiss();
            }
        });

        ImgUtil.setGlideHead(this, ApiHttpClient.VIDEO_URL + bean.getAd_images(), image_view);

       /* Glide.with(this)
                .load(ApiHttpClient.VIDEO_URL+bean.getAd_images())
                .into(image_view);*/


        text_name.setText(bean.getTitle());
        image_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRefresh = false;
                confirmRedPacket(bean.getT_id());
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    //加好友
    private void addfriend(String oid) {
        Api.AddJumpFriend(SPUtil.getInstance(this).getString("uid"), oid, new ApiResponseHandler(ShakePhoneActivity.this) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (apiResponse.getCode() == 0) {
                    ToastUtils.showShort(ShakePhoneActivity.this, apiResponse.getMessage());
                } else {
                    ToastUtils.showShort(ShakePhoneActivity.this, apiResponse.getMessage());
                }
            }
        });
    }

    //打开红包
    private void confirmRedPacket(String t_id) {
        Api.confirmRedPacket(SPUtil.getInstance(this).getString("uid"),
                t_id, "", new ApiResponseHandler(this) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        mCode = apiResponse.getCode();
                        if (mCode == 0) {
                            RedPacketBean bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                            if (bean == null) {
                                return;
                            }
                            startActivity(new Intent(ShakePhoneActivity.this
                                    , RedPacketDetailActivity.class)
                                    .putExtra("bean", bean));
                        } else {
                            ToastUtils.showShort(ShakePhoneActivity.this, apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        super.onFailure(errMessage);
                    }
                });
    }

    private class ShakeSensorListener implements SensorEventListener {
        private static final int ACCELERATE_VALUE = 20;

        @Override
        public void onSensorChanged(SensorEvent event) {
//          Log.e("zhengyi.wzy", "type is :" + event.sensor.getType());

            // 判断是否处于刷新状态(例如微信中的查找附近人)
            if (isRefresh) {
                return;
            }
            float[] values = event.values;
            /**
             * 一般在这三个方向的重力加速度达到20就达到了摇晃手机的状态 x : x轴方向的重力加速度，向右为正 y :
             * y轴方向的重力加速度，向前为正 z : z轴方向的重力加速度，向上为正
             */
            float x = Math.abs(values[0]);
            float y = Math.abs(values[1]);
            float z = Math.abs(values[2]);
            Log.e("zhengyi.wzy", "x is :" + x + " y is :" + y + " z is :" + z);
            if (x >= ACCELERATE_VALUE || y >= ACCELERATE_VALUE
                    || z >= ACCELERATE_VALUE) {
//                Toast.makeText(ShakePhoneActivity.this,"accelerate speed :" + (x >= ACCELERATE_VALUE ? x : y >= ACCELERATE_VALUE ? y : z),Toast.LENGTH_SHORT).show();
                VibratorHelper.Vibrate(ShakePhoneActivity.this, 300);
                isRefresh = true;
//                getAdRedPacket2();
                getJumpFriendOrRedPacket();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

    }
}
