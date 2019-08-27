package com.qcwl.debo.ui.found.fans.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FansDynamicCommentBean {

    /**
     * mc_id : 37
     * mc_content : ，，，，
     * moments_id : 59
     * comment_time : 2017-08-08 10:20:50
     * reply_uid : 0
     * state : 0
     * upvote_num : 0
     * is_upvote : 1
     * avatar : http://123.57.148.47/debo/data/upload/user/user_34/53.png
     * replay_list : [{"mc_id":"39","mc_content":"hahahahaha","uid":"34","moments_id":"59","comment_time":"2017-08-08 10:21:04","reply_uid":"18","state":"0","upvote_num":"0","reply_name":"AAABBB","avatar":"http://123.57.148.47/debo/data/upload/user/user_34/53.png"},{"mc_id":"38","mc_content":"testtest","uid":"34","moments_id":"59","comment_time":"2017-08-08 10:20:51","reply_uid":"18","state":"0","upvote_num":"0","reply_name":"AAABBB","avatar":"http://123.57.148.47/debo/data/upload/user/user_34/3.png"}]
     */

    private String mc_id;
    private String mc_content;
    private String moments_id;
    private String comment_time;
    private String uid;
    private String reply_uid;
    private String upvote_num;
    private int is_upvote;
    private String avatar;
    private String name;
    private String reply_name;
    private List<FansDynamicCommentBean> replay_list;

    private String reply_mobile;
    private String mobile;

    public String getReply_mobile() {
        return reply_mobile;
    }

    public void setReply_mobile(String reply_mobile) {
        this.reply_mobile = reply_mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMc_id() {
        return mc_id;
    }

    public void setMc_id(String mc_id) {
        this.mc_id = mc_id;
    }

    public String getMc_content() {
        return mc_content;
    }

    public void setMc_content(String mc_content) {
        this.mc_content = mc_content;
    }

    public String getMoments_id() {
        return moments_id;
    }

    public void setMoments_id(String moments_id) {
        this.moments_id = moments_id;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReply_uid() {
        return reply_uid;
    }

    public void setReply_uid(String reply_uid) {
        this.reply_uid = reply_uid;
    }

    public String getUpvote_num() {
        return upvote_num;
    }

    public void setUpvote_num(String upvote_num) {
        this.upvote_num = upvote_num;
    }

    public int getIs_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(int is_upvote) {
        this.is_upvote = is_upvote;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getReply_name() {
        return reply_name;
    }

    public void setReply_name(String reply_name) {
        this.reply_name = reply_name;
    }

    public List<FansDynamicCommentBean> getReplay_list() {
        return replay_list;
    }

    public void setReplay_list(List<FansDynamicCommentBean> replay_list) {
        this.replay_list = replay_list;
    }
}
