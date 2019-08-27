package com.qcwl.debo.ui.found.bump;

/**
 * Created by admin on 2018/5/26.
 */

public class JumpFriOrRPBean {


    /**
     * code : 0/1
     * message : 撞到人/恭喜撞到红包
     * data : {"t_id":"89","uid":"105","title":"中国雕刻网","ad_link":"www.zgdk.cn","ad_content":"领先的雕刻贸易市场！","ad_images":"/data/upload/advertisement/advertisement_105/59c26a79622ab3.37238925.jpg"}
     */

        /**
         * 人：
         * id: 102
         * mobile: 18790958006
         * sex: 0
         * user_nickname: 大发
         * avatar: http://debo.shangtongyuntian.com/data/upload/user/user_102/5a7150f5ea9261.19729100.jpg
         *
         * 红包：
         * t_id : 89
         * uid : 105
         * title : 中国雕刻网
         * ad_link : www.zgdk.cn
         * ad_content : 领先的雕刻贸易市场！
         * ad_images : /data/upload/advertisement/advertisement_105/59c26a79622ab3.37238925.jpg
         */
        private String id;
        private String sex;
        private String mobile;
        private String user_nickname;
        private String avatar;
        private String t_id;
        private String uid;
        private String title;
        private String ad_link;
        private String ad_content;
        private String ad_images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public void setAvatar(String avater) {
        this.avatar = avater;
    }

    public String getT_id() {
            return t_id;
        }

        public void setT_id(String t_id) {
            this.t_id = t_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAd_link() {
            return ad_link;
        }

        public void setAd_link(String ad_link) {
            this.ad_link = ad_link;
        }

        public String getAd_content() {
            return ad_content;
        }

        public void setAd_content(String ad_content) {
            this.ad_content = ad_content;
        }

        public String getAd_images() {
            return ad_images;
        }

        public void setAd_images(String ad_images) {
            this.ad_images = ad_images;
        }

    @Override
    public String toString() {
        return "JumpFriOrRPBean{" +
                "id='" + id + '\'' +
                ", sex='" + sex + '\'' +
                ", mobile='" + mobile + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", t_id='" + t_id + '\'' +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", ad_link='" + ad_link + '\'' +
                ", ad_content='" + ad_content + '\'' +
                ", ad_images='" + ad_images + '\'' +
                '}';
    }
}
