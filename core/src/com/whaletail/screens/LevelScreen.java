package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.whaletail.CantReachGame;
import com.whaletail.actors.EnemySquare;
import com.whaletail.gui.MenuScreenViewActor;
import com.whaletail.gui.Text;
import com.whaletail.gui.ViewActor;
import com.whaletail.listeners.LevelDifficultyActorGestureListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.whaletail.CantReachGame.V_HEIGHT;
import static com.whaletail.CantReachGame.V_WIDTH;
import static com.whaletail.Constants.EASY;
import static com.whaletail.Constants.HARD;
import static com.whaletail.Constants.MEDIUM;

public class LevelScreen extends BaseScreen {


    private Text easyFont;
    private Text mediumFont;
    private Text hardFont;
    private ViewActor shim;

    private Texture foreground;
    private CantReachGame game;
    private Stage stage;
    private ViewActor[] viewActors;

    public LevelScreen(CantReachGame game) {
        this.game = game;
        stage = new Stage(new FillViewport(V_WIDTH, V_HEIGHT, game.cam));
        viewActors = new ViewActor[3];
    }

    @Override
    public void show() {

        Image background = new Image(game.asset.get("background.png", Texture.class));
        foreground = game.asset.get("foreground.png", Texture.class);
        Gdx.input.setInputProcessor(stage);

        setupMainText();
        viewActors[0] = new MenuScreenViewActor(easyFont, new EnemySquare.View(game.asset.get("enemy-1.png", Texture.class), 20, 3));
        viewActors[1] = new MenuScreenViewActor(mediumFont, new EnemySquare.View(game.asset.get("enemy-3.png", Texture.class), 10, 1));
        viewActors[2] = new MenuScreenViewActor(hardFont, new EnemySquare.View(game.asset.get("enemy-2.png", Texture.class), 20, 2));

        stage.addActor(background);

        for (ViewActor viewActor : viewActors) {
            stage.addActor(viewActor);
        }

        stage.addActor(easyFont);
        stage.addActor(mediumFont);
        stage.addActor(hardFont);

        for (ViewActor viewActor : viewActors) {
            viewActor.move(.4f);
        }
    }

    private void update(){
        if (shim != null && shim.getX() + shim.getWidth() >= stage.getWidth()) {
            shim.addAction(sequence(delay(.05f), run(
                    new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(game.gameScreen);
                        }
                    })));
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update();
        stage.act(delta);
        stage.draw();
    }

    private void setupMainText() {
        mediumFont = new Text(MEDIUM, game.font90);
        mediumFont.setPosition(game.cam.viewportWidth - mediumFont.getWidth(), game.cam.position.y - mediumFont.getHeight() / 2);
        easyFont = new Text(EASY, game.font90);
        easyFont.setPosition(game.cam.viewportWidth - easyFont.getWidth(), mediumFont.getY() + mediumFont.getHeight() + 25);
        hardFont = new Text(HARD, game.font90);
        hardFont.setPosition(game.cam.viewportWidth - hardFont.getWidth(), mediumFont.getY() - hardFont.getHeight() - 25);

        easyFont.addListener(new LevelDifficultyActorGestureListener(this, 0));
        mediumFont.addListener(new LevelDifficultyActorGestureListener(this, 1));
        hardFont.addListener(new LevelDifficultyActorGestureListener(this, 2));

    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    public void startGame(int difficulty) {
        game.difficulty = difficulty;
        ViewActor viewActor = viewActors[difficulty];
        viewActor.setDirection(new Vector2(stage.getWidth(), viewActor.getY()));
        viewActor.move((MathUtils.random(3) + 3) / 10f);EnemySquare.View view = new EnemySquare.View(foreground, 1, 1);
        shim = new ViewActor(new Vector2(0 - view.getWidth(), 0),
                new Vector2(0, 0),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.25f), moveTo(this.direction.x, this.direction.y, speed)));
            }
        };
        stage.addActor(shim);
        shim.toFront();
        shim.move(.7f);
    }
}
