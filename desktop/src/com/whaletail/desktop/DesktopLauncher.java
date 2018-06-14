package com.whaletail.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.whaletail.CantReachGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = CantReachGame.V_WIDTH;
        config.height = CantReachGame.V_HEIGHT;
        new LwjglApplication(new CantReachGame(), config);
    }
}
