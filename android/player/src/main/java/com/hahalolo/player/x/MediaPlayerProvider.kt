/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import androidx.media2.player.MediaPlayer
import com.hahalolo.player.media.Media

interface MediaPlayerProvider {

  fun acquirePlayer(media: Media): MediaPlayer

  fun releasePlayer(
      media: Media,
      player: MediaPlayer
  )

  fun cleanUp()
}
