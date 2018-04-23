package com.dws.wardrobeos.models;

import io.realm.RealmObject;

public class ClothItem extends RealmObject {

    private String type;
    private String brand;
    private int color;
    private String photo;
    private String info;
    private boolean source;

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {return this.type;}

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {return this.brand;}

    public void setColor(int color) {
        this.color = color;
    }
    public int getColor() {
        return this.color;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {return this.photo;}

    public void setSource(boolean source) {
        this.source = source;
    }

    public boolean getSource() {return this.source;}
}
