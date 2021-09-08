/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.customtab;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

/**
 * Created by ndn on 10/15/18.
 */
public final class CustomTab {

    private CustomTab() {

    }

    public static void openUrl(Context context, String url) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            if (context instanceof Activity) {
                CustomTabActivityHelper.openCustomTab((Activity) context, customTabsIntent, Uri.parse(url), new WebviewFallback());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openUrl(Context context, String url, String toolbar) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            if (context instanceof Activity) {
                CustomTabActivityHelper.openCustomTab((Activity) context, customTabsIntent, Uri.parse(url), new WebviewFallback(), toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
