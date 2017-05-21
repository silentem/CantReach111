package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.WhaleGdxGame;
import com.whaletail.gui.Button;
import com.whaletail.gui.Text;
import com.whaletail.gui.ViewActor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.whaletail.Constants.CAN_NOT;
import static com.whaletail.Constants.NUMBER;
import static com.whaletail.Constants.REACH;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LossScreen implements Screen {

    private WhaleGdxGame game;
    private Texture background;
    private Image againButton;
    private Button homeButton;
    private Text scoreText;
    private Text bestScoreText;
    private OrthographicCamera cam;
    private Stage stage;

    private ViewActor view1;
    private ViewActor view2;

    private Text canNotText;
    private Text reachText;
    private Text numberText;

    public LossScreen(WhaleGdxGame game) {
        this.game = game;
        cam = game.cam;
        stage = new Stage(new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, cam));

    }

    @Override
    public void show() {
        background = game.asset.get("background.png", Texture.class);
        cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
        createGUI();
    }

    private void createGUI() {

        Gdx.input.setInputProcessor(stage);

        againButton = new Image(game.asset.get("againButton.png", Texture.class));
        againButton.setPosition(cam.position.x - againButton.getWidth() / 2,
                cam.position.y / 2 - againButton.getHeight() / 2);
        againButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(game.playScreen);
            }
        });
        againButton.setOrigin(againButton.getWidth() / 2, againButton.getHeight() / 2);
        againButton.setScale(.5f, .5f);
        againButton.addAction(
                forever(
                        sequence(
                                scaleTo(.45f, .45f, .55f),
                                scaleTo(.5f, .5f, .55f))));


        homeButton = new Button(game.asset.get("homeButton.png", Texture.class));
        homeButton.setPosition(10,
                cam.viewportHeight - homeButton.getHeight() - 50);
        homeButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.setScreen(game.menuScreen);
            }

        });

        scoreText = new Text("Your score: " + game.score, game.font30);
        scoreText.setX(cam.position.x - scoreText.getWidth()/2);
        scoreText.setY(cam.position.y - scoreText.getHeight()/2);

        bestScoreText = new Text("Your best score: " + game.prefs.getInteger("best_score", 0), game.font30);
        bestScoreText.setX(cam.position.x - bestScoreText.getWidth()/2);
        bestScoreText.setY(scoreText.getY() - scoreText.getHeight() - 10);

        canNotText = new Text(CAN_NOT, game.font90);
        canNotText.setX(game.cam.position.x - canNotText.getWidth() / 2 - 50);
        canNotText.setY(game.cam.viewportHeight - 50);

        reachText = new Text(REACH, game.font90);
        reachText.setX(game.cam.position.x - canNotText.getWidth() / 2);
        reachText.setY(canNotText.getY() - canNotText.getHeight());

        numberText = new Text(NUMBER, game.font90);
        numberText.setX(game.cam.position.x - canNotText.getWidth() / 2 + 50);
        numberText.setY(reachText.getY() - reachText.getHeight());

        stage.addActor(scoreText);
        stage.addActor(bestScoreText);
        stage.addActor(canNotText);
        stage.addActor(reachText);
        stage.addActor(numberText);
        stage.addActor(againButton);
        stage.addActor(homeButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42 / 256f, 44 / 256f, 44 / 256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.batch.draw(background,
                cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2);
        game.batch.end();
        update(delta);
        stage.draw();
    }

    private void update(float delta) {
        stage.act(delta);
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
        stage.clear();
    }

    @Override
    public void dispose() {
        System.out.println("LossScreen.dispose");
        stage.clear();
        stage.dispose();
    }

}
