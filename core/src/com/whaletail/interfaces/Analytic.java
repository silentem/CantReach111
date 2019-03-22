package com.whaletail.interfaces;

public interface Analytic {

    void submitScore(int score);

    void turnedOnMusic();

    void turnedOffMusic();

    void goneHome();

    void pressedPlay();

    void pressedRetry(int tries, OnRetryPressed onRetryPressed);

    void pressedOnWatchAd();

    void hard();

    void medium();

    void easy();

}
