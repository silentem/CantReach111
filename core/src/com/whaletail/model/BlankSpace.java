package com.whaletail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlankSpace {

    @SerializedName("topLeft")
    @Expose
    private RandomPoint topLeft;
    @SerializedName("bottomRight")
    @Expose
    private RandomPoint bottomRight;

    public RandomPoint getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(RandomPoint topLeft) {
        this.topLeft = topLeft;
    }

    public RandomPoint getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(RandomPoint bottomRight) {
        this.bottomRight = bottomRight;
    }

}