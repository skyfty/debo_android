package com.qcwl.debo.ui.my.sign;

import java.util.List;

/**
 * Created by AlMn on 2018/1/16.
 */

public class SignResult {

//    private String[] total_num_format;
    private SignBaseInfo base_info;
    private List<SignBean> daily_sign_record;

//    public String[] getTotal_num_format() {
//        return total_num_format;
//    }
//
//    public void setTotal_num_format(String[] total_num_format) {
//        this.total_num_format = total_num_format;
//    }

    private String year;
    private String month;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public SignBaseInfo getBase_info() {
        return base_info;
    }

    public void setBase_info(SignBaseInfo base_info) {
        this.base_info = base_info;
    }

    public List<SignBean> getDaily_sign_record() {
        return daily_sign_record;
    }

    public void setDaily_sign_record(List<SignBean> daily_sign_record) {
        this.daily_sign_record = daily_sign_record;
    }
}
