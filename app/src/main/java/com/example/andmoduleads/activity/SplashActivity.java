package com.example.andmoduleads.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.example.andmoduleads.SharePreferenceUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;

public class SplashActivity extends AppCompatActivity {

    private long TIMEOUT_SPLASH = 30000;
    private long TIME_DELAY_SPLASH = 5000;
    private boolean isFirstRunApp = true;
    // in case use inter splash : inter
    // in case use app open start : app_open_start
    private final String typeAdsSplash = "inter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AperoAd.getInstance().initAdsNetwork();
        // For apps that require remote config firebase, you need to finish loading remote config to load the ad
        handleFetchedRemoteConfig();
    }

    private void handleFetchedRemoteConfig() {
        AppPurchase.getInstance().setBillingListener((integer) -> {
            loadAdsSplash();
        }, 5000);
    }

    private void loadAdsSplash() {
        if (AppPurchase.getInstance().isPurchased()) {
            MyApplication.getApplication().isAdCloseSplash.postValue(true);
            navigateToNextScreen();
        } else {
            MyApplication.getApplication().isAdCloseSplash.postValue(false);

            /* In the splash screen will usually use inter splash or ads open app.
            Depending on which ad is used, the function will be used to load that ad */

            if (typeAdsSplash.equals("inter")) {
                AperoAd.getInstance().loadSplashInterstitialAds(
                        this,
                        BuildConfig.inter_splash,
                        TIMEOUT_SPLASH,
                        TIME_DELAY_SPLASH,
                        false,
                        new AperoAdCallback() {
                            @Override
                            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                                super.onAdFailedToLoad(adError);
                                if (isDestroyed() || isFinishing()) return;
                                MyApplication.getApplication().isAdCloseSplash.postValue(true);
                            }

                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                if (isDestroyed() || isFinishing()) return;
                                MyApplication.getApplication().isAdCloseSplash.postValue(true);
                                navigateToNextScreen();
                            }

                            @Override
                            public void onAdSplashReady() {
                                super.onAdSplashReady();
                                if (isDestroyed() || isFinishing()) return;
                                showInterSplash();
                            }
                        }
                );
            } else {
                AppOpenManager.getInstance().loadOpenAppAdSplash(
                        this,
                        TIMEOUT_SPLASH,
                        TIME_DELAY_SPLASH,
                        false,
                        new AdCallback() {
                            @Override
                            public void onAdSplashReady() {
                                super.onAdSplashReady();
                                if (isDestroyed() || isFinishing()) return;
                                showAdsOpenAppSplash();
                            }

                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                if (isDestroyed() || isFinishing()) return;
                                MyApplication.getApplication().isAdCloseSplash.postValue(true);
                                navigateToNextScreen();
                            }

                            @Override
                            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                                super.onAdFailedToLoad(i);
                                if (isDestroyed() || isFinishing()) return;
                                MyApplication.getApplication().isAdCloseSplash.postValue(true);
                                navigateToNextScreen();
                            }
                        }
                );
            }
        }
    }

    private void showInterSplash() {
        AperoAd.getInstance().onShowSplash(this, new AperoAdCallback() {
            @Override
            public void onAdFailedToShow(@Nullable ApAdError adError) {
                super.onAdFailedToShow(adError);
                if (isDestroyed() || isFinishing()) return;
                MyApplication.getApplication().isAdCloseSplash.postValue(true);
            }

            @Override
            public void onNextAction() {
                super.onNextAction();
                if (isDestroyed() || isFinishing()) return;
                navigateToNextScreen();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (isDestroyed() || isFinishing()) return;
                MyApplication.getApplication().isAdCloseSplash.postValue(true);
            }
        });
    }

    private void showAdsOpenAppSplash() {
        AppOpenManager.getInstance().showAppOpenSplash(
                this,
                new AdCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        if (isDestroyed() || isFinishing()) return;
                        navigateToNextScreen();
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        if (isDestroyed() || isFinishing()) return;
                        MyApplication.getApplication().isAdCloseSplash.postValue(true);
                        navigateToNextScreen();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        if (isDestroyed() || isFinishing()) return;
                        MyApplication.getApplication().isAdCloseSplash.postValue(true);
                        navigateToNextScreen();
                    }
                }
        );
    }

    private void navigateToNextScreen() {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        if (SharePreferenceUtils.isFirstOpenApp(this)) {
            loadNativeAdsFirstLanguageOpen();
            navigateToLFO();
        } else {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLFO(){
        Intent intent = new Intent(this, LanguageFirstOpenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadNativeAdsFirstLanguageOpen() {
        if (MyApplication.getApplication().getStorageCommon().nativeAdsLanguage.getValue() == null
                && !AppPurchase.getInstance().isPurchased()) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                    this,
                    BuildConfig.native_language,
                    R.layout.custom_native_ads_language_first,
                    new AperoAdCallback() {
                        @Override
                        public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                            super.onNativeAdLoaded(nativeAd);
                            MyApplication.getApplication().getStorageCommon().nativeAdsLanguage.postValue(nativeAd);
                        }

                        @Override
                        public void onAdFailedToLoad(@Nullable ApAdError adError) {
                            super.onAdFailedToLoad(adError);
                            MyApplication.getApplication().getStorageCommon().nativeAdsLanguage.postValue(null);
                        }
                    }
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstRunApp) {
            isFirstRunApp = false;
            return;
        }
        if (typeAdsSplash.equals("inter")) {
            AperoAd.getInstance().onCheckShowSplashWhenFail(this, new AperoAdCallback() {
                @Override
                public void onAdFailedToShow(@Nullable ApAdError adError) {
                    super.onAdFailedToShow(adError);
                    if (isDestroyed() || isFinishing()) return;
                    MyApplication.getApplication().isAdCloseSplash.postValue(true);
                }

                @Override
                public void onNextAction() {
                    super.onNextAction();
                    if (isDestroyed() || isFinishing()) return;
                    navigateToNextScreen();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    if (isDestroyed() || isFinishing()) return;
                    MyApplication.getApplication().isAdCloseSplash.postValue(true);
                }
            }, 1000);
        } else {
            AppOpenManager.getInstance().onCheckShowAppOpenSplashWhenFail(
                    this,
                    new AdCallback() {
                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            if (isDestroyed() || isFinishing()) return;
                            navigateToNextScreen();
                        }

                        @Override
                        public void onAdFailedToShow(@Nullable AdError adError) {
                            super.onAdFailedToShow(adError);
                            if (isDestroyed() || isFinishing()) return;
                            MyApplication.getApplication().isAdCloseSplash.postValue(true);
                            navigateToNextScreen();
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            if (isDestroyed() || isFinishing()) return;
                            MyApplication.getApplication().isAdCloseSplash.postValue(true);
                            navigateToNextScreen();
                        }
                    },
                    1000
            );
        }
    }
}
