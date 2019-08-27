package com.qcwl.debo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.qcwl.debo.R;


public class SharerightDialog extends TopBaseDialog<SharerightDialog> implements View.OnClickListener {

    private Context context;
    private LinearLayout agency_task,data_statistics,quick_memory;
    private MyOnclickListener myOnclickListener;
    private int type;
    public SharerightDialog(Context context, View animateView) {
        super(context, animateView);
        this.context = context;
    }

    public SharerightDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void setMyOnclickListener(MyOnclickListener myOnclickListener){
        this.myOnclickListener = myOnclickListener;
    }

    public void setType(int type){
        this.type = type;
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter2());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_more_right, null);
        agency_task = (LinearLayout) inflate.findViewById(R.id.agency_task);
        data_statistics = (LinearLayout) inflate.findViewById(R.id.data_statistics);
        quick_memory = (LinearLayout) inflate.findViewById(R.id.quick_memory);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        data_statistics.setOnClickListener(this);
        agency_task.setOnClickListener(this);
        quick_memory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agency_task:
                myOnclickListener.onClick(6);
                break;
            case R.id.data_statistics:
                myOnclickListener.onClick(7);
                break;
            case R.id.quick_memory:
                myOnclickListener.onClick(8);
                break;
        }
        dismiss();
    }

    public interface MyOnclickListener{
        void onClick(int i);
    }

    private class FlipVerticalSwingEnter2 extends com.qcwl.debo.widget.BaseAnimatorSet {
        @Override
        public void setAnimation(View view) {
            animatorSet.playTogether(//
                    ObjectAnimator.ofFloat(view, "rotationX", 90, 0, 0, 0),//
                    ObjectAnimator.ofFloat(view, "alpha", 0.15f, 0.25f, 0.35f, 0.45f, 0.55f, 0.65f, 0.75f, 0.85f, 0.95f, 1));
        }
    }
}
