package com.whaletail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RandomPoint {

    @SerializedName("x")
    @Expose
    private RandomValue x;
    @SerializedName("y")
    @Expose
    private RandomValue y;

    public RandomValue getX() {
        return x;
    }

    public void setX(RandomValue x) {
        this.x = x;
    }

    public RandomValue getY() {
        return y;
    }

    public void setY(RandomValue y) {
        this.y = y;
    }

}