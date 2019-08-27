package com.qcwl.debo.calendarView.listener;

/**
 * 页面切换接口
 */
public interface OnPagerChangeListener {
    /**
     * @param date date[0]年份  date[1]月份  date[2]日
     */
    void onPagerChanged(int[] date);
}
