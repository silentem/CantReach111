package com.whaletail;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.World;
import com.whaletail.gui.Score;
import com.whaletail.screens.LoadingScreen;
import com.whaletail.screens.LossScreen;
import com.whaletail.screens.MenuScreen;
import com.whaletail.screens.PlayScreen;

public class WhaleGdxGame extends Game {

    public static final int V_WIDTH = 480;
    public static final int V_HEIGHT = 800;

    public SpriteBatch batch;
    public AssetManager asset;
    public OrthographicCamera cam;
    public Preferences prefs;
    public World world;

    public BitmapFont font90;
    public BitmapFont font30;

    public Score score;

    public MenuScreen menuScreen;
    private LoadingScreen loadingScreen;
    public PlayScreen playScreen;
    public LossScreen lossScreen;

    @Override
    public void create() {
        System.out.println("START");
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        asset = new AssetManager();
        batch = new SpriteBatch();
        prefs = Gdx.app.getPreferences("WhaleTailPreferences");

        menuScreen = new MenuScreen(this);
        loadingScreen = new LoadingScreen(this);
        playScreen = new PlayScreen(this);
        lossScreen = new LossScreen(this);

        initFonts();

        setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        System.out.println("WhaleGdxGame.dispose");
        batch.dispose();
        asset.dispose();
        menuScreen.dispose();
        playScreen.dispose();
        lossScreen.dispose();
    }

    private void initFonts(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 30;
        font30 = generator.generateFont(params);
        params.size = 90;
        font90 = generator.generateFont(params);
    }

}