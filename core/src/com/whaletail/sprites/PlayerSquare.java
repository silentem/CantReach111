package com.whaletail.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import static com.whaletail.Constants.PPM;
import static com.whaletail.sprites.EnemySquare.ENEMY_SPACE;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class PlayerSquare extends Actor {
    public enum State {STANDING, TELEPORTING, MOVING}

    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 32;

    public State state;
    private Sprite sprite;
    private Texture texture;
    private Texture teleAnimTexture;
    private Texture moveAnimTexture;
    private Array<TextureRegion> animFrames;
    private Array<TextureRegion> moveAnimFrames;
    private Animation<TextureRegion> tAnimation;
    private Array<Shards> shards;
    private float time;
    private boolean invulnerable;
    public boolean animated;
    private World world;
    private Body body;
    private boolean dead;
    private Vector2 direction;
    private OrthographicCamera camera;

    public PlayerSquare(World world, OrthographicCamera camera, float x, float y) {
        this.world = world;
        this.camera = camera;
        texture = new Texture("player.png");
        sprite = new Sprite(texture);
        teleAnimTexture = new Texture("animation.png");
        moveAnimTexture = new Texture("move_animation.png");
        state = State.STANDING;
        invulnerable = false;
        animated = false;
        dead = false;
        animFrames = new Array<TextureRegion>();
        moveAnimFrames = new Array<TextureRegion>();
        time = 0;
        shards = new Array<Shards>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                animFrames.add(new TextureRegion(teleAnimTexture, j * 64, i * 64, 64, 64));
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 1 && j == 3) continue;
                moveAnimFrames.add(new TextureRegion(moveAnimTexture, j * 32, i * 64, 32, 64));
            }
        }
        tAnimation = new Animation<TextureRegion>(1f / 45f, animFrames);
        body = createBody();
        setX(x);
        setY(y);
        setBounds(getX(), getY(), getWidth(), getHeight());
        setTouchable(Touchable.enabled);
        direction = new Vector2(getX(), getY());
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
        body.setUserData(texture);
        shape.dispose();
        return body;
    }

    public void update() {
        if (super.getY() >= direction.y && state == State.MOVING) {
            animated = false;
            state = State.STANDING;
        }
    }

    @Override
    protected void positionChanged() {
        if (!isDead()) {
            sprite.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
            setPosition(getX(), getY());
            if (getY() > camera.viewportHeight / 2 && !isDead() && state == State.MOVING) {
                camera.position.y = getY();
                camera.update();
            }
        }
        super.positionChanged();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void lose() {
        dead = true;
        invulnerable = true;
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        body.destroyFixture(body.getFixtureList().get(0));
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        world.destroyBody(body);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shards.add(new Shards(i, j, x, y));
            }
        }
        for (Shards shard : shards) {
            shard.getBody().setAngularVelocity(25);
        }
    }

    public void move() {
        System.out.println("moving");
        if (state == State.STANDING) {
            direction.y = direction.y + ENEMY_SPACE;
            addAction(Actions.moveTo(getX(), direction.y, 0.2f));
            animated = true;
            state = State.MOVING;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isDead()) {
            if (state == State.STANDING) {
                sprite.draw(batch);
                time = 0;
            } else if (state == State.TELEPORTING && !tAnimation.isAnimationFinished(time)) {
                time += Gdx.graphics.getDeltaTime();
                invulnerable = true;
                animated = true;
                if (camera.position.y < direction.y + ENEMY_SPACE * 2) {
                    camera.position.y += 45/3;
                }
                batch.draw(tAnimation.getKeyFrame(time), getX() - getWidth() / 2 - 16, getY() - getHeight() / 2 - 16);
            } else if (state == State.TELEPORTING && tAnimation.isAnimationFinished(time)) {
                jump();
                invulnerable = false;
                animated = false;
                state = State.STANDING;
                time = 0;
            } else if (state == State.MOVING) {
                batch.draw(texture, getX() - getWidth() / 2, getY() - getHeight() / 2);
            }
        } else {
            for (Shards shard : shards) {
                shard.render(batch);
            }
        }
    }

    public void render() {
        update();

    }

    public void jump() {
        state = State.TELEPORTING;
        direction.y += ENEMY_SPACE * 2;
        setY(getY() + ENEMY_SPACE * 2);
        state = State.STANDING;
    }

    public boolean collides(EnemySquare square) {
        if (invulnerable) return false;
        Rectangle enemy = new Rectangle(square.getX() - square.getWidth() / 2, square.getY() - square.getHeight() / 2, square.getWidth(), square.getHeight());
        Rectangle player = new Rectangle(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
        return player.overlaps(enemy);
    }

    private class Shards {
        private Body body;
        private float w;
        private float h;
        private Texture texture;
        private TextureRegion textureRegion;

        Shards(int i, int j, float x, float y) {
            texture = new Texture("player.png");
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
            fixtureDef.density = 0.8f;
            fixtureDef.friction = 0.1f;
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
            System.out.println("x = " + x);
            System.out.println("y = " + y);
            sb.draw(textureRegion, x, y, w / 2, h / 2, w, h, 1, 1, angle);
        }

        void dispose() {
            texture.dispose();
        }
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
        texture.dispose();
        teleAnimTexture.dispose();
        for (Shards shard : shards) {
            shard.dispose();
        }
        moveAnimTexture.dispose();
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

    @Override
    public float getWidth() {
        return PLAYER_WIDTH;
    }

    @Override
    public float getHeight() {
        return PLAYER_HEIGHT;
    }
}
