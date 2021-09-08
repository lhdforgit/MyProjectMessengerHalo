/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

/**
 * @author ngannd
 * Create by ngannd on 07/12/2018
 */
class NetworkChangeReceiver extends BroadcastReceiver {

    private WeakReference<NetworkChangeListener> mNetworkChangeListenerWeakReference;

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkChangeListener networkChangeListener = mNetworkChangeListenerWeakReference.get();
        if (networkChangeListener != null) {
            networkChangeListener.onNetworkChange(isNetworkConnected(context));
        }
    }

    void setNetworkChangeListener(NetworkChangeListener networkChangeListener) {
        mNetworkChangeListenerWeakReference = new WeakReference<>(networkChangeListener);
    }

    void removeNetworkChangeListener() {
        if (mNetworkChangeListenerWeakReference != null) {
            mNetworkChangeListenerWeakReference.clear();
        }
    }

    boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isAvailable() && netInfo.isConnected();

    }

    //Interface to send opt to listeners
    interface NetworkChangeListener {
        void onNetworkChange(boolean isNetworkAvailable);
    }
}
