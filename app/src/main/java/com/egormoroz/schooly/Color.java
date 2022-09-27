package com.egormoroz.schooly;

import java.nio.Buffer;

public class Color {

    Float colorX, colorY, colorZ;
    int viewR,viewG,viewB;


    public Color(Float colorX, Float colorY, Float colorZ,int viewR,int viewG,int viewB
    ) {
        this.colorX = colorX;
        this.colorY = colorY;
        this.colorZ = colorZ;
        this.viewR = viewR;
        this.viewG = viewG;
        this.viewB = viewB;
    }

    public Color() {
    }

    public Float getColorX() {
        return this.colorX;
    }

    public void setColorX(Float colorX) {
        this.colorX = colorX;
    }

    public Float getColorY() {
        return this.colorY;
    }

    public void setColorY(Float colorY) {
        this.colorY = colorY;
    }

    public Float getColorZ() {
        return this.colorZ;
    }

    public void setColorZ(Float colorZ) {
        this.colorZ = colorZ;
    }

    public int getViewR() {
        return this.viewR;
    }

    public void setViewR(int viewR) {
        this.viewR = viewR;
    }

    public int getViewG() {
        return this.viewG;
    }

    public void setViewG(int viewG) {
        this.viewG = viewG;
    }

    public int getViewB() {
        return this.viewB;
    }

    public void setViewB(int viewB) {
        this.viewB = viewB;
    }
}

