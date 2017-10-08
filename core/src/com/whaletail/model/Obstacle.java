package com.whaletail.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Obstacle {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("width")
    @Expose
    private RandomValue width;
    @SerializedName("height")
    @Expose
    private RandomValue height;
    @SerializedName("blankSpace")
    @Expose
    private BlankSpace blankSpace;
    @SerializedName("movement")
    @Expose
    private Integer movement;
    @SerializedName("speed")
    @Expose
    private Integer speed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RandomValue getWidth() {
        return width;
    }

    public void setWidth(RandomValue width) {
        this.width = width;
    }

    public RandomValue getHeight() {
        return height;
    }

    public void setHeight(RandomValue height) {
        this.height = height;
    }

    public BlankSpace getBlankSpace() {
        return blankSpace;
    }

    public void setBlankSpace(BlankSpace blankSpace) {
        this.blankSpace = blankSpace;
    }

    public Movement getMovement() {
        switch (movement) {
            case 0 : {
                return Movement.ROTATE;
            }
            case 1 : {
                return Movement.LINEAR;
            }
            case 2 : {
                return Movement.LINEAR_AND_ROTATE;
            }
            default : {
                throw new RuntimeException("Wrong movement value");
            }
        }
    }

    public void setMovement(Integer movement) {
        this.movement = movement;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
}