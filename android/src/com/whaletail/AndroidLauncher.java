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

import java.util.ArrayList;

public class AndroidLauncher extends AndroidApplication {


    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-8186248102983118~8660017752");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        View viewGame = initializeForView(new WhaleGdxGame(), config);
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
