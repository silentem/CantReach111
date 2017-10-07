package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.whaletail.CantReachGame;
import com.whaletail.gui.LevelButton;

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

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        ActorGestureListener levelButtonListener = new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(game.gameScreen);
            }
        };
        for (int i = 1; i <= 16; i++) {
            LevelButton actor = new LevelButton(game.asset.get("enemy-4.png", Texture.class), game.font30, game.font20, i, "0/111");
            actor.addListener(levelButtonListener);
            table.add(actor).size(64).pad(10);
            if (i % 4 == 0) {
                table.row();
            }
        }
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
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void dispose() {
        stage.dispose();
    }
}
