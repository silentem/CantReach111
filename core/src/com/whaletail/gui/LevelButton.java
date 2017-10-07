package com.whaletail.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LevelButton extends Image {

    private String level;
    private BitmapFont font;
    private BitmapFont progressFont;
    private GlyphLayout[] glyphLayout;
    private String progress;

    public LevelButton(Texture texture, BitmapFont font, BitmapFont progressFont, int level, String progress) {
        super(texture);
        this.level = Integer.toString(level);
        this.font = font;
        this.progressFont = progressFont;
        this.progress = progress;
        glyphLayout = new GlyphLayout[2];
        glyphLayout[0] = new GlyphLayout(font, this.level);
        glyphLayout[1] = new GlyphLayout(progressFont, this.progress);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        font.draw(batch, level, getX() + getWidth() / 2 - glyphLayout[0].width / 2, getY() + getHeight() / 2 + glyphLayout[0].height / 2);
        progressFont.draw(batch, progress, getX() + getWidth() / 2 - glyphLayout[1].width / 2, getY() - 2);

    }
}
