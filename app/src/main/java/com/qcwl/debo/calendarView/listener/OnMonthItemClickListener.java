package com.qcwl.debo.calendarView.listener;

import android.view.View;

import com.qcwl.debo.calendarView.DateBean;


/**
 * 日期点击接口
 */
public interface OnMonthItemClickListener {
    /**
     * @param view
     * @param date
     */
    void onMonthItemClick(View view, DateBean date);
}
