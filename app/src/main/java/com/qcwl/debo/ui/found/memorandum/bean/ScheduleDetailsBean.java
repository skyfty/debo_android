package com.qcwl.debo.ui.found.memorandum.bean;

import java.util.ArrayList;

/**
 * Created by admin on 2018/6/14.
 */

public class ScheduleDetailsBean {

    /**
     * id : 2
     * uid : 521
     * start_time : 1528300099
     * end_time : 1528300799
     * content : 新增备忘录
     * status : 0
     * path : ["\/data\/upload\/memorandum\/521\/5b1740aab5c456.10997556.jpg","\/data\/upload\/memorandum\/521\/5b1740aab72b34.81240964.jpg","\/data\/upload\/memorandum\/521\/5b1740aab8ee16.55133124.jpg","\/data\/upload\/memorandum\/521\/5b1740aababcc3.18974105.jpg","\/data\/upload\/memorandum\/521\/5b1740aabcbd07.75403994.jpg","\/data\/upload\/memorandum\/521\/5b1740aabe5285.03567463.jpg","\/data\/upload\/memorandum\/521\/5b1740aac00b01.08523457.jpg","\/data\/upload\/memorandum\/521\/5b1740aac196c5.13396719.jpg","\/data\/upload\/memorandum\/521\/5b1740aac36dd3.48771232.jpg"]
     * m_type : 1
     * m_count : 0
     * font_color : null
     * name : 工作
     * back_color : #46c4f3
     */

    private String id;
    private String uid;
    private String start_time;
    private String end_time;
    private String content;
    private String status;
    private ArrayList<String> path;
    private String m_type;
    private String m_count;
    private Object font_color;
    private String name;
    private String back_color;

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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
    }

    public String getM_type() {
        return m_type;
    }

    public void setM_type(String m_type) {
        this.m_type = m_type;
    }

    public String getM_count() {
        return m_count;
    }

    public void setM_count(String m_count) {
        this.m_count = m_count;
    }

    public Object getFont_color() {
        return font_color;
    }

    public void setFont_color(Object font_color) {
        this.font_color = font_color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBack_color() {
        return back_color;
    }

    public void setBack_color(String back_color) {
        this.back_color = back_color;
    }

}
