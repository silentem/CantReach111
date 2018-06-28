package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.whaletail.CantReachGame;
import com.whaletail.actors.EnemySquare;
import com.whaletail.gui.Button;
import com.whaletail.gui.MenuScreenViewActor;
import com.whaletail.gui.Text;
import com.whaletail.gui.ViewActor;
import com.whaletail.listeners.MenuActorGestureListener;
import com.whaletail.listeners.MusicActorGestureListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.whaletail.CantReachGame.V_HEIGHT;
import static com.whaletail.CantReachGame.V_WIDTH;
import static com.whaletail.Constants.ANNOUNCEMENT;
import static com.whaletail.Constants.CAN_NOT;
import static com.whaletail.Constants.NUMBER;
import static com.whaletail.Constants.REACH;
import static com.whaletail.actors.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class MenuScreen extends BaseScreen {

    private Text canNotFont;
    private Text reachFont;
    private Text numberFont;
    private Text announcementFont;

    private CantReachGame game;
    private Button playButton;
    private Image musicButton;
    private Texture foreground;
    private ViewActor[] viewActors;
    private Stage stage;
    private ViewActor shim;
    private Music backgroundMusic;

    public MenuScreen(CantReachGame game) {
        this.game = game;
        stage = new Stage(new FillViewport(V_WIDTH, V_HEIGHT, game.cam));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Chris_Zabriskie_-_06_-_Divider.mp3"));
        viewActors = new ViewActor[3];
    }

    @Override
    public void show() {

        Image background = new Image(game.asset.get("background.png", Texture.class));
        foreground = game.asset.get("foreground.png", Texture.class);
        Gdx.input.setInputProcessor(stage);

        setupPlayButton();
        setupMusicButton();
        setupMainText();
        viewActors[0] = new MenuScreenViewActor(canNotFont, new EnemySquare.View(game.asset.get("enemy-1.png", Texture.class), 10, 3));
        viewActors[1] = new MenuScreenViewActor(reachFont, new EnemySquare.View(game.asset.get("enemy-3.png", Texture.class), 10, 1));
        viewActors[2] = new MenuScreenViewActor(numberFont, new EnemySquare.View(game.asset.get("enemy-2.png", Texture.class), 15, 2));

        stage.addActor(background);
        stage.addActor(musicButton);
        for (ViewActor viewActor : viewActors) {
            stage.addActor(viewActor);
        }
        stage.addActor(canNotFont);
        stage.addActor(reachFont);
        stage.addActor(numberFont);
        stage.addActor(announcementFont);
        stage.addActor(playButton);

        for (ViewActor viewActor : viewActors) {
            viewActor.move(.4f);
        }
    }


    private void setupMusicButton() {
        if (game.prefs.getBoolean("play-music", true)) {
            backgroundMusic.setLooping(true);
            backgroundMusic.play();
            musicButton = new Image(game.asset.get("musicButton.png", Texture.class));
        } else {
            musicButton = new Image(game.asset.get("noMusicButton.png", Texture.class));
        }
        musicButton.setPosition(50, 50);
        musicButton.addListener(new MusicActorGestureListener(this));
    }

    private void setupPlayButton() {
        playButton = new Button(game.asset.get("playButton.png", Texture.class));
        playButton.setPosition(stage.getWidth() / 2 - playButton.getWidth() / 2,
                stage.getHeight() / 2 - playButton.getHeight());

        playButton.addListener(new MenuActorGestureListener(this));

        playButton.setOrigin(playButton.getWidth() / 2, playButton.getHeight() / 2);
    }

    public void startGame() {
        game.analytic.pressedPlay();
        for (int i = 0; i < 6; i++) {
            int y = 10 + ENEMY_SPACE + i * ENEMY_SPACE;
            EnemySquare.View view = new EnemySquare.View(game.asset.get("enemy-" + (MathUtils.random(3) + 1) + ".png", Texture.class), MathUtils.random(5) + 5, MathUtils.random(3) + 1);
            ViewActor actor = new ViewActor(new Vector2(0 - view.getWidth(), y),
                    new Vector2(stage.getWidth(), y),
                    view);
            stage.addActor(actor);
            actor.toFront();
            actor.move((MathUtils.random(3) + 3) / 10f);
        }

        for (ViewActor viewActor : viewActors) {
            viewActor.setDirection(new Vector2(stage.getWidth(), viewActor.getY()));
            viewActor.move((MathUtils.random(3) + 3) / 10f);
        }

        EnemySquare.View view = new EnemySquare.View(foreground, 1, 1);
        shim = new ViewActor(new Vector2(0 - view.getWidth(), 0),
                new Vector2(0, 0),
                view) {
            @Override
            public void move(float speed) {
                addAction(sequence(delay(.25f), moveTo(this.direction.x, this.direction.y, speed)));
            }
        };
        stage.addActor(shim);
        shim.toFront();
        shim.move(.7f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42 / 256f, 44 / 256f, 44 / 256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        stage.act(delta);
        stage.draw();

    }

    private void update() {
        if (shim != null && shim.getX() + shim.getWidth() >= stage.getWidth()) {
            shim.addAction(sequence(delay(.05f), run(
                    new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(game.levelScreen);
                        }
                    })));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    private void setupMainText() {
        canNotFont = new Text(CAN_NOT, game.font90);
        canNotFont.setX(game.cam.position.x - canNotFont.getWidth() / 2 - 50);
        canNotFont.setY(game.cam.viewportHeight - 50);
        reachFont = new Text(REACH, game.font90);
        reachFont.setX(game.cam.position.x - canNotFont.getWidth() / 2);
        reachFont.setY(canNotFont.getY() - canNotFont.getHeight());
        numberFont = new Text(NUMBER, game.font90);
        numberFont.setX(game.cam.position.x - canNotFont.getWidth() / 2 + 50);
        numberFont.setY(reachFont.getY() - reachFont.getHeight());
        announcementFont = new Text(ANNOUNCEMENT, game.font30);
        announcementFont.setX(game.cam.position.x - announcementFont.getWidth() / 2);
        announcementFont.setY(numberFont.getY() - numberFont.getHeight() - 50);
        announcementFont.addAction(
                forever(
                        sequence(
                                scaleTo(.45f, .45f, .55f),
                                scaleTo(.5f, .5f, .55f))));

    }

    public void playPauseMusic() {
        if (backgroundMusic.isPlaying()) {
            game.prefs.putBoolean("play-music", false);
            game.prefs.flush();
            musicButton.setDrawable(new SpriteDrawable(new Sprite(game.asset.get("noMusicButton.png", Texture.class))));
            backgroundMusic.pause();
            game.analytic.turnedOffMusic();
        } else {
            game.prefs.putBoolean("play-music", true);
            game.prefs.flush();
            musicButton.setDrawable(new SpriteDrawable(new Sprite(game.asset.get("musicButton.png", Texture.class))));
            backgroundMusic.play();
            game.analytic.turnedOnMusic();
        }
    }
}