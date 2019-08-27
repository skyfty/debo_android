package com.qcwl.debo.ui.found.fans;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView 分割线
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int orientation;

    public MyItemDecoration(int orientation, int space) {
        this.orientation = orientation;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super.getItemOffsets(outRect, view, parent, state);
        if (orientation == 0) {//垂直方向
            outRect.set(0, 0, 0, space);
        } else if (orientation == 1) {//水平方向
            outRect.set(space, 0, space, 0);
        }
    }
}
