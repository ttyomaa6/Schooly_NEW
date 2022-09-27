package com.egormoroz.schooly.ui.people;

import android.util.Log;

public class UserPeopleAdapter {
    private int hashMod = 1000000007;
    private String Nick;
    private String Avatar ;
    private String bio;
    public UserPeopleAdapter(){
    }

    public String getNick() {
        return Nick;
    }

    public void setNick(String nick) {
        Nick = nick;
    }

    public String getAvatar() {return Avatar;}

    public void setAvatar(String Avatar){
        this.Avatar = Avatar;
    }



    public String getBio(){
        return this.bio;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public UserPeopleAdapter(String nick,  String Avatar, String bio) {
        this.Nick = nick;
        this.Avatar = Avatar;
        this.bio=bio;
    }
    @Override
    public int hashCode() {
        return (((getNick().hashCode() * getAvatar().hashCode()) % hashMod) * getAvatar().hashCode()) % hashMod;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof UserPeopleAdapter)) {
            return false;
        }
        UserPeopleAdapter newClass = (UserPeopleAdapter) o;

        return newClass.getNick().equals(getNick()) &&
                newClass.getAvatar().equals(getAvatar()) &&
                newClass.getBio().equals(getBio());
    }
}
