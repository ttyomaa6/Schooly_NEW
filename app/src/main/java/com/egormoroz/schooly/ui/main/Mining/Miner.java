package com.egormoroz.schooly.ui.main.Mining;

public class Miner {
    private long inHour=1;
    private String minerImage;
    private long minerPrice=1;

    public Miner(long inHour, String  minerImage, long minerPrice){
        this.inHour=inHour;
        this.minerImage=minerImage;
        this.minerPrice=minerPrice;
    }
    public Miner(){
    }

    public long getInHour(){
        return this.inHour;
    }

    public void setInHour(long inHour){
        this.inHour=inHour;
    }

    public String  getMinerImage(){
        return this.minerImage;
    }

    public void setMinerImage(String  minerImage){
        this.minerImage=minerImage;
    }

    public long getMinerPrice(){
        return this.minerPrice;
    }

    public void setMinerPrice(long minerPrice){
        this.minerPrice=minerPrice;
    }

}
