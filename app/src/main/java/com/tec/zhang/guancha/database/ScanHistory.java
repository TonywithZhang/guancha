package com.tec.zhang.guancha.database;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class ScanHistory extends LitePalSupport {
    private String newsTitle;
    private String articleUrl;
    private int codeId;
    private List<String> articleSegments;
    private List<String> picturesUrl;
    private String articlePic;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public List<String> getArticleSegments() {
        return articleSegments;
    }

    public void setArticleSegments(List<String> articleSegments) {
        this.articleSegments = articleSegments;
    }

    public List<String> getPicturesUrl() {
        return picturesUrl;
    }

    public void setPicturesUrl(List<String> picturesUrl) {
        this.picturesUrl = picturesUrl;
    }

    public String getArticlePic() {
        return articlePic;
    }

    public void setArticlePic(String articlePic) {
        this.articlePic = articlePic;
    }
}
