package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.CantReachGame;

import static com.whaletail.CantReachGame.V_HEIGHT;
import static com.whaletail.CantReachGame.V_WIDTH;


/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LoadingScreen extends BaseScreen {


    private CantReachGame game;
    private Image logo;
    private final Stage stage;
    private long startTime;


    public LoadingScreen(CantReachGame game) {
        this.game = game;
        logo = new Image(new TextureRegion(new Texture("CompanyIconInverted.png")));
        fitLogoToScreen();
        stage = new Stage(new FitViewport(V_WIDTH, V_HEIGHT, game.cam));
        stage.addActor(logo);
    }

    private void fitLogoToScreen() {
        int imageLength = V_WIDTH;
        logo.setWidth(logo.getWidth() * imageLength / logo.getHeight());
        logo.setHeight(imageLength);
        logo.setPosition(game.cam.viewportWidth / 2 - logo.getWidth() / 2,
                game.cam.viewportHeight / 2 - logo.getHeight() / 2);
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

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();

        update();
    }

    private void update() {
        if (game.asset.update() && TimeUtils.timeSinceMillis(startTime) > 1000) {
            game.setScreen(game.menuScreen);
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
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
        game.asset.load("baseline_favorite_white_48.png", Texture.class);
        game.asset.load("baseline_favorite_border_white_48.png", Texture.class);
    }

}
