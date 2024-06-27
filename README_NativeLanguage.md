Create a variable in the MyApplication to store the value of ad native
~~~
public MutableLiveData<ApNativeAd> nativeAdsLanguage = new MutableLiveData<>();
~~~

# SplashActivity
~~~
// load native ad
private void loadNativeAdsFirstLanguageOpen() {
        if (MyApplication.getApplication().getStorageCommon().nativeAdsLanguage.getValue() == null
                && !AppPurchase.getInstance().isPurchased()) {
            AperoAd.getInstance().loadNativePrioritySameTime(
                    this,
                    BuildConfig.ad_native_priority,
                    BuildConfig.ad_native,
                    com.ltl.apero.languageopen.R.layout.custom_native_ads_language_first,
                    new AperoAdCallback() {
                        @Override
                        public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                            super.onNativeAdLoaded(nativeAd);
                            MyApplication.getApplication().getStorageCommon()
                                    .nativeAdsLanguage.postValue(nativeAd);
                        }

                        @Override
                        public void onAdFailedToLoad(@Nullable ApAdError adError) {
                            super.onAdFailedToLoad(adError);
                            MyApplication.getApplication().getStorageCommon()
                                    .nativeAdsLanguage.postValue(null);
                        }
                    }
            );
        }
    }

private void startMain() {
        if (!SharePreferenceUtils.isFirstOpenApp(this)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            loadNativeAdsFirstLanguageOpen();
            startActivity(new Intent(SplashActivity.this, LanguageFirstOpenActivity.class));
        }
        finish();
    }

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startMain();
    }
~~~

# SharePreferenceUtils
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/java/com/example/andmoduleads/SharePreferenceUtils.java)

# LanguageFirstOpenActivity
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/java/com/example/andmoduleads/activity/LanguageFirstOpenActivity.java)

# LanguageFirstOpenAdapter
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/java/com/example/andmoduleads/adapter/LanguageFirstOpenAdapter.java)

# Model Language
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/java/com/example/andmoduleads/model/Language.java)

# Layout LanguageFirstOpenActivity
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/layout/activity_language_first_open.xml)

# Layout Item Language ( for adapter )
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/layout/item_language_first_open_app.xml)

# Layout loading ad native
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/layout/layout_loading_ads_native.xml)

# Layout native
[File](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/layout/custom_native_ads_language_first.xml)

[bg_ad](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/drawable/bg_ad.xml)

[bg_native_ad](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/drawable/bg_native_ad.xml)

[colors](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/values/colors.xml)

[ic_checked](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/drawable/ic_checked.xml)

[ic_unchecked](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/drawable/ic_unchecked.xml)

[background_ads_install](https://github.com/AperoVN/Apero-Sample-Module-Ads-Publishing/blob/develop/app/src/main/res/drawable/background_ads_install.xml)
