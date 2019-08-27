/**
 * Copyright 2018 bejson.com
 */
package com.qcwl.debo.model;

import java.io.Serializable;

/**
 * Auto-generated: 2018-09-19 14:5:4
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Joke_like_list implements Serializable {

    private String id;
    private String uid;
    private String video_url;
    private String img_url;
    private String time;
    private String lat;
    private String lng;
    private String city;
    private String share_video_record_num;
    private String comment_num;
    private String upvote_num;
    private int is_upvote;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLng() {
        return lng;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setShare_video_record_num(String share_video_record_num) {
        this.share_video_record_num = share_video_record_num;
    }

    public String getShare_video_record_num() {
        return share_video_record_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setUpvote_num(String upvote_num) {
        this.upvote_num = upvote_num;
    }

    public String getUpvote_num() {
        return upvote_num;
    }

    public void setIs_upvote(int is_upvote) {
        this.is_upvote = is_upvote;
    }

    public int getIs_upvote() {
        return is_upvote;
    }

}