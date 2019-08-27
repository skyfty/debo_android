package com.qcwl.debo.ui.my.sign;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by AlMn on 2018/3/6.
 */

public class A {

    public static void main(String[] args){
        Locale.setDefault(Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(day_of_week);
    }

}
