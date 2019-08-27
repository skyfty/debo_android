package com.qcwl.debo.view;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/1/10.
 */

public class TimeUtils {

    public static String formatTimeByDay(long s) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern("d");
        Timestamp date = new Timestamp(s*1000);
        return dateFormat.format(date);
    }

    public static String formatTimeByMonth(long s) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern("M");
        Timestamp date = new Timestamp(s*1000);
        return dateFormat.format(date);
    }

    public static String formatTimeByYear(long s) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern("y");
        Timestamp date = new Timestamp(s * 1000);
        return dateFormat.format(date);
    }
}
