/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import android.os.Looper
import androidx.annotation.CallSuper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.Util
import com.hahalolo.player.core.DefaultTrackSelectorHolder
import com.hahalolo.player.core.VolumeChangedListener
import com.hahalolo.player.core.VolumeChangedListeners
import com.hahalolo.player.core.VolumeInfoController
import com.hahalolo.player.media.VolumeInfo
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Extend the [SimpleExoPlayer] to have custom configuration.
 *
 * @author ndn (2018/06/25).
 */
open class HaloExoPlayer(
    context: Context,
    clock: Clock = Clock.DEFAULT,
    renderersFactory: RenderersFactory = DefaultRenderersFactory(context.applicationContext),
    // TrackSelector is initialized at the same time a new Player instance is created.
    // This process will set the BandwidthMeter to the TrackSelector. Therefore we need to have
    // unique TrackSelector per Player instance.
    override val trackSelector: DefaultTrackSelector =
        DefaultTrackSelector(context.applicationContext),
    loadControl: LoadControl = DefaultLoadControl.Builder().createDefaultLoadControl(),
    bandwidthMeter: BandwidthMeter =
        DefaultBandwidthMeter.Builder(context.applicationContext).build(),
    looper: Looper = Util.getLooper()
) : SimpleExoPlayer(
    context,
    renderersFactory,
    trackSelector,
    loadControl,
    bandwidthMeter,
    AnalyticsCollector(clock),
    clock,
    looper
), VolumeInfoController, DefaultTrackSelectorHolder {

    private val volumeChangedListeners by lazy(NONE) { VolumeChangedListeners() }
    private var playerVolumeInfo = VolumeInfo(false, 1.0F) // backing field.

    override val volumeInfo
        get() = playerVolumeInfo

    @CallSuper
    override fun setVolume(audioVolume: Float) {
        this.setVolumeInfo(VolumeInfo(audioVolume == 0f, audioVolume))
    }

    override fun setVolumeInfo(volumeInfo: VolumeInfo): Boolean {
        val changed = this.playerVolumeInfo != volumeInfo // Compare equality, not reference.
        if (changed) {
            this.playerVolumeInfo = volumeInfo
            super.setVolume(if (volumeInfo.mute) 0F else volumeInfo.volume)
            val mute = volumeInfo.mute || volumeInfo.volume == 0F
            super.setAudioAttributes(super.getAudioAttributes(), !mute)
            this.volumeChangedListeners.onVolumeChanged(volumeInfo)
        }
        return changed
    }

    override fun addVolumeChangedListener(listener: VolumeChangedListener) {
        volumeChangedListeners.add(listener)
    }

    override fun removeVolumeChangedListener(listener: VolumeChangedListener?) {
        volumeChangedListeners.remove(listener)
    }
}
