package com.weproov.app.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import com.weproov.app.MyApplication;

import java.util.Set;


public final class PrefUtils {

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return settings.getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return settings.getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return settings.getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return settings.getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return settings.getStringSet(key, defaultValue);
    }

    public static void putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).unregisterOnSharedPreferenceChangeListener(listener);
    }
}

