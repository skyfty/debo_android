package com.qcwl.debo.ui.circle;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoActivity extends BaseActivity{//AppCompatActivity

    @Bind(R.id.video_player)
    JCVideoPlayerStandard videoPlayer;

    private String video_path, video_img, video_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        initData();
    }

    private void initData() {
        if (getIntent() == null) {
            return;
        }
        video_img = getIntent().getStringExtra("video_img");
        video_path = getIntent().getStringExtra("video_path");
        video_desc = getIntent().getStringExtra("video_desc");
//        video_path = "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";
//        video_img = "http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640";
        videoPlayer.setUp(video_path, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, video_desc);
        ImgUtil.load(this, video_img, videoPlayer.thumbImageView);
        videoPlayer.startButton.setImageResource(R.mipmap.play_button);
        videoPlayer.backButton.setVisibility(View.VISIBLE);
        videoPlayer.startVideo();
        videoPlayer.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        videoPlayer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

}
