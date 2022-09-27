package com.egormoroz.schooly.ui.profile;

import android.net.Uri;

public class ProfileSearchItem {
    private long level;
    private Uri uri;
    private String nickName;
    public ProfileSearchItem(){

    }
    public long getLevel(){
        return level;
    }
    public void setLevel(long level){
        this.level = level;
    }
    public Uri getUri(){
        return uri;
    }
    public void setUri(Uri uri){
        this.uri = uri;
    }
    public String getNickName(){
        return nickName;
    }
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
}
