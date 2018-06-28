package com.whaletail.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class Text extends Actor {

    private String text;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private float x;
    private float y;
    private Stage stage;

    public Text(String text, BitmapFont font) {
        this.text = text;
        long start = System.currentTimeMillis();
        this.font = font;
        System.out.println(System.currentTimeMillis() - start);
        glyphLayout = new GlyphLayout(font, text);
        setTouchable(Touchable.enabled);
    }

    public Text(String text, BitmapFont font, Stage stage) {
        this.text = text;
        this.stage = stage;
        this.font = font;
        glyphLayout = new GlyphLayout(font, text);
        setTouchable(Touchable.enabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (stage != null) {
            font.draw(batch, text,
                    stage.getWidth() - getWidth() - 10,
                    stage.getCamera().position.y + stage.getHeight() / 2 - 10);

        } else {
            font.draw(batch, text, x, y);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.x = x;
        this.y = y;
        setBounds(x, y- getHeight(), getWidth(), getHeight());
    }

    @Override
    public void setX(float x) {
        this.x = x;
        setBounds(x, y, getWidth(), getHeight());
    }

    @Override
    public void setY(float y) {
        this.y = y;
        setBounds(x, y, getWidth(), getHeight());
    }

    public void setText(String text) {
        this.text = text;
        glyphLayout.setText(font, text);
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
