package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import com.whaletail.WhaleGdxGame;


/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LoadingScreen implements Screen {


    private WhaleGdxGame game;
    private TextureRegion logo;
    private long startTime;


    public LoadingScreen(WhaleGdxGame game) {
        this.game = game;
        logo = new TextureRegion(new Texture("CompanyIconInverted.png"));
    }

    @Override
    public void show() {
        startTime = System.currentTimeMillis();
        queueAssets();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42 / 256f, 44 / 256f, 44 / 256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(game.cam.combined);

        game.batch.begin();
        game.batch.draw(logo,
                game.cam.viewportWidth / 2 - logo.getTexture().getWidth() / 2,
                game.cam.viewportHeight / 2 - logo.getTexture().getHeight() / 2,
                logo.getTexture().getWidth() / 2, logo.getTexture().getHeight() / 2,
                logo.getTexture().getWidth(), logo.getTexture().getHeight(), .25f, .25f, 0);
        game.batch.end();

        update(delta);
    }

    private void update(float delta) {
        if (game.asset.update() && TimeUtils.timeSinceMillis(startTime) > 1000) {
            game.setScreen(game.menuScreen);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        logo.getTexture().dispose();
    }

    private void queueAssets() {
        game.asset.load("background.png", Texture.class);
        game.asset.load("musicButton.png", Texture.class);
        game.asset.load("noMusicButton.png", Texture.class);
        game.asset.load("swipe_icon.png", Texture.class);
        game.asset.load("tap_icon.png", Texture.class);
        game.asset.load("foreground.png", Texture.class);
        game.asset.load("playButton.png", Texture.class);
        game.asset.load("enemy-1.png", Texture.class);
        game.asset.load("enemy-2.png", Texture.class);
        game.asset.load("enemy-3.png", Texture.class);
        game.asset.load("enemy-4.png", Texture.class);
        game.asset.load("player-1.png", Texture.class);
        game.asset.load("player-2.png", Texture.class);
        game.asset.load("player-3.png", Texture.class);
        game.asset.load("player-4.png", Texture.class);
        game.asset.load("againButton.png", Texture.class);
        game.asset.load("homeButton.png", Texture.class);
    }

}
