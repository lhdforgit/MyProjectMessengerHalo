/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.customtab.shared;

import android.content.ComponentName;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;

import java.lang.ref.WeakReference;

/**
 * Implementation for the CustomTabsServiceConnection that avoids leaking the
 * ServiceConnectionCallback
 */
public class ServiceConnection extends CustomTabsServiceConnection {
    // A weak reference to the ServiceConnectionCallback to avoid leaking it.
    private WeakReference<ServiceConnectionCallback> mConnectionCallback;

    public ServiceConnection(ServiceConnectionCallback connectionCallback) {
        mConnectionCallback = new WeakReference<>(connectionCallback);
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ServiceConnectionCallback connectionCallback = mConnectionCallback.get();
        if (connectionCallback != null) connectionCallback.onServiceConnected(client);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ServiceConnectionCallback connectionCallback = mConnectionCallback.get();
        if (connectionCallback != null) connectionCallback.onServiceDisconnected();
    }
}
