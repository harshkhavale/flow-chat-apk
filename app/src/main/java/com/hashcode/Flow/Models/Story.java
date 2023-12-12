package com.hashcode.Flow.Models;

public class Story {
    private String imgUrl;
    private long timeStamp;


    public Story() {
    }

    public Story(String imgUrl, long timeStamp) {
        this.imgUrl = imgUrl;
        this.timeStamp = timeStamp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
