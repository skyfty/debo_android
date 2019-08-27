package com.qcwl.debo.ui.found.bump;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.baidu.mobstat.StatService;
import com.qcwl.debo.R;
import com.qcwl.debo.chat.BaseActivity;
import com.qcwl.debo.utils.TitleBarBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BumpActivity extends BaseActivity {
    @Bind(R.id.checkbox_single)
    CheckBox mCheckboxSingle;
    @Bind(R.id.checkbox_double)
    CheckBox mCheckboxDouble;


//    @Bind(R.id.image_gif)
//    ImageView imageGif;
//    @Bind(R.id.view_start)
//    View viewStart;
//    @Bind(R.id.animation_view)
//    LottieAnimationView animationView;
//    @Bind(R.id.relative_layout)
//    RelativeLayout relativeLayout;

    private int mDuration = 0;
    private int duration = 0;
    //    private AnimationDrawable anim, animationDrawable;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bump);
        ButterKnife.bind(this);
        initTitleBar();
//        listener();


    }

//    private void listener() {
//        //750px ＊　805px
//        int height = (int) (ScreenUtils.getScreenWidth(this) / 750.0 * 805);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, height);
//        params.topMargin = ScreenUtils.dp2px(this, 70);
//        relativeLayout.setLayoutParams(params);
//        animationView.setImageAssetsFolder("images");
//        animationView.setAnimation("bump.json");
//        animationView.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                StatService.onEvent(BumpActivity.this, "撞一撞页面", "撞一撞事件");
//                viewStart.setClickable(false);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                getAdRedPacket2();
//                animationView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        animationView.setProgress(0);
//                        viewStart.setClickable(true);
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        viewStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                animationView.playAnimation();
//            }
//        });
//    }


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
        StatService.onPageStart(this, "启动撞一撞页面");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, "结束撞一撞页面");
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

    @OnClick({R.id.checkbox_single, R.id.checkbox_double})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkbox_single:
                isCheckBox(mCheckboxSingle);
                mCheckboxDouble.setChecked(false);
                break;
            case R.id.checkbox_double:
                isCheckBox(mCheckboxDouble);
                mCheckboxSingle.setChecked(false);
                break;
        }
    }

    private void isCheckBox(CheckBox checkBox){
        if (checkBox.isChecked()) {
            startActivity(new Intent(this,ShakePhoneActivity.class));
        }
    }
}
