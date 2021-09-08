/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import android.view.LayoutInflater
import com.hahalolo.player.R
import com.hahalolo.player.core.Playback
import com.hahalolo.player.core.ViewRendererProvider

internal class VideoViewProvider : ViewRendererProvider() {

  override fun createRenderer(
    playback: Playback,
    rendererType: Int
  ): Any {
    val container = playback.container
    return LayoutInflater.from(container.context)
        .inflate(R.layout.video_view, container, false)
  }
}
