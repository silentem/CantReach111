package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.WhaleGdxGame;
import com.whaletail.gui.Button;
import com.whaletail.gui.Text;
import com.whaletail.gui.ViewActor;
import com.whaletail.sprites.EnemySquare;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.whaletail.Constants.*;
import static com.whaletail.sprites.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class MenuScreen implements Screen {

    private Text canNotFont;
    private Text reachFont;
    private Text numberFont;

    private FitViewport viewport;
    private WhaleGdxGame game;
    private Button playButton;
    private Texture background;
    private Texture foreground;
    private ViewActor view1;
    private ViewActor view2;
    private ViewActor view3;
    private Stage stage;
    private Array<ViewActor> actors;
    private ViewActor shim;

    public MenuScreen(final WhaleGdxGame game) {
        this.game = game;
        viewport = new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, game.cam);
        stage = new Stage(viewport);
    }

    @Override
    public void show() {
        background = game.asset.get("background.png", Texture.class);
        foreground = game.asset.get("foreground.png", Texture.class);
        createGUI();

        EnemySquare.View view = new EnemySquare.View(game.asset.get("enemy-1.png", Texture.class), 10, 3);
        view1 = new ViewActor(new Vector2(0 - view.getWidth(),
                canNotFont.getY() - canNotFont.getHeight() / 2 - view.getHeight() / 2),
                new Vector2(canNotFont.getX() - view.getWidth(),
                        canNotFont.getY() - canNotFont.getHeight() / 2 - view.getHeight() / 2),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.2f), moveTo(direction.x, direction.y, speed)));
            }
        };

        view = new EnemySquare.View(game.asset.get("enemy-3.png", Texture.class), 10, 1);
        view2 = new ViewActor(new Vector2(0 - view.getWidth(),
                reachFont.getY() - reachFont.getHeight() / 2 - view.getHeight() / 2),
                new Vector2(reachFont.getX() - view.getWidth(),
                        reachFont.getY() - reachFont.getHeight() / 2 - view.getHeight() / 2),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.2f), moveTo(direction.x, direction.y, speed)));
            }
        };
        view = new EnemySquare.View(game.asset.get("enemy-2.png", Texture.class), 15, 2);
        view3 = new ViewActor(new Vector2(0 - view.getWidth(),
                numberFont.getY() - numberFont.getHeight() / 2 - view.getHeight() / 2),
                new Vector2(numberFont.getX() - view.getWidth(),
                        numberFont.getY() - numberFont.getHeight() / 2 - view.getHeight() / 2),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.2f), moveTo(direction.x, direction.y, speed)));
            }
        };

        Gdx.input.setInputProcessor(stage);

        stage.addActor(view1);
        stage.addActor(view2);
        stage.addActor(view3);
        stage.addActor(canNotFont);
        stage.addActor(reachFont);
        stage.addActor(numberFont);
        stage.addActor(playButton);

        actors = new Array<>();
        float speed = .4f;
        view1.move(speed);
        view2.move(speed);
        view3.move(speed);
    }

    private void createGUI() {
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        playButton = new Button(game.asset.get("playButton.png", Texture.class));
        playButton.setPosition(width / 2 - playButton.getWidth() / 2,
                height / 2 - playButton.getHeight());
        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                start();
            }
        });

        playButton.setOrigin(playButton.getWidth() / 2, playButton.getHeight() / 2);

        int size = 80;
        canNotFont = new Text(CAN_NOT, game.font90);
        canNotFont.setX(game.cam.position.x - canNotFont.getWidth() / 2 - 50);
        canNotFont.setY(game.cam.viewportHeight - 50);
        reachFont = new Text(REACH, game.font90);
        reachFont.setX(game.cam.position.x - canNotFont.getWidth() / 2);
        reachFont.setY(canNotFont.getY() - canNotFont.getHeight());
        numberFont = new Text(NUMBER, game.font90);
        numberFont.setX(game.cam.position.x - canNotFont.getWidth() / 2 + 50);
        numberFont.setY(reachFont.getY() - reachFont.getHeight());
    }

    private void start() {

        for (int i = 0; i < 6; i++) {
            int y = 10 + ENEMY_SPACE + i * ENEMY_SPACE;
            EnemySquare.View view = new EnemySquare.View(game.asset.get("enemy-" + (MathUtils.random(3) + 1) + ".png", Texture.class), MathUtils.random(5) + 5, MathUtils.random(3) + 1);
            ViewActor actor = new ViewActor(new Vector2(0 - view.getWidth(), y),
                    new Vector2(viewport.getScreenWidth(), y),
                    view);
            actors.add(actor);
            stage.addActor(actor);
            actor.toBack();
            actor.move((MathUtils.random(3) + 3) / 10f);
        }
        view1.setDirection(new Vector2(viewport.getScreenWidth(), view1.getY()));
        view1.move((MathUtils.random(3) + 3) / 10f);
        view2.setDirection(new Vector2(viewport.getScreenWidth(), view2.getY()));
        view2.move((MathUtils.random(3) + 3) / 10f);
        view3.setDirection(new Vector2(viewport.getScreenWidth(), view3.getY()));
        view3.move((MathUtils.random(3) + 3) / 10f);

        EnemySquare.View view = new EnemySquare.View(foreground, 1, 1);
        shim = new ViewActor(new Vector2(0 - view.getWidth(), 0),
                new Vector2(0, 0),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.25f), moveTo(this.direction.x, this.direction.y, speed)));
            }
        };
        actors.add(shim);
        stage.addActor(shim);
        shim.toFront();
        shim.move(.7f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42/256f, 44/256f, 44/256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(game.cam.combined);
        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();

        game.batch.begin();
        game.batch.draw(background, 0, 0, width, height);
        game.batch.end();

        update(delta);
        stage.draw();


    }

    private void update(float delta) {
        stage.act(delta);
        if (shim != null && shim.getX() + shim.getWidth() >= viewport.getWorldWidth()) {
            shim.addAction(sequence(delay(.05f), run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(game.playScreen);
                }
            })));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
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
        System.out.println("MenuScreen.dispose");
        stage.clear();
        stage.dispose();
    }

}
