package com.whaletail;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.whaletail.analytics.Analytic;

public class AndroidLauncher extends AndroidApplication {


    private AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-8186248102983118~8660017752");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        View viewGame = initializeForView(new CantReachGame(new Analytic() {
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
        }), config);
        adView = new AdView(this);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }
        });

        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-8186248102983118/1715636775");

        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice("82E51369C4FF66743DE3293622A03E1A");
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        relativeLayout.addView(viewGame);
        relativeLayout.addView(adView, adParams);
        adView.loadAd(builder.build());

        setContentView(relativeLayout);

    }
}
