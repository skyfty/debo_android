package com.qcwl.debo.model;

import java.io.Serializable;
import java.util.List;

public class PersonalBean implements Serializable{
    private String is_fans;
    private int joke_count;
    private String upvote_count;
    private String like_count;
    private String follow_count;
    private String fans_count;
    private User_info user_info;
    private List<Joke_list> joke_list;
    private List<Joke_like_list> joke_like_list;
    public void setIs_fans(String is_fans) {
        this.is_fans = is_fans;
    }
    public String getIs_fans() {
        return is_fans;
    }

    public void setJoke_count(int joke_count) {
        this.joke_count = joke_count;
    }
    public int getJoke_count() {
        return joke_count;
    }

    public void setUpvote_count(String upvote_count) {
        this.upvote_count = upvote_count;
    }
    public String getUpvote_count() {
        return upvote_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }
    public String getLike_count() {
        return like_count;
    }

    public void setFollow_count(String follow_count) {
        this.follow_count = follow_count;
    }
    public String getFollow_count() {
        return follow_count;
    }

    public void setFans_count(String fans_count) {
        this.fans_count = fans_count;
    }
    public String getFans_count() {
        return fans_count;
    }

    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }
    public User_info getUser_info() {
        return user_info;
    }

    public void setJoke_list(List<Joke_list> joke_list) {
        this.joke_list = joke_list;
    }
    public List<Joke_list> getJoke_list() {
        return joke_list;
    }

    public void setJoke_like_list(List<Joke_like_list> joke_like_list) {
        this.joke_like_list = joke_like_list;
    }
    public List<Joke_like_list> getJoke_like_list() {
        return joke_like_list;
    }
}
