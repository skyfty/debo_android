package com.qcwl.debo.model;

import java.util.List;

public class GetRedPacketRecordBean {
    private InfoBean info;
    private List<ListsBean> lists;

    @Override
    public String toString() {
        return "GetRedPacketRecordBean{" +
                "info=" + info +
                ", lists=" + lists +
                '}';
    }

    public GetRedPacketRecordBean() {
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class InfoBean{
        private String   total_get;
        private String   avatar;
        private String   user_nickname;


        public InfoBean() {
        }

        public String getTotal_get() {
            return total_get;
        }

        public void setTotal_get(String total_get) {
            this.total_get = total_get;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        @Override
        public String toString() {
            return "InfoBean{" +
                    "total_get='" + total_get + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", user_nickname='" + user_nickname + '\'' +
                    '}';
        }
    }
    public static class ListsBean{
        private String id;
        private String uid;
        private String offer_uid;
        private String time;
        private String money;
        private String p_id;
        private String user_nickname;

        public ListsBean() {
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

        public String getOffer_uid() {
            return offer_uid;
        }

        public void setOffer_uid(String offer_uid) {
            this.offer_uid = offer_uid;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getP_id() {
            return p_id;
        }

        public void setP_id(String p_id) {
            this.p_id = p_id;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        @Override
        public String toString() {
            return "ListsBean{" +
                    "id='" + id + '\'' +
                    ", uid='" + uid + '\'' +
                    ", offer_uid='" + offer_uid + '\'' +
                    ", time='" + time + '\'' +
                    ", money='" + money + '\'' +
                    ", p_id='" + p_id + '\'' +
                    ", user_nickname='" + user_nickname + '\'' +
                    '}';
        }
    }
}
