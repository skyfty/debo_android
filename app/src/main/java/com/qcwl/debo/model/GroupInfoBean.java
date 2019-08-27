package com.qcwl.debo.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */

public class GroupInfoBean implements Serializable{

    private String affiliations_count;
    private boolean allowinvites;
    private String created;
    private String custom;
    private String description;
    private String id;
    private String is_show_name;
    private int maxusers;
    private boolean membersonly;
    private String name;
    private String owner;
    private boolean is_public;
    private List<Infos> affiliations;
    private String myname_for_group;
    private boolean isEdit;

    public String getMyname_for_group() {
        return myname_for_group;
    }

    public void setMyname_for_group(String myname_for_group) {
        this.myname_for_group = myname_for_group;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public class Infos implements Serializable{
        private String avatar;
        private String member;
        private String member_name;
        private String member_type;
        private String user_nickname;
        private String owner;
        private boolean isDel;

        public boolean isDel() {
            return isDel;
        }

        public void setDel(boolean del) {
            isDel = del;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }

        public String getMember_type() {
            return member_type;
        }

        public void setMember_type(String member_type) {
            this.member_type = member_type;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        @Override
        public String toString() {
            return "Infos{" +
                    "avatar='" + avatar + '\'' +
                    ", member='" + member + '\'' +
                    ", member_name='" + member_name + '\'' +
                    ", member_type='" + member_type + '\'' +
                    ", user_nickname='" + user_nickname + '\'' +
                    ", owner='" + owner + '\'' +
                    ", isDel=" + isDel +
                    '}';
        }
    }

    public String getAffiliations_count() {
        return affiliations_count;
    }

    public void setAffiliations_count(String affiliations_count) {
        this.affiliations_count = affiliations_count;
    }

    public boolean isAllowinvites() {
        return allowinvites;
    }

    public void setAllowinvites(boolean allowinvites) {
        this.allowinvites = allowinvites;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_show_name() {
        return is_show_name;
    }

    public void setIs_show_name(String is_show_name) {
        this.is_show_name = is_show_name;
    }

    public int getMaxusers() {
        return maxusers;
    }

    public void setMaxusers(int maxusers) {
        this.maxusers = maxusers;
    }

    public boolean isMembersonly() {
        return membersonly;
    }

    public void setMembersonly(boolean membersonly) {
        this.membersonly = membersonly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean is_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    public List<Infos> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<Infos> affiliations) {
        this.affiliations = affiliations;
    }
}
