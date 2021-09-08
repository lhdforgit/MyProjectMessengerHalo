/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player.media

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author ndn (2018/06/24).
 */
@Parcelize
data class PlaybackInfo(
  var resumeWindow: Int, // TODO rename to windowIndex
  var resumePosition: Long // TODO rename to position
) : Parcelable {

  constructor() : this(
      INDEX_UNSET,
      TIME_UNSET
  )

  companion object {
    const val TIME_UNSET = Long.MIN_VALUE + 1
    const val INDEX_UNSET = -1
  }
}
