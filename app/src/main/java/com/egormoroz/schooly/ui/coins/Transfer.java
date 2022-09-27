package com.egormoroz.schooly.ui.coins;

public class Transfer{

    private long sum;
    private String who;
    private String type;

    public Transfer(long sum, String  who, String type){
        this.sum=sum;
        this.who=who;
        this.type=type;
    }
    public Transfer(){
    }

    public long getSum(){
        return this.sum;
    }

    public void setSum(long sum){
        this.sum=sum;
    }

    public String  getWho(){
        return this.who;
    }

    public void setWho(String who){
        this.who=who;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type=type;
    }
}
