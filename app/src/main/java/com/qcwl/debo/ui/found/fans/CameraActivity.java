package com.qcwl.debo.ui.found.fans;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.lisenter.JCameraLisenter;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.PublishCircleActivity;
import com.qcwl.debo.ui.found.jinshanyun.ReleasePassageActivity;

import java.io.File;

public class CameraActivity extends BaseActivity {//AppCompatActivity {
    private JCameraView jCameraView;

    private int is_cycle=0;
    public static final int CODE_CAMERA = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        if (getIntent()==null) {
            return;
        }
        is_cycle=getIntent().getIntExtra("is_cycle",0);
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);//BUTTON_STATE_BOTH
        jCameraView.setTip("长按拍摄视频");
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_LOW);//MEDIA_QUALITY_MIDDLE
        jCameraView.setErrorLisenter(new ErrorLisenter() {
            @Override
            public void onError() {
                //错误监听
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "没有录音权限", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraLisenter() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                //String path = FileUtil.saveBitmap("JCamera", bitmap);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                String thumbPath = FileUtil.saveBitmap("JCamera", firstFrame);
                Intent intent = new Intent();
                if (is_cycle==1) {
                    intent.setClass(CameraActivity.this,PublishCircleActivity.class);
                }else if(is_cycle == 2){
//                    ToastUtils.showShort(CameraActivity.this,"进入段子录制视频界面");
                    intent.setClass(CameraActivity.this,ReleasePassageActivity.class);
                }else{
                    intent.setClass(CameraActivity.this,PublishFansDynamicActivity.class);
                }
                intent.putExtra("publishType",1);
                intent.putExtra("thumbPath", thumbPath);
                intent.putExtra("videoPath",url);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void quit() {
                //退出按钮
                CameraActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        Log.i("CJT", "onResume");
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("CJT", "onPause");
        super.onPause();
        jCameraView.onPause();
    }
}