package com.qcwl.debo.ui.found.fans.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansDynamicBean {

    private int is_upvote;//1点赞
    private String moments_id;
    private String moments_uid;
    private String moments_content;
    private String create_time;
    private String position;
    private String lat;
    private String lng;
    private String user_nickname;
    private String avatar;
    private List<String> images;

    private String video_path;
    private String video_img;

    private String comment_num;
    private String upvote_num;

    private int type;

    private int video_img_width;
    private int video_img_height;

    public int getVideo_img_width() {
        return video_img_width;
    }

    public int getVideo_img_height() {
        return video_img_height;
    }


    public void setVideo_img_height(int video_img_height) {
        this.video_img_height = video_img_height;
    }

    public void setVideo_img_width(int video_img_width) {
        this.video_img_width = video_img_width;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getUpvote_num() {
        return upvote_num;
    }

    public void setUpvote_num(String upvote_num) {
        this.upvote_num = upvote_num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIs_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(int is_upvote) {
        this.is_upvote = is_upvote;
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

    public String getMoments_content() {
        return moments_content;
    }

    public void setMoments_content(String moments_content) {
        this.moments_content = moments_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getVideo_img() {
        return video_img;
    }

    public void setVideo_img(String video_img) {
        this.video_img = video_img;
    }
}
