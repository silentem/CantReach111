package com.whaletail.actors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.whaletail.CantReachGame;

import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.whaletail.Constants.PPM;
import static com.whaletail.actors.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class PlayerSquare extends Actor {


    private enum State {STANDING, TELEPORTING, MOVING}

    private static final int PLAYER_WIDTH = 32;

    private static final int PLAYER_HEIGHT = 32;

    private State state;
    private CantReachGame game;
    private boolean overlaps;
    private View view;
    private Array<Shard> shards;
    private boolean invulnerable;
    public boolean animated;
    private boolean lostLife;
    private World world;
    private Body body;
    private boolean dead;
    private Vector2 destination;
    private OrthographicCamera camera;
    private Vector2 jumpPos;
    public boolean shouldJump;

    public PlayerSquare(CantReachGame game, OrthographicCamera camera, float x, float y) {
        this.game = game;
        this.camera = camera;
        world = game.world;
        view = new View();
        state = State.STANDING;
        invulnerable = false;
        animated = false;
        dead = false;
        jumpPos = new Vector2();
        shards = new Array<>();
        body = createBody();
        setX(x);
        setY(y);
        setBounds(getX(), getY(), getWidth(), getHeight());
        setTouchable(Touchable.enabled);
        destination = new Vector2(getX(), getY());
    }

    private Body createBody() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        Body body = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 2 / PPM, getHeight() / 2 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    private void startTeleAnim() {
        view.getImage().setOrigin(view.getWidth() / 2, view.getHeight() / 2);
        view.getImage().addAction(
                sequence(
                        scaleTo(0f, 0f, .05f),
                        run(new Runnable() {
                            @Override
                            public void run() {
                                addAction(moveTo(getX(), destination.y, .1f));
                            }
                        }),
                        moveTo(getX() - view.getWidth() / 2, destination.y - view.getHeight() / 2, .1f),
                        scaleTo(1f, 1f, .05f),
                        run(new Runnable() {
                            @Override
                            public void run() {
                                invulnerable = false;
                                animated = false;
                                state = State.STANDING;
                                view.resetImage();
                            }
                        })
                )
        );
    }

    @Override
    protected void positionChanged() {
        if (!isDead()) {
            view.getImage().setPosition(getOriginX(), getOriginY());
            setPosition(getX(), getY());
            if (getY() > camera.viewportHeight / 2 - 128 && !isDead() && (state == State.MOVING || state == State.TELEPORTING)) {
                camera.position.y = getY() + 128;
                camera.update();
            }
        }
        super.positionChanged();
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (super.getY() >= destination.y && state == State.MOVING) {
            animated = false;
            state = State.STANDING;
        }
        view.getImage().act(delta);
    }

    public void win() {
        invulnerable = true;
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shards.add(new Shard(i, j, x, y));
            }
        }
        for (Shard shard : shards) {
            shard.getBody().setAngularVelocity(25);
        }
    }

    public void lose() {
        dead = true;
        invulnerable = true;
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        body.destroyFixture(body.getFixtureList().get(0));
        world.destroyBody(body);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shards.add(new Shard(i, j, x, y));
            }
        }
        for (Shard shard : shards) {
            shard.getBody().setAngularVelocity(25);
        }
    }

    public void move() {
        System.out.println("moving");
        if (state == State.STANDING) {
            destination.y = destination.y + ENEMY_SPACE;
            addAction(Actions.moveTo(getX(), destination.y, .1f));
            animated = true;
            state = State.MOVING;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isDead() && !overlaps) {
            if (!lostLife) {
                view.getImage().draw(batch, parentAlpha);
            } else {
                if ((System.currentTimeMillis() % 100) <= 50) {
                    view.getImage().draw(batch, parentAlpha);
                }
            }
        } else {
            for (Shard shard : shards) {
                shard.render(batch);
            }
        }
    }

    public void jump() {
        if (!animated) {
            invulnerable = true;
            animated = true;
            state = State.TELEPORTING;
            jumpPos.set(getX(), getY());
            destination.y += ENEMY_SPACE * 2;
            startTeleAnim();
        }
    }

    public boolean collides(EnemySquare square) {
        Rectangle enemy = new Rectangle(square.getX() - square.getWidth() / 2, square.getY() - square.getHeight() / 2, square.getWidth(), square.getHeight());
        Rectangle player = new Rectangle(body.getPosition().x * PPM - getWidth() / 2, body.getPosition().y * PPM - getHeight() / 2, getWidth(), getHeight());
        if (player.overlaps(enemy)) {
            overlaps = true;
            return !isInvulnerable() && !isLostLife();
        }
        overlaps = false;
        return false;
    }

    private class View extends Actor {
        private Image image;
        private Texture texture;
        private Random r;
        private int tnum;

        View() {
            r = new Random();
            resetImage();
        }

        void resetImage() {
            int prnum = tnum;
            while (tnum == prnum) {
                tnum = r.nextInt(4) + 1;
            }
            texture = game.asset.get("player-" + tnum + ".png", Texture.class);
            image = new Image(texture);
            image.setPosition(PlayerSquare.this.getOriginX(), PlayerSquare.this.getOriginY());
        }

        Texture getTexture() {
            return texture;
        }

        Image getImage() {
            return image;
        }

        @Override
        public float getWidth() {
            return image.getWidth();
        }

        @Override
        public float getHeight() {
            return image.getHeight();
        }
    }

    private class Shard {
        private Body body;
        private float w;
        private float h;
        private Texture texture;
        private TextureRegion textureRegion;


        Shard(int i, int j, float x, float y) {
            texture = view.getTexture();
            textureRegion = new TextureRegion(texture);
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            def.fixedRotation = true;
            w = getWidth() / 4;
            h = getHeight() / 4;
            def.position.set((x + w * j) / PPM, (y + h * i) / PPM);
            body = world.createBody(def);
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(w / 2 / PPM, h / 2 / PPM);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = .8f;
            fixtureDef.friction = .1f;
            body.createFixture(fixtureDef);
            body.setUserData(texture);
            shape.dispose();
        }

        Body getBody() {
            return body;
        }

        void render(Batch sb) {
            float angle = body.getAngle() * MathUtils.radiansToDegrees;
            float x = getBody().getPosition().x * PPM - w / 2;
            float y = getBody().getPosition().y * PPM - h / 2;
            sb.draw(textureRegion, x, y, w / 2, h / 2, w, h, 1, 1, angle);
        }

        void dispose() {
            texture.dispose();
        }
    }

    private boolean isInvulnerable() {
        return invulnerable;
    }

    @Override
    public float getOriginX() {
        return getX() - getWidth() / 2;
    }

    @Override
    public float getOriginY() {
        return getY() - getHeight() / 2;
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
    }

    public void dispose() {
        for (Shard shard : shards) {
            shard.dispose();
        }
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        body.setTransform(x / PPM, y / PPM, 0);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        body.setTransform(x / PPM, body.getPosition().y, 0);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        body.setTransform(body.getPosition().x, y / PPM, 0);
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public boolean isLostLife() {
        return lostLife;
    }

    public void setLostLife(boolean lostLife) {
        this.lostLife = lostLife;
    }

    @Override
    public float getWidth() {
        return PLAYER_WIDTH;
    }

    @Override
    public float getHeight() {
        return PLAYER_HEIGHT;
    }

}
