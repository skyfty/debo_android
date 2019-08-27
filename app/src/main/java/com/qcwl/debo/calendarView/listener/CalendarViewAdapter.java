package com.qcwl.debo.calendarView.listener;

import android.view.View;
import android.widget.TextView;

import com.qcwl.debo.calendarView.DateBean;


public interface CalendarViewAdapter {
    /**
     * 返回阳历、阴历两个TextView
     *
     * @param view
     * @param date
     * @return
     */
    TextView[] convertView(View view, DateBean date);
}
