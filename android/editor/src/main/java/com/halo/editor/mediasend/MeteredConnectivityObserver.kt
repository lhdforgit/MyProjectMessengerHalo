/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.annotation.MainThread
import androidx.core.net.ConnectivityManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.editor.util.ServiceUtil

/**
 * Lifecycle-bound observer for whether or not the active network connection is metered.
 */
internal class MeteredConnectivityObserver @MainThread constructor(
    private val context: Context,
    lifecycleOwner: LifecycleOwner
) : BroadcastReceiver(), DefaultLifecycleObserver {

    private val connectivityManager: ConnectivityManager =
        ServiceUtil.getConnectivityManager(context)
    private val metered: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(owner: LifecycleOwner) {
        context.registerReceiver(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onDestroy(owner: LifecycleOwner) {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        metered.postValue(
            ConnectivityManagerCompat.isActiveNetworkMetered(
                connectivityManager
            )
        )
    }

    /**
     * @return An observable value that is false when the network is unmetered, and true if the
     * network is either metered or unavailable.
     */
    fun isMetered(): LiveData<Boolean> {
        return metered
    }

    init {
        metered.value = ConnectivityManagerCompat.isActiveNetworkMetered(
            connectivityManager
        )
        lifecycleOwner.lifecycle.addObserver(this)
    }
}