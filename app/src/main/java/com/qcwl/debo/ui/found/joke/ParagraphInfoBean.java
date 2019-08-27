package com.qcwl.debo.ui.found.joke;

/**
 * Created by admin on 2018/5/24.
 */

public class ParagraphInfoBean {
        /**
         * uid : 493
         * content : 444
         * comment_num : 1
         * follow : null
         * play_num : null
         * title : null
         * user_nickname : 嘚啵12
         * avatar :
         */

        private String uid;
        private String content;
        private String comment_num;
        private int follow;
        private int play_num;
        private String title;
        private String user_nickname;
        private String avatar;
    private int is_follow;
    private int is_upvote;

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getIs_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(int is_upvote) {
        this.is_upvote = is_upvote;
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

        public String getComment_num() {
            return comment_num;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }

        public int getFollow() {
            return follow;
        }

        public void setFollow(int follow) {
            this.follow = follow;
        }

        public int getPlay_num() {
            return play_num;
        }

        public void setPlay_num(int play_num) {
            this.play_num = play_num;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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
        return "ParagraphInfoBean{" +
                "uid='" + uid + '\'' +
                ", content='" + content + '\'' +
                ", comment_num='" + comment_num + '\'' +
                ", follow=" + follow +
                ", play_num=" + play_num +
                ", title='" + title + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", is_follow=" + is_follow +
                ", is_upvote=" + is_upvote +
                '}';
    }
}
