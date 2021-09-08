/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.hahalolo.player.core.Master

@RequiresApi(VERSION_CODES.N)
internal class MasterNetworkCallback(val master: Master) : NetworkCallback() {

  override fun onBlockedStatusChanged(
    network: Network,
    blocked: Boolean
  ) {
    master.onNetworkChanged()
  }

  override fun onCapabilitiesChanged(
    network: Network,
    networkCapabilities: NetworkCapabilities
  ) {
    master.onNetworkChanged()
  }

  override fun onLost(network: Network) {
    master.onNetworkChanged()
  }

  override fun onLinkPropertiesChanged(
    network: Network,
    linkProperties: LinkProperties
  ) {
    master.onNetworkChanged()
  }

  override fun onUnavailable() {
    master.onNetworkChanged()
  }

  override fun onLosing(
    network: Network,
    maxMsToLive: Int
  ) {
    master.onNetworkChanged()
  }

  override fun onAvailable(network: Network) {
    master.onNetworkChanged()
  }
}
