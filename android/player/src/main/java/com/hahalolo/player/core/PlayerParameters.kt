/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

data class PlayerParameters(
  // Video
  val maxVideoWidth: Int = Int.MAX_VALUE,
  val maxVideoHeight: Int = Int.MAX_VALUE,
  val maxVideoBitrate: Int = Int.MAX_VALUE,
  // Audio
  val maxAudioBitrate: Int = Int.MAX_VALUE
) {

  fun playerShouldStart() = maxAudioBitrate > 0
          || (maxVideoBitrate > 0 && maxVideoWidth > 0 && maxVideoHeight > 0)

  companion object {
    val DEFAULT = PlayerParameters()
  }
}
