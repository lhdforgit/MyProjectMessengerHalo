/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import com.google.android.exoplayer2.Player
import com.hahalolo.player.media.Media

/**
 * @author ndn (2018/10/27).
 *
 * A Pool to store unused Player instance. As initializing a Player is relatively expensive,
 * we try to cache them for reuse.
 */
interface ExoPlayerProvider {

  fun acquirePlayer(media: Media): Player

  fun releasePlayer(
      media: Media,
      player: Player
  )

  fun cleanUp()
}
