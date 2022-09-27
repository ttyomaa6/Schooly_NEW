package com.egormoroz.schooly;

import java.nio.Buffer;

public class FacePart {
    private String type;
    private String partTitle;
    String model;
    String uid;
    Buffer buffer;
    String facePartImage;
    Float colorX,colorY,colorZ;


    public FacePart(String type, String  partTitle,
                    String model,String uid,Buffer buffer,String facePartImage,Float colorX,Float colorY
                    ,Float colorZ
    ){
        this.type=type;
        this.partTitle=partTitle;
        this.model=model;
        this.uid=uid;
        this.buffer=buffer;
        this.facePartImage=facePartImage;
        this.colorX=colorX;
        this.colorY=colorY;
        this.colorZ=colorZ;
    }
    public FacePart(){
    }

    public String  getPartType(){
        return this.type;
    }

    public void setPartType(String  type){
        this.type=type;
    }

    public String getPartTitle(){
        return this.partTitle;
    }

    public void setPartTitle(String partTitle){
        this.partTitle=partTitle;
    }

    public String getModel(){
        return this.model;
    }

    public void setModel(String model){
        this.model=model;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public Buffer getBuffer(){
        return this.buffer;
    }

    public void setBuffer(Buffer buffer){
        this.buffer=buffer;
    }

    public String getFacePartImage(){
        return this.facePartImage;
    }

    public void setFacePartImage(String facePartImage){
        this.facePartImage=facePartImage;
    }

    public Float getColorX(){
        return this.colorX;
    }

    public void setColorX(Float colorX){
        this.colorX=colorX;
    }

    public Float getColorY(){
        return this.colorY;
    }

    public void setColorY(Float colorY){
        this.colorY=colorY;
    }

    public Float getColorZ(){
        return this.colorZ;
    }

    public void setColorZ(Float colorZ){
        this.colorZ=colorZ;
    }


}