package com.whaletail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.whaletail.interfaces.AdWatcher;
import com.whaletail.interfaces.Analytic;
import com.whaletail.interfaces.GameService;
import com.whaletail.interfaces.OnAdCallback;
import com.whaletail.interfaces.OnRetryPressed;


public class AndroidLauncher extends AndroidApplication implements RewardedVideoAdListener, GameService {

    private static final String TAG = "TAG";


    private static final int RC_SIGN_IN = 9001;

    private FirebaseAnalytics mFirebaseAnalytics;
    private RewardedVideoAd mRewardedVideoAd;


    private static String SAVED_LEADERBOARD_REQUESTED = "SAVED_LEADERBOARD_REQUESTED";
    private static String SAVED_ACHIEVEMENTS_REQUESTED = "SAVED_ACHIEVEMENTS_REQUESTED";

    private boolean mLeaderboardRequested;
    private boolean mAchievementsRequested;

    private GoogleSignInAccount signedInAccount;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-8186248102983118~8660017752");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct == null) {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        } else {
            signedInAccount = acct;
        }

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


        final InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8186248102983118/7491976786");
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice("0DC86204419E0F6C995FC85EB3744EAF").build());

        loadRewardedVideoAd();
        CantReachGame game = new CantReachGame(new Analytic() {
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
            public void pressedRetry(int tries, final OnRetryPressed onRetryPressed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }

                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int i) {
                                onRetryPressed.onWatchedAd();
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }

                            @Override
                            public void onAdClosed() {
                                onRetryPressed.onWatchedAd();
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        });
                    }
                });


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

            @Override
            public void hard() {
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("hard", params);
            }

            @Override
            public void medium() {

                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("medium", params);
            }

            @Override
            public void easy() {

                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("easy", params);
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
                this);
        View view = initializeForView(game, config);

        setContentView(R.layout.general_activity);

        ((AdView) findViewById(R.id.banner)).loadAd(new AdRequest.Builder()
                .addTestDevice("64b00758-fdc5-4402-91ef-f3045bcadcb8").build());
        ((FrameLayout) findViewById(R.id.fl_game_view)).addView(view, 0);

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
                        .addTestDevice("64b00758-fdc5-4402-91ef-f3045bcadcb8")
                        .build());
    }


    @Override
    public void displayLeaderboard() {
//        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
//                getString(R.string.leaderboard_cantreach111rating)), 24);
    }

    @Override
    public void displayAchievements() {
        GoogleSignInAccount lastSignedInAccount;
        if (signedInAccount == null) {
            lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        } else {
            lastSignedInAccount = signedInAccount;
        }
        Games.getAchievementsClient(this, lastSignedInAccount)
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivity(intent);
                    }
                });
    }

    private void showAchievements(GoogleSignInAccount lastSignedInAccount) {
        if (lastSignedInAccount != null) {
            Games.getAchievementsClient(this, lastSignedInAccount)
                    .getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, 25);
                        }
                    });
        }
    }

    private GoogleSignInAccount achieve(int score, @StringRes final int id) {
        final GoogleSignInAccount lastSignedInAccount;
        if (signedInAccount == null) {
            lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        } else {
            lastSignedInAccount = signedInAccount;
        }
        if (lastSignedInAccount != null) {
            logAchievement(score);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Games.getAchievementsClient(AndroidLauncher.this, lastSignedInAccount)
                            .unlock(getString(id));

                }
            });
        }
        return lastSignedInAccount;
    }

    @Override
    public void reach20Points() {
        achieve(20, R.string.achievement_20_points);
    }

    @Override
    public void reach40Points() {
        achieve(40, R.string.achievement_40_points);
    }

    @Override
    public void reach60Points() {
        showAchievements(achieve(60, R.string.achievement_60_points));
    }

    @Override
    public void reach80Points() {
        showAchievements(achieve(80, R.string.achievement_80_points));
    }

    @Override
    public void reach100Points() {
        showAchievements(achieve(100, R.string.achievement_100_points));
    }

    @Override
    public void reach111Points() {
        showAchievements(achieve(111, R.string.achievement_you_got_it));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                signedInAccount = result.getSignInAccount();

                GamesClient gamesClient = Games.getGamesClient(this, signedInAccount);
                gamesClient.setViewForPopups(getWindow().getDecorView().findViewById(android.R.id.content));
            }
        }
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
