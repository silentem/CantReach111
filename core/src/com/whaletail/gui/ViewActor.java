package com.whaletail.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.whaletail.actors.EnemySquare;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */


public class ViewActor extends Actor {

    protected Vector2 direction;
    protected EnemySquare.View view;

    public ViewActor(Vector2 position,
              Vector2 direction,
              EnemySquare.View view) {
        this.direction = direction;
        this.view = view;
        setPosition(position.x, position.y);
    }

    public ViewActor(Vector2 position,
              EnemySquare.View view) {
        this.view = view;
        setPosition(position.x, position.y);
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public void move(float speed) {
        addAction(moveTo(direction.x, direction.y, speed));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        view.draw(batch, getX(), getY());
    }

    void dispose() {
        view.dispose();
    }

    @Override
    public float getWidth() {
        return view.getWidth();
    }
}
