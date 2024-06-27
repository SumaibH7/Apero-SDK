package com.example.andmoduleads.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.example.andmoduleads.SharePreferenceUtils;

import java.util.Locale;

public class LanguageUtils {
    private static Locale myLocale;

    // Save the installed language
    public static void saveLocale(String lang, Context context) {
        SharePreferenceUtils.setLanguage(context, lang);
    }

    // Reload saved languages and change them
    public static void loadLocale(Context context) {
        String language = SharePreferenceUtils.getLanguage(context);
        if (language.equals("")) {
            Configuration config = new Configuration();
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources()
                    .updateConfiguration(config, context.getResources().getDisplayMetrics());
        } else {
            changeLang(language, context);
        }
    }

    // method for changing the language
    public static void changeLang(String lang, Context context) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang, context);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
