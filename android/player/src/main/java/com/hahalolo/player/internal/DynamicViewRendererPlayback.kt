/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.internal

import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import com.hahalolo.player.core.Bucket
import com.hahalolo.player.core.Manager
import com.hahalolo.player.core.Playback

// Playback whose Container is to contain the actual Renderer, and the Renderer is created
// on-demand, right before the playback should start.
internal class DynamicViewRendererPlayback(
  manager: Manager,
  bucket: Bucket,
  container: ViewGroup,
  config: Config
) : Playback(manager, bucket, container, config) {

  override fun onPlay() {
    playable?.setupRenderer(this)
    super.onPlay()
  }

  override fun onPause() {
    super.onPause()
    playable?.teardownRenderer(this)
  }

  override fun onAttachRenderer(renderer: Any?): Boolean {
    if (renderer == null) return false
    require(renderer is View && renderer !== container)
    if (container.contains(renderer)) return false

    val parent = renderer.parent
    if (parent is ViewGroup && parent !== container) {
      parent.removeView(renderer)
    }

    // default implementation
    container.removeAllViews()
    container.addView(renderer)
    return true
  }

  override fun onDetachRenderer(renderer: Any?): Boolean {
    if (renderer == null) return false
    require(renderer is View && renderer !== container)
    if (!container.contains(renderer)) return false
    container.removeView(renderer)
    return true
  }
}
