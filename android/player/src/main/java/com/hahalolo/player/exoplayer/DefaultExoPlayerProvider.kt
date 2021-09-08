/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.Util

/**
 * @author ndn (2018/10/27).
 */
class DefaultExoPlayerProvider @JvmOverloads constructor(
    context: Context,
    private val clock: Clock = Clock.DEFAULT,
    private val bandwidthMeterFactory: BandwidthMeterFactory = DefaultBandwidthMeterFactory(),
    private val trackSelectorFactory: TrackSelectorFactory = DefaultTrackSelectorFactory(),
    private val loadControl: LoadControl = DefaultLoadControl(),
    private val renderersFactory: RenderersFactory =
        DefaultRenderersFactory(context.applicationContext)
) : RecycledExoPlayerProvider(context) {

    override fun createExoPlayer(context: Context): Player = HaloExoPlayer(
        context,
        clock,
        renderersFactory,
        trackSelectorFactory.createDefaultTrackSelector(context),
        loadControl,
        bandwidthMeterFactory.createBandwidthMeter(context),
        Util.getLooper()
    )
}
