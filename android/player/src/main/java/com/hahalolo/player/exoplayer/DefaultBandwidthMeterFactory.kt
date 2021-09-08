/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

class DefaultBandwidthMeterFactory : BandwidthMeterFactory {

  override fun createBandwidthMeter(context: Context): BandwidthMeter =
    DefaultBandwidthMeter.Builder(context.applicationContext).build()
}
