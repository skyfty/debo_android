package com.qcwl.debo.ui.my.sign;

/**
 * Created by AlMn on 2018/1/16.
 */

public class SignBaseInfo {

    /**
     * total_sign_num : 1
     * curr_month_sign_num : 1
     * points : 0
     */

    private String total_sign_num;
    private String curr_month_sign_num;
    private String points;

    public void setTotal_sign_num(String total_sign_num) {
        this.total_sign_num = total_sign_num;
    }

    public void setCurr_month_sign_num(String curr_month_sign_num) {
        this.curr_month_sign_num = curr_month_sign_num;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTotal_sign_num() {
        return total_sign_num;
    }

    public String getCurr_month_sign_num() {
        return curr_month_sign_num;
    }

    public String getPoints() {
        return points;
    }
}
