package com.whaletail.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private Stage stage;

    public Text(Object text, BitmapFont font) {
        this.text = text.toString();
        long start = System.currentTimeMillis();
        this.font = font;
        System.out.println(System.currentTimeMillis() - start);
        glyphLayout = new GlyphLayout(font, text.toString());
    }

    public Text(Object text, BitmapFont font, Stage stage) {
        this.text = text.toString();
        this.stage = stage;
        long start = System.currentTimeMillis();
        this.font = font;
        System.out.println(System.currentTimeMillis() - start);
        glyphLayout = new GlyphLayout(font, text.toString());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (stage != null) {
            font.draw(batch, text.toString(),
                    stage.getWidth()  - getWidth() - 10,
                    stage.getCamera().position.y + stage.getHeight()/2 - 10);

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
