package com.example.andmoduleads.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.AperoAd;
import com.ads.control.billing.AppPurchase;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.example.andmoduleads.SharePreferenceUtils;
import com.example.andmoduleads.adapter.LanguageFirstOpenAdapter;
import com.example.andmoduleads.databinding.ActivityLanguageFirstOpenBinding;
import com.example.andmoduleads.model.Language;
import com.example.andmoduleads.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageFirstOpenActivity extends AppCompatActivity implements LanguageFirstOpenAdapter.OnLanguageClickListener {

    private LanguageFirstOpenAdapter adapter;
    private List<Language> languages;
    private ActivityLanguageFirstOpenBinding binding;
    private String languageCode = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageFirstOpenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        showAdsNativeLanguage();
    }

    private void initView() {
        initLanguage();
        initEvent();
    }

    private void showAdsNativeLanguage() {
        if (!AppPurchase.getInstance().isPurchased()) {
            MyApplication.getApplication().getStorageCommon().nativeAdsLanguage.observe(this, apNativeAd -> {
                if (apNativeAd != null) {
                    AperoAd.getInstance().populateNativeAdView(
                            LanguageFirstOpenActivity.this,
                            apNativeAd,
                            binding.layoutAdNative,
                            binding.layoutShimmer.shimmerContainerNative
                    );
                } else {
                    binding.layoutAdNative.setVisibility(View.GONE);
                }
            });
        } else {
            binding.layoutAdNative.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        binding.imgDone.setOnClickListener(view -> {
            changeLanguage();
        });
    }

    private void initLanguage() {
        languages = new ArrayList<>();
        languages.add(new Language("en", getString(R.string.english), R.drawable.ic_language_en, false));
        languages.add(new Language("zh", getString(R.string.china), R.drawable.ic_language_en, false));
        languages.add(new Language("fr", getString(R.string.france), R.drawable.ic_language_en, false));

        Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        Language languageSystem = null;
        for (int i = 0; i < languages.size(); i++){
            if (languages.get(i).equals(locale.getLanguage())){
                languageSystem = languages.get(i);
                languageCode = locale.getLanguage();
            }
        }
        if (languageSystem != null){
            languages.remove(languageSystem);
            languages.add(0, languageSystem);
        }
        languages.get(0).setChoose(true);
        setupAdapter();
    }

    private void setupAdapter() {
        adapter = new LanguageFirstOpenAdapter(this, languages, this);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickItemListener(Language language) {
        languageCode = language.getCode();
    }

    private void changeLanguage(){
        SharePreferenceUtils.setLanguage(this, languageCode);
        LanguageUtils.changeLang(SharePreferenceUtils.getLanguage(this), this);
        SharePreferenceUtils.setFirstOpenApp(LanguageFirstOpenActivity.this, false);
        startActivity(new Intent(LanguageFirstOpenActivity.this, MainActivity.class));
        finish();
    }
}
