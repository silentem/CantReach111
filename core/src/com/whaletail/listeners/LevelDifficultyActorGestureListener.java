package com.whaletail.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.whaletail.screens.LevelScreen;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class LevelDifficultyActorGestureListener extends ActorGestureListener {

    private LevelScreen levelScreen;
    private boolean started;
    private int difficulty;

    public LevelDifficultyActorGestureListener(LevelScreen levelScreen, int difficulty) {
        this.levelScreen = levelScreen;
        this.difficulty = difficulty;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        if (!started) {
            levelScreen.startGame(difficulty);
            started = true;
        }
    }
}
