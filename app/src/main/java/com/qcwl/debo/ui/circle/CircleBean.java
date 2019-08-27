package com.qcwl.debo.ui.circle;

import java.util.List;

/**
 * Created by Administrator on 2017/7/21.
 */

public class CircleBean {


    /**
     * moments_id : 14
     * moments_uid : 28
     * moments_content : testtesttesttest
     * create_time : 2017-07-24 14:59:37
     * position :
     * lat :
     * lng :
     * images : ["http://123.57.148.47/debo/data/upload/moments_pub_talking/talking_18/78.jpg","http://123.57.148.47/debo/data/upload/moments_pub_talking/talking_18/30.gif","http://123.57.148.47/debo/data/upload/moments_pub_talking/talking_18/62.gif"]
     * user_nickname : test
     * avatar : http://123.57.148.47/debo/data/upload/user/user_29/1.jpg
     */

//    1499682053

    private String is_upvote;//1点赞

    private int type;
    private List<CommentBean> comment_list;

    private List<PraiseBean> upvote_list;

    private String moments_id;
    private String moments_uid;
    private String moments_content;
    private String create_time;
    private String position;
    private String lat;
    private String lng;
    private String user_nickname;
    private String avatar;
    private String mobile;
    private List<String> images;

    private String video_path;
    private String video_img;

    private int circle_type;

    private int video_img_width;
    private int video_img_height;

    public int getVideo_img_width() {
        return video_img_width;
    }

    public void setVideo_img_width(int video_img_width) {
        this.video_img_width = video_img_width;
    }

    public int getVideo_img_height() {
        return video_img_height;
    }

    public void setVideo_img_height(int video_img_height) {
        this.video_img_height = video_img_height;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVideo_img() {
        return video_img;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public void setVideo_img(String video_img) {
        this.video_img = video_img;
    }

    public String getVideo_path() {
        return video_path;
    }

    public int getCircle_type() {
        return circle_type;
    }

    public void setCircle_type(int circle_type) {
        this.circle_type = circle_type;
    }

    public String getIs_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(String is_upvote) {
        this.is_upvote = is_upvote;
    }

    public List<PraiseBean> getUpvote_list() {
        return upvote_list;
    }

    public void setUpvote_list(List<PraiseBean> upvote_list) {
        this.upvote_list = upvote_list;
    }

    public List<CommentBean> getComment_list() {
        return comment_list;
    }

    public void setComment_list(List<CommentBean> comment_list) {
        this.comment_list = comment_list;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
