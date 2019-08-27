package com.qcwl.debo.ui.found.near.bean;

/**
 * Created by Administrator on 2017/8/1.
 */

public class TrumpetBean {


    /**
     * horn_id : 4
     * horn_name : 600米以内喊话
     * horn_price : 60.00
     * is_free : 1
     * is_purchase : 0
     */
    private int imgId;
    private String horn_id;
    private int distance;
    private String horn_price;
    private int is_free;
    private int is_purchase;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getHorn_id() {
        return horn_id;
    }

    public void setHorn_id(String horn_id) {
        this.horn_id = horn_id;
    }


    public String getHorn_price() {
        return horn_price;
    }

    public void setHorn_price(String horn_price) {
        this.horn_price = horn_price;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public int getIs_purchase() {
        return is_purchase;
    }

    public void setIs_purchase(int is_purchase) {
        this.is_purchase = is_purchase;
    }
}
