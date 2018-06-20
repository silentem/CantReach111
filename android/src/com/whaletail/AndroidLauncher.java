package com.whaletail;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.whaletail.interfaces.AdWatcher;
import com.whaletail.interfaces.Analytic;
import com.whaletail.interfaces.GameService;
import com.whaletail.interfaces.OnAdCallback;

import google.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements RewardedVideoAdListener, GameHelper.GameHelperListener, GameService {

    private static final String TAG = "TAG";


    private static final int RC_SIGN_IN = 9001;

    private FirebaseAnalytics mFirebaseAnalytics;
    private RewardedVideoAd mRewardedVideoAd;


    private static String SAVED_LEADERBOARD_REQUESTED = "SAVED_LEADERBOARD_REQUESTED";
    private static String SAVED_ACHIEVEMENTS_REQUESTED = "SAVED_ACHIEVEMENTS_REQUESTED";

    private boolean mLeaderboardRequested;
    private boolean mAchievementsRequested;

    private GameHelper gameHelper;


    private OnAdCallback onAdCallback;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_LEADERBOARD_REQUESTED, mLeaderboardRequested);
        outState.putBoolean(SAVED_ACHIEVEMENTS_REQUESTED, mAchievementsRequested);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLeaderboardRequested = savedInstanceState.getBoolean(SAVED_LEADERBOARD_REQUESTED, false);
        mAchievementsRequested = savedInstanceState.getBoolean(SAVED_ACHIEVEMENTS_REQUESTED, false);
    }

    @Override
    public void onSignInFailed() {
        // handle sign-in failure (e.g. show Sign In button)
        mLeaderboardRequested = false;
        mAchievementsRequested = false;
    }

    @Override
    public void onSignInSucceeded() {

        if (mLeaderboardRequested) {
            displayLeaderboard();
            mLeaderboardRequested = false;
        }

        if (mAchievementsRequested) {
            displayAchievements();
            mAchievementsRequested = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, "ca-app-pub-8186248102983118~8660017752");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


        loadRewardedVideoAd();

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.setup(this);
        gameHelper.setMaxAutoSignInAttempts(0);
        gameHelper.beginUserInitiatedSignIn();


        initialize(
                new CantReachGame(new Analytic() {
                    @Override
                    public void submitScore(int score) {
                        Bundle params = new Bundle();
                        params.putString("score", String.valueOf(score));
                        mFirebaseAnalytics.logEvent("reached_score", params);
                    }

                    @Override
                    public void turnedOnMusic() {
                        Bundle params = new Bundle();
                        params.putBoolean("music", true);
                        mFirebaseAnalytics.logEvent("turn_on_music", params);
                    }

                    @Override
                    public void turnedOffMusic() {
                        Bundle params = new Bundle();
                        params.putBoolean("music", false);
                        mFirebaseAnalytics.logEvent("turn_off_music", params);
                    }

                    @Override
                    public void goneHome() {
                        Bundle params = new Bundle();
                        params.putString("nav", "home");
                        mFirebaseAnalytics.logEvent("navigation", params);

                    }

                    @Override
                    public void pressedPlay() {
                        Bundle params = new Bundle();
                        params.putString("nav", "play");
                        mFirebaseAnalytics.logEvent("navigation", params);

                    }

                    @Override
                    public void pressedRetry(int tries) {
                        Bundle params = new Bundle();
                        params.putString("nav", "retry");
                        params.putInt("tries", tries);
                        mFirebaseAnalytics.logEvent("navigation", params);
                    }

                    @Override
                    public void pressedOnWatchAd() {
                        Bundle params = new Bundle();
                        params.putBoolean("ad", true);
                        mFirebaseAnalytics.logEvent("pressed_on_ad", params);
                    }
                },
                        new AdWatcher() {
                            @Override
                            public void showAd(OnAdCallback callback) {
                                onAdCallback = callback;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mRewardedVideoAd.isLoaded()) {
                                            mRewardedVideoAd.show();
                                        }
                                    }
                                });
                            }
                        },
                        this), config);

    }

    private void logAchievement(int points) {
        Bundle params = new Bundle();
        params.putString("achievement" + points, String.valueOf(points));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, params);
    }

    private void loadRewardedVideoAd() {
        String productionAd = "ca-app-pub-8186248102983118/5801237239";
        String testAd = "ca-app-pub-3940256099942544/5224354917";
        mRewardedVideoAd.loadAd(productionAd,
                new AdRequest.Builder()
//                        .addTestDevice("AE901101C564DAFE18B7BA29B1A6CA1A")
                        .build());
    }


    @Override
    public void displayLeaderboard() {
        if (gameHelper.isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_cantreach111rating)), 24);
        } else {
            gameHelper.beginUserInitiatedSignIn();
            mLeaderboardRequested = true;
        }
    }

    @Override
    public void displayAchievements() {
        if (gameHelper.isSignedIn()) {
            startActivityForResult(
                    Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 25);
        } else {
            gameHelper.beginUserInitiatedSignIn();
            mAchievementsRequested = true;
        }
    }

    @Override
    public void reach20Points() {
        if (gameHelper.isSignedIn()) {
            startActivityForResult(
                    Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 25);
            Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                    .unlock(getString(R.string.achievement_20_points));
            logAchievement(20);
        }
    }

    @Override
    public void reach40Points() {
        logAchievement(40);
        Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                .unlock(getString(R.string.achievement_40_points));
    }

    @Override
    public void reach60Points() {
        logAchievement(60);
        Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                .unlock(getString(R.string.achievement_60_points));
    }

    @Override
    public void reach80Points() {
        logAchievement(80);
        Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                .unlock(getString(R.string.achievement_80_points));
    }

    @Override
    public void reach100Points() {
        logAchievement(100);
        Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                .unlock(getString(R.string.achievement_100_points));
    }

    @Override
    public void reach111Points() {
        logAchievement(111);
        Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                .unlock(getString(R.string.achievement_you_got_it));
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        loadRewardedVideoAd();
        if (onAdCallback != null) {
            onAdCallback.onAdWatched();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        if (i < 5) {
            loadRewardedVideoAd();
        }
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
