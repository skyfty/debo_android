package com.qcwl.debo.zxing.android.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.baidu.mobstat.StatService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.qcwl.debo.R;
import com.qcwl.debo.http.Api;
import com.qcwl.debo.http.ApiResponse;
import com.qcwl.debo.http.ApiResponseHandler;
import com.qcwl.debo.model.QrCodeBeen;
import com.qcwl.debo.ui.contact.AddFriendsActivity;
import com.qcwl.debo.ui.found.RedPacketBean;
import com.qcwl.debo.ui.found.bump.RedPacketDialog;
import com.qcwl.debo.ui.login.ErWeiMaWebActivity;
import com.qcwl.debo.utils.SPUtil;
import com.qcwl.debo.zxing.android.BeepManager;
import com.qcwl.debo.zxing.android.CaptureActivity;
import com.qcwl.debo.zxing.android.CaptureActivityHandler;
import com.qcwl.debo.zxing.android.InactivityTimer;
import com.qcwl.debo.zxing.android.IntentSource;
import com.qcwl.debo.zxing.camera.CameraManager;
import com.qcwl.debo.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * 二维码扫描界面
 */
public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = "CaptureFragment";

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    // 相机控制
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    // 电量控制
    private InactivityTimer inactivityTimer;
    // 声音、震动控制
    private BeepManager beepManager;

    private int flag = 0;

    private String tip="";

    public CaptureFragment() {
        // Required empty public constructor
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        initView(view);
        StatService.onPageStart(getActivity(),"启动瞅一瞅页面");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments()!=null) {
            tip=getArguments().getString("tip");
            flag=getArguments().getInt("flag",1);
        }
    }

    private void initView(View view) {
        hasSurface = false;

        inactivityTimer = new InactivityTimer(getActivity());
        beepManager = new BeepManager(getActivity());

        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StatService.onPageEnd(getActivity(),"结束瞅一瞅页面");
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
        resumeData();
    }

    private void resumeData() {
        // CameraManager必须在这里初始化，而不是在onCreate()中。
        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
        // 当扫描框的尺寸不正确时会出现bug
        cameraManager = new CameraManager(getActivity());

        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
            if (handler != null) {
                handler.restartPreviewAndDecode();
            }

        } else {
            // 防止sdk8的设备初始化预览异常
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    public void onPause() {
        pauseData();
        StatService.onPause(this);
        super.onPause();
    }

    private void pauseData() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (inactivityTimer != null) {
            inactivityTimer.onPause();
        }
        if (beepManager != null) {
            beepManager.close();
        }
        if (cameraManager != null) {
            cameraManager.closeDriver();
        }
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }


    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        boolean fromLiveScan = barcode != null;
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
        if (fromLiveScan) {
            beepManager.playBeepSoundAndVibrate();
            String json = rawResult.getText();
            try {
                QrCodeBeen been = JSON.parseObject(json, QrCodeBeen.class);
                Intent intent = new Intent();
                if ("user".equals(been.getType())) {
                    if (flag == 1) {
                        if ("register".equals(tip)) {
                            intent.putExtra("result", been.getParam());
                            getActivity().setResult(RESULT_OK, intent);
                        } else if ("addFriend".equals(tip)) {
                            startActivity(new Intent(getActivity(), AddFriendsActivity.class).putExtra("content", been.getParam()));
                        }
                        getActivity().finish();
                    } else if (flag == 2) {
                        mobile = been.getParam();
                        getAdRedPacket(mobile);
                    }
                }
            } catch (Exception e) {
                startActivity(new Intent(getActivity(), ErWeiMaWebActivity.class).putExtra("url", json));
                //finish();
            }

        }

    }

    private String mobile = "";

    RedPacketBean bean;

    private void getAdRedPacket(String mobile) {
        Api.getAdRedPacket(SPUtil.getInstance(getActivity()).getString("uid"), 1, mobile,
                new ApiResponseHandler(getActivity()) {
                    @Override
                    public void onSuccess(ApiResponse apiResponse) {
                        if (apiResponse.getCode() == 0) {
                            bean = JSON.parseObject(apiResponse.getData(), RedPacketBean.class);
                            if (bean == null) {
                                return;
                            }
                            new RedPacketDialog(getActivity()
                                    , bean.getT_id()
                                    , bean.getAvatar()
                                    , bean.getUser_nickname())
                                    .show();
                        } else if (apiResponse.getCode() == -4 || apiResponse.getCode() == -6 || apiResponse.getCode() == -8) {
                            resultDialog(apiResponse.getMessage());
                        } else {
                            resultDialog("");
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        super.onFailure(errMessage);
                    }
                });
    }

    private void resultDialog(String message) {
        if (TextUtils.isEmpty(message)) {
            message = "没有获得红包呦，继续加油吧！";
        }
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                })
                .create().show();
    }

    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(((CaptureActivity) getActivity()), decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            //displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            //displayFrameworkBugMessageAndExit();
        }
    }
}
