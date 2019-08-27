package com.qcwl.debo.view;

import android.animation.ObjectAnimator;
import android.view.View;

import com.qcwl.debo.widget.BaseAnimatorSet;


public class FlipVerticalSwingEnter extends BaseAnimatorSet {
    public FlipVerticalSwingEnter() {
        duration = 1000;
    }

    @Override
    public void setAnimation(View view) {
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(view, "rotationX", 90, 0, 0, 0),//
                ObjectAnimator.ofFloat(view, "alpha", 0.15f, 0.25f, 0.35f, 0.45f, 0.55f, 0.65f, 0.75f, 0.85f, 0.95f, 1));
    }
}
