package com.example.andmoduleads.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.helper.adnative.NativeAdConfig;
import com.ads.control.helper.adnative.NativeAdHelper;
import com.ads.control.helper.adnative.params.NativeAdParam;
import com.ads.control.helper.banner.BannerAdConfig;
import com.ads.control.helper.banner.BannerAdHelper;
import com.ads.control.helper.banner.params.BannerAdParam;
import com.ads.control.util.AppConstant;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.R;
import com.example.andmoduleads.databinding.ActivityAdsBinding;
import com.example.andmoduleads.utils.Constant;
import com.example.andmoduleads.utils.NetworkUtil;

public class AdsActivity extends AppCompatActivity {

    private ActivityAdsBinding binding;
    private String ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getDataIntent();
        initView();
    }

    private void getDataIntent() {
        ads = getIntent().getStringExtra(Constant.TYPE_ADS);
    }

    private void loadAndShowNative() {
        if (!AppPurchase.getInstance().isPurchased() && NetworkUtil.isOnline()) {
            AperoAd.getInstance().loadNativeAdResultCallback(
                    this,
                    BuildConfig.native_normal,
                    R.layout.custom_native_ads_language_first,
                    new AperoAdCallback() {
                        @Override
                        public void onAdFailedToLoad(@Nullable ApAdError adError) {
                            super.onAdFailedToLoad(adError);
                            binding.flAdsNative.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                            super.onNativeAdLoaded(nativeAd);
                            AperoAd.getInstance().populateNativeAdView(
                                    AdsActivity.this,
                                    nativeAd,
                                    binding.flAdsNative,
                                    binding.shimmerNativeAds.shimmerContainerNative
                            );
                        }
                    }
            );
        } else {
            binding.flAdsNative.setVisibility(View.GONE);
        }
    }

    private void loadAndShowBanner() {
        // in case banner normal : normal
        // in case banner collapsible : collapsible
        String typeBanner = "Normal";

        /* Banners will have 2 types: regular banners and collapsible banners,
        depending on the type of ad, use the load function accordingly */

        if (!AppPurchase.getInstance().isPurchased() && NetworkUtil.isOnline()) {
            if (typeBanner.equals("Normal")) {
                AperoAd.getInstance().loadBanner(this, BuildConfig.banner_normal);
            } else {
                AperoAd.getInstance().loadCollapsibleBanner(this,
                        BuildConfig.banner_collapsible,
                        AppConstant.CollapsibleGravity.BOTTOM,
                        null);
            }
        } else {
            binding.flAdsBanner.setVisibility(View.GONE);
        }
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

    private void aperoNative() {
        NativeAdHelper nativeAdHelper = new NativeAdHelper(
                this,
                this,
                new NativeAdConfig(
                        BuildConfig.native_normal,
                        true,
                        true,
                        R.layout.custom_native_ads_language_first
                )
        );

        nativeAdHelper.setNativeContentView(binding.flAdsNative)
                .setShimmerLayoutView(binding.shimmerNativeAds.shimmerContainerNative);

        if (nativeAdHelper.getNativeAd() != null) {
            nativeAdHelper.requestAds(new NativeAdParam.Ready(nativeAdHelper.getNativeAd()));
        } else {
            nativeAdHelper.requestAds(NativeAdParam.Request.create());
        }
    }

    private void initView() {
        switch (ads) {
            case Constant.INTERSTITIAL:
                binding.txtTitle.setText(Constant.INTERSTITIAL);
                break;
            case Constant.NATIVE:
                binding.txtTitle.setText(Constant.NATIVE);
                binding.flAdsNative.setVisibility(View.VISIBLE);
                aperoNative();
                //loadAndShowNative();
                break;
            case Constant.BANNER:
                binding.txtTitle.setText(Constant.BANNER);
                binding.flAdsBanner.setVisibility(View.VISIBLE);
                aperoBanner();
                //loadAndShowBanner();
                break;
            case Constant.REWARD:
                binding.txtTitle.setText(Constant.REWARD);
                break;
            case Constant.INTERSTITIAL_BY_CLICK:
                binding.txtTitle.setText(Constant.INTERSTITIAL_BY_CLICK);
                break;
        }
    }
}
