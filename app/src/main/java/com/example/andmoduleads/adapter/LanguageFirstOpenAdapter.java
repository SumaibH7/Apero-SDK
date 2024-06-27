package com.example.andmoduleads.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.andmoduleads.R;
import com.example.andmoduleads.databinding.ItemLanguageFirstOpenAppBinding;
import com.example.andmoduleads.model.Language;

import java.util.List;

public class LanguageFirstOpenAdapter extends
        RecyclerView.Adapter<LanguageFirstOpenAdapter.LanguageFirstOpenViewHolder> {
    private Context context;
    private List<Language> languages;
    private final OnLanguageClickListener onLanguageClickListener;

    public LanguageFirstOpenAdapter(Context context, List<Language> languages, OnLanguageClickListener onLanguageClickListener) {
        this.context = context;
        this.languages = languages;
        this.onLanguageClickListener = onLanguageClickListener;
    }

    @NonNull
    @Override
    public LanguageFirstOpenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageFirstOpenViewHolder(
                ItemLanguageFirstOpenAppBinding.inflate(LayoutInflater.from(context), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageFirstOpenViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageFirstOpenViewHolder extends RecyclerView.ViewHolder {
        private ItemLanguageFirstOpenAppBinding binding;

        public LanguageFirstOpenViewHolder(ItemLanguageFirstOpenAppBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(int position){
            Language language = languages.get(position);
            binding.tvNameLanguage.setText(language.getName());
            binding.imgIconLanguage.setImageResource(language.getIdIcon());
            if (language.isChoose()){
                binding.imgChooseLanguage.setImageResource(R.drawable.ic_checked);
            } else {
                binding.imgChooseLanguage.setImageResource(R.drawable.ic_unchecked);
            }
            binding.getRoot().setOnClickListener(view -> {
                onLanguageClickListener.onClickItemListener(language);
                for (int i = 0; i < languages.size(); i++) {
                    languages.get(i).setChoose(i == position);
                }
                notifyDataSetChanged();
            });
        }
    }

    public interface OnLanguageClickListener {
        void onClickItemListener(Language language);
    }
}
