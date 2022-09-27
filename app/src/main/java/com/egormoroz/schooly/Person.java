package com.egormoroz.schooly;

public class Person {
    private FacePart hair;
    private FacePart brows;
    private FacePart eyes;
    private FacePart nose;
    private FacePart lips;
    private FacePart skinColor;
    private FacePart mustache;
    private FacePart body;
    private FacePart ears;
    private FacePart head;

    public Person(FacePart hair,FacePart brows,FacePart eyes,FacePart nose,FacePart lips,
                  FacePart skinColor,FacePart mustache,FacePart body,FacePart ears,FacePart head){
        this.hair=hair;
        this.brows=brows;
        this.eyes=eyes;
        this.nose=nose;
        this.lips=lips;
        this.skinColor=skinColor;
        this.mustache=mustache;
        this.body=body;
        this.ears=ears;
        this.head=head;
    }

    public Person(){

    }

    public FacePart  getHair(){
        return this.hair;
    }

    public void setHair(FacePart  hair){
        this.hair=hair;
    }

    public FacePart getBrows(){
        return this.brows;
    }

    public void setBrows(FacePart brows){
        this.brows=brows;
    }

    public FacePart  getEyes(){
        return this.eyes;
    }

    public void setEyes(FacePart  eyes){
        this.eyes=eyes;
    }

    public FacePart getNose(){
        return this.nose;
    }

    public void setNose(FacePart nose){
        this.nose=nose;
    }

    public FacePart  getLips(){
        return this.lips;
    }

    public void setLips(FacePart  lips){
        this.lips=lips;
    }

    public FacePart getSkinColor(){
        return this.skinColor;
    }

    public void setSkinColor(FacePart skinColor){
        this.skinColor=skinColor;
    }

    public FacePart  getMustache(){
        return this.mustache;
    }

    public void setMustache(FacePart  mustache){
        this.mustache=mustache;
    }

    public FacePart getBody(){
        return this.body;
    }

    public void setBody(FacePart body){
        this.body=body;
    }

    public FacePart  getEars(){
        return this.ears;
    }

    public void setEars(FacePart  ears){
        this.ears=ears;
    }

    public FacePart getHead(){
        return this.head;
    }

    public void setHead(FacePart head){
        this.head=head;
    }
}
