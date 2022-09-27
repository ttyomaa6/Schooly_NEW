package com.egormoroz.schooly.ui.profile;

public class Reason {
    private String reason;

    public Reason(String  reason){
        this.reason=reason;
    }
    public Reason(){
    }

    public String  getReason(){
        return this.reason;
    }

    public void setReason(String  reason){
        this.reason=reason;
    }
}