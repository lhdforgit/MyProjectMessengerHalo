/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.cache.pref.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

public class PrefUtilsImpl implements PrefUtils {

    private static final String PrefUtilsImpl_TARGET_PREF = "PrefUtilsImpl-target-pref";
    private static final String HEIGHT_KEY_BROAD = "HEIGHT_KEY_BROAD-target-pref-key";
    private static final int HEIGHT_KEY_BROAD_DEFAULT = 550;
    private int WIDTH_SCREEN = 0;

    private SharedPreferences sharedPref;
    private static PrefUtils prefUtils;

    private PrefUtilsImpl(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (context instanceof Activity){
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            WIDTH_SCREEN = Math.min(height, width);
        }
        this.sharedPref = context.getSharedPreferences(PrefUtilsImpl_TARGET_PREF, Context.MODE_PRIVATE);
    }

    public static PrefUtils getInstance(@NonNull Context context) {
        if (prefUtils == null) {
            prefUtils = new PrefUtilsImpl(context);
        }
        return prefUtils;
    }

    @Override
    public void insertHeightKeybroad(int heightKeybroad) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(HEIGHT_KEY_BROAD, heightKeybroad);
        editor.apply();
    }

    @Override
    public int getHeightKeybroad() {
        return sharedPref.getInt(HEIGHT_KEY_BROAD, WIDTH_SCREEN > 0 ? (WIDTH_SCREEN / 4 * 3) : HEIGHT_KEY_BROAD_DEFAULT);
    }
}
