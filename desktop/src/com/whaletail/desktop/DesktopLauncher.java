package com.whaletail.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.whaletail.WhaleGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = WhaleGdxGame.V_WIDTH;
		config.height = WhaleGdxGame.V_HEIGHT;
		new LwjglApplication(new WhaleGdxGame(), config);
	}
}
