package com.qcwl.debo.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.ui.circle.ImgUtil;
import com.qcwl.debo.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/8/31.
 */
public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.banner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.imageView)
    ImageView imageView;

    private List<Integer> images;
    private SPUtil sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ButterKnife.bind(this);
      /*  imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        convenientBanner.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        initData();
        Handler handler = new Handler();
        sp = SPUtil.getInstance(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               /* if (sp.isFirst()) {
                 // imageView.setVisibility(View.GONE);
                    //设置播放加载路径
                    imageView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sss));
                    //播放
                    imageView.start();
                    //循环播放
                    imageView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            imageView.start();
                        }
                    });

                    sp.setFirst(false);
                } else {
                    startNewPage();
                }*/

                if (sp.isFirst()) {
                    imageView.setVisibility(View.GONE);
                    sp.setFirst(false);
                } else {
                    startNewPage();
                }
            }
        }, 1000);
    }

    private void startNewPage() {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }

    private void initData() {
        images = new ArrayList<>();
        images.add(R.mipmap.app_guide_01);
        images.add(R.mipmap.app_guide_02);
        images.add(R.mipmap.app_guide_03);
        convenientBanner.setPages(
                new CBViewHolderCreator<ImageHolderView>() {
                    @Override
                    public ImageHolderView createHolder() {
                        return new ImageHolderView();
                    }
                }, images);
        convenientBanner.setCanLoop(false);
        convenientBanner.setManualPageable(true);
        convenientBanner.getViewPager().setOffscreenPageLimit(3);
    }

    public class ImageHolderView implements Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Integer data) {
            ImgUtil.load(context, data, imageView);
            if (position == images.size() - 1) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startNewPage();
                    }
                });
            }
        }
    }
}
