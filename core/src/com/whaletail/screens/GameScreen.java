package com.whaletail.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.whaletail.CantReachGame;
import com.whaletail.actors.EnemySquare;
import com.whaletail.actors.PlayerSquare;
import com.whaletail.gui.Score;
import com.whaletail.gui.Text;
import com.whaletail.listeners.GameActorGestureListener;
import com.whaletail.listeners.TutorialActorGestureListener;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.whaletail.Constants.CAN_REACH_TUT;
import static com.whaletail.Constants.GRAVITY;
import static com.whaletail.Constants.SWIPE_TUT;
import static com.whaletail.Constants.TAP_TUT;
import static com.whaletail.actors.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class GameScreen extends BaseScreen {

    private CantReachGame game;
    private OrthographicCamera gameCam;
    private PlayerSquare player;
    private List<EnemySquare> enemySquares;
    private Text font;
    private static final int MAX_SIZE = 16;
    private EnemySquare last;
    private Box2DDebugRenderer b2dr;
    private Stage stage;
    private Stage stageHUD;
    private Stage stageTutorial;
    private Label tapText;
    private Label swipeText;
    private Label canReachText;
    private Image tapImage;
    private Image swipeImage;
    private Image extraLifeImage;
    private boolean isTutorialPassed;

    public GameScreen(CantReachGame game) {
        this.game = game;
        gameCam = game.cam;
        stage = new Stage(new FillViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
        stageHUD = new Stage(new FitViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
        b2dr = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        isTutorialPassed = game.prefs.getBoolean("tutorial_passed", false);
        if (!isTutorialPassed) {
            setupTutorial();
        } else {
            Gdx.input.setInputProcessor(stage);
        }
        game.world = new World(new Vector2(0, 0), true);
        player = new PlayerSquare(game, gameCam, stage.getViewport().getWorldWidth() / 2, 64);
        enemySquares = new ArrayList<>();
        game.score = new Score();
        font = new Text(game.score.toString(), game.font30, stage);
        Image background = new Image(game.asset.get("background.png", Texture.class)) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(game.asset.get("background.png", Texture.class),
                        0, game.cam.position.y - stage.getHeight() / 2);
            }
        };
        stage.addActor(background);
        background.setZIndex(0);
        stage.addActor(player);
        int speed = 0;
        for (int i = 0; i < MAX_SIZE; i++) {
            speed = getRandomSpeed(speed);
            EnemySquare enemySquare = new EnemySquare(
                    player.getY() + ENEMY_SPACE + i * ENEMY_SPACE, speed, gameCam.viewportWidth, game);
            enemySquares.add(enemySquare);
            stage.addActor(enemySquare);
        }


        String extraLineFileName = "baseline_favorite_white_48.png";
        extraLifeImage = new Image(game.asset.get(extraLineFileName, Texture.class)) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setPosition(0, stage.getCamera().position.y + stage.getHeight() / 2 - 50);
                super.draw(batch, parentAlpha);
            }
        };

        extraLifeImage.setHeight(50);
        extraLifeImage.setWidth(50);

        extraLifeImage.addAction(
                forever(
                        sequence(
                                scaleTo(.90f, .90f, .55f),
                                scaleTo(.95f, .95f, .55f))));
        stageHUD.addActor(font);
        if (game.hasExtraLife) {
            stageHUD.addActor(extraLifeImage);
        }
        last = enemySquares.get(MAX_SIZE - 1);
        stage.addListener(new GameActorGestureListener(this));
    }

    private void setupTutorial() {
        stageTutorial = new Stage(new FitViewport(CantReachGame.V_WIDTH, CantReachGame.V_HEIGHT, gameCam));
        Gdx.input.setInputProcessor(stageTutorial);
        stage.addAction(alpha(.5f));
        stageHUD.addAction(alpha(.5f));
        tapImage = new Image(game.asset.get("tap_icon.png", Texture.class));
        swipeImage = new Image(game.asset.get("swipe_icon.png", Texture.class));
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = game.font30;
        tapText = new Label(TAP_TUT, ls);
        swipeText = new Label(SWIPE_TUT, ls);
        canReachText = new Label(CAN_REACH_TUT, ls);
        tapImage.setPosition(stage.getWidth() / 2 - tapImage.getWidth() / 2, stage.getHeight() / 2 - tapImage.getHeight() / 2);
        tapText.setX(stage.getWidth() / 2 - tapText.getWidth() / 2);
        tapText.setY(tapImage.getY() - tapText.getHeight() - 50);
        swipeImage.setPosition(stage.getWidth() / 2 - swipeImage.getWidth() / 2, stage.getHeight() / 2 - swipeImage.getHeight() / 2);
        swipeText.setX(stage.getWidth() / 2 - swipeText.getWidth() / 2);
        swipeText.setY(swipeImage.getY() - swipeText.getHeight() - 50);
        canReachText.setPosition(stage.getWidth() / 2 - canReachText.getWidth() / 2,
                stage.getHeight() / 2 - canReachText.getHeight() / 2);
        stageTutorial.addActor(tapImage);
        stageTutorial.addActor(tapText);

        stageTutorial.addListener(new TutorialActorGestureListener(this));

    }


    private int getRandomSpeed(int prev) {
        int speed = 0;
        if (0 <= game.score.getScore() && game.score.getScore() <= 30) {
            if (prev < 5) {
                return MathUtils.random(2) + 3;
            } else {
                return MathUtils.random(1) + 1;
            }
        } else if (30 < game.score.getScore() && game.score.getScore() <= 60) {
            if (prev < 7) {
                return MathUtils.random(1) + 4;
            } else {
                return MathUtils.random(2) + 3;
            }
        } else if (60 < game.score.getScore()) {
            if (prev < 8) {
                return MathUtils.random(2) + 6;
            } else {
                return MathUtils.random(3) + 4;
            }
        }
        return speed;
    }

    private void lose() {
        game.world.setGravity(new Vector2(0, GRAVITY));
        player.lose();
        System.out.println("You lost");
        float delay = 2f;

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                int bestScore = game.prefs.getInteger("best_score", 0);
                if (game.score.getScore() > bestScore) {
                    game.prefs.putInteger("best_score", game.score.getScore());
                    game.prefs.flush();
                }
                game.setScreen(game.lossScreen);
            }

        }, delay);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(42 / 256f, 44 / 256f, 44 / 256f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();

        stageHUD.getViewport().apply();
        stageHUD.act(delta);
        stageHUD.draw();

        if (!isTutorialPassed) {
            stageTutorial.getViewport().apply();
            stageTutorial.act(delta);
            stageTutorial.draw();
        }
//        b2dr.render(game.world, gameCam.combined.scl(PPM));

    }

    private void update() {
        game.world.step(1 / 60f, 6, 2);


        if (game.score.getScore() == 111) {
            win();
        }
        for (EnemySquare enemySquare : enemySquares) {
            if (enemySquare.getY() < gameCam.position.y - gameCam.viewportHeight / 2) {
                enemySquare.createNew(last.getY() + last.getHeight() + ENEMY_SPACE - last.getHeight(), getRandomSpeed(last.getSpeed()));
                last = enemySquare;
            }
            if (enemySquare.getX() - enemySquare.getWidth() / 2 > gameCam.viewportWidth && enemySquare.isRightLeft()) {
                enemySquare.reset();
            } else if (enemySquare.getX() < 0 - enemySquare.getWidth() && !enemySquare.isRightLeft()) {
                enemySquare.reset();
            }
            if (player.collides(enemySquare)) {
                if (game.hasExtraLife) {
                   loseLife();
                } else  {
                    lose();
                }

            }
            if (player.isDead()) {
                enemySquare.stop();
            }
        }

    }

    private void loseLife(){
        game.hasExtraLife = false;
        extraLifeImage.remove();

        game.prefs.putBoolean("extraLife", false);
        game.prefs.flush();
        player.setInvulnerable(true);
        float delay = 1f;

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                player.setInvulnerable(false);
            }

        }, delay);
    }

    private void win() {
        game.world.setGravity(new Vector2(0, GRAVITY));
        player.setInvulnerable(true);
        float delay = 4f;

        game.prefs.putBoolean("lose", true);
        game.prefs.flush();

        player.win();
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.lose();
        }

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                game.setScreen(game.lossScreen);
            }

        }, delay);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {
        stage.clear();
        stageHUD.clear();
    }

    @Override
    public void dispose() {
        System.out.println("GameScreen.dispose");
        stageHUD.dispose();
        stage.dispose();
        if (player != null) {
            player.dispose();
        }
        for (EnemySquare enemySquare : enemySquares) {
            enemySquare.dispose();
        }
        game.world.dispose();
    }

    public int tapThroughTutorial(int count) {
        switch (count) {
            case 1: {
                tapImage.remove();
                tapText.remove();
                stageTutorial.addActor(swipeImage);
                stageTutorial.addActor(swipeText);
                return count;
            }
            case 2: {
                swipeImage.remove();
                swipeText.remove();
                stageTutorial.addActor(canReachText);
                return count;
            }
            case 3: {
                canReachText.remove();
                stageTutorial.dispose();
                game.prefs.putBoolean("tutorial_passed", true);
                game.prefs.flush();
                isTutorialPassed = true;
                stageTutorial = null;
                stage.addAction(alpha(1f));
                stageHUD.addAction(alpha(1f));
                Gdx.input.setInputProcessor(stage);
                return 4;
            }
        }
        return 0;
    }

    public void addScore(int amount) {
        game.score.add(amount);
        font.setText(game.score.toString());
    }

    public PlayerSquare getPlayer() {
        return player;
    }
}
