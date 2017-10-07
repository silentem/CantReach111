package com.whaletail.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class Button extends Image {

    private Texture texture;
    private Sprite sprite;
    private float x;
    private float y;

    public Button(Texture texture) {
        super(texture);
        this.texture = texture;
        sprite = new Sprite(texture);
        setTouchable(Touchable.enabled);
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
    public void setX(float x) {
        setBounds(x, y, getWidth(), getHeight());
        this.x = x;
    }

    @Override
    public void setY(float y) {
        setBounds(x, y, getWidth(), getHeight());
        this.y = y;
    }

}
