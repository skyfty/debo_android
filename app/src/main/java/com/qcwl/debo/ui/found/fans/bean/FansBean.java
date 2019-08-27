package com.qcwl.debo.ui.found.fans.bean;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FansBean {


    /**
     * id : 21
     * user_nickname : DDD
     * signature :
     * small_avatar :
     * is_concern : 1
     */

    private String id;
    private String user_nickname;
    private String signature;
    private String small_avatar;
    private int is_concern;
    private String is_fans;

    public String getIs_fans() {
        return is_fans;
    }

    public void setIs_fans(String is_fans) {
        this.is_fans = is_fans;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSmall_avatar() {
        return small_avatar;
    }

    public void setSmall_avatar(String small_avatar) {
        this.small_avatar = small_avatar;
    }

    public int getIs_concern() {
        return is_concern;
    }

    public void setIs_concern(int is_concern) {
        this.is_concern = is_concern;
    }
}
