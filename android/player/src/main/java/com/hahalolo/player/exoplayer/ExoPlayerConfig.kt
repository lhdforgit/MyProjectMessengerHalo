/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.util.Clock

/**
 * Detailed config for building a [com.google.android.exoplayer2.SimpleExoPlayer]. Only for
 * advanced user.
 *
 * @see createHaloPlayer
 */
data class ExoPlayerConfig(
    internal val clock: Clock = Clock.DEFAULT,
    // DefaultTrackSelector parameters
    internal val trackSelectorParameters: Parameters = Parameters.DEFAULT_WITHOUT_CONTEXT,
    internal val trackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(),
    // DefaultBandwidthMeter parameters
    internal val overrideInitialBitrateEstimate: Long = -1,
    internal val resetOnNetworkTypeChange: Boolean = true,
    internal val slidingWindowMaxWeight: Int = DefaultBandwidthMeter.DEFAULT_SLIDING_WINDOW_MAX_WEIGHT,
    // DefaultRenderersFactory parameters
    internal val enableDecoderFallback: Boolean = true,
    internal val allowedVideoJoiningTimeMs: Long = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS,
    internal val extensionRendererMode: Int = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON,
    internal val playClearSamplesWithoutKeys: Boolean = false,
    internal val mediaCodecSelector: MediaCodecSelector = MediaCodecSelector.DEFAULT,
    // DefaultLoadControl parameters
    internal val allocator: DefaultAllocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
    internal val minBufferMs: Int = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
    internal val maxBufferMs: Int = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
    internal val bufferForPlaybackMs: Int = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
    internal val bufferForPlaybackAfterRebufferMs: Int = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
    internal val prioritizeTimeOverSizeThresholds: Boolean = DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS,
    internal val targetBufferBytes: Int = DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
    internal val backBufferDurationMs: Int = DefaultLoadControl.DEFAULT_BACK_BUFFER_DURATION_MS,
    internal val retainBackBufferFromKeyframe: Boolean = DefaultLoadControl.DEFAULT_RETAIN_BACK_BUFFER_FROM_KEYFRAME,
    // Other configurations
    internal val cache: Cache? = null,
    internal val drmSessionManagerProvider: DefaultDrmSessionManagerProvider? = null
) {

    companion object {
        /**
         * Every fields are default, following the setup by ExoPlayer.
         */
        @JvmStatic
        val DEFAULT = ExoPlayerConfig()

        /**
         * Reduce some setting for fast start playback.
         */
        @JvmStatic
        val FAST_START = ExoPlayerConfig(
            minBufferMs = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS / 10,
            maxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS / 10,
            bufferForPlaybackMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS / 10,
            bufferForPlaybackAfterRebufferMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS / 10
        )
    }

    internal fun createLoadControl(): LoadControl = DefaultLoadControl.Builder()
        .setAllocator(allocator)
        .setBufferDurationsMs(
            minBufferMs,
            maxBufferMs,
            bufferForPlaybackMs,
            bufferForPlaybackAfterRebufferMs
        )
        .createDefaultLoadControl()

    internal fun createBandwidthMeterFactory(): BandwidthMeterFactory =
        object : BandwidthMeterFactory {
            override fun createBandwidthMeter(context: Context): BandwidthMeter {
                return DefaultBandwidthMeter.Builder(context.applicationContext)
                    .setClock(clock)
                    .setResetOnNetworkTypeChange(resetOnNetworkTypeChange)
                    .setSlidingWindowMaxWeight(slidingWindowMaxWeight)
                    .apply {
                        if (overrideInitialBitrateEstimate > 0) {
                            setInitialBitrateEstimate(overrideInitialBitrateEstimate)
                        }
                    }
                    .build()
            }
        }

    internal fun createTrackSelectorFactory(): TrackSelectorFactory =
        object : TrackSelectorFactory {
            override fun createDefaultTrackSelector(context: Context): DefaultTrackSelector {
                val parameters: Parameters =
                    if (trackSelectorParameters === Parameters.DEFAULT_WITHOUT_CONTEXT) {
                        trackSelectorParameters.buildUpon()
                            .setViewportSizeToPhysicalDisplaySize(context, true)
                            .build()
                    } else {
                        trackSelectorParameters
                    }

                return DefaultTrackSelector(parameters, trackSelectionFactory)
            }
        }
}