package com.example.andmoduleads.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ads.control.admob.Admob;
import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.AdsInterCallBack;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.PreloadNativeCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;

public class PreloadAdsUtils {
    private static final String TAG = "PreloadAdsUtils";
    private static PreloadAdsUtils instance;

    public static PreloadAdsUtils getInstance() {
        if (instance == null) {
            instance = new PreloadAdsUtils();
        }
        return instance;
    }

    private int loadTimesFailHigh = 0;
    private int loadTimesFailMedium = 0;
    private int loadTimesFailNormal = 0;
    private final int limitLoad = 2;

    public void loadInterSameTime(final Context context, String idAdInterPriority, String idAdInterNormal, AperoAdCallback adListener) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            return;
        }
        loadTimesFailHigh = 0;
        loadTimesFailNormal = 0;
        if (MyApplication.getApplication().getStorageCommon().interPriority == null) {
            loadInterPriority(context, idAdInterPriority, adListener);
        }
        if (MyApplication.getApplication().getStorageCommon().interNormal == null) {
            loadInterNormal(context, idAdInterNormal, adListener);
        }
    }

    private void loadInterNormal(Context context, String idAdInterNormal, AperoAdCallback adListener) {
        Log.e(TAG, "loadInterNormal: ");
        AperoAd.getInstance().getInterstitialAds(context, idAdInterNormal, new AperoAdCallback() {
            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                adListener.onInterstitialLoad(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                super.onAdFailedToLoad(adError);
                Log.e(TAG, "onAdFailedToLoad: Normal");
                if (loadTimesFailNormal < limitLoad) {
                    loadTimesFailNormal++;
                    loadInterNormal(context, idAdInterNormal, adListener);
                }
            }
        });
    }

    private void loadInterPriority(Context context, String idAdInterPriority, AperoAdCallback adListener) {
        Log.e(TAG, "loadInterPriority: ");
        AperoAd.getInstance().getInterstitialAds(context, idAdInterPriority, new AperoAdCallback() {
            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                adListener.onInterPriorityLoaded(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                super.onAdFailedToLoad(adError);
                Log.e(TAG, "onAdFailedToLoad: Priority");
                if (loadTimesFailHigh < limitLoad) {
                    loadTimesFailHigh++;
                    loadInterPriority(context, idAdInterPriority, adListener);
                }
            }
        });
    }

    public void showInterSameTime(
            Context context,
            ApInterstitialAd interPriority,
            ApInterstitialAd interNormal,
            Boolean reload,
            AdsInterCallBack adCallback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adCallback != null) {
                adCallback.onNextAction();
            }
            return;
        }
        if (interPriority != null) {
            Log.e(TAG, "showInterSameTime: Ad priority");
            AperoAd.getInstance().forceShowInterstitial(
                    context,
                    interPriority,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        @Override
                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialPriorityShowed();
                        }
                    },
                    reload);
        } else if (interNormal != null) {
            Log.e(TAG, "showInterSameTime: Ad normal");
            AperoAd.getInstance().forceShowInterstitial(
                    context,
                    interNormal,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();

                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialNormalShowed();
                        }
                    },
                    reload);
        } else {
            adCallback.onNextAction();
        }
    }

    public void setLayoutNative(int layoutNativeCustom){
        if (MyApplication.getApplication().getStorageCommon().nativeAdHigh != null){
            MyApplication.getApplication().getStorageCommon().apNativeAdHigh =
                    new ApNativeAd(layoutNativeCustom, MyApplication.getApplication().getStorageCommon().nativeAdHigh);
        } else if (MyApplication.getApplication().getStorageCommon().nativeAdMedium != null){
            MyApplication.getApplication().getStorageCommon().apNativeAdMedium =
                    new ApNativeAd(layoutNativeCustom, MyApplication.getApplication().getStorageCommon().nativeAdMedium);
        } else if (MyApplication.getApplication().getStorageCommon().nativeAdNormal != null){
            MyApplication.getApplication().getStorageCommon().apNativeAdNormal =
                    new ApNativeAd(layoutNativeCustom, MyApplication.getApplication().getStorageCommon().nativeAdNormal);
        }
    }

    public void preLoadNativeSameTime(final Activity activity) {
        if (AppPurchase.getInstance().isPurchased(activity)) {
            return;
        }
        loadTimesFailHigh = 0;
        loadTimesFailMedium = 0;
        loadTimesFailNormal = 0;

        PreloadNativeCallback callBack = new PreloadNativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                MyApplication.getApplication().getStorageCommon().nativeAdNormal = nativeAd;
            }

            @Override
            public void onNativeHighAdLoaded(NativeAd nativeAd) {
                MyApplication.getApplication().getStorageCommon().nativeAdHigh = nativeAd;
            }

            @Override
            public void onNativeMediumAdLoaded(NativeAd nativeAd) {
                MyApplication.getApplication().getStorageCommon().nativeAdMedium = nativeAd;
            }

            @Override
            public void onNativeAdShow() {}

            @Override
            public void onNativeHighAdShow() {}

            @Override
            public void onNativeMediumAdShow() {}
        };

        if (MyApplication.getApplication().getStorageCommon().nativeAdHigh == null) {
            loadNativeHigh(activity, BuildConfig.ad_native_priority, callBack);
        }
        if (MyApplication.getApplication().getStorageCommon().nativeAdMedium == null) {
            loadNativeMedium(activity, BuildConfig.ad_native_medium, callBack);
        }
        if (MyApplication.getApplication().getStorageCommon().nativeAdNormal == null) {
            loadNativeNormal(activity, BuildConfig.ad_native, callBack);
        }
    }

    private void loadNativeHigh(
            Activity activity,
            String idNativeHigh,
            PreloadNativeCallback callBack) {
        Admob.getInstance().loadNativeAd(
                activity,
                idNativeHigh,
                new AdCallback(){
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        Log.d(TAG, "loadNativeHigh");
                        callBack.onNativeHighAdLoaded(unifiedNativeAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        Log.d(TAG, "FailToLoadNativeHigh");
                        if (loadTimesFailHigh < limitLoad) {
                            loadTimesFailHigh++;
                            loadNativeHigh(activity, idNativeHigh, callBack);
                        }
                    }
                }
        );
    }

    private void loadNativeMedium(
            Activity activity,
            String idNativeMedium,
            PreloadNativeCallback callBack) {
        Admob.getInstance().loadNativeAd(
                activity,
                idNativeMedium,
                new AdCallback(){
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        Log.d(TAG, "loadNativeMedium");
                        callBack.onNativeMediumAdLoaded(unifiedNativeAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        Log.d(TAG, "FailToLoadNativeMedium");
                        if (loadTimesFailMedium < limitLoad) {
                            loadTimesFailMedium++;
                            loadNativeMedium(activity, idNativeMedium, callBack);
                        }
                    }
                }
        );
    }

    private void loadNativeNormal(
            Activity activity,
            String idNativeNormal,
            PreloadNativeCallback callBack) {
        Admob.getInstance().loadNativeAd(
                activity,
                idNativeNormal,
                new AdCallback(){
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        Log.d(TAG, "loadNativeNormal");
                        callBack.onNativeAdLoaded(unifiedNativeAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        Log.d(TAG, "FailToLoadNativeNormal");
                        if (loadTimesFailNormal < limitLoad) {
                            loadTimesFailNormal++;
                            loadNativeNormal(activity, idNativeNormal, callBack);
                        }
                    }
                }
        );
    }

    public void showPreNativeSametime(
            Activity activity,
            FrameLayout adPlaceHolder,
            ShimmerFrameLayout containerShimmerLoading) {

        PreloadNativeCallback callback = new PreloadNativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {

            }

            @Override
            public void onNativeHighAdLoaded(NativeAd nativeAd) {

            }

            @Override
            public void onNativeMediumAdLoaded(NativeAd nativeAd) {

            }

            @Override
            public void onNativeAdShow() {
                MyApplication.getApplication().getStorageCommon().apNativeAdNormal = null;
                MyApplication.getApplication().getStorageCommon().nativeAdNormal = null;
                PreloadAdsUtils.getInstance().preLoadNativeSameTime(activity);
            }

            @Override
            public void onNativeHighAdShow() {
                MyApplication.getApplication().getStorageCommon().nativeAdHigh = null;
                MyApplication.getApplication().getStorageCommon().apNativeAdHigh = null;
                PreloadAdsUtils.getInstance().preLoadNativeSameTime(activity);
            }

            @Override
            public void onNativeMediumAdShow() {
                MyApplication.getApplication().getStorageCommon().nativeAdMedium = null;
                MyApplication.getApplication().getStorageCommon().apNativeAdMedium = null;
                PreloadAdsUtils.getInstance().preLoadNativeSameTime(activity);
            }
        };

        if (MyApplication.getApplication().getStorageCommon().apNativeAdHigh != null) {
            Log.d(TAG, "showPreNativeSametime: nativeAdHigh");
            AperoAd.getInstance().populateNativeAdView(
                    activity,
                    MyApplication.getApplication().getStorageCommon().apNativeAdHigh,
                    adPlaceHolder,
                    containerShimmerLoading
            );
            callback.onNativeHighAdShow();
        } else if (MyApplication.getApplication().getStorageCommon().apNativeAdMedium != null) {
            Log.d(TAG, "showPreNativeSametime: nativeAdMedium");
            AperoAd.getInstance().populateNativeAdView(
                    activity,
                    MyApplication.getApplication().getStorageCommon().apNativeAdMedium,
                    adPlaceHolder,
                    containerShimmerLoading
            );
            callback.onNativeMediumAdShow();
        } else if (MyApplication.getApplication().getStorageCommon().apNativeAdNormal != null) {
            Log.d(TAG, "showPreNativeSametime: nativeAdNormal");
            AperoAd.getInstance().populateNativeAdView(
                    activity,
                    MyApplication.getApplication().getStorageCommon().apNativeAdNormal,
                    adPlaceHolder,
                    containerShimmerLoading
            );
            callback.onNativeAdShow();
        } else {
            adPlaceHolder.setVisibility(View.GONE);
        }
    }

}
