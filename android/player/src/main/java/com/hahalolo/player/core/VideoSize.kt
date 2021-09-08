/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

@Deprecated("From 1.1.0+, this data is no longer used anymore.")
data class VideoSize(
  val maxWidth: Int,
  val maxHeight: Int
) {
  companion object {
    val NONE = VideoSize(Int.MIN_VALUE, Int.MIN_VALUE)
    val SD = VideoSize(720 /* auto */, 480)
    val HD = VideoSize(1280 /* auto */, 720)
    val FHD = VideoSize(1920 /* auto */, 1080)
    val UHD = VideoSize(3840 /* auto */, 2160)
    val ORIGINAL = VideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
  }
}
