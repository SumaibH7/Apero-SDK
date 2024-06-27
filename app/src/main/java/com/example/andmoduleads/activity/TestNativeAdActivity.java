package com.example.andmoduleads.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.andmoduleads.databinding.ActivityTestNativeAdBinding;
import com.example.andmoduleads.utils.PreloadAdsUtils;

public class TestNativeAdActivity extends AppCompatActivity {

    private ActivityTestNativeAdBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestNativeAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showPreNative();
    }

    private void showPreNative() {
        PreloadAdsUtils.getInstance().showPreNativeSametime(
                TestNativeAdActivity.this,
                binding.frAds,
                binding.includeNative.shimmerContainerNative
        );
    }
}