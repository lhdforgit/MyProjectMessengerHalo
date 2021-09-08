/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.exoplayer.internal

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.AudioComponent
import com.hahalolo.player.core.PlayerEventListener
import com.hahalolo.player.core.VolumeInfoController
import com.hahalolo.player.media.VolumeInfo

fun Player.getVolumeInfo(): VolumeInfo {
  return when (this) {
    is VolumeInfoController -> VolumeInfo(this.volumeInfo)
    is AudioComponent -> { // will match SimpleExoPlayer
      val volume = this.volume
      VolumeInfo(volume == 0f, volume)
    }
    else -> throw UnsupportedOperationException(javaClass.name + " doesn't support this.")
  }
}

fun Player.setVolumeInfo(volume: VolumeInfo) {
  when (this) {
    is VolumeInfoController -> this.setVolumeInfo(volume)
    is AudioComponent -> { // will match SimpleExoPlayer
      if (volume.mute) {
        this.volume = 0f
      } else {
        this.volume = volume.volume
      }
      this.setAudioAttributes(this.audioAttributes, !volume.mute)
    }
    else -> throw UnsupportedOperationException(javaClass.name + " doesn't support this.")
  }
}

fun Player.addEventListener(listener: PlayerEventListener) {
  this.addListener(listener)
  this.videoComponent?.addVideoListener(listener)
  this.audioComponent?.addAudioListener(listener)
  this.textComponent?.addTextOutput(listener)
  this.metadataComponent?.addMetadataOutput(listener)
}

fun Player.removeEventListener(listener: PlayerEventListener) {
  this.removeListener(listener)
  this.videoComponent?.removeVideoListener(listener)
  this.audioComponent?.removeAudioListener(listener)
  this.textComponent?.removeTextOutput(listener)
  this.metadataComponent?.removeMetadataOutput(listener)
}
