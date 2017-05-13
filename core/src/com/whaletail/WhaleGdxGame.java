package com.whaletail;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.whaletail.screens.LossScreen;
import com.whaletail.screens.MenuScreen;
import com.whaletail.screens.PlayScreen;

public class WhaleGdxGame extends Game {
	public SpriteBatch batch;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 800;
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

    @Override
    public void render() {
        super.render();
    }
}
