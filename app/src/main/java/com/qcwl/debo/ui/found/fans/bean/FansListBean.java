package com.qcwl.debo.ui.found.fans.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansListBean {

    private String fans_num;
    private List<FansBean> fans_list;

    public String getFans_num() {
        return fans_num;
    }

    public void setFans_num(String fans_num) {
        this.fans_num = fans_num;
    }

    public List<FansBean> getFans_list() {
        return fans_list;
    }

    public void setFans_list(List<FansBean> fans_list) {
        this.fans_list = fans_list;
    }
}
