package com.whaletail.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.whaletail.Constants;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class Text extends Actor {

    private Object text;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private float x;
    private float y;
    private OrthographicCamera camera;

    public Text(Object text, BitmapFont font) {
        this.text = text.toString();
        long start = System.currentTimeMillis();
        this.font = font;
        System.out.println(System.currentTimeMillis() - start);
        glyphLayout = new GlyphLayout(font, text.toString());
    }

    public Text(Object text, BitmapFont font, OrthographicCamera camera) {
        this.text = text.toString();
        this.camera = camera;
        long start = System.currentTimeMillis();
        this.font = font;
        System.out.println(System.currentTimeMillis() - start);
        glyphLayout = new GlyphLayout(font, text.toString());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (camera != null) {
            font.draw(batch, text.toString(),
                    camera.position.x + camera.viewportWidth / 2 - getWidth() - 10,
                    camera.position.y + camera.viewportHeight / 2 - 10);
        } else {
            font.draw(batch, text.toString(), x, y);
        }
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text.toString();
    }

    public void setText(Object text) {
        this.text = text;
        glyphLayout.setText(font, text.toString());
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return glyphLayout.width;
    }

    @Override
    public float getHeight() {
        return glyphLayout.height;
    }
}
