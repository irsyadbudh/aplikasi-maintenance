package com.example.signuploginrealtime.ListItem;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "RadioPrefs";

    public static void setRadioButtonStatus(Context context, String key, boolean status) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, status);
        editor.apply();
    }

    public static boolean getRadioButtonStatus(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false); // Default status is false
    }
}
