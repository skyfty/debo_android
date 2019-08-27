package com.qcwl.debo.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.qcwl.debo.R;

/**
 * Created by AlMn on 2016/10/31 031.
 */

public class ListUtils {

    //注释加了emptyview后列表数据为空时无法下拉刷新
    public static void setEmptyView(Context context, AbsListView view, LinearLayout emptyView) {
        //LinearLayout emptyView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_emptyview_bankcard, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) view.getParent()).addView(emptyView);
        view.setEmptyView(emptyView);
    }

}
