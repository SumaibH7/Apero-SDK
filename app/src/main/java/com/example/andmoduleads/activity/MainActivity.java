package com.example.andmoduleads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.ads.wrapper.ApRewardItem;
import com.ads.control.billing.AppPurchase;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.event.AperoAdjust;
import com.ads.control.event.AperoLogEventManager;
import com.ads.control.funtion.PurchaseListener;
import com.ads.control.helper.banner.BannerAdConfig;
import com.ads.control.helper.banner.BannerAdHelper;
import com.ads.control.helper.banner.params.BannerAdParam;
import com.example.andmoduleads.AdsInterCallBack;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.example.andmoduleads.databinding.ActivityMainBinding;
import com.example.andmoduleads.utils.Constant;
import com.example.andmoduleads.utils.NetworkUtil;
import com.example.andmoduleads.utils.PreloadAdsUtils;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";
    private static final String TAG = "MAIN_TEST";
    //adjust
    private static final String EVENT_TOKEN_SIMPLE = "g3mfiw";
    private static final String EVENT_TOKEN_REVENUE = "a4fd35";
    private ActivityMainBinding binding;
    private boolean onCheckUserEarnedReward;

    private boolean isShowDialogExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AperoAd.getInstance().setCountClickToShowAds(3);
        PreloadAdsUtils.getInstance().preLoadNativeSameTime(this);

        loadAdInterstitial();
        loadAdInterstitialByClick();
        loadAdReward();
        aperoBanner();
        listenerPurchase();

        Button btnIAP = findViewById(R.id.btIap);
        if (AppPurchase.getInstance().isPurchased()) {
            btnIAP.setText("Consume Purchase");
        } else {
            btnIAP.setText("Purchase");
        }

        initEvent();

    }

    private void aperoBanner() {
        BannerAdHelper bannerAdHelper = new BannerAdHelper(
                this,
                this,
                new BannerAdConfig(
                        BuildConfig.banner_normal,
                        true,
                        true
                )
        );
        bannerAdHelper.setBannerContentView(binding.flAdsBanner);
        bannerAdHelper.requestAds(BannerAdParam.Request.create());
    }

    private void initEvent() {
        binding.btnShowInterstitial.setOnClickListener(view -> showInterstitial());

        binding.btnShowNative.setOnClickListener(view -> navigateToAdsScreen(Constant.NATIVE));

        binding.btnShowBanner.setOnClickListener(view -> navigateToAdsScreen(Constant.BANNER));

        binding.btnShowReward.setOnClickListener(view -> showReward());

        binding.btIap.setOnClickListener(v -> showIAP());

        binding.btnInterPreload.setOnClickListener(v -> showPreInter());

        binding.btnShowInterstitialByClick.setOnClickListener(v -> showAdInterstitialByClick());

        binding.btnShowNativeAdapter.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
        });
    }

    private void showAdInterstitialByClick() {
        if (MyApplication.getApplication().getStorageCommon().interstitialAdByClick != null
                && MyApplication.getApplication().getStorageCommon().interstitialAdByClick.isReady()
                && !AppPurchase.getInstance().isPurchased()) {
            AperoAd.getInstance().showInterstitialAdByTimes(this, MyApplication.getApplication().getStorageCommon().interstitialAdByClick, new AperoAdCallback() {
                @Override
                public void onNextAction() {
                    super.onNextAction();
                    navigateToAdsScreen(Constant.INTERSTITIAL_BY_CLICK);
                }

                @Override
                public void onAdFailedToShow(@Nullable ApAdError adError) {
                    super.onAdFailedToShow(adError);
                    navigateToAdsScreen(Constant.INTERSTITIAL_BY_CLICK);
                }
            }, true);
        } else {
            navigateToAdsScreen(Constant.INTERSTITIAL_BY_CLICK);
        }
    }

    private void loadAdInterstitialByClick() {
        if (!AppPurchase.getInstance().isPurchased()
                && MyApplication.getApplication().getStorageCommon().interstitialAdByClick == null
                && NetworkUtil.isOnline()) {
            AperoAd.getInstance().getInterstitialAds(
                    this,
                    BuildConfig.interstitial_by_click,
                    new AperoAdCallback() {
                        @Override
                        public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                            super.onInterstitialLoad(interstitialAd);
                            MyApplication.getApplication().getStorageCommon().interstitialAdByClick = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@Nullable ApAdError adError) {
                            super.onAdFailedToLoad(adError);
                            MyApplication.getApplication().getStorageCommon().interstitialAdByClick = null;
                        }
                    });
        }
    }

    private void listenerPurchase() {
        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Log.e("PurchaseListener", "ProductPurchased:" + productId);
                Log.e("PurchaseListener", "transactionDetails:" + transactionDetails);
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Log.e("PurchaseListener", "displayErrorMessage:" + errorMsg);
            }

            @Override
            public void onUserCancelBilling() {

            }
        });
    }

    private void showIAP() {
        if (AppPurchase.getInstance().isPurchased()) {
            AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST);
        } else {
            InAppDialog dialog = new InAppDialog(this);
            dialog.setCallback(() -> {
                AppPurchase.getInstance().purchase(this, PRODUCT_ID);
                AppOpenManager.getInstance().disableAdResumeByClickAction();
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    private void showPreInter() {
        boolean isReload = false;
        PreloadAdsUtils.getInstance().showInterSameTime(this,
                MyApplication.getApplication().getStorageCommon().interPriority,
                MyApplication.getApplication().getStorageCommon().interNormal,
                isReload,
                new AdsInterCallBack() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                    }

                    @Override
                    public void onInterstitialNormalShowed() {
                        Log.e("AdsInterCommon", "onInterstitialNormalShowed: ");
                        AperoLogEventManager.onTrackEvent("Inter_show" + getClass().getSimpleName());
                        if (!isReload) {
                            MyApplication.getApplication().getStorageCommon().interNormal = null;
                        }
                    }

                    @Override
                    public void onInterstitialPriorityShowed() {
                        Log.e("AdsInterCommon", "onInterstitialPriorityShowed: ");
                        AperoLogEventManager.onTrackEvent("Inter_show" + getClass().getSimpleName());
                        if (!isReload) {
                            MyApplication.getApplication().getStorageCommon().interPriority = null;
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        AperoLogEventManager.onTrackEvent("Inter_click" + getClass().getSimpleName());
                    }

                    @Override
                    public void onNextAction() {
                        startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                    }
                }
        );
    }

    private void showInterstitial() {
        if (MyApplication.getApplication().getStorageCommon().interstitialAd != null
                && MyApplication.getApplication().getStorageCommon().interstitialAd.isReady()
                && !AppPurchase.getInstance().isPurchased()) {
            AperoAd.getInstance().forceShowInterstitial(
                    MainActivity.this,
                    MyApplication.getApplication().getStorageCommon().interstitialAd,
                    new AperoAdCallback() {
                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            navigateToAdsScreen(Constant.INTERSTITIAL);
                        }

                        @Override
                        public void onAdFailedToShow(@Nullable ApAdError adError) {
                            super.onAdFailedToShow(adError);
                            navigateToAdsScreen(Constant.INTERSTITIAL);
                        }
                    },
                    true
            );
            /* For positions that only appear inter once, for example,
            the position of inter screen on boarding, then set shouldReloadAds = false */
        } else {
            navigateToAdsScreen(Constant.INTERSTITIAL);
        }
    }

    private void showReward() {
        if (AppPurchase.getInstance().isPurchased()) {
            navigateToAdsScreen(Constant.REWARD);
        }
        if (MyApplication.getApplication().getStorageCommon().rewardAd != null
                && MyApplication.getApplication().getStorageCommon().rewardAd.isReady()
                && NetworkUtil.isOnline()) {
            AperoAd.getInstance().forceShowRewardAd(
                    this,
                    MyApplication.getApplication().getStorageCommon().rewardAd,
                    new AperoAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull ApRewardItem rewardItem) {
                            super.onUserEarnedReward(rewardItem);
                            onCheckUserEarnedReward = true;
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            if (onCheckUserEarnedReward) {
                                navigateToAdsScreen(Constant.REWARD);
                                onCheckUserEarnedReward = false;
                            }
                            MyApplication.getApplication().getStorageCommon().rewardAd = null;
                            loadAdReward();
                        }

                        @Override
                        public void onAdFailedToShow(@Nullable ApAdError adError) {
                            super.onAdFailedToShow(adError);
                            Toast.makeText(MainActivity.this, "Can't load ad. Please try again", Toast.LENGTH_SHORT).show();
                            loadAdReward();
                        }
                    }
            );
        } else {
            Toast.makeText(MainActivity.this, "Can't load ad. Please try again", Toast.LENGTH_SHORT).show();
            loadAdReward();
        }
    }

    private void navigateToAdsScreen(String ads) {
        Intent intent = new Intent(this, AdsActivity.class);
        intent.putExtra(Constant.TYPE_ADS, ads);
        startActivity(intent);
    }

    private void loadInterSameTime() {
        PreloadAdsUtils.getInstance().loadInterSameTime(
                this,
                BuildConfig.ads_inter_priority,
                BuildConfig.ad_interstitial,
                new AperoAdCallback() {
                    @Override
                    public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);
                        MyApplication.getApplication().getStorageCommon().interNormal = interstitialAd;
                        Log.e("AdsInterCommon", "onInterstitialLoad: ");
                    }

                    @Override
                    public void onInterPriorityLoaded(@Nullable ApInterstitialAd interstitialAd) {
                        super.onInterPriorityLoaded(interstitialAd);
                        MyApplication.getApplication().getStorageCommon().interPriority = interstitialAd;
                        Log.e("AdsInterCommon", "onInterPriorityLoaded: ");
                    }
                });
    }

    private void loadAdInterstitial() {
        if (!AppPurchase.getInstance().isPurchased()
                && MyApplication.getApplication().getStorageCommon().interstitialAd == null) {
            AperoAd.getInstance().getInterstitialAds(
                    this,
                    BuildConfig.interstitial,
                    new AperoAdCallback() {
                        @Override
                        public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                            super.onInterstitialLoad(interstitialAd);
                            MyApplication.getApplication().getStorageCommon().interstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@Nullable ApAdError adError) {
                            super.onAdFailedToLoad(adError);
                            MyApplication.getApplication().getStorageCommon().interstitialAd = null;
                        }
                    });
        }
    }

    private void loadAdReward() {
        if (!AppPurchase.getInstance().isPurchased()
                && MyApplication.getApplication().getStorageCommon().rewardAd == null
                && NetworkUtil.isOnline()) {
            MyApplication.getApplication().getStorageCommon().rewardAd = AperoAd.getInstance()
                    .getRewardAd(this, BuildConfig.reward, new AperoAdCallback() {});
        }
    }

    public void onTrackSimpleEventClick(View v) {
        AperoAdjust.onTrackEvent(EVENT_TOKEN_SIMPLE);
    }

    public void onTrackRevenueEventClick(View v) {
        AperoAdjust.onTrackRevenue(EVENT_TOKEN_REVENUE, 1f, "EUR");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNativeExit();
        loadInterSameTime();
    }

    private void loadNativeExit() {

    }

    @Override
    public void onBackPressed() {
        /*if (unifiedNativeAd == null)
            return;

        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd, 1);
        dialogExitApp1.setDialogExitListener(new DialogExitListener() {
            @Override
            public void onExit(boolean exit) {
                MainActivity.super.onBackPressed();
            }
        });
        dialogExitApp1.setCancelable(false);
        dialogExitApp1.show();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        AppPurchase.getInstance().handleActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "ProductPurchased:" + data.toString());
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
