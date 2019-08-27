package com.qcwl.debo.ui.found.memorandum.bean;

/**
 * Created by admin on 2018/6/14.
 */

public class ScheduleClassifyBean {

    /**
     * name : 工作
     * m_type : 1
     * count : 4
     */

    private String name;
    private String m_type;
    private String count;
    private String back_color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getM_type() {
        return m_type;
    }

    public void setM_type(String m_type) {
        this.m_type = m_type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getBack_color() {
        return back_color;
    }

    public void setBack_color(String back_color) {
        this.back_color = back_color;
    }

    @Override
    public String toString() {
        return "ScheduleClassifyBean{" +
                "name='" + name + '\'' +
                ", m_type='" + m_type + '\'' +
                ", count='" + count + '\'' +
                ", back_color='" + back_color + '\'' +
                '}';
    }
}
