package com.qcwl.debo.ui.found.joke;

/**
 * Created by qcwl on 2017/11/20.
 */

public class VideoInfoBean {

    private String Format;
    private String StreamType;
    private String JobId;
    private String Duration;
    private String Height;
    private String Encrypt;
    private String PlayURL;
    private String Width;
    private String Fps;
    private String Bitrate;
    private String Definition;
    private String Size;
    private String CoverURL;
    private String is_follow;
    private String comment_num;
    private String upvote_num;
    private String is_my_upvote;
    private String play_num;

    public VideoInfoBean(String format, String streamType, String jobId, String duration, String height, String encrypt, String playURL, String width,
                         String fps, String bitrate, String definition,
                         String size, String coverURL, String is_follow, String comment_num, String upvote_num, String is_my_upvote, String play_num) {
        Format = format;
        StreamType = streamType;
        JobId = jobId;
        Duration = duration;
        Height = height;
        Encrypt = encrypt;
        PlayURL = playURL;
        Width = width;
        Fps = fps;
        Bitrate = bitrate;
        Definition = definition;
        Size = size;
        CoverURL = coverURL;
        this.is_follow = is_follow;
        this.comment_num = comment_num;
        this.upvote_num = upvote_num;
        this.is_my_upvote = is_my_upvote;
        this.play_num = play_num;
    }
    public VideoInfoBean() {
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getStreamType() {
        return StreamType;
    }

    public void setStreamType(String streamType) {
        StreamType = streamType;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getEncrypt() {
        return Encrypt;
    }

    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }

    public String getPlayURL() {
        return PlayURL;
    }

    public void setPlayURL(String playURL) {
        PlayURL = playURL;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public String getFps() {
        return Fps;
    }

    public void setFps(String fps) {
        Fps = fps;
    }

    public String getBitrate() {
        return Bitrate;
    }

    public void setBitrate(String bitrate) {
        Bitrate = bitrate;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition(String definition) {
        Definition = definition;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getCoverURL() {
        return CoverURL;
    }

    public void setCoverURL(String coverURL) {
        CoverURL = coverURL;
    }

    public String getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(String is_follow) {
        this.is_follow = is_follow;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public String getUpvote_num() {
        return upvote_num;
    }

    public void setUpvote_num(String upvote_num) {
        this.upvote_num = upvote_num;
    }

    public String getIs_my_upvote() {
        return is_my_upvote;
    }

    public void setIs_my_upvote(String is_my_upvote) {
        this.is_my_upvote = is_my_upvote;
    }

    public String getPlay_num() {
        return play_num;
    }

    public void setPlay_num(String play_num) {
        this.play_num = play_num;
    }

    @Override
    public String toString() {
        return "VideoInfoBean{" +
                "Format='" + Format + '\'' +
                ", StreamType='" + StreamType + '\'' +
                ", JobId='" + JobId + '\'' +
                ", Duration='" + Duration + '\'' +
                ", Height='" + Height + '\'' +
                ", Encrypt='" + Encrypt + '\'' +
                ", PlayURL='" + PlayURL + '\'' +
                ", Width='" + Width + '\'' +
                ", Fps='" + Fps + '\'' +
                ", Bitrate='" + Bitrate + '\'' +
                ", Definition='" + Definition + '\'' +
                ", Size='" + Size + '\'' +
                ", CoverURL='" + CoverURL + '\'' +
                ", is_follow='" + is_follow + '\'' +
                ", comment_num='" + comment_num + '\'' +
                ", upvote_num='" + upvote_num + '\'' +
                ", is_my_upvote='" + is_my_upvote + '\'' +
                ", play_num='" + play_num + '\'' +
                '}';
    }
}
