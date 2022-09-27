package com.egormoroz.schooly.ui.news;

public class Comment {
    private String text,  commentId,postTime,nick,
            comments,type,image, parentId;
    private   long likes_count;


    public Comment(){

    }

    public Comment( String text, long likes_count, String commentId,
                    String postTime,String nick,String image,String type, String parentId){
        this.image = image;
        this.likes_count = likes_count;
        this.text = text;
        this.commentId = commentId;
        this.postTime=postTime;
        this.nick=nick;
        this.type=type;
        this.parentId = parentId;
    }

    public String getImage (){
        return  this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText (){
        return  this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getLikes_count (){
        return  this.likes_count;
    }

    public void setLikesCount(long likes_count) {
        this.likes_count = likes_count;
    }

    public String getCommentId() {return this.commentId;}

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComments (){
        return  this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPostTime() {return this.postTime;}

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getNick() {return this.nick;}

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getType() {return this.type;}

    public void setType(String type) {
        this.type = type;
    }

    public void setParentId(String parentId){this.parentId = parentId;}
    public String getParentId(){return parentId;}
}
