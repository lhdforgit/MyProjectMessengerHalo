/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.ViewGroup
import com.hahalolo.player.core.Bucket
import com.hahalolo.player.core.Manager
import com.hahalolo.player.core.Playback

// Playback whose Container is also the Renderer.
// This Playback will request the Playable to setup the Renderer as soon as it is active, and
// release the Renderer as soon as it is inactive.
internal class StaticViewRendererPlayback(
  manager: Manager,
  bucket: Bucket,
  container: ViewGroup,
  config: Config
) : Playback(manager, bucket, container, config) {

  override fun onActive() {
    super.onActive()
    playable?.setupRenderer(this)
  }

  override fun onInActive() {
    playable?.teardownRenderer(this)
    super.onInActive()
  }

  override fun acquireRenderer(): Any? {
    return this.container
  }

  /**
   * This operation would always be false if the renderer is not null, since the renderer is never
   * released to any pool.
   */
  override fun releaseRenderer(renderer: Any?) = renderer == null

  override fun onAttachRenderer(renderer: Any?): Boolean {
    require(renderer == null || renderer === container)
    return true // true because we can always use this renderer.
  }

  override fun onDetachRenderer(renderer: Any?): Boolean {
    require(renderer == null || renderer === container)
    return true
  }
}
