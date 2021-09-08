/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ui.PlayerView
import com.hahalolo.player.core.AbstractPlayable
import com.hahalolo.player.core.Bridge
import com.hahalolo.player.core.Master
import com.hahalolo.player.core.Playback
import com.hahalolo.player.media.Media

class PlayerViewPlayable(
  master: Master,
  media: Media,
  config: Config,
  bridge: Bridge<PlayerView>
) : AbstractPlayable<PlayerView>(master, media, config, bridge) {

  override var renderer: Any?
    get() = bridge.renderer
    set(value) {
      require(value is PlayerView?)
      bridge.renderer = value
    }

  override fun onRendererAttached(playback: Playback, renderer: Any?) {
    val controller = playback.config.controller
    if (renderer is PlayerView) {
      if (controller is ControlDispatcher) {
        renderer.setControlDispatcher(controller)
        renderer.useController = true
      } else {
        renderer.setControlDispatcher(null)
        renderer.useController = false
      }
    }
    super.onRendererAttached(playback, renderer)
    if (renderer is PlayerView && renderer.useController && controller == null) {
      throw IllegalStateException(
        "To enable `useController`, Playback $playback must have a non-null Playback.Controller."
      )
    }
  }

  override fun onRendererDetached(playback: Playback, renderer: Any?) {
    if (renderer is PlayerView) {
      renderer.setControlDispatcher(null)
      renderer.useController = false
    }
    super.onRendererDetached(playback, renderer)
  }
}
