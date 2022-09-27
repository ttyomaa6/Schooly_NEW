package com.egormoroz.schooly.ui.profile;

import com.egormoroz.schooly.ui.news.NewsItem;

import java.util.ArrayList;

public class Complain {
    private String appealedPerson;
    private String sneakPerson;
    private String reason;
    private String description;
    private String uid;
    NewsItem newsItem;

    public Complain(String sneakPerson,String  appealedPerson,String reason,String description,String uid
    ,NewsItem newsItem){
        this.appealedPerson=appealedPerson;
        this.reason=reason;
        this.sneakPerson=sneakPerson;
        this.description=description;
        this.uid=uid;
        this.newsItem=newsItem;
    }
    public Complain(){
    }

    public String  getSneakPerson(){
        return this.sneakPerson;
    }

    public void setSneakPerson(String  sneakPerson){
        this.sneakPerson=sneakPerson;
    }

    public String  getAppealedPerson(){
        return this.appealedPerson;
    }

    public void setAppealedPerson(String  appealedPerson){
        this.appealedPerson=appealedPerson;
    }

    public String  getReason(){
        return this.reason;
    }

    public void setReason(String reason){
        this.reason=reason;
    }

    public String  getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public String  getUid(){
        return this.uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public NewsItem  getNewsItem(){
        return this.newsItem;
    }

    public void setNewsItem(NewsItem newsItem){
        this.newsItem=newsItem;
    }
}
