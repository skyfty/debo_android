package com.qcwl.debo.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qcwl.debo.R;


/**
 * Created by Administrator on 2017/6/9.
 */

public class TitleBarBuilder {
    protected RelativeLayout leftLayout, root;
    protected ImageView leftImage;
    private TextView leftTv;
    protected RelativeLayout rightLayout;
    protected ImageView rightImage;
    private TextView rightTv;
    protected TextView titleView;
    protected RelativeLayout titleLayout;
    private ProgressBar progressbar;

    public TitleBarBuilder(Activity context) {
        root = (RelativeLayout) context.findViewById(R.id.root);
        leftLayout = (RelativeLayout) context.findViewById(R.id.left_layout);
        leftImage = (ImageView) context.findViewById(R.id.left_image);
        leftTv = (TextView) context.findViewById(R.id.left_tv);
        rightLayout = (RelativeLayout) context.findViewById(R.id.right_layout);
        rightImage = (ImageView) context.findViewById(R.id.right_image);
        rightTv = (TextView) context.findViewById(R.id.right_tv);
        titleView = (TextView) context.findViewById(R.id.title);
        progressbar = (ProgressBar) context.findViewById(R.id.progressbar);
        titleLayout = (RelativeLayout) context.findViewById(R.id.root);
        setAlpha(1);
    }

    public TitleBarBuilder(View context) {
        root = (RelativeLayout) context.findViewById(R.id.root);
        leftLayout = (RelativeLayout) context.findViewById(R.id.left_layout);
        leftImage = (ImageView) context.findViewById(R.id.left_image);
        leftTv = (TextView) context.findViewById(R.id.left_tv);
        rightLayout = (RelativeLayout) context.findViewById(R.id.right_layout);
        rightImage = (ImageView) context.findViewById(R.id.right_image);
        rightTv = (TextView) context.findViewById(R.id.right_tv);
        titleView = (TextView) context.findViewById(R.id.title);
        titleLayout = (RelativeLayout) context.findViewById(R.id.root);
        progressbar = (ProgressBar) context.findViewById(R.id.progressbar);
        setAlpha(1);
    }

    public TitleBarBuilder setTitle(String str) {
        titleView.setText(str);
        return this;
    }

    public TitleBarBuilder setTextLeft(String str) {
        leftTv.setVisibility(View.VISIBLE);
        leftLayout.setVisibility(View.VISIBLE);
        leftTv.setText(str);
        return this;
    }

    public TitleBarBuilder setTextRight(String str) {
        rightTv.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        rightTv.setText(str);
        return this;
    }

    public TitleBarBuilder setImageLeftRes(int res) {
        leftImage.setVisibility(View.VISIBLE);
        leftLayout.setVisibility(View.VISIBLE);
        leftImage.setImageResource(res);
        return this;
    }

    public TitleBarBuilder setImageRightRes(int res) {
        rightImage.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        rightImage.setImageResource(res);
        return this;
    }
    public void setImageRightGone() {
       rightImage.setVisibility(View.GONE);
    }

    public TitleBarBuilder setLeftListener(View.OnClickListener listener) {
        if (leftTv.getVisibility() == View.VISIBLE || leftImage.getVisibility() == View.VISIBLE) {
            leftLayout.setOnClickListener(listener);
        }
        return this;
    }

    public TitleBarBuilder setRightListener(View.OnClickListener listener) {
        if (rightTv.getVisibility() == View.VISIBLE || rightImage.getVisibility() == View.VISIBLE) {
            rightLayout.setOnClickListener(listener);
        }
        return this;
    }

    public TitleBarBuilder setTitleBarBackGround(int res) {
        root.setBackgroundResource(res);
        return this;
    }

    public TitleBarBuilder setAlpha(float alpha) {
        root.getBackground().setAlpha((int) (alpha*255));
        return this;
    }

    public TitleBarBuilder setProgress(int visibility) {
        progressbar.setVisibility(visibility);
        return this;
    }
}
