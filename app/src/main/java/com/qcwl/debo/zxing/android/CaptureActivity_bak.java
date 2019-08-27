//package com.qcwl.debo.zxing.android;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.DecodeHintType;
//import com.google.zxing.Result;
//import com.qcwl.debo.R;
//import com.qcwl.debo.chat.BaseActivity;
//import com.qcwl.debo.http.Api;
//import com.qcwl.debo.http.ApiResponse;
//import com.qcwl.debo.http.ApiResponseHandler;
//import com.qcwl.debo.model.QrCodeBeen;
//import com.qcwl.debo.ui.circle.ImgUtil;
//import com.qcwl.debo.ui.circle.ScreenUtils;
//import com.qcwl.debo.ui.contact.AddFriendsActivity;
//import com.qcwl.debo.ui.found.RedPacketBean;
//import com.qcwl.debo.ui.found.bump.RedPacketDetailActivity;
//import com.qcwl.debo.ui.found.bump.RedPacketDialog;
//import com.qcwl.debo.ui.login.ErWeiMaWebActivity;
//import com.qcwl.debo.utils.TitleBarBuilder;
//import com.qcwl.debo.utils.ToastUtils;
//import com.qcwl.debo.zxing.camera.CameraManager;
//import com.qcwl.debo.zxing.view.ViewfinderView;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Map;
//
//
///**
// * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
// * 然后在扫描成功的时候覆盖扫描结果
// */
//public final class CaptureActivity_bak extends BaseActivity implements SurfaceHolder.Callback {
//
//    private static final String TAG = CaptureActivity_bak.class.getSimpleName();
//    private LinearLayout linear;
//    private TextView tv;
//    // 相机控制
//    private CameraManager cameraManager;
//    private CaptureActivityHandler handler;
//    private ViewfinderView viewfinderView;
//    private boolean hasSurface;
//    private IntentSource source;
//    private Collection<BarcodeFormat> decodeFormats;
//    private Map<DecodeHintType, ?> decodeHints;
//    private String characterSet;
//    // 电量控制
//    private InactivityTimer inactivityTimer;
//    // 声音、震动控制
//    private BeepManager beepManager;
//
//    private int flag = 0;
//    private View linearLayout;
//    private ImageView imageCode;
//    private SurfaceView surfaceView;
//
//    private boolean isfirst = true;
//
//    public ViewfinderView getViewfinderView() {
//        return viewfinderView;
//    }
//
//    public Handler getHandler() {
//        return handler;
//    }
//
//    public CameraManager getCameraManager() {
//        return cameraManager;
//    }
//
//    public void drawViewfinder() {
//        viewfinderView.drawViewfinder();
//    }
//
//    /**
//     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
//     */
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//        // 保持Activity处于唤醒状态
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.capture);
//
//        hasSurface = false;
//
//        inactivityTimer = new InactivityTimer(this);
//        beepManager = new BeepManager(this);
//
//        new TitleBarBuilder(this).setTitle(getIntent().getStringExtra("title")).setImageLeftRes(R.mipmap.back).setLeftListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
//        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
//        linear = (LinearLayout) findViewById(R.id.linear);
//        tv = (TextView) findViewById(R.id.tv);
//
//        linearLayout = findViewById(R.id.linearlayout);
//        imageCode = (ImageView) findViewById(R.id.image_code);
//
//        //int space = ScreenUtils.getScreenWidth(this) / 4;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
//        params.leftMargin = ScreenUtils.dp2px(this, 50);
//        params.rightMargin = ScreenUtils.dp2px(this, 50);
//        imageCode.setLayoutParams(params);
//
//        if (getIntent() == null) {
//            return;
//        }
//        flag = getIntent().getIntExtra("flag", 1);
//        tv.setText(getIntent().getStringExtra("title"));
//
//        listener();
//    }
//
//    private void listener() {
//        if (flag == 2) {
//            linear.setVisibility(View.VISIBLE);
//            pauseData();
//            ImgUtil.load(this, sp.getString("qr_code"), imageCode);
//            findViewById(R.id.layout_bump).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //碰一碰
//                    linearLayout.setVisibility(View.GONE);
//                    viewfinderView.setVisibility(View.VISIBLE);
//                    resumeData();
//                }
//            });
//            findViewById(R.id.layout_code).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //我的二维码
//                    linearLayout.setVisibility(View.VISIBLE);
//                    viewfinderView.setVisibility(View.GONE);
//                    pauseData();
//                }
//            });
//        } else {
//            linear.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        resumeData();
//    }
//
//    private void resumeData() {
//        // CameraManager必须在这里初始化，而不是在onCreate()中。
//        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
//        // 当扫描框的尺寸不正确时会出现bug
//        cameraManager = new CameraManager(getApplication());
//
//        viewfinderView.setCameraManager(cameraManager);
//
//        handler = null;
//
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        if (hasSurface) {
//            // activity在paused时但不会stopped,因此surface仍旧存在；
//            // surfaceCreated()不会调用，因此在这里初始化camera
//            initCamera(surfaceHolder);
//            if (handler != null) {
//                handler.restartPreviewAndDecode();
//            }
//
//        } else {
//            // 防止sdk8的设备初始化预览异常
//            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//            // 重置callback，等待surfaceCreated()来初始化camera
//            surfaceHolder.addCallback(this);
//        }
//
//        beepManager.updatePrefs();
//        inactivityTimer.onResume();
//
//        source = IntentSource.NONE;
//        decodeFormats = null;
//        characterSet = null;
//    }
//
//    @Override
//    protected void onPause() {
//        pauseData();
//        super.onPause();
//    }
//
//    private void pauseData() {
//        if (handler != null) {
//            handler.quitSynchronously();
//            handler = null;
//        }
//        if (inactivityTimer != null) {
//            inactivityTimer.onPause();
//        }
//        if (beepManager != null) {
//            beepManager.close();
//        }
//        if (cameraManager != null) {
//            cameraManager.closeDriver();
//        }
//        if (!hasSurface) {
//            SurfaceHolder surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.removeCallback(this);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        inactivityTimer.shutdown();
//        super.onDestroy();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (!hasSurface) {
//            hasSurface = true;
//            initCamera(holder);
//            if (isfirst && flag == 2) {
//                viewfinderView.setVisibility(View.GONE);
//                linearLayout.setVisibility(View.VISIBLE);
//                isfirst = false;
//            }
//        }
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        hasSurface = false;
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width,
//                               int height) {
//
//    }
//
//    /**
//     * 扫描成功，处理反馈信息
//     *
//     * @param rawResult
//     * @param barcode
//     * @param scaleFactor
//     */
//    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
//        inactivityTimer.onActivity();
//        boolean fromLiveScan = barcode != null;
//        //这里处理解码完成后的结果，此处将参数回传到Activity处理
//        if (fromLiveScan) {
//            beepManager.playBeepSoundAndVibrate();
//            String json = rawResult.getText();
//            try {
//                QrCodeBeen been = JSON.parseObject(json, QrCodeBeen.class);
//                Intent intent = new Intent();
//                if ("user".equals(been.getType())) {
//                    if (flag == 1) {
//                        if ("register".equals(getIntent().getStringExtra("tip"))) {
//                            intent.putExtra("result", been.getParam());
//                            setResult(RESULT_OK, intent);
//                        } else if ("addFriend".equals(getIntent().getStringExtra("tip"))) {
//                            startActivity(new Intent(this, AddFriendsActivity.class).putExtra("content", been.getParam()));
//                        }
//                        finish();
//                    } else if (flag == 2) {
//                        mobile = been.getParam();
//                        getAdRedPacket(mobile);
//                    }
//                }
//            } catch (Exception e) {
//                startActivity(new Intent(this, ErWeiMaWebActivity.class).putExtra("url", json));
//                //finish();
//            }
//
//        }
//
//    }
//
//    private String mobile = "";
//
//    RedPacketBean bean;
//
//    private void getAdRedPacket(String mobile) {
//        Api.getAdRedPacket(sp.getString("uid"), 1, mobile, new ApiResponseHandler(this) {
//            @Override
//            public void onSuccess(ApiResponse apiResponse) {
//                if (apiResponse.getCode() == 0) {
//                    bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
//                    if (bean == null) {
//                        return;
//                    }
//                    new RedPacketDialog(CaptureActivity_bak.this
//                            , bean.getT_id()
//                            , bean.getAvatar()
//                            , bean.getUser_nickname())
//                            .show();
//                } else if (apiResponse.getCode() == -4 || apiResponse.getCode() == -6 || apiResponse.getCode() == -8) {
//                    resultDialog(apiResponse.getMessage());
//                } else {
//                    resultDialog("");
//                }
//            }
//
//            @Override
//            public void onFailure(String errMessage) {
//                super.onFailure(errMessage);
//            }
//        });
//    }
//
//    private void resultDialog(String message) {
//        if (TextUtils.isEmpty(message)) {
//            message = "没有获得红包呦，继续加油吧！";
//        }
//        new android.support.v7.app.AlertDialog.Builder(this)
//                .setTitle("提示")
//                .setMessage(message)
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .create().show();
//    }
//
//    private void confirmRedPacket(String t_id, String mobile) {
//        if (bean == null) {
//            ToastUtils.showShort(this, "不能打开红包");
//            return;
//        }
//        Api.confirmRedPacket(sp.getString("uid"), t_id, mobile, new ApiResponseHandler(this) {
//            @Override
//            public void onSuccess(ApiResponse apiResponse) {
//                if (apiResponse.getCode() == 0) {
//                    bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
//                    if (bean == null) {
//                        return;
//                    }
//                    startActivity(new Intent(CaptureActivity_bak.this, RedPacketDetailActivity.class).putExtra("bean", bean));
//                    finish();
//                } else {
//                    ToastUtils.showShort(CaptureActivity_bak.this, apiResponse.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(String errMessage) {
//                super.onFailure(errMessage);
//            }
//        });
//    }
//
//
//    /**
//     * 初始化Camera
//     *
//     * @param surfaceHolder
//     */
//    private void initCamera(SurfaceHolder surfaceHolder) {
//        if (surfaceHolder == null) {
//            throw new IllegalStateException("No SurfaceHolder provided");
//        }
//        if (cameraManager.isOpen()) {
//            return;
//        }
//        try {
//            // 打开Camera硬件设备
//            cameraManager.openDriver(surfaceHolder);
//            // 创建一个handler来打开预览，并抛出一个运行时异常
//            if (handler == null) {
//                handler = new CaptureActivityHandler(this, decodeFormats,
//                        decodeHints, characterSet, cameraManager);
//            }
//        } catch (IOException ioe) {
//            Log.w(TAG, ioe);
//            //displayFrameworkBugMessageAndExit();
//        } catch (RuntimeException e) {
//            Log.w(TAG, "Unexpected error initializing camera", e);
//            //displayFrameworkBugMessageAndExit();
//        }
//    }
//
//    /**
//     * 显示底层错误信息并退出应用
//     */
//    private void displayFrameworkBugMessageAndExit() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.app_name));
//        builder.setMessage(getString(R.string.msg_camera_framework_bug));
//        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
//        builder.setOnCancelListener(new FinishListener(this));
//        builder.show();
//    }
//
//}
