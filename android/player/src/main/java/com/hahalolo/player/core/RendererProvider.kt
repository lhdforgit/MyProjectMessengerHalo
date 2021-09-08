/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hahalolo.player.media.Media

/**
 * A pool to cache the renderer for the Playback.
 */
interface RendererProvider : DefaultLifecycleObserver {

  /**
   * Returns a renderer for the [playback] that can be used to render the content of [media], or
   * `null` if no renderer is available.
   */
  @JvmDefault
  fun acquireRenderer(
    playback: Playback,
    media: Media
  ): Any? = null

  /**
   * Releases the [renderer] back to the pool. Returns `true` if either the renderer is null (so
   * nothing needed to be done), or the renderer is successfully released back to the pool.
   */
  @JvmDefault
  fun releaseRenderer(
    playback: Playback,
    media: Media,
    renderer: Any?
  ): Boolean = renderer == null

  /**
   * Cleans up this pool.
   */
  @JvmDefault
  fun clear() = Unit

  @JvmDefault
  override fun onDestroy(owner: LifecycleOwner) {
    clear()
  }
}

typealias RendererProviderFactory = () -> RendererProvider
