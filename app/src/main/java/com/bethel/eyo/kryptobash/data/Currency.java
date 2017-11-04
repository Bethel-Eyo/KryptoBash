package com.bethel.eyo.kryptobash.data;



public class Currency {
    String name, sign;
    int thumbnail;

    public Currency(String name, String sign, int thumbnail) {
        this.name = name;
        this.sign = sign;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {

        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
