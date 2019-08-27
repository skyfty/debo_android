package com.qcwl.debo.zxing.android;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.zxing.Result;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;
import com.qcwl.debo.zxing.android.fragment.CaptureFragment;
import com.qcwl.debo.zxing.android.fragment.ImageFragment;
import com.qcwl.debo.zxing.camera.CameraManager;
import com.qcwl.debo.zxing.view.ViewfinderView;

import java.util.ArrayList;
import java.util.List;


/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public final class CaptureActivity extends BaseActivity {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private LinearLayout linear;
    //private TextView tv;

    private int flag = 0;

    private String tip = "";

    private List<Fragment> fragments;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public ViewfinderView getViewfinderView() {
        CaptureFragment fragment = null;
        if (fragments != null) {
            fragment = (CaptureFragment) fragments.get(0);
            return fragment.getViewfinderView();
        }
        return null;
    }

    public void drawViewfinder() {
        CaptureFragment fragment = null;
        if (fragments != null) {
            fragment = (CaptureFragment) fragments.get(0);
            fragment.drawViewfinder();
        }
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        CaptureFragment fragment = null;
        if (fragments != null) {
            fragment = (CaptureFragment) fragments.get(0);
            fragment.handleDecode(rawResult, barcode, scaleFactor);
        }
    }

    public CameraManager getCameraManager() {
        CaptureFragment fragment = null;
        if (fragments != null) {
            fragment = (CaptureFragment) fragments.get(0);
            return fragment.getCameraManager();
        }
        return null;
    }

    public Handler getHandler() {
        CaptureFragment fragment = null;
        if (fragments != null) {
            fragment = (CaptureFragment) fragments.get(0);
            return fragment.getHandler();
        }
        return null;
    }

    @Override
    public void onCreate(Bundle args) {
        super.onCreate(args);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);

        linear = (LinearLayout) findViewById(R.id.linear);
        //tv = (TextView) findViewById(R.id.tv);

        if (getIntent() == null) {
            return;
        }
        flag = getIntent().getIntExtra("flag", 1);
        //tv.setText(getIntent().getStringExtra("title"));
        tip = getIntent().getStringExtra("tip");
        new TitleBarBuilder(this)
                .setTitle(getIntent().getStringExtra("title"))
                .setImageLeftRes(R.mipmap.back)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        CaptureFragment captureFragment = new CaptureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tip", tip);
        bundle.putInt("flag", flag);
        captureFragment.setArguments(bundle);

        ImageFragment imageFragment = new ImageFragment();

        fragments = new ArrayList<>();
        fragments.add(captureFragment);
        fragments.add(imageFragment);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        //ft.add(R.id.frame_layout, captureFragment);
        //ft.add(R.id.frame_layout, imageFragment);

        if (flag == 2) {
            //ft.show(fragments.get(1)).hide(fragments.get(0)).commit();
            ft.replace(R.id.frame_layout, fragments.get(1)).commit();
        } else {
            //ft.show(fragments.get(0)).hide(fragments.get(1)).commit();
            ft.replace(R.id.frame_layout, fragments.get(0)).commit();
        }

        listener();
    }



    private void listener() {
        if (flag == 2) {
            linear.setVisibility(View.VISIBLE);

            findViewById(R.id.layout_bump).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //碰一碰
                    ft = fm.beginTransaction();
                    //ft.show(fragments.get(0)).hide(fragments.get(1)).commit();
                    ft.replace(R.id.frame_layout, fragments.get(0)).commit();

                }
            });
            findViewById(R.id.layout_code).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //我的二维码
                    ft = fm.beginTransaction();
                    //ft.show(fragments.get(1)).hide(fragments.get(0)).commit();
                    ft.replace(R.id.frame_layout, fragments.get(1)).commit();
                }
            });
        } else {
            linear.setVisibility(View.GONE);
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

}
