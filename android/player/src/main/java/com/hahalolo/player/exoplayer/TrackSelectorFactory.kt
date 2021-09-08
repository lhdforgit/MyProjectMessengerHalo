/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.content.Context
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

/**
 * An interface that is used by the [DefaultExoPlayerProvider] to create a new
 * [DefaultTrackSelector] when needed.
 */
interface TrackSelectorFactory {

    /**
     * Creates a new [DefaultTrackSelector] instance, given the [Context] of the Application.
     */
    fun createDefaultTrackSelector(context: Context): DefaultTrackSelector
}

/**
 * Default implementation of the [TrackSelectorFactory].
 */
class DefaultTrackSelectorFactory : TrackSelectorFactory {
    override fun createDefaultTrackSelector(context: Context): DefaultTrackSelector =
        DefaultTrackSelector(context)
}