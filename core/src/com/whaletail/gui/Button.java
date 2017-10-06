package com.whaletail.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class Button extends Actor {

    private Texture texture;
    private Sprite sprite;
    private float x;
    private float y;

    public Button(Texture texture) {
        this(texture, 0, 0);
    }

    private Button(Texture texture, float x, float y) {
        this.texture = texture;
        sprite = new Sprite(texture);
        this.x = x;
        this.y = y;
        setTouchable(Touchable.enabled);
        setBounds(x, y, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    public float getWidth() {
        return texture.getWidth();
    }

    @Override
    public float getHeight() {
        return texture.getHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, x, y, getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        setBounds(x, y, getWidth(), getHeight());
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        setBounds(x, y, getWidth(), getHeight());
        this.y = y;
    }

}
