package com.whaletail.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;

import static com.whaletail.Constants.PPM;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class EnemySquare extends Actor {

    private static final int ENEMY_WIDTH = 16;
    public static final int ENEMY_HEIGHT = 16;
    public static final int GAP = 6;
    public static final int ENEMY_SPACE = GAP + 64 + GAP;

    private Texture texture1;
    private Texture texture2;
    private Texture texture3;
    private Texture texture4;
    private Sprite sprite;
    private int speed;
    private boolean rightLeft;
    private float maxWidth;
    private int enemyWidth;
    private World world;
    private Body body;
    private int height;
    private int heightMultipl;

    public EnemySquare(float y, int speed, float maxWidth, World world) {
        this.speed = speed;
        this.maxWidth = maxWidth;
        this.world = world;
        heightMultipl = MathUtils.random(3) + 1;
        height = ENEMY_HEIGHT * heightMultipl;
        texture1 = new Texture("enemy-1.png");
        texture2 = new Texture("enemy-2.png");
        texture3 = new Texture("enemy-3.png");
        texture4 = new Texture("enemy-4.png");
        sprite = new Sprite();
        setTextureBySpeed();
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

    private void setTextureBySpeed(){
        if (MathUtils.random(10) == 9) {
            sprite.setTexture(texture4);
        } else if (speed <= 5) {
            sprite.setTexture(texture1);
        } else if (speed >= 9) {
            sprite.setTexture(texture3);
        } else {
            sprite.setTexture(texture2);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        render(batch);
    }

    public void render(Batch sb) {
        float x = body.getPosition().x * PPM - getWidth() / 2;
        float y = body.getPosition().y * PPM - getHeight() / 2;
        for (float i = x; i < x + (ENEMY_WIDTH * enemyWidth); i += ENEMY_WIDTH) {
            for (float j = y; j < y + height; j += ENEMY_HEIGHT) {
                sb.draw(sprite.getTexture(), i, j);
            }
        }
    }

    public void reset() {
        if (rightLeft) {
            setPosition(0 - getWidth(), body.getPosition().y * PPM);
        } else {
            setPosition(maxWidth + getWidth() / 2, body.getPosition().y * PPM);
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
        texture1.dispose();
        texture2.dispose();
        texture3.dispose();
        texture4.dispose();
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
        return height;
    }


    public void createNew(float y, int speed) {
        this.speed = speed;
        rightLeft = MathUtils.randomBoolean();
        enemyWidth = MathUtils.random(10) + 3;

        setTextureBySpeed();
        heightMultipl = MathUtils.random(3) + 1;
        height = ENEMY_HEIGHT * heightMultipl;
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
