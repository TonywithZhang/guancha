package com.tec.zhang.guancha.recycler;

public class CommentBean {
    private String headerTitle;
    private int viewType;

    private String userHeaderImageUrl;
    private String userName;
    private String commentTime;
    private boolean parentExists;
    private String parentUserName;
    private String parentCommentTime;
    private String parentComment;
    private boolean ParentUserPraised;
    private String parentPraisedNumber;
    private boolean ParentDisliked;
    private String parentDislikedNumber;
    private String comment;
    private boolean userPraised;
    private String praisedNumber;
    private boolean disliked;
    private String dislikedNumber;

    public CommentBean(int viewType,String imageUrl,String userName,String commentTime,String comment,boolean userPraised,String praisedNumber,boolean disliked,String dislikedNumber,boolean parentExists){
        this.userHeaderImageUrl = imageUrl;
        this.userName = userName;
        this.commentTime = commentTime;
        this.comment = comment;
        this.userPraised = userPraised;
        this.praisedNumber = praisedNumber;
        this.disliked = disliked;
        this.dislikedNumber = dislikedNumber;
        this.parentExists = parentExists;
        this.viewType = viewType;
    }
    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getUserHeaderImageUrl() {
        return userHeaderImageUrl;
    }

    public void setUserHeaderImageUrl(String userHeaderImageUrl) {
        this.userHeaderImageUrl = userHeaderImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public boolean isParentExists() {
        return parentExists;
    }

    public void setParentExists(boolean parentExists) {
        this.parentExists = parentExists;
    }

    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    public String getParentCommentTime() {
        return parentCommentTime;
    }

    public void setParentCommentTime(String parentCommentTime) {
        this.parentCommentTime = parentCommentTime;
    }

    public String getParentComment() {
        return parentComment;
    }

    public void setParentComment(String parentComment) {
        this.parentComment = parentComment;
    }

    public boolean isParentUserPraised() {
        return ParentUserPraised;
    }

    public void setParentUserPraised(boolean parentUserPraised) {
        ParentUserPraised = parentUserPraised;
    }

    public String getParentPraisedNumber() {
        return parentPraisedNumber;
    }

    public void setParentPraisedNumber(String parentPraisedNumber) {
        this.parentPraisedNumber = parentPraisedNumber;
    }

    public boolean isParentDisliked() {
        return ParentDisliked;
    }

    public void setParentDisliked(boolean parentDisliked) {
        ParentDisliked = parentDisliked;
    }

    public String getParentDislikedNumber() {
        return parentDislikedNumber;
    }

    public void setParentDislikedNumber(String parentDislikedNumber) {
        this.parentDislikedNumber = parentDislikedNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isUserPraised() {
        return userPraised;
    }

    public void setUserPraised(boolean userPraised) {
        this.userPraised = userPraised;
    }

    public String getPraisedNumber() {
        return praisedNumber;
    }

    public void setPraisedNumber(String praisedNumber) {
        this.praisedNumber = praisedNumber;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public String getDislikedNumber() {
        return dislikedNumber;
    }

    public void setDislikedNumber(String dislikedNumber) {
        this.dislikedNumber = dislikedNumber;
    }

    public void setViewType(int viewType){
        if (viewType < 1 || viewType > 2) throw new IllegalArgumentException("viewType设置错误！");
        this.viewType = viewType;
    }
    public int getViewType(){
        return viewType;
    }
}
