package com.whaletail.analytics;

public interface Analytic {

    void submitScore(int score);

    void turnedOnMusic();

    void turnedOffMusic();

    void goneHome();

    void pressedPlay();

    void pressedRetry(int tries);

}
