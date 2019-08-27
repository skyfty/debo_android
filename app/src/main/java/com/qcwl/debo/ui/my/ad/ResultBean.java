package com.qcwl.debo.ui.my.ad;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ResultBean {
    private String touch_num;
    private List<AdvertisementBean> touch;

    public String getTouch_num() {
        return touch_num;
    }

    public void setTouch_num(String touch_num) {
        this.touch_num = touch_num;
    }

    public List<AdvertisementBean> getTouch() {
        return touch;
    }

    public void setTouch(List<AdvertisementBean> touch) {
        this.touch = touch;
    }
}
