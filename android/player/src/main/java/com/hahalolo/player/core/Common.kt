/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.core

import android.content.Context
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import com.google.android.exoplayer2.ExoPlayerLibraryInfo

object Common {

  const val REPEAT_MODE_OFF = 0 // Player.REPEAT_MODE_OFF
  const val REPEAT_MODE_ONE = 1 // Player.REPEAT_MODE_ONE
  const val REPEAT_MODE_ALL = 2 // Player.REPEAT_MODE_ALL
  const val REPEAT_MODE_GROUP = 3

  const val STATE_IDLE = 1 // Player.STATE_IDLE
  const val STATE_BUFFERING = 2 // Player.STATE_BUFFERING
  const val STATE_READY = 3 // Player.STATE_READY
  const val STATE_ENDED = 4 // Player.STATE_ENDED

  internal val PLAY = PlaybackAction(true)
  internal val PAUSE = PlaybackAction(false)

  // ExoPlayer's doesn't catch a RuntimeException and crash if Device has too many App installed.
  @RestrictTo(LIBRARY_GROUP)
  fun getUserAgent(
    context: Context,
    appName: String
  ): String {
    val versionName = try {
      val packageName = context.packageName
      val info = context.packageManager.getPackageInfo(packageName, 0)
      info.versionName
    } catch (e: Exception) {
      "?"
    }

    return "$appName/$versionName (Linux;Android ${Build.VERSION.RELEASE}) ${ExoPlayerLibraryInfo.VERSION_SLASHY}"
  }
}
