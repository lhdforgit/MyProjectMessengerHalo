/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.exoplayer2.ui.PlayerView
import com.hahalolo.player.R
import com.hahalolo.player.core.Playback
import com.hahalolo.player.core.ViewRendererProvider
import com.hahalolo.player.media.Media

class PlayerViewProvider : ViewRendererProvider() {

  override fun getRendererType(
    container: ViewGroup,
    media: Media
  ): Int {
    // Note: we want to use SurfaceView on API 24 and above. But reusing SurfaceView doesn't seem to
    // be straight forward, as it is not trivial to clean the cache of old video ...
    return if (media.mediaDrm != null /* || Build.VERSION.SDK_INT >= 24 */) {
      R.layout.player_surface_view
    } else {
      R.layout.player_textureview
    }
  }

  override fun createRenderer(
    playback: Playback,
    rendererType: Int
  ): PlayerView {
    return LayoutInflater.from(playback.container.context)
        .inflate(rendererType, playback.container, false) as PlayerView
  }
}
