/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.x

import androidx.media2.common.MediaItem
import androidx.media2.common.SessionPlayer
import androidx.media2.common.UriMediaItem
import androidx.media2.player.MediaPlayer
import androidx.media2.widget.VideoView
import com.hahalolo.player.core.AbstractBridge
import com.hahalolo.player.core.PlayerParameters
import com.hahalolo.player.media.Media
import com.hahalolo.player.media.PlaybackInfo
import com.hahalolo.player.media.VolumeInfo

/**
 * [com.hahalolo.player.core.Bridge] for [VideoView]
 */
// Really experimental implementation of AbstractBridge using androidx.media2 APIs.
// Not production-ready.
class VideoViewBridge(
    private val media: Media,
    private val playerProvider: MediaPlayerProvider
) : AbstractBridge<VideoView>() {

  private val mediaItem: MediaItem = UriMediaItem.Builder(media.uri)
      .build()

  private var player: MediaPlayer? = null

  override var renderer: VideoView? = null
    set(value) {
      if (field === value) return // same reference
      field = value
      val player = this.player
      if (player != null && value != null) value.setPlayer(player)
    }

  override val playerState: Int
    get() = this.player?.playerState ?: SessionPlayer.PLAYER_STATE_IDLE

  override fun isPlaying(): Boolean =
    this.player?.playerState == SessionPlayer.PLAYER_STATE_PLAYING

  override fun seekTo(positionMs: Long) {
    this.player?.seekTo(positionMs)
  }

  override var repeatMode: Int = player?.repeatMode ?: SessionPlayer.REPEAT_MODE_NONE
    set(value) {
      field = value
      player?.repeatMode = value
    }

  override var playbackInfo: PlaybackInfo = PlaybackInfo()

  override var volumeInfo: VolumeInfo = VolumeInfo()

  // TODO(ndn): update the Player with new parameters.
  override var playerParameters: PlayerParameters = PlayerParameters.DEFAULT

  override fun prepare(loadSource: Boolean) {
    if (loadSource) ready()
  }

  override fun ready() {
    if (player == null) {
      player = playerProvider.acquirePlayer(media)
          .also {
            it.setMediaItem(mediaItem)
            it.repeatMode = repeatMode
            renderer?.setPlayer(it)
            it.prepare()
          }
    }
  }

  override fun play() {
    super.play()
    requireNotNull(player).play()
  }

  override fun pause() {
    super.pause()
    player?.pause()
  }

  override fun reset(resetPlayer: Boolean) {
    player?.reset()
  }

  override fun release() {
    player?.let {
      // TODO any other local clean up?
      it.reset()
      playerProvider.releasePlayer(media, it)
    }
    player = null
  }
}
