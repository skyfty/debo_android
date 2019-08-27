package com.qcwl.debo.model;

/**
 * Created by Administrator on 2017/8/2.
 */

public class GroupBean {

    private String groupid;
    private String groupname;
    private String g_avatar;
    private String qroup_type;
    private String is_show_name;

    public String getIs_show_name() {
        return is_show_name;
    }

    public void setIs_show_name(String is_show_name) {
        this.is_show_name = is_show_name;
    }

    public String getQroup_type() {
        return qroup_type;
    }

    public void setQroup_type(String qroup_type) {
        this.qroup_type = qroup_type;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getG_avatar() {
        return g_avatar;
    }

    public void setG_avatar(String g_avatar) {
        this.g_avatar = g_avatar;
    }
}
