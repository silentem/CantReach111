package com.whaletail.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

import static com.whaletail.Constants.PPM;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class EnemySquare extends Sprite {

    private static final int ENEMY_WIDTH = 16;
    public static final int ENEMY_HEIGHT = 64;
    public static final int GAP = 6;
    public static final int ENEMY_SPACE = GAP + ENEMY_HEIGHT + GAP;

    private Texture texture;
    private int speed;
    private boolean rightLeft;
    private float maxWidth;
    private int enemyWidth;
    private World world;
    private Body body;

    public EnemySquare(float y, int speed, float maxWidth, World world) {
        this.speed = speed;
        this.maxWidth = maxWidth;
        this.world = world;
        texture = new Texture("enemy1_mat.png");
        setTexture(texture);
        enemyWidth = MathUtils.random(12) + 5;
        rightLeft = MathUtils.randomBoolean();
        body = createBody();
        if (rightLeft) {
            setPosition(0 - getWidth() - MathUtils.random(200), y);
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        } else {
            setPosition(maxWidth + MathUtils.random(200), y);
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }

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

    public int getSpeed() {
        return speed;
    }

    public void render(SpriteBatch sb) {
        float x = body.getPosition().x * PPM - getWidth() / 2;
        float y = body.getPosition().y * PPM - getHeight() / 2;
        for (float i = x; i < x + (ENEMY_WIDTH * enemyWidth); i += ENEMY_WIDTH) {
            sb.draw(texture, i, y);
        }
    }

    public void reset() {
        if (rightLeft) {
            setPosition(0 - getWidth(), body.getPosition().y * PPM);
        } else {
            setPosition(maxWidth + getWidth()/2, body.getPosition().y * PPM);
        }
    }

    public void stop() {
        float delay = 0.5f;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                body.setLinearVelocity(0, 0);
            }
        }, delay);

    }

    public boolean isRightLeft() {
        return rightLeft;
    }

    public void dispose() {
        texture.dispose();
    }

    @Override
    public void setPosition(float x, float y) {
        body.setTransform(x / PPM, y / PPM, 0);
    }

    @Override
    public float getX() {
        return body.getPosition().x * PPM;
    }

    @Override
    public float getY() {
        return body.getPosition().y * PPM;
    }

    @Override
    public void setX(float x) {
        setPosition(x, body.getPosition().y * PPM);
    }

    @Override
    public void setY(float y) {
        setPosition(body.getPosition().x * PPM, y);
    }

    @Override
    public float getWidth() {
        return ENEMY_WIDTH * enemyWidth;
    }

    @Override
    public float getHeight() {
        return ENEMY_HEIGHT;
    }



    public void createNew(float y, int speed) {
        this.speed = speed;
        rightLeft = MathUtils.randomBoolean();
        enemyWidth = MathUtils.random(10) + 3;
        setTexture(texture);
        body.destroyFixture(body.getFixtureList().get(0));
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 2 / PPM, getHeight() / 2 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        if (rightLeft) {
            setPosition(0 - getWidth() - MathUtils.random(200), y);
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        } else {
            setPosition(maxWidth + MathUtils.random(200), y);
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }
    }
}
