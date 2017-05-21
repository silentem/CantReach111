package com.whaletail.gui;

/**
 * @author Whaletail
 * @email silentem1113@gmail.com
 */

public class Score {

    private int score;

    public void add(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return Integer.toString(score);
    }
}
