package com.qcwl.debo.ui.found.joke;

import java.io.Serializable;

/**
 * Created by qcwl on 2017/11/14.
 */

public class VideoBean implements Serializable {

    /**
     * id : 7
     * uid : 498
     * video_url : /data/upload/joke_video/498/5b03f93752e139.86273982.mp4
     * img_url : /data/upload/joke_video/498/5b03f9372b7074.38349472.png
     * city : 北京市
     * upvote : 2
     * user_nickname : 我不需要昵称
     * avatar : http://debo.shangtongyuntian.com/data/upload/user/user_498/5ad6c076056857.21266301.png
     */

    private String id;
    private String uid;
    private String video_url;
    private String img_url;
    private String city;
    private String upvote;
    private String user_nickname;
    private String avatar;
    private int time;
    private String title;
    private String is_upvote;
    private String is_follow;
    private String comment_num;
    private String upvote_num;

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

    public String getIs_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(String is_upvote) {
        this.is_upvote = is_upvote;
    }

    public String getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(String is_follow) {
        this.is_follow = is_follow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpvote() {
        return upvote;
    }

    public void setUpvote(String upvote) {
        this.upvote = upvote;
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

    @Override
    public String toString() {
        return "VideoBean{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", video_url='" + video_url + '\'' +
                ", img_url='" + img_url + '\'' +
                ", city='" + city + '\'' +
                ", upvote='" + upvote + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
                '}';
    }
}
