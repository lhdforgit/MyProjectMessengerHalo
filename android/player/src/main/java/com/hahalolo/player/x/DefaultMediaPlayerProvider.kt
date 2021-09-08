/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import android.content.Context
import androidx.core.util.Pools
import androidx.media2.player.MediaPlayer
import com.google.android.exoplayer2.util.Util
import com.hahalolo.player.media.Media
import com.hahalolo.player.onEachAcquired
import kotlin.math.max

internal class DefaultMediaPlayerProvider(
  private val context: Context
) : MediaPlayerProvider {

  companion object {
    // Max number of Player instance are cached in the Pool
    // Magic number: Build.VERSION.SDK_INT / 6 --> API 16 ~ 18 will set pool size to 2, etc.
    internal val MAX_POOL_SIZE = max(Util.SDK_INT / 6, Runtime.getRuntime().availableProcessors())
  }

  private val playerPool = Pools.SimplePool<MediaPlayer>(MAX_POOL_SIZE)

  override fun acquirePlayer(media: Media): MediaPlayer {
    return if (media.mediaDrm != null) MediaPlayer(context.applicationContext) else run {
      playerPool.acquire() ?: MediaPlayer(context.applicationContext)
    }
  }

  override fun releasePlayer(
      media: Media,
      player: MediaPlayer
  ) {
    if (media.mediaDrm == null) {
      playerPool.release(player)
    } else {
      player.close()
    }
  }

  override fun cleanUp() {
    playerPool.onEachAcquired { it.close() }
  }
}
