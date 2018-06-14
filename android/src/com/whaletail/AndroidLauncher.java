package com.whaletail;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.whaletail.interfaces.AdWatcher;
import com.whaletail.interfaces.Analytic;
import com.whaletail.interfaces.OnAdCallback;

public class AndroidLauncher extends AndroidApplication implements RewardedVideoAdListener {


    private FirebaseAnalytics mFirebaseAnalytics;
    private RewardedVideoAd mRewardedVideoAd;


    private OnAdCallback onAdCallback;

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
                        }), config);

    }

    private void loadRewardedVideoAd() {
        String productionAd = "ca-app-pub-8186248102983118/5801237239";
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
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
        if (onAdCallback != null) {
            onAdCallback.onAdWatched();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

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
