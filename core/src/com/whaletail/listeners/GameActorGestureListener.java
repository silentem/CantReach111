package com.whaletail.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.whaletail.actors.PlayerSquare;
import com.whaletail.screens.GameScreen;

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
        PlayerSquare player = gameScreen.getPlayer();
        if (!player.animated && !player.isDead()) {
            gameScreen.addScore(1);
            player.move();
            player.shouldJump = false;
        }
    }

    @Override
    public void fling(InputEvent event, float velocityX, float velocityY, int button) {
        PlayerSquare player = gameScreen.getPlayer();
        if (!player.animated && player.shouldJump && !player.isDead()) {
            gameScreen.addScore(2);
            player.jump();
        }
    }

    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
        gameScreen.getPlayer().shouldJump = true;
    }

}
