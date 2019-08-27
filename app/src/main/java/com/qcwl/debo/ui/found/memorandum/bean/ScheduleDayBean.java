package com.qcwl.debo.ui.found.memorandum.bean;

import java.util.List;

/**
 * Created by admin on 2018/6/14.
 */

public class ScheduleDayBean {

    /**
     * id : 2
     * start_time : 1528300099
     * end_time : 1528300799
     * content : 新增备忘录
     * status : 0
     * m_type : 1
     * is_remind : 0
     * has_img : 1
     * img_url : ["/data/upload/memorandum/521/5b1740aab5c456.10997556.jpg","/data/upload/memorandum/521/5b1740aab72b34.81240964.jpg","/data/upload/memorandum/521/5b1740aab8ee16.55133124.jpg","/data/upload/memorandum/521/5b1740aababcc3.18974105.jpg","/data/upload/memorandum/521/5b1740aabcbd07.75403994.jpg","/data/upload/memorandum/521/5b1740aabe5285.03567463.jpg","/data/upload/memorandum/521/5b1740aac00b01.08523457.jpg","/data/upload/memorandum/521/5b1740aac196c5.13396719.jpg","/data/upload/memorandum/521/5b1740aac36dd3.48771232.jpg"]
     * font_color : 1
     * back_color : 2
     * name : 工作
     */

    private String id;
    private String start_time;
    private String end_time;
    private String content;
    private String status;
    private String m_type;
    private int is_remind;
    private int has_img;
    private String font_color;
    private String back_color;
    private String name;
    private String rem_time;
    private List<String> img_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getM_type() {
        return m_type;
    }

    public void setM_type(String m_type) {
        this.m_type = m_type;
    }

    public int getIs_remind() {
        return is_remind;
    }

    public void setIs_remind(int is_remind) {
        this.is_remind = is_remind;
    }

    public int getHas_img() {
        return has_img;
    }

    public void setHas_img(int has_img) {
        this.has_img = has_img;
    }

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getBack_color() {
        return back_color;
    }

    public void setBack_color(String back_color) {
        this.back_color = back_color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRem_time() {
        return rem_time;
    }

    public void setRem_time(String rem_time) {
        this.rem_time = rem_time;
    }

    public List<String> getImg_url() {
        return img_url;
    }

    public void setImg_url(List<String> img_url) {
        this.img_url = img_url;
    }

}
