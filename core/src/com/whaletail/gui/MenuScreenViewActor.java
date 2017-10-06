package com.whaletail.gui;

import com.badlogic.gdx.math.Vector2;
import com.whaletail.actors.EnemySquare;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class MenuScreenViewActor extends ViewActor {

    public MenuScreenViewActor(Text textToRelyOn, EnemySquare.View view) {
        super(new Vector2(0 - view.getWidth(),
                        textToRelyOn.getY() - textToRelyOn.getHeight() / 2 - view.getHeight() / 2),
                new Vector2(textToRelyOn.getX() - view.getWidth(),
                        textToRelyOn.getY() - textToRelyOn.getHeight() / 2 - view.getHeight() / 2),
                view);
    }
    @Override
    public void move(float speed) {
        addAction(sequence(delay(.2f), moveTo(direction.x, direction.y, speed)));
    }
}
