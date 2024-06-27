package com.example.andmoduleads;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {

    public static String PREF_NAME = "data";
    public static String IS_FIRST_OPEN_APP = "is_first_open_app";
    public static String LANGUAGE_CODE = "language_code";

    public static void setFirstOpenApp(Context context, boolean isFirstOpenApp) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(IS_FIRST_OPEN_APP, isFirstOpenApp).apply();
    }

    public static boolean isFirstOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(IS_FIRST_OPEN_APP, true);
    }

    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putString(LANGUAGE_CODE, languageCode).apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getString(LANGUAGE_CODE, "");
    }
}
