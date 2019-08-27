package com.qcwl.debo.ui.my.wallet.trading;

/**
 * Created by Administrator on 2017/9/7.
 */

public class RadioBean {

    private int indent;
    private String text;
    private boolean checked;

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public RadioBean() {
    }

    public RadioBean(int indent, String text, boolean checked) {
        this.indent = indent;
        this.text = text;
        this.checked = checked;
    }
}
