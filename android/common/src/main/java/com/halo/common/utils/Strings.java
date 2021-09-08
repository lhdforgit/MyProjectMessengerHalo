/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.halo.constant.BuildConfig;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 */
public final class Strings {

    public static void log(Object o) {
        if (BuildConfig.DEBUG) log("", o);
    }

    public static void log(String message, Object o) {

        String json = "";
        if (o instanceof String) {
            json = (String) o;
        } else {
            json = (o == null ? "NULL" : new Gson().toJson(o));
        }
        int maxLogSize = 2600;
        int partSize = json.length() / maxLogSize;
        if (partSize > 0 && BuildConfig.DEBUG)
            android.util.Log.d("TESTAPP_ANHSONDAY", (TextUtils.isEmpty(message) ? "" : (message + ": ")) + "Size: " + json.length());

        for (int i = 0; i <= partSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > json.length() ? json.length() : end;
            if (BuildConfig.DEBUG) android.util.Log.d("TESTAPP_ANHSONDAY",
                    (TextUtils.isEmpty(message)
                            ? "" : (message + ": "))
                            + (partSize > 0 ? "PART " + (i + 1) + ": " : "")
                            + json.substring(start, end));
        }
    }
}
