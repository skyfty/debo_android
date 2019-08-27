package com.qcwl.debo.ui.found.joke.video_new;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.qcwl.debo.R;
import com.qcwl.debo.http.ApiHttpClient;
import com.qcwl.debo.ui.circle.ScreenUtils;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class ViewPagerAdapter/* extends PagerAdapter */{
 /*   private Context context;
    private String[] s;
    private JZVideoPlayerStandardLoopVideo videoPlayer;

    public ViewPagerAdapter(Application application, String[] s) {
        this.context = application;
        this.s = s;
    }

    @Override
    public int getCount() {
        return s.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_guide_video, null);
        Log.i("ViewPagerAdapter", "............VIEW=" + view);
        videoPlayer = (JZVideoPlayerStandardLoopVideo) view.findViewById(R.id.videoPlayer);
        Log.i("ViewPagerAdapter", "............videoPlayer=" + videoPlayer);
        playVideo(ApiHttpClient.VIDEO_URL + s[position]);
        container.addView(videoPlayer);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void playVideo(String url) {
        Log.i("ViewPagerAdapter", "............url=" + url + "　　　" + videoPlayer);
        videoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        videoPlayer.startButton.setVisibility(View.GONE);
        videoPlayer.backButton.setVisibility(View.GONE);
        videoPlayer.fullscreenButton.setVisibility(View.GONE);
        videoPlayer.progressBar.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(ApiHttpClient.VIDEO_URL + "/data/upload/joke_video/17977/shipin(1742).png")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        videoPlayer.thumbImageView.setImageBitmap(resource);
                        int width = ScreenUtils.getScreenWidth(context);
                        int height = (int) (1.0f * width * resource.getHeight() / resource.getWidth());
                        videoPlayer.setLayoutParams(new LinearLayout.LayoutParams(-1, height));
                    }
                });
    }*/
}
