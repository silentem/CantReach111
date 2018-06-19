package com.whaletail;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.whaletail.interfaces.AdWatcher;
import com.whaletail.interfaces.Analytic;
import com.whaletail.interfaces.GameService;
import com.whaletail.interfaces.OnAdCallback;

public class AndroidLauncher extends AndroidApplication implements RewardedVideoAdListener {

    private static final String TAG = "TAG";


    private static final int RC_SIGN_IN = 9001;

    private FirebaseAnalytics mFirebaseAnalytics;
    private RewardedVideoAd mRewardedVideoAd;


    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    private OnAdCallback onAdCallback;


    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;

    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                        }
                    }
                });
    }

    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                        } else {
                        }

                    }
                });
    }


    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                }


                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        startSignInIntent();
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
                        },
                        new GameService() {
                            @Override
                            public void reach20Points() {
                                logAchievement(20);
                                Games.getAchievementsClient(AndroidLauncher.this, GoogleSignIn.getLastSignedInAccount(AndroidLauncher.this))
                                        .unlock(getString(R.string.achievement_20_points));
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
                        }), config);

    }

    private void logAchievement(int points) {
        Bundle params = new Bundle();
        params.putString("achievement" + points, String.valueOf(points));
        mFirebaseAnalytics.logEvent("achievement" + points, params);
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
