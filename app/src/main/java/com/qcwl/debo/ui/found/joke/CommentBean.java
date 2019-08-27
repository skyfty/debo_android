package com.qcwl.debo.ui.found.joke;

/**
 * Created by qcwl on 2017/11/20.
 */

public class CommentBean {
    private String id;
    private  String video_id;
    private String uid;
    private String content;
    private String time;
    private String user_nickname;
    private String avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
        return "CommentBean{" +
                "id='" + id + '\'' +
                ", video_id='" + video_id + '\'' +
                ", uid='" + uid + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
