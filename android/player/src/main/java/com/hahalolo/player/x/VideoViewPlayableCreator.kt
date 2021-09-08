/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import androidx.media2.widget.VideoView
import com.hahalolo.player.core.Master
import com.hahalolo.player.core.Playable
import com.hahalolo.player.core.Playable.Config
import com.hahalolo.player.core.PlayableCreator
import com.hahalolo.player.media.Media

/**
 * Default implementation of [PlayableCreator] that supports [VideoView]
 */
class VideoViewPlayableCreator @JvmOverloads constructor(
  private val master: Master,
  private val playerProvider: MediaPlayerProvider = DefaultMediaPlayerProvider(master.app)
) : PlayableCreator<VideoView>(VideoView::class.java) {

  override fun createPlayable(
    config: Config,
    media: Media
  ): Playable {
    return VideoViewPlayable(
        master,
        media,
        config,
        VideoViewBridge(media, playerProvider)
    )
  }

  override fun cleanUp() {
    playerProvider.cleanUp()
  }
}
