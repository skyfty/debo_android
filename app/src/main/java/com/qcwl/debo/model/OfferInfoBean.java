package com.qcwl.debo.model;

public class OfferInfoBean {
    private String offer_avatar;
    private String offer_user_nickname;
    private String content;
    private String content2;
    private String is_first_get;//表不是第一次领取 或者 没有领取记录0发领取信息
    private String first_get_name;//第一次领取 领取人的名字


    public OfferInfoBean(String offer_avatar, String offer_user_nickname, String content, String content2,String is_first_get,String first_get_name) {
        this.offer_avatar = offer_avatar;
        this.offer_user_nickname = offer_user_nickname;
        this.content = content;
        this.content2 = content2;
        this.is_first_get = is_first_get;
        this.first_get_name = first_get_name;
    }

    public OfferInfoBean() {
    }

    public String getOffer_avatar() {
        return offer_avatar;
    }

    public void setOffer_avatar(String offer_avatar) {
        this.offer_avatar = offer_avatar;
    }

    public String getOffer_user_nickname() {
        return offer_user_nickname;
    }

    public void setOffer_user_nickname(String offer_user_nickname) {
        this.offer_user_nickname = offer_user_nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getIs_first_get() {
        return is_first_get;
    }

    public void setIs_first_get(String is_first_get) {
        this.is_first_get = is_first_get;
    }

    public String getFirst_get_name() {
        return first_get_name;
    }

    public void setFirst_get_name(String first_get_name) {
        this.first_get_name = first_get_name;
    }

    @Override
    public String toString() {
        return "OfferInfoBean{" +
                "offer_avatar='" + offer_avatar + '\'' +
                ", offer_user_nickname='" + offer_user_nickname + '\'' +
                ", content='" + content + '\'' +
                ", content2='" + content2 + '\'' +
                ", is_first_get='" + is_first_get + '\'' +
                ", first_get_name='" + first_get_name + '\'' +
                '}';
    }
}
