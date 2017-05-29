package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.whaletail.Constants;
import com.whaletail.WhaleGdxGame;
import com.whaletail.gui.Score;
import com.whaletail.gui.Text;
import com.whaletail.sprites.EnemySquare;
import com.whaletail.sprites.PlayerSquare;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.whaletail.Constants.PPM;
import static com.whaletail.sprites.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class PlayScreen implements Screen {

    private static final float GRAVITY = -9.8f;
    public static final String TAP_TUT = "Tap to move \none forward";
    public static final String SWIPE_TUT = "Swipe to \njump over one";

    private WhaleGdxGame game;
    private OrthographicCamera gameCam;
    private Image background;
    private PlayerSquare player;
    private Array<EnemySquare> enemySquares;
    private Text font;
    private int maxSize;
    private EnemySquare last;
    private Box2DDebugRenderer b2dr;
    private Stage stage;
    private Stage stageHUD;
    private Stage stageTutorial;
    //    private CancelButton cancelButton;
    private boolean shouldJump;

    private boolean debug;


    public PlayScreen(WhaleGdxGame game) {
        this.game = game;
        gameCam = game.cam;
        debug = false;
        stage = new Stage(new FillViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam));
        stageHUD = new Stage(new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam));
        b2dr = new Box2DDebugRenderer();
    }

    @Override
    public void show() {

        if (!game.prefs.getBoolean("tutorial_passed", false)) {
            stageTutorial = new Stage(new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam));
            Gdx.input.setInputProcessor(stageTutorial);
            stage.addAction(alpha(.5f));
            stageHUD.addAction(alpha(.5f));
            final Image tapImage = new Image(game.asset.get("tap_icon.png", Texture.class));
            final Image swipeImage = new Image(game.asset.get("swipe_icon.png", Texture.class));
            final Text tapText = new Text(TAP_TUT, game.font30);
            final Text swipeText = new Text(SWIPE_TUT, game.font30);
            tapImage.setPosition(stage.getWidth() / 2 - tapImage.getWidth() / 2, stage.getHeight() / 2 - tapImage.getHeight() / 2);
            tapText.setX(stage.getWidth() / 2 - tapText.getWidth() / 2);
            tapText.setY(0);
            swipeImage.setPosition(stage.getWidth() / 2 - swipeImage.getWidth() / 2, stage.getHeight() / 2 - swipeImage.getHeight() / 2);
            tapText.setX(stage.getWidth() / 2 - swipeText.getWidth() / 2);
            tapText.setY(0);
            stageTutorial.addActor(tapImage);
            stageTutorial.addActor(tapText);

            stageTutorial.addListener(new ActorGestureListener() {
                int count = 0;

                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    this.count++;
                    if (this.count == 1) {
                        System.out.println("1 tap");
                        tapImage.remove();
                        tapText.remove();
                        stageTutorial.addActor(swipeImage);
                        stageTutorial.addActor(swipeText);
                        this.count++;
                    } else if (this.count >= 2) {
                        System.out.println("2 tap");
                        swipeImage.remove();
                        swipeText.remove();
                        stageTutorial.dispose();
                        stageTutorial = null;
                        Gdx.input.setInputProcessor(stage);
                        this.count++;
                    }
                }
            });

            game.prefs.putBoolean("tutorial_passed", true);
            game.prefs.flush();
        } else {
            stageTutorial = null;
            Gdx.input.setInputProcessor(stage);
        }

        shouldJump = false;
        game.world = new World(new Vector2(0, 0), true);
        player = new PlayerSquare(game, gameCam, stage.getViewport().getWorldWidth() / 2, 64);
        enemySquares = new Array<>();
        game.score = new Score();
        font = new Text(game.score, game.font30, stage);
        maxSize = 16;
        background = new Image(game.asset.get("background.png", Texture.class)) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(game.asset.get("background.png", Texture.class),
                        0, game.cam.position.y - stage.getHeight() / 2);
            }
        };
        stage.addActor(background);
        background.setZIndex(0);
        stage.addActor(player);
        int speed = 0;
        for (int i = 0; i < maxSize; i++) {
            speed = getRandomSpeed(speed);
            EnemySquare enemySquare = new EnemySquare(
                    player.getY() + ENEMY_SPACE + i * ENEMY_SPACE, speed, gameCam.viewportWidth, game);
            enemySquares.add(enemySquare);
            stage.addActor(enemySquare);
        }
        stageHUD.addActor(font);
        last = enemySquares.get(maxSize - 1);
//        cancelButton = new CancelButton();
//        stage.addActor(cancelButton);
        stage.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (!player.animated && !player.isDead()) {
                    game.score.add(1);
                    font.setText(game.score);
                    System.out.println("Score = " + game.score);
                    player.move();
                    shouldJump = false;
                }
            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                if (!player.animated && shouldJump) {
                    game.score.add(2);
                    font.setText(game.score);
                    System.out.println("Score = " + game.score);
                    player.state = PlayerSquare.State.TELEPORTING;
                }
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
//                Rectangle cBRect = new Rectangle(cancelButton.getX(), cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
//                if (cBRect.contains(x, y)) {
//                    shouldJump = false;
//                } else {
//                    shouldJump = true;
//                }
            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                shouldJump = true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                cancelIsShown = false;
//                Rectangle cBRect = new Rectangle(cancelButton.getX(), cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
//                if (cBRect.contains(x, y)) {
//                    shouldJump = false;
//
//                    player.move();
//                } else {
//                    shouldJump = true;
//                }
            }
        });
    }


    private int getRandomSpeed(int prev) {
        int speed = 0;
        if (0 <= game.score.getScore() && game.score.getScore() <= 30) {
            if (prev < 5) {
                return MathUtils.random(5) + 4;
            } else {
                return MathUtils.random(3) + 3;
            }
        } else if (30 < game.score.getScore() && game.score.getScore() <= 60) {
            if (prev < 5) {
                return MathUtils.random(8) + 5;
            } else {
                return MathUtils.random(4) + 3;
            }
        } else if (60 < game.score.getScore()) {
            if (prev < 5) {
                return MathUtils.random(8) + 7;
            } else {
                return MathUtils.random(5) + 4;
            }
        }
        return speed;
    }

    private void lose() {
        game.world.setGravity(new Vector2(0, GRAVITY));
        player.lose();
        System.out.println("You lost");
        float delay = 2f;

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                int bestScore = game.prefs.getInteger("best_score", 0);
                if (game.score.getScore() > bestScore) {
                    game.prefs.putInteger("best_score", game.score.getScore());
                    game.prefs.flush();
                }
                game.setScreen(game.lossScreen);
            }

        }, delay);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42 / 256f, 44 / 256f, 44 / 256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        player.update();

        player.render();
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();

        stageHUD.getViewport().apply();
        stageHUD.act(delta);
        stageHUD.draw();

        if (stageTutorial != null) {
            stageTutorial.getViewport().apply();
            stageTutorial.act(delta);
            stageTutorial.draw();
        }
//        b2dr.render(game.world, gameCam.combined.scl(PPM));

    }

    public Stage getStage() {
        return stage;
    }

    private void update(float delta) {
        game.world.step(1 / 60f, 6, 2);

        for (EnemySquare enemySquare : enemySquares) {
            if (enemySquare.getY() < gameCam.position.y - gameCam.viewportHeight / 2) {
                enemySquare.createNew(last.getY() + last.getHeight() + ENEMY_SPACE - last.getHeight(), getRandomSpeed(last.getSpeed()));
                last = enemySquare;
            }
            if (enemySquare.getX() - enemySquare.getWidth() / 2 > gameCam.viewportWidth && enemySquare.isRightLeft()) {
                enemySquare.reset();
            } else if (enemySquare.getX() < 0 - enemySquare.getWidth() && !enemySquare.isRightLeft()) {
                enemySquare.reset();
            }
            if (player.collides(enemySquare)) {
                lose();

            }
            if (player.isDead()) {
                enemySquare.stop();
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        stageHUD.clear();
    }

    @Override
    public void dispose() {
        System.out.println("PlayScreen.dispose");
        stageHUD.dispose();
        stage.dispose();
        game.world.dispose();
    }

//    private class CancelButton extends Actor {
//        private Texture cancelButtonTexture;
//        private float x;
//
//        private float y;
//
//        public CancelButton() {
//            cancelButtonTexture = game.asset.get("cancelButton.png", Texture.class);
//            setBounds(getX(), getY(), getWidth(), getHeight());
//        }
//
//        @Override
//        public void draw(Batch batch, float parentAlpha) {
//            if (cancelIsShown && !player.isDead()) {
//                batch.draw(cancelButtonTexture, getX(), getY());
//            }
//        }
//
//        @Override
//        public float getWidth() {
//            return cancelButtonTexture.getWidth();
//        }
//
//        @Override
//        public float getHeight() {
//            return cancelButtonTexture.getHeight();
//        }
//
//        @Override
//        public float getX() {
//            return x = gameCam.position.x + gameCam.viewportWidth / 2 - getWidth();
//        }
//
//        @Override
//        public float getY() {
//            return y = gameCam.position.y - gameCam.viewportHeight / 4;
//        }
//
//        public void dispose() {
//            cancelButtonTexture.dispose();
//        }
//
//    }

}
