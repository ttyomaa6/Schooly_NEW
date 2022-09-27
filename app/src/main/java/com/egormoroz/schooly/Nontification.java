package com.egormoroz.schooly;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Nontification {

    private String nick;
    private String typeDispatch;
    private String typeView;
    //    private HashMap<String, Object> timestamp;
    private String timestamp;
    private String clothesName;
    private String clothesImage;
    private String type;
    private String uid;
    double clothesProfit;

    public Nontification(String  nick,String typeDispatch,String typeView,String timestamp
            ,String clothesName,String clothesImage,String type,String uid,double clothesProfit){
        this.nick=nick;
        this.typeDispatch=typeDispatch;
        this.typeView=typeView;
        HashMap<String, Object> timestampObj = new HashMap<String, Object>();
        timestampObj.put("date", ServerValue.TIMESTAMP);
        this.timestamp = timestamp;
        this.clothesImage=clothesImage;
        this.clothesName=clothesName;
        this.type=type;
        this.uid=uid;
        this.clothesProfit=clothesProfit;
    }
    public Nontification(){
    }

    public String  getNick(){
        return this.nick;
    }

    public void setNick(String  nick){
        this.nick=nick;
    }

    public String  getTypeDispatch(){
        return this.typeDispatch;
    }

    public void setTypeDispatch(String  typeDispatch){
        this.typeDispatch=typeDispatch;
    }

    public String  getTypeView(){
        return this.typeView;
    }

    public void setTypeView(String  typeView){
        this.typeView=typeView;
    }

    public String  getTimestamp(){
        return this.timestamp;
    }
    public void setTimestamp(String  timestamp){
        this.timestamp=timestamp;
    }

//    public HashMap<String, Object> getTimestamp() {
//        //If there is a dateCreated object already, then return that
//        if (timestamp != null) {
//            return timestamp;
//        }
//        //Otherwise make a new object set to ServerValue.TIMESTAMP
//        HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
//        dateCreatedObj.put("date", ServerValue.TIMESTAMP);
//        return dateCreatedObj;
//    }
//
//    @Exclude
//    public long getDateCreatedLong() {
//        return (long)timestamp.get("date");
//    }


    public String  getClothesImage(){
        return this.clothesImage;
    }

    public void setClothesImage(String  clothesImage){
        this.clothesImage=clothesImage;
    }

    public String  getClothesName(){
        return this.clothesName;
    }

    public void setClothesName(String  clothesName){
        this.clothesName=clothesName;
    }

    public String  getType(){
        return this.type;
    }

    public void setType(String  type){
        this.type=type;
    }

    public String  getUid(){
        return this.uid;
    }

    public void setUid(String  uid){
        this.uid=uid;
    }

    public double  getClothesProfit(){
        return this.clothesProfit;
    }

    public void setClothesProfit(double  clothesProfit){
        this.clothesProfit=clothesProfit;
    }
}

