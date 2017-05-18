package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.whaletail.WhaleGdxGame;
import com.whaletail.sprites.EnemySquare;
import com.whaletail.sprites.PlayerSquare;

import static com.whaletail.Constants.PPM;
import static com.whaletail.sprites.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class PlayScreen implements Screen {

    private static final float GRAVITY = -9.8f;

    private int score;
    private WhaleGdxGame game;
    private static OrthographicCamera gameCam;
    private Viewport gamePort;
    private Texture background;
    private PlayerSquare player;
    private Array<EnemySquare> enemySquares;
    private BitmapFont font;
    private BitmapFont.Glyph glyph;
    private int maxSize;
    private EnemySquare last;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Stage stage;
    private boolean cancelIsShown;
    private CancelButton cancelButton;
    private boolean shouldJump;


    public PlayScreen(WhaleGdxGame game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam);
        gamePort.apply();
        shouldJump = true;
        stage = new Stage(gamePort);
        gameCam.setToOrtho(false, gamePort.getWorldWidth(), gamePort.getWorldHeight());
        world = new World(new Vector2(0, 0), true);
        player = new PlayerSquare(world, gameCam, gamePort.getWorldWidth() / 2, 64);
        enemySquares = new Array<EnemySquare>();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        glyph = new BitmapFont.Glyph();
        maxSize = 16;
        background = new Texture("background.png");
        b2dr = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(player);
        int speed = 0;
        for (int i = 0; i < maxSize; i++) {
            speed = getRandomSpeed(speed);
            EnemySquare enemySquare = new EnemySquare(player.getY() + ENEMY_SPACE + i * ENEMY_SPACE, speed, gameCam.viewportWidth, world);
            enemySquares.add(enemySquare);
            stage.addActor(enemySquare);
        }
        last = enemySquares.get(maxSize - 1);
        cancelButton = new CancelButton();
        stage.addActor(cancelButton);
        stage.setKeyboardFocus(player);
        stage.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (!player.animated && !player.isDead()) {
                    score += 1;
                    System.out.println("Score = " + score);
                    player.move();
                }
            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                if (!player.animated && shouldJump) {
                    score += 2;
                    System.out.println("Score = " + score);
                    player.state = PlayerSquare.State.TELEPORTING;
                }
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                showCancelButton();
            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                shouldJump = true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                cancelIsShown = false;
                Rectangle cBRect = new Rectangle(cancelButton.getX(), cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
                if (cBRect.contains(x, y)) {
                    shouldJump = false;
                    player.state = PlayerSquare.State.STANDING;
                } else {
                    shouldJump = true;
                }
            }
        });
    }

    private class CancelButton extends Actor {

        private Texture cancelButtonTexture;
        private float x;
        private float y;

        public CancelButton() {
            cancelButtonTexture = new Texture("cancelButton.png");
            setBounds(getX(), getY(), getWidth(), getHeight());
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (cancelIsShown && !player.isDead()) {
                batch.draw(cancelButtonTexture, getX(), getY());
            }
        }

        @Override
        public float getWidth() {
            return cancelButtonTexture.getWidth();
        }

        @Override
        public float getHeight() {
            return cancelButtonTexture.getHeight();
        }

        @Override
        public float getX() {
            return x = gameCam.position.x + gameCam.viewportWidth / 2 - getWidth();
        }

        @Override
        public float getY() {
            return y = gameCam.position.y - gameCam.viewportHeight / 2;
        }
    }

    private void showCancelButton() {
        cancelIsShown = true;
    }

    private int getRandomSpeed(int prev) {
        int speed;
        if (prev < 5) {
            return  MathUtils.random(9) + 5;
        } else {
            return  MathUtils.random(5) + 3;
        }
//        if (prev != 9){
//            return prev + 1;
//        } else return 1;
//        do {
//            if (0 <= score && score < 30) {
//                speed = MathUtils.random(5) + 3;
//            } else if (30 <= score && score < 50) {
//                speed = MathUtils.random(7) + 3;
//            } else if (50 <= score && score < 70) {
//                speed = MathUtils.random(8) + 6;
//            } else {
//                speed = MathUtils.random(9) + 8;
//            }
//        } while (prev == speed);
//        return speed;
    }

    public void update() {
        world.step(1 / 60f, 6, 2);

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

    private void lose() {
        final float x = gameCam.position.x;
        final float y = gameCam.viewportHeight / 2;
        world.setGravity(new Vector2(0, GRAVITY));
        player.lose();
        System.out.println("You lost");
        float delay = 2.0f;

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                System.out.println(x + " " + y);
                game.setScreen(new LossScreen(game, score));
            }

        }, delay);
        gameCam.position.set(gameCam.position.x, gameCam.position.y, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update();
        player.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        game.batch.draw(background,
                gameCam.position.x - gameCam.viewportWidth / 2,
                gameCam.position.y - gameCam.viewportHeight / 2,
                gameCam.viewportWidth, gameCam.viewportHeight);
        font.draw(game.batch, String.format("%03d", score), gameCam.position.x, gameCam.position.y);

        game.batch.end();
        player.render();

//        b2dr.render(world, gameCam.combined.scl(PPM));
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
        world.dispose();
        b2dr.dispose();
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.dispose();
        }
        player.dispose();
        font.dispose();
    }

}
