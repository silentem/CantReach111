package com.whaletail.listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.whaletail.screens.MenuScreen;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class MusicActorGestureListener extends ActorGestureListener {

    private MenuScreen menuScreen;

    public MusicActorGestureListener(MenuScreen menuScreen) {
        this.menuScreen = menuScreen;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        menuScreen.playMusic();
    }
}
