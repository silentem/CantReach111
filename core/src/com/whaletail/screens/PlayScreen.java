package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.CantReachGame;
import com.whaletail.actors.EnemySquare;
import com.whaletail.actors.PlayerSquare;
import com.whaletail.gui.Score;
import com.whaletail.gui.Text;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.whaletail.actors.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class PlayScreen implements Screen {

    private static final float GRAVITY = -9.8f;
    private static final String TAP_TUT = "Tap to move \none forward";
    private static final String SWIPE_TUT = "Swipe to \njump over one";
    private static final String CAN_REACH_TUT = "Can you \nreach 111?";

    private CantReachGame game;
    private OrthographicCamera gameCam;
    private PlayerSquare player;
    private Array<EnemySquare> enemySquares;
    private Text font;
    private static final int MAX_SIZE = 16;
    private EnemySquare last;
    private Box2DDebugRenderer b2dr;
    private Stage stage;
    private Stage stageHUD;
    private Stage stageTutorial;
    //    private CancelButton cancelButton;
    private boolean shouldJump;

    public PlayScreen(CantReachGame game) {
        this.game = game;
        gameCam = game.cam;
        stage = new Stage(new FillViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
        stageHUD = new Stage(new FitViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
        b2dr = new Box2DDebugRenderer();
    }

    @Override
    public void show() {

        if (!game.prefs.getBoolean("tutorial_passed", false)) {
            stageTutorial = new Stage(new FitViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
            Gdx.input.setInputProcessor(stageTutorial);
            stage.addAction(alpha(.5f));
            stageHUD.addAction(alpha(.5f));
            final Image tapImage = new Image(game.asset.get("tap_icon.png", Texture.class));
            final Image swipeImage = new Image(game.asset.get("swipe_icon.png", Texture.class));
            Label.LabelStyle ls = new Label.LabelStyle();
            ls.font = game.font30;
            final Label tapText = new Label(TAP_TUT, ls);
            final Label swipeText = new Label(SWIPE_TUT, ls);
            final Label canReachText = new Label(CAN_REACH_TUT, ls);
            tapImage.setPosition(stage.getWidth() / 2 - tapImage.getWidth() / 2, stage.getHeight() / 2 - tapImage.getHeight() / 2);
            tapText.setX(stage.getWidth() / 2 - tapText.getWidth() / 2);
            tapText.setY(tapImage.getY() - tapText.getHeight() - 50);
            swipeImage.setPosition(stage.getWidth() / 2 - swipeImage.getWidth() / 2, stage.getHeight() / 2 - swipeImage.getHeight() / 2);
            swipeText.setX(stage.getWidth() / 2 - swipeText.getWidth() / 2);
            swipeText.setY(swipeImage.getY() - swipeText.getHeight() - 50);
            canReachText.setPosition(stage.getWidth() / 2 - canReachText.getWidth() / 2,
                    stage.getHeight() / 2 - canReachText.getHeight() / 2);
            stageTutorial.addActor(tapImage);
            stageTutorial.addActor(tapText);

            stageTutorial.addListener(new ActorGestureListener() {
                int count = 0;

                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    this.count++;
                    if (this.count == 1) {
                        System.out.println("1 tap");
                        System.out.println(count);
                        tapImage.remove();
                        tapText.remove();
                        stageTutorial.addActor(swipeImage);
                        stageTutorial.addActor(swipeText);
                    } else if (this.count == 2) {
                        System.out.println("2 tap");
                        System.out.println(count);
                        swipeImage.remove();
                        swipeText.remove();
                        stageTutorial.addActor(canReachText);
                    } else if (this.count == 3) {
                        System.out.println(count);
                        canReachText.remove();
                        stageTutorial.dispose();
                        stageTutorial = null;
                        stage.addAction(alpha(1f));
                        stageHUD.addAction(alpha(1f));
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
        Image background = new Image(game.asset.get("background.png", Texture.class)) {
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
        for (int i = 0; i < MAX_SIZE; i++) {
            speed = getRandomSpeed(speed);
            EnemySquare enemySquare = new EnemySquare(
                    player.getY() + ENEMY_SPACE + i * ENEMY_SPACE, speed, gameCam.viewportWidth, game);
            enemySquares.add(enemySquare);
            stage.addActor(enemySquare);
        }
        stageHUD.addActor(font);
        last = enemySquares.get(MAX_SIZE - 1);
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
                if (!player.animated && shouldJump && !player.isDead()) {
                    game.score.add(2);
                    font.setText(game.score);
                    System.out.println("Score = " + game.score);
                    player.jump();
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
                return MathUtils.random(3) + 4;
            } else {
                return MathUtils.random(2) + 2;
            }
        } else if (30 < game.score.getScore() && game.score.getScore() <= 60) {
            if (prev < 7) {
                return MathUtils.random(2) + 5;
            } else {
                return MathUtils.random(3) + 3;
            }
        } else if (60 < game.score.getScore()) {
            if (prev < 8) {
                return MathUtils.random(2) + 6;
            } else {
                return MathUtils.random(3) + 4;
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

        update();
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

    private void update() {
        game.world.step(1 / 60f, 6, 2);


        if (game.score.getScore() == 111) {
            win();
        }
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

    private void win() {
        game.world.setGravity(new Vector2(0, GRAVITY));
        player.setInvulnerable(true);
        float delay = 4f;

        game.prefs.putBoolean("win", true);
        game.prefs.flush();

        player.win();
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.win();
        }

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                game.setScreen(game.lossScreen);
            }

        }, delay);
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
        player.dispose();
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.dispose();
        }
        game.world.dispose();
    }

}
