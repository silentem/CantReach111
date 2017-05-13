package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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
    private int maxSize;
    private EnemySquare last;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Stage stage;


    public PlayScreen(WhaleGdxGame game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam);
        gamePort.apply();
        stage = new Stage(gamePort);
        gameCam.setToOrtho(false, gamePort.getWorldWidth(), gamePort.getWorldHeight());
        world = new World(new Vector2(0, 0), true);
        player = new PlayerSquare(world, gameCam, gamePort.getWorldWidth() / 2, 64);
        enemySquares = new Array<EnemySquare>();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        maxSize = 16;
        int speed = 0;
        for (int i = 0; i < maxSize; i++) {
            speed = getRandomSpeed(speed);
            enemySquares.add(new EnemySquare(player.getY() + ENEMY_SPACE + i * ENEMY_SPACE, speed, gameCam.viewportWidth, world));
        }
        last = enemySquares.get(maxSize - 1);
        background = new Texture("background.png");
        b2dr = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(player);
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
                if (!player.animated) {
                    score += 2;
                    System.out.println("Score = " + score);
                    player.state = PlayerSquare.State.TELEPORTING;
                }
            }
        });
    }

    private int getRandomSpeed(int prev) {
        int speed;
        do {
            speed = MathUtils.random(4) + 1;
        } while (prev == speed);
        return speed;
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
                gameCam.position.set(gameCam.position.x, gameCam.position.y, 0);
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
        float delay = 2.5f;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                System.out.println(x + " " + y);
                game.setScreen(new LossScreen(game, score));
            }
        }, delay);

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
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.render(game.batch);
        }

        player.render();

//        b2dr.render(world, gameCam.combined.scl(PPM));
        stage.act(delta);
        stage.draw();
        font.draw(game.batch, String.format("%03d", score), gameCam.position.x, gameCam.position.y + gameCam.viewportHeight / 2 - 10);
        game.batch.end();
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
