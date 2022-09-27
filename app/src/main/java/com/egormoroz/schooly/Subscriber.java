package com.egormoroz.schooly;

public class Subscriber {
    private String sub;

    public Subscriber(String  sub){
        this.sub=sub;
    }
    public Subscriber(){
    }

    public String  getSub(){
        return this.sub;
    }

    public void setSub(String  sub){
        this.sub=sub;
    }
    //
}