/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer

import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.hahalolo.player.core.Playback

class DefaultControlDispatcher(private val playback: Playback) : ControlDispatcher {

  override fun dispatchSeekTo(
    player: Player,
    windowIndex: Int,
    positionMs: Long
  ): Boolean {
    player.seekTo(windowIndex, positionMs)
    return true
  }

  override fun dispatchSetShuffleModeEnabled(
    player: Player,
    shuffleModeEnabled: Boolean
  ): Boolean {
    player.shuffleModeEnabled = shuffleModeEnabled
    return true
  }

  override fun dispatchSetPlayWhenReady(
    player: Player,
    playWhenReady: Boolean
  ): Boolean {
    val playable = playback.playable
    if (playable != null) {
      if (playWhenReady) playback.manager.play(playable)
      else playback.manager.pause(playable)
    }
    return true
  }

  override fun dispatchSetRepeatMode(
    player: Player,
    repeatMode: Int
  ): Boolean {
    player.repeatMode = repeatMode
    return true
  }

  override fun dispatchStop(
    player: Player,
    reset: Boolean
  ): Boolean {
    val playable = playback.playable
    if (playable != null) playback.manager.pause(playable)
    player.stop(reset)
    return true
  }
}
