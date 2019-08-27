package com.qcwl.debo.ui.found.fans.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansHomeDataBean {

    private String user_nickname;
    private String mobile;
    private int fans_num;
    private List<FansHomeBean> trends_list;

    public int getFans_num() {
        return fans_num;
    }

    public void setFans_num(int fans_num) {
        this.fans_num = fans_num;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public List<FansHomeBean> getTrends_list() {
        return trends_list;
    }

    public void setTrends_list(List<FansHomeBean> trends_list) {
        this.trends_list = trends_list;
    }
}
