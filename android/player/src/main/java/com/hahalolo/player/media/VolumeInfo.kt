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
data class VolumeInfo(
  val mute: Boolean = false,
  val volume: Float = 1F
) : Parcelable {

  constructor(original: VolumeInfo) : this(original.mute, original.volume)
}
