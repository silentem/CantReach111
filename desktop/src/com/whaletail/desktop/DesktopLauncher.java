package com.whaletail.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.whaletail.CantReachGame;
import com.whaletail.analytics.Analytic;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = CantReachGame.V_WIDTH;
		config.height = CantReachGame.V_HEIGHT;
		new LwjglApplication(new CantReachGame(new Analytic() {
			@Override
			public void submitScore(int score) {
			}

			@Override
			public void turnedOnMusic() {

			}

			@Override
			public void turnedOffMusic() {

			}

			@Override
			public void goneHome() {

			}

			@Override
			public void pressedPlay() {

			}

			@Override
			public void pressedRetry(int tries) {

			}
		}), config);
	}
}
