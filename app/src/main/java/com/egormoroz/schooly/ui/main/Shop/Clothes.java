package com.egormoroz.schooly.ui.main.Shop;

import com.google.firebase.database.IgnoreExtraProperties;

import java.nio.Buffer;
import java.util.ArrayList;
@IgnoreExtraProperties
public class Clothes {
    private String type;
    private String clothesImage;
    private long clothesPrice=1;
    private long purchaseNumber;
    private String clothesTitle;
    private String currencyType;
    private long timesTamp;
    private String creator;
    private String description;
    private String bodyType;
    private String exclusive;
    String model;
    String uid;
    long purchaseToday;
    Buffer buffer;
    Float x,y,z,transformRatio;


    public Clothes(String type, String  clothesImage, long clothesPrice,String clothesTitle
            ,long purchaseNumber,long timesTamp,String creator,String currencyType,String description,
                   String model,long purchaseToday,String bodyType,String uid,String exclusive,Buffer buffer
    ,Float x,Float y,Float z,Float transformRatio){
        this.type=type;
        this.clothesImage=clothesImage;
        this.clothesPrice=clothesPrice;
        this.clothesTitle=clothesTitle;
        this.purchaseNumber=purchaseNumber;
        this.timesTamp=timesTamp;
        this.creator=creator;
        this.currencyType=currencyType;
        this.description=description;
        this.model=model;
        this.purchaseToday=purchaseToday;
        this.bodyType=bodyType;
        this.uid=uid;
        this.exclusive=exclusive;
        this.buffer=buffer;
        this.x=x;
        this.y=y;
        this.z=z;
        this.transformRatio=transformRatio;
    }
    public Clothes(){
    }

    public String  getClothesType(){
        return this.type;
    }

    public void setClothesType(String  type){
        this.type=type;
    }

    public String getClothesImage(){
        return this.clothesImage;
    }

    public void setClothesImage(String clothesImage){
        this.clothesImage=clothesImage;
    }

    public long getClothesPrice(){
        return this.clothesPrice;
    }

    public void setClothesPrice(long clothesPrice){
        this.clothesPrice=clothesPrice;
    }

    public String getClothesTitle(){
        return this.clothesTitle;
    }

    public void setClothesTitle(String clothesTitle){
        this.clothesTitle=clothesTitle;
    }

    public long getPurchaseNumber(){
        return this.purchaseNumber;
    }

    public void setPurchaseNumber(long purchaseNumber){
        this.purchaseNumber=purchaseNumber;
    }

    public String getCreator(){
        return this.creator;
    }

    public void setCreator(String creator){
        this.creator=creator;
    }

    public String getCurrencyType(){
        return this.currencyType;
    }

    public void setCurrencyType(String currencyType){
        this.currencyType=currencyType;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public long getTimesTamp(){
        return this.timesTamp;
    }

    public void setTimesTamp(long timesTamp){
        this.timesTamp=timesTamp;
    }

    public String getModel(){
        return this.model;
    }

    public void setModel(String model){
        this.model=model;
    }

    public long getPurchaseToday(){
        return this.purchaseToday;
    }

    public void setPurchaseToday(long purchaseToday){
        this.purchaseToday=purchaseToday;
    }

    public String getBodyType(){
        return this.bodyType;
    }

    public void setBodyType(String bodyType){
        this.bodyType=bodyType;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public String getExclusive(){
        return this.exclusive;
    }

    public void setExclusive(String exclusive){
        this.exclusive=exclusive;
    }

    public Buffer getBuffer(){
        return this.buffer;
    }

    public void setBuffer(Buffer buffer){
        this.buffer=buffer;
    }

    public Float getX(){
        return this.x;
    }

    public void setX(Float x){
        this.x=x;
    }

    public Float getY(){
        return this.y;
    }

    public void setY(Float y){
        this.y=y;
    }

    public Float getZ(){
        return this.z;
    }

    public void setZ(Float z){
        this.z=z;
    }

    public Float getTransformRatio(){
        return this.transformRatio;
    }

    public void setTransformRatio(Float transformRatio){
        this.transformRatio=transformRatio;
    }

}