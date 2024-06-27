package com.example.andmoduleads;

import androidx.lifecycle.MutableLiveData;

import com.ads.control.ads.AperoAd;
import com.ads.control.billing.PurchaseItem;
import com.ads.control.config.AdjustConfig;
import com.ads.control.config.AperoAdConfig;
import com.ads.control.application.AdsMultiDexApplication;
import com.ads.control.billing.AppPurchase;
import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.example.andmoduleads.activity.SplashActivity;
import com.example.andmoduleads.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends AdsMultiDexApplication {
    private final String API_KEY_ADS = "V9kT7UfQE1XvRhSqZHHGJFdPSIvjNCPfx6juf8AC2S0Xtw2L7GODoHNF6R720vFwAFVV6Md5yVFrUBHCWRxHPbaPYhm3TVSzzUHtTfNLWUiOLvyX+BIgPQAX1GoYX74ZwOhEgPcxeJm6SoDVHvly8B0a/WO8ZLSWcByYbciuVHE=";

    private final String ADJUST_TOKEN = "cc4jvudppczk";
    private final String EVENT_PURCHASE_ADJUST = "gzel1k";
    private final String EVENT_AD_IMPRESSION_ADJUST = "gzel1k";

    private static MyApplication context;
    private StorageCommon storageCommon;
    public static MyApplication getApplication() {
        return context;
    }
    public StorageCommon getStorageCommon() {
        return storageCommon;
    }
    public MutableLiveData<Boolean> isAdCloseSplash = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        Admob.getInstance().setNumToShowAds(0);
        NetworkUtil.initNetwork(this);
        storageCommon = new StorageCommon();
        initBilling();
        initAds();
    }

    private void initAds() {
        String environment = BuildConfig.env_dev ? AperoAdConfig.ENVIRONMENT_DEVELOP : AperoAdConfig.ENVIRONMENT_PRODUCTION;
        aperoAdConfig = new AperoAdConfig(this,API_KEY_ADS, AperoAdConfig.PROVIDER_ADMOB, environment);

        // Optional: setup Adjust event
        AdjustConfig adjustConfig = new AdjustConfig(ADJUST_TOKEN);
        adjustConfig.setEventAdImpression(EVENT_AD_IMPRESSION_ADJUST);
        adjustConfig.setEventNamePurchase(EVENT_PURCHASE_ADJUST);
        aperoAdConfig.setAdjustConfig(adjustConfig);

        // Optional: enable ads resume
        aperoAdConfig.setIdAdResume(BuildConfig.open_resume);

        // set id app_open_app ( if use )
        AppOpenManager.getInstance().setSplashAdId(BuildConfig.ads_open_app);

        // Optional: setup list device test - recommended to use
        listTestDevice.add("EC25F576DA9B6CE74778B268CB87E431");
        aperoAdConfig.setListDeviceTest(listTestDevice);

        AperoAd.getInstance().init(this, aperoAdConfig, false);

        // Auto disable ad resume after user click ads and back to app
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }

    private void initBilling() {
        List<PurchaseItem> listPurchaseItem = new ArrayList<>();
        listPurchaseItem.add(new PurchaseItem("PRODUCT_ID", AppPurchase.TYPE_IAP.PURCHASE));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITH_FREE_TRIAL", "trial_id", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITHOUT_FREE_TRIAL", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        AppPurchase.getInstance().initBilling(this, listPurchaseItem);
    }
}
