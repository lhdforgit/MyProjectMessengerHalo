/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

/**
 * An observer to allow client to know when the [Playable] (defined by its tag)'s [Playback] is
 * changed. Client can use the [Manager.observe] method to register an observer.
 */
typealias PlayableObserver = (
  Any /* Playable Tag */,
  Playback? /* Previous Playback */,
  Playback? /* Next Playback */
) -> Unit

/**
 * Refer to values of [com.google.android.exoplayer2.C.NetworkType]
 */
typealias NetworkType = Int
