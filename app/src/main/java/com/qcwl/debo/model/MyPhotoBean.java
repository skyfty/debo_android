package com.qcwl.debo.model;

import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */

public class MyPhotoBean {

    private int circle_type;
    private String create_time;
    private List<String> images;
    private String lat;
    private String lng;
    private String moments_content;
    private String moments_id;
    private String moments_uid;
    private String position;
    private String type;
    private int is_both;//1、只是朋友圈或人脉圈 ； 不是1的话就是同时

    public int getIs_both() {
        return is_both;
    }

    public void setIs_both(int is_both) {
        this.is_both = is_both;
    }

    public int getCircle_type() {
        return circle_type;
    }

    public void setCircle_type(int circle_type) {
        this.circle_type = circle_type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getMoments_content() {
        return moments_content;
    }

    public void setMoments_content(String moments_content) {
        this.moments_content = moments_content;
    }

    public String getMoments_id() {
        return moments_id;
    }

    public void setMoments_id(String moments_id) {
        this.moments_id = moments_id;
    }

    public String getMoments_uid() {
        return moments_uid;
    }

    public void setMoments_uid(String moments_uid) {
        this.moments_uid = moments_uid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
