package com.qcwl.debo.view;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.View;

import com.qcwl.debo.widget.BaseAnimatorSet;


public class BounceTopEnter2 extends BaseAnimatorSet {
	public BounceTopEnter2() {
		duration = 1000;
	}

	@Override
	public void setAnimation(View view) {
		DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0, 1, 1, 1),//
				ObjectAnimator.ofFloat(view, "translationX",  30,-250 , -10, 0));
	}
}
