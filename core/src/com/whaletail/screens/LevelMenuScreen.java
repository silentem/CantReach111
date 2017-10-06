package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.whaletail.CantReachGame;

import static com.whaletail.CantReachGame.V_HEIGHT;
import static com.whaletail.CantReachGame.V_WIDTH;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LevelMenuScreen extends BaseScreen {
    

    private CantReachGame game;
    private Stage stage;

    public LevelMenuScreen(CantReachGame game) {
        this.game = game;
        stage = new Stage(new ExtendViewport(V_WIDTH, V_HEIGHT, game.cam));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Image background = new Image(game.asset.get("background.png", Texture.class));
        stage.addActor(background);
    }

    @Override
    public void render(float delta) {
        update();

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    private void update() {

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
