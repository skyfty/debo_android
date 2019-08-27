package com.qcwl.debo.ui.my.ad;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class AdvertisementBean {

    /**
     * t_id : 101
     * uid : 18
     * title : 撞一撞
     * ad_link :
     * price : 100.00
     * p_num : 20
     * p_time : 1501557704
     * ad_content : 撞一撞，撞出惊喜
     * ad_images : ["http://123.57.148.47/debo/data/upload/advertisement/advertisement_36/597ffd077b0ea5.79682051.jpeg","http://123.57.148.47/debo/data/upload/advertisement/advertisement_36/597ffd077b0ea7.85875090.jpg","http://123.57.148.47/debo/data/upload/advertisement/advertisement_36/597ffd077edf32.59148965.png"]
     * ad_type : 2
     * remaining_sum : 96.20
     * remain_num : 19
     * time_range : 1504972800
     * start_time : 1501603200
     * p_status : 3
     */
    private boolean visible;
    private boolean checked;
    private String t_id;
    private String uid;
    private String title;
    private String ad_link;
    private String price;
    private String p_num;
    private String p_time;
    private String ad_content;
    private int ad_type;
    private String remaining_sum;
    private String remain_num;
    private String time_range;
    private String start_time;
    private String p_status;
    private List<String> ad_images;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getP_num() {
        return p_num;
    }

    public void setP_num(String p_num) {
        this.p_num = p_num;
    }

    public String getP_time() {
        return p_time;
    }

    public void setP_time(String p_time) {
        this.p_time = p_time;
    }

    public String getAd_content() {
        return ad_content;
    }

    public void setAd_content(String ad_content) {
        this.ad_content = ad_content;
    }

    public int getAd_type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public String getRemaining_sum() {
        return remaining_sum;
    }

    public void setRemaining_sum(String remaining_sum) {
        this.remaining_sum = remaining_sum;
    }

    public String getRemain_num() {
        return remain_num;
    }

    public void setRemain_num(String remain_num) {
        this.remain_num = remain_num;
    }

    public String getTime_range() {
        return time_range;
    }

    public void setTime_range(String time_range) {
        this.time_range = time_range;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public List<String> getAd_images() {
        return ad_images;
    }

    public void setAd_images(List<String> ad_images) {
        this.ad_images = ad_images;
    }
}
