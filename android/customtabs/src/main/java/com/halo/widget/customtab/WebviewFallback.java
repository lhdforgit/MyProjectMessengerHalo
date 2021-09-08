/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.customtab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by ndn on 10/15/18.
 * A Fallback that opens a Webview when Custom Tabs is not available
 */
public class WebviewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra(WebviewActivity.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }

    @Override
    public void openUri(Activity activity, Uri uri, String toolbar) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra(WebviewActivity.EXTRA_URL, uri.toString());
        intent.putExtra(WebviewActivity.EXTRA_TOOLBAR, toolbar);
        activity.startActivity(intent);
    }
}
