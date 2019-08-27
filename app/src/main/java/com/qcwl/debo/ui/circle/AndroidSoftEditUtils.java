package com.qcwl.debo.ui.circle;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/3/16.
 */

public class AndroidSoftEditUtils {
    public static void init(Activity activity){
        new AndroidSoftEditUtils(activity);
    }
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams layoutParams;

    private AndroidSoftEditUtils(Activity activity){
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        layoutParams = (FrameLayout.LayoutParams)mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent(){
        int usableHeightNow = computeUsableHeight();
        if(usableHeightNow !=usableHeightPrevious){
            int height = mChildOfContent.getRootView().getHeight();
            int heightDifference = height-usableHeightNow;
            if(heightDifference>(height/4)){
                layoutParams.height = height-heightDifference;
            }else {
                layoutParams.height = height;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight(){
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }
}
