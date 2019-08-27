package com.qcwl.debo.chat.chatroom;

/**
 * Created by AlMn on 2018/1/18.
 */

public class ChatRoomBean {


    /**
     * id : 1
     * chatroom_id : 23412341234124123
     * chatroom_name : sad
     * city : 北京
     * time : 2341212341234
     * status : 0
     * room_avatar : http://jashfiahsdfklashflaskfasjdfl
     */

    private String id;
    private String chatroom_id;
    private String chatroom_name;
    private String city;
    private String time;
    private String status;
    private String room_avatar;

    public void setId(String id) {
        this.id = id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public void setChatroom_name(String chatroom_name) {
        this.chatroom_name = chatroom_name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoom_avatar(String room_avatar) {
        this.room_avatar = room_avatar;
    }

    public String getId() {
        return id;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public String getChatroom_name() {
        return chatroom_name;
    }

    public String getCity() {
        return city;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getRoom_avatar() {
        return room_avatar;
    }
}
