package com.tec.zhang.guancha.recycler;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsSingle {
    private String imageURL;
    private String newsTitle;
    private String commentNum;
    private String beLong;

    public NewsSingle(String imageURL, String newsTitle, String commentNum, String beLong) {
        this.imageURL = imageURL;
        this.newsTitle = newsTitle;
        this.commentNum = commentNum;
        this.beLong = beLong;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getBeLong() {
        return beLong;
    }

    public void setBeLong(String beLong) {
        this.beLong = beLong;
    }

}
