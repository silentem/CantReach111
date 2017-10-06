package com.whaletail.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.whaletail.screens.GameScreen;

import static sun.audio.AudioPlayer.player;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class GameActorGestureListener extends ActorGestureListener {

    private GameScreen gameScreen;

    public GameActorGestureListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        gameScreen.movePlayer();
    }

    @Override
    public void fling(InputEvent event, float velocityX, float velocityY, int button) {
        gameScreen.jumpPlayer();
    }

    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
        gameScreen.stopPlayer();
    }

}
