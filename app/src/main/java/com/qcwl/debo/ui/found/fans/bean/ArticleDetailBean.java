package com.qcwl.debo.ui.found.fans.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ArticleDetailBean {

    /**
     * m_id : 2
     * uid : 42
     * title : 致粉丝：今天我们有太多话想说了！
     * content : 致粉丝：
     没想到今天这么一个在喃们看来很普通，但是对于我们大美圈来说非常特殊的日子，原本这篇文章是首条，安排我来写，但是被临时加的广告占了位置，毕竟我们平台的广告量太大了！对于写惯了负能量吐槽的胸姐来说，按照首条标准准备的这篇稿写起来压力老大了！这篇稿我憋了一个礼拜，也无从下手，因为这是我们自己的狂欢啊，我写再多，喜欢我们的人会更开心，不喜欢我们的人会觉得胸姐又装逼，但是没办法啊，因为我们的公众号越来越牛逼了！
     * add_time : 1501603150
     * issue : 2
     * img : ["/data/upload/advertisement/advertisement_36/597ffd077b0ea5.79682051.jpeg",""]
     * read_num : 9
     * comment_num : 0
     * zan_num : 0
     * type : 1
     */

    private String m_id;
    private String uid;
    private String title;
    private String content;
    private String add_time;
    private String issue;
    private int read_num;
    private int comment_num;
    private int zan_num;
    private String type;
    private List<String> img;

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getZan_num() {
        return zan_num;
    }

    public void setZan_num(int zan_num) {
        this.zan_num = zan_num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }
}
