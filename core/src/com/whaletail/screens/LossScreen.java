package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.WhaleGdxGame;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LossScreen implements Screen, GestureDetector.GestureListener {

    private WhaleGdxGame game;
    private Texture background;
    private Texture again_button;
    private Rectangle again_buttonBounds;
    private BitmapFont font;
    private int score;
    private FitViewport gamePort;
    private OrthographicCamera gameCam;

    public LossScreen(WhaleGdxGame game, int score) {
        this.game = game;
        this.score = score;
        background = new Texture("menu_background.png");
        again_button = new Texture("play_button.png");
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT, gameCam);
        gameCam.setToOrtho(false, gamePort.getWorldWidth(), gamePort.getWorldHeight());
        gameCam.position.set(0, 0, 0);
        again_buttonBounds = new Rectangle();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, WhaleGdxGame.V_WIDTH, WhaleGdxGame.V_HEIGHT);
        game.batch.draw(again_button, gameCam.viewportWidth / 2 - again_button.getWidth() / 2, gameCam.viewportHeight / 2);
        font.draw(game.batch, String.format("Your score: %03d", score),
                gameCam.viewportWidth / 2 - again_button.getWidth() / 2,
                gameCam.viewportHeight / 2 + 100);
        game.batch.end();
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

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
//        again_buttonBounds.set(gameCam.viewportWidth / 2 - again_button.getWidth() / 2, gameCam.viewportHeight / 2,
//                again_button.getWidth(), again_button.getHeight());
//        System.out.println("x = " + x + " y =  " + y);
//        System.out.println(again_buttonBounds);
//        if (again_buttonBounds.contains(x, y)) {
        game.setScreen(new PlayScreen(game));
//        }
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
}
