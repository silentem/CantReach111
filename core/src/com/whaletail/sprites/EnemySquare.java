package com.whaletail.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private static final int ENEMY_HEIGHT = 16;
    private static final int GAP = 6;
    public static final int ENEMY_SPACE = GAP + 64 + GAP;

    private View view;
    private int speed;
    private boolean rightLeft;
    private float maxWidth;
    private World world;
    private Body body;

    public EnemySquare(float y, int speed, float maxWidth, World world) {
        this.speed = speed;
        this.maxWidth = maxWidth;
        this.world = world;
        createNew(y, speed);
    }

    public void createNew(float y, int speed) {
        rightLeft = MathUtils.randomBoolean();
        createView(speed);
        createBody();
        if (rightLeft) {
            setPosition(0 - getWidth() - MathUtils.random(200), y);
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        } else {
            setPosition(maxWidth + MathUtils.random(200), y);
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }
    }

    private void createView(int speed) {
        int vCount = MathUtils.random(3) + 1;
        int hCount = MathUtils.random(12) + 5;
        Texture pattern;
        if (MathUtils.random(10) == 9) {
            pattern = new Texture("enemy-4.png");
        } else if (speed <= 5) {
            pattern = new Texture("enemy-1.png");
        } else if (speed >= 9) {
            pattern = new Texture("enemy-2.png");
        } else {
            pattern = new Texture("enemy-3.png");
        }
        view = new View(pattern, hCount, vCount);
    }

    private void createBody() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 2 / PPM, getHeight() / 2 / PPM);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        if (body == null) {
            body = world.createBody(def);
        } else {
            body.destroyFixture(body.getFixtureList().get(0));
        }
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = body.getPosition().x * PPM - getWidth() / 2;
        float y = body.getPosition().y * PPM - getHeight() / 2;
        view.draw(batch, x, y);
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

    public void dispose() {
        view.dispose();
    }

    public boolean isRightLeft() {
        return rightLeft;
    }

    @Override
    public void setPosition(float x, float y) {
        body.setTransform(x / PPM, y / PPM, 0);
    }

    public int getSpeed() {
        return speed;
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
        return ENEMY_WIDTH * view.hCount;
    }


    @Override
    public float getHeight() {
        return ENEMY_HEIGHT * view.vCount;
    }

    public static class View {
        private Texture texturePattern;
        private int hCount;
        private int vCount;

        public View(Texture texturePattern, int hCount, int vCount) {
            this.texturePattern = texturePattern;
            this.hCount = hCount;
            this.vCount = vCount;
        }

        public void draw(Batch batch, float x, float y) {
            for (float i = x; i < x + ENEMY_WIDTH * hCount; i += ENEMY_WIDTH) {
                for (float j = y; j < y + ENEMY_HEIGHT * vCount; j += ENEMY_HEIGHT) {
                    batch.draw(texturePattern, i, j);
                }
            }
        }

        public void dispose() {
            texturePattern.dispose();
        }
    }
}
