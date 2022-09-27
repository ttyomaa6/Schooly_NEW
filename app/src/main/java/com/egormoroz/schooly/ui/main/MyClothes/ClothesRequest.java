package com.egormoroz.schooly.ui.main.MyClothes;

public class ClothesRequest {
    private String type;
    private String clothesImage;
    private long clothesPrice=1;
    private String clothesTitle;
    private String currencyType;
    private long timesTamp;
    private String creator;
    private String description;
    private String bodyType;
    private String uid;
    private String exclusive;
    String model;


    public ClothesRequest(String type, String  clothesImage, long clothesPrice,String clothesTitle
            ,long timesTamp,String creator,String currencyType,String description,
                          String model,String bodyType,String uid,String exclusive){
        this.type=type;
        this.clothesImage=clothesImage;
        this.clothesPrice=clothesPrice;
        this.clothesTitle=clothesTitle;
        this.timesTamp=timesTamp;
        this.creator=creator;
        this.currencyType=currencyType;
        this.description=description;
        this.model=model;
        this.bodyType=bodyType;
        this.uid= uid;
        this.exclusive=exclusive;

    }
    public ClothesRequest(){
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
}
