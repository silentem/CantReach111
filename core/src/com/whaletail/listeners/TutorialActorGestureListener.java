package com.whaletail.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.whaletail.screens.GameScreen;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class TutorialActorGestureListener extends ActorGestureListener {

    private GameScreen gameScreen;
    private int count;

    public TutorialActorGestureListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        count = 0;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        this.count = gameScreen.tapThroughTutorial(++this.count);
    }
}
