package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.WhaleGdxGame;
import com.whaletail.sprites.EnemySquare;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class MenuScreen implements Screen, GestureDetector.GestureListener {

    private WhaleGdxGame game;
    private Texture playButton;
    private Texture background;
    private Texture text;
    private ViewActor view1;
    private ViewActor view2;
    private ViewActor view3;
    private Stage stage;

    public MenuScreen(WhaleGdxGame game) {
        this.game = game;
        playButton = new Texture("playButton.png");
        background = new Texture("background.png");
        stage = new Stage();
        view1 = new ViewActor(new Vector2(0, 100),
                new Vector2(100, 100),
                new EnemySquare.View(new Texture("enemy-1.png"), 14, 3));
        view2 = new ViewActor(new Vector2(0, 175),
                new Vector2(200, 175),
                new EnemySquare.View(new Texture("enemy-3.png"), 9, 1));
        view3 = new ViewActor(new Vector2(0, 275),
                new Vector2(300, 275),
                new EnemySquare.View(new Texture("enemy-2.png"), 7, 2));
        stage.addActor(view1);
        stage.addActor(view2);
        stage.addActor(view3);

        text = new Texture("text.png");
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void show() {
        view1.move();
        view2.move();
        view3.move();
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        game.batch.draw(background, 0, 0, width, height);
        game.batch.draw(text,
                width / 2 - text.getWidth() / 2,
                height - text.getHeight() - 50);
        game.batch.draw(playButton,
                width / 2 - playButton.getWidth() / 2,
                height / 2 - playButton.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();

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

    }

    @Override
    public void dispose() {
        playButton.dispose();
        background.dispose();
        text.dispose();
        view1.dispose();
        view2.dispose();
        view3.dispose();
        stage.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        game.setScreen(new PlayScreen(game));
        this.dispose();
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private class ViewActor extends Actor {

        private Vector2 direction;
        private EnemySquare.View view;

        ViewActor(Vector2 position,
                  Vector2 direction,
                  EnemySquare.View view) {
            this.direction = direction;
            this.view = view;
            setPosition(position.x, position.y);
        }

        void move() {
            addAction(Actions.moveTo(direction.x, direction.y, 1f));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            view.draw(batch, getX(), getY());
        }

        void dispose(){
            view.dispose();
        }

    }
}
