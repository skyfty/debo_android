package com.qcwl.debo.ui.found.memorandum.bean;

import java.util.List;

/**
 * Created by admin on 2018/6/14.
 */

public class ScheduleMonthBean {
    /**
     * day : 2018-06-06
     * count : 6
     * color : [{"back_color":"1"},{"back_color":"5"}]
     * type : [{"m_type":"1"},{"m_type":"5"}]
     */

    private String day;
    private String count;
    private List<ColorBean> color;
    private List<TypeBean> type;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<ColorBean> getColor() {
        return color;
    }

    public void setColor(List<ColorBean> color) {
        this.color = color;
    }

    public List<TypeBean> getType() {
        return type;
    }

    public void setType(List<TypeBean> type) {
        this.type = type;
    }

    public static class ColorBean {
        /**
         * back_color : 1
         */

        private String back_color;

        public String getBack_color() {
            return back_color;
        }

        public void setBack_color(String back_color) {
            this.back_color = back_color;
        }

        @Override
        public String toString() {
            return "ColorBean{" +
                    "back_color='" + back_color + '\'' +
                    '}';
        }
    }

    public static class TypeBean {
        /**
         * m_type : 1
         */

        private String m_type;

        public String getM_type() {
            return m_type;
        }

        public void setM_type(String m_type) {
            this.m_type = m_type;
        }

        @Override
        public String toString() {
            return "TypeBean{" +
                    "m_type='" + m_type + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ScheduleMonthBean{" +
                "day='" + day + '\'' +
                ", count='" + count + '\'' +
                ", color=" + color +
                ", type=" + type +
                '}';
    }


}
